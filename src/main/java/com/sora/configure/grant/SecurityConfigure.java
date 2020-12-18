package com.sora.configure.grant;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.http.Response;
import com.sora.configure.grant.dynamic.DynamicAccessDecisionManager;
import com.sora.configure.grant.dynamic.DynamicSecurityMetadataSource;
import com.sora.configure.grant.filter.AuthenticationFilter;
import com.sora.configure.grant.filter.JwtAuthOncePerFilter;
import com.sora.configure.grant.handler.CustomLogoutHandler;
import com.sora.configure.grant.service.CustomDetailsService;
import com.sora.configure.grant.service.CustomProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Properties;

@EnableWebSecurity
public class SecurityConfigure extends WebSecurityConfigurerAdapter {

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public CustomLogoutHandler customLogoutHandler;

    @Autowired
    public CustomDetailsService customDetailsService;

    @Autowired
    public CustomProcessService customProcessService;

    @Autowired
    public JwtAuthOncePerFilter jwtAuthOncePerFilter;

    @Autowired
    public DynamicAccessDecisionManager dynamicAccessDecisionManager;

    @Autowired
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(16);
    }

    @Bean
    public Producer producer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "160");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.noise.color", "black");
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha captcha = new DefaultKaptcha();
        captcha.setConfig(config);
        return captcha;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        configurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return configurationSource;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(customDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Override
    @Autowired
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.ico", "/csrf", "/captcha/login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(dynamicAccessDecisionManager);
                        object.setSecurityMetadataSource(dynamicSecurityMetadataSource);
                        return object;
                    }
                })
                .anyRequest().authenticated().and()
                .formLogin().loginProcessingUrl("/account/login").permitAll().and()
                .logout().logoutSuccessHandler(customLogoutHandler).logoutUrl("/logout").permitAll().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(new AuthenticationFilter("/account/login", authenticationManager(),
                        customProcessService, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthOncePerFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint((x, y, z) -> Response.stream(RespBody.body(RespState.NOT_AUTHORIZED)))
                .accessDeniedHandler((x, y, z) -> Response.stream(RespBody.body(RespState.ACCESS_DENIED)))
                .and()
                .headers().frameOptions().sameOrigin().cacheControl().and()
                .and()
                .csrf().disable().cors().configurationSource(corsConfigurationSource());
    }
}
