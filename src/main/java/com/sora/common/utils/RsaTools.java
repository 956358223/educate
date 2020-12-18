package com.sora.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RsaTools {

    private static final Integer DEFAULT_KEY_SIZE = 1024;

    private static final String DEFAULT_ALGORITHM = "RSA";

    private static final Integer MAX_DECRYPT_BLOCK = 128;

    private static final Integer DEFAULT_KEYS_SIZE = 2048;

    private static RSAPublicKey publicKey;

    private static RSAPrivateKey privateKey;

    public static RsaTools getInstance() {
        try {
            RsaTools.generate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new RsaTools();
    }

    public static void generate() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DEFAULT_ALGORITHM, new BouncyCastleProvider());
        keyPairGenerator.initialize(DEFAULT_KEYS_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    public static void generate(int size) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DEFAULT_ALGORITHM, new BouncyCastleProvider());
        keyPairGenerator.initialize(size);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    public void generate(String publicPath, String privatePath, String body, Integer size) throws Exception {
        this.generate(size);
        this.write(publicPath, encodePublicKey(body).getBytes());
        this.write(privatePath, encodePrivateKey(body).getBytes());
    }

    public static void write(String path, byte[] bytes) throws IOException {
        File dest = new File(path);
        if (!dest.exists()) {
            dest.createNewFile();
        }
        Files.write(dest.toPath(), bytes);
    }

    public String encodePrivateKey(String body) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        SecureRandom random = new SecureRandom();
        random.setSeed(body.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey, random);
        return Base64.getEncoder().encodeToString(cipher.doFinal(body.getBytes("utf-8")));
    }

    public String encodePublicKey(String body) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(body.getBytes("utf-8")));
    }

    public String decodePrivateKey(String body) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return decryptPrivateKey(body);
    }

    public String decodePublicKey(String body) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(body)));
    }

    private String decryptPrivateKey(String body) {
        try {
            byte[] privateKeyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            byte[] data = decryptToBytes(body);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] decryptToBytes(String body) {
        return Base64.getDecoder().decode(body);
    }

    private String encodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }


    public static void main(String[] args) throws Exception {
        RsaTools tools = RsaTools.getInstance();
        String body = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
//        String encodes = tools.encodePrivateKey(body);
//        System.out.println("加密前内容：" + body);
//        System.out.println("私钥加密后：" + encodes);
//        System.out.println("公钥加密后：" + tools.encodePublicKey(body));
//        System.out.println("公钥解密后：" + tools.decodePublicKey(encodes));

        tools.generate("D:\\LocalDfs\\public.pub", "D:\\LocalDfs\\private.key", body, body.length());
    }


}
