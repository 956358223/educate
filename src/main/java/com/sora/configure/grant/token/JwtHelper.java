package com.sora.configure.grant.token;

import com.sora.configure.grant.domain.Principals;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class JwtHelper {

    @Value("${jwt.prefix}")
    private String prefix;

    @Value("${jwt.type}")
    private String jwtType;

    @Value("${jwt.secure}")
    private String secure;

    @Value("${jwt.expire.first}")
    private Long firstExpire;

    @Value("${jwt.expire.refresh}")
    private Long refreshExpire;

    @Autowired
    private RedisTemplate redisTemplate;

    private Clock clock = DefaultClock.INSTANCE;

    private String getId() {
        return UUID.randomUUID().toString();
    }

    public JwtEntity create(Map<String, Object> claims, String subject, String sole) {
        String token = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (firstExpire * 1000))).signWith(SignatureAlgorithm.HS512, secure)
                .setId(getId())
                .compact();
        redisTemplate.opsForValue().set(getTokenKey(subject, sole), token, firstExpire, TimeUnit.SECONDS);
        return JwtEntity.builder().username(subject).token(token).type(jwtType).expire(firstExpire).refresh(false).build();
    }

    public JwtEntity refresh(String token) {
        final Date create = clock.now();
        final Claims claims = getClaims(token);
        claims.setIssuedAt(create);
        Date expire = new Date(System.currentTimeMillis() + (refreshExpire * 1000));
        String refreshToken = Jwts.builder().setClaims(claims).setExpiration(expire).signWith(SignatureAlgorithm.HS256, secure).compact();
        redisTemplate.opsForValue().set(getTokenKey(getSubject(token), getSole(token)), refreshToken, refreshExpire, TimeUnit.SECONDS);
        return JwtEntity.builder().username(claims.getSubject()).token(refreshToken).type(jwtType).expire(expire.getTime() - create.getTime() / 1000).refresh(false).build();
    }

    public JwtEntity getJwtStore(String token) {
        Claims claims = getClaims(token);
        if (claims == null) return null;
        Date expire = claims.getExpiration();
        Date create = claims.getIssuedAt();
        return JwtEntity.builder().username(claims.getSubject()).token(token).type(jwtType).expire((expire.getTime() - create.getTime()) / 1000).refresh(false).build();
    }

    public String getSubject(String token) {
        return apply(token, Claims::getSubject);
    }

    public String getSole(String token) {
        return (String) Optional.ofNullable(getClaims(token).get("sole")).orElse("");
    }

    public Date getExpire(String token) {
        return apply(token, Claims::getExpiration);
    }

    public boolean isExpired(String token) {
        return getExpire(token).before(new Date());
    }

    public <T> T apply(String token, Function<Claims, T> function) {
        return function.apply(getClaims(token));
    }

    public Claims getClaims(String token) {
        Claims claims = null;
        if (!StringUtils.isEmpty(token)) {
            try {
                claims = Jwts.parser().setSigningKey(secure).parseClaimsJws(token.replace(jwtType, "")).getBody();
            } catch (ExpiredJwtException e) {
                log.error("Jwt Expired!");
            } catch (UnsupportedJwtException e) {
                log.error("Jwt Unsupported!");
            } catch (MalformedJwtException e) {
                log.error("Jwt Malformed!");
            } catch (SignatureException e) {
                log.error("Jwt Signature!");
            } catch (IllegalArgumentException e) {
                log.error("Jwt IllegalArgument!");
            }
        }
        return claims;
    }

    public String getTokenKey(String username, String sole) {
        return prefix + "[" + username + "]:" + sole + ":token";
    }

    public String getMenusKey(String username, String sole) {
        return prefix + "[" + username + "]:" + sole + ":menus";
    }

    public boolean verify(String token, UserDetails details) {
        if (StringUtils.isEmpty(token)) return false;
        Principals principals = (Principals) details;
        final String subject = getSubject(token);
        return subject.equals(principals.getUsername()) && !isExpired(token);
    }

}
