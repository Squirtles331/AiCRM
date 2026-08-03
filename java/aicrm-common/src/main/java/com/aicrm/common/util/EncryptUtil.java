package com.aicrm.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加密工具：SHA-256 摘要 + AES 对称加解密
 * <p>
 * AES 密钥由入参 key 经 SHA-256 派生（16 字节），算法 AES/ECB/PKCS5Padding。
 * 生产环境高强度场景建议切换 GCM 或 KMS 托管密钥。
 */
public final class EncryptUtil {

    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    private EncryptUtil() {
    }

    /** SHA-256 摘要（十六进制小写） */
    public static String sha256(String plain) {
        return digest("SHA-256", plain);
    }

    /** MD5 摘要（十六进制小写，仅用于非安全场景如缓存键） */
    public static String md5(String plain) {
        return digest("MD5", plain);
    }

    /** AES 加密：返回 Base64 */
    public static String aesEncrypt(String plain, String key) {
        if (plain == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, buildKey(key));
            return Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("AES 加密失败", e);
        }
    }

    /** AES 解密：入参为 Base64 */
    public static String aesDecrypt(String cipherText, String key) {
        if (cipherText == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, buildKey(key));
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES 解密失败", e);
        }
    }

    private static SecretKeySpec buildKey(String key) {
        byte[] digest = sha256Bytes(key == null ? "" : key);
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest, 0, keyBytes, 0, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static String digest(String algorithm, String plain) {
        return hex(sha256Bytes(plain));
    }

    private static byte[] sha256Bytes(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(plain.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("不支持的摘要算法", e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
