package com.cooksys.cloud.commons.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static utility class to read/write files to memory, and checksum utility.
 *
 * @author Tim Davidson
 */
public class FileUtils {

    /**
     * Reads a file from disk into memory
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] fileToBytes(String filePath) throws IOException {
        final Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    /**
     * Writes a file to disk from memory
     *
     * @param bytes
     * @param filePath
     * @throws IOException
     */
    public static void bytesToFile(byte[] bytes, String filePath) throws IOException {
        final FileOutputStream stream = new FileOutputStream(filePath);
        try {
            stream.write(bytes);
        } finally {
            stream.close();
        }
    }

    /**
     * Generates a unique checksum of a byte array.  Used in file transfer operations to verify the file is sent/recvd successfuly.
     *
     * @param bytes
     * @return
     */
    public static String checksum(byte[] bytes) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("SHA1");
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (messageDigest == null) {
            return null;
        }

        messageDigest.update(bytes);
        final byte[] mdbytes = messageDigest.digest();

        // convert the byte to hex format
        final StringBuffer sb = new StringBuffer("");

        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
