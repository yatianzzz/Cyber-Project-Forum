package com.example.system.common;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {
    private static final int KEY_SIZE = 1024;
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";

    private static KeyPair keyPair;

    private static Map<String, String> rsaMap;

    private static org.bouncycastle.jce.provider.BouncyCastleProvider bouncyCastleProvider = null;

    public static synchronized org.bouncycastle.jce.provider.BouncyCastleProvider getInstance() {
        if (bouncyCastleProvider == null) {
            bouncyCastleProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        }
        return bouncyCastleProvider;
    }

    // generate and store RSA
    static {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", getInstance());
            SecureRandom random = new SecureRandom();
            generator.initialize(KEY_SIZE, random);
            keyPair = generator.generateKeyPair();
            storeRSA();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // store RSA into cache
    private static void storeRSA() {
        rsaMap = new HashMap<>();
        PublicKey publicKey = keyPair.getPublic();
        String publicKeyStr = new String(Base64.encodeBase64(publicKey.getEncoded()));
        rsaMap.put(PUBLIC_KEY, publicKeyStr);

        PrivateKey privateKey = keyPair.getPrivate();
        String privateKeyStr = new String(Base64.encodeBase64(privateKey.getEncoded()));
        rsaMap.put(PRIVATE_KEY, privateKeyStr);
    }

    // decrypt private key
    public static String decryptWithPrivate(String encryptText) throws Exception {
        if (StringUtils.isBlank(encryptText)) {
            return null;
        }
        byte[] en_byte = Base64.decodeBase64(encryptText.getBytes());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", getInstance());
        PrivateKey privateKey = keyPair.getPrivate();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] res = cipher.doFinal(en_byte);
        return new String(res);
    }

    /**
     * java端 使用公钥加密(此方法暂时用不到)
     *
     * @param plaintext 明文内容
     * @return byte[]
     * @throws UnsupportedEncodingException e
     */
    /*
    public static byte[] encrypt(String plaintext) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(plaintext, "utf-8");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        //获取公钥指数
        BigInteger e = rsaPublicKey.getPublicExponent();
        //获取公钥系数
        BigInteger n = rsaPublicKey.getModulus();
        //获取明文字节数组
        BigInteger m = new BigInteger(encode.getBytes());
        //进行明文加密
        BigInteger res = m.modPow(e, n);
        return res.toByteArray();

    }

     */

    /**
     * java端 使用私钥解密(此方法暂时用不到)
     *
     * @param cipherText 加密后的字节数组
     * @return 解密后的数据
     * @throws UnsupportedEncodingException e
     */
    /*
    public static String decrypt(byte[] cipherText) throws UnsupportedEncodingException {
        RSAPrivateKey prk = (RSAPrivateKey) keyPair.getPrivate();
        BigInteger d = prk.getPrivateExponent();
        BigInteger n = prk.getModulus();
        BigInteger c = new BigInteger(cipherText);
        BigInteger m = c.modPow(d, n);
        byte[] mt = m.toByteArray();
        String en = new String(mt);
        return URLDecoder.decode(en, "UTF-8");
    }
    */

    // get public key
    public static String getPublicKey() {
        return rsaMap.get(PUBLIC_KEY);
    }

    // get private key
    public static String getPrivateKey() {
        return rsaMap.get(PRIVATE_KEY);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(RSAUtils.getPrivateKey());
        System.out.println(RSAUtils.getPublicKey());
        //byte[] usernames = RSAUtils.encrypt("username");
        //System.out.println(RSAUtils.decrypt(usernames));
    }
}