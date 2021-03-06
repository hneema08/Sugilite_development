package edu.cmu.hcii.sugilite.sharing.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HashedString implements Serializable {

    private final byte[] hash;
    private boolean isServerSalted;

    public HashedString(String string) {
        this.hash = hash(string);
        this.isServerSalted = false;
    }

    public HashedString(byte[] hash, boolean isServerSalted) {
        this.hash = hash;
        this.isServerSalted = isServerSalted;
    }

    public static HashedString fromEncodedString(String encodedString, boolean isServerSalted) {
        return new HashedString(bytesFromHex(encodedString), isServerSalted);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    @Override
    public boolean equals(Object obj) {
        byte[] hash2;

        if (obj instanceof HashedString) {
            hash2 = ((HashedString)obj).hash;
        } else if (obj instanceof String) {
            hash2 = hash((String)obj);
        } else {
            return false;
        }

        //local hash currently uses SHA-256, but the remote salted hash uses SHA-512
        if (hash.length != hash2.length) {
            return false;
        }
        for (int i = 0; i < hash.length; i++) {
            if (hash[i] != hash2[i]) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        return bytesToHex(hash);
    }


    private static byte[] hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] bytesFromHex(String hexString) {
        char[] hexChars = hexString.toCharArray();
        byte[] result = new byte[hexChars.length / 2];
        for (int j = 0; j < hexChars.length; j += 2) {
            result[j / 2] = (byte) (Arrays.binarySearch(HEX_ARRAY, hexChars[j]) * 16 + Arrays.binarySearch(HEX_ARRAY, hexChars[j + 1]));
        }
        return result;
    }

    public boolean isServerSalted() {
        return isServerSalted;
    }

    public static void main(String[] args) {
        System.out.println("Hashing \"Starbucks\":");
        HashedString hashed = new HashedString("Starbucks");
        System.out.println(hashed);
        System.out.println("Reinterpreting output:");
        HashedString interpreted = fromEncodedString(hashed.toString(), false);
        System.out.println(interpreted);
        System.out.println("Equals \"Starbucks\"? (want true)");
        System.out.println(interpreted.equals("Starbucks"));
        System.out.println(interpreted.equals(new HashedString("Starbucks")));
        System.out.println("Equals \"Caribou\"? (want false)");
        System.out.println(interpreted.equals("Caribou"));
        System.out.println(interpreted.equals(new HashedString("Caribou")));
    }
}
