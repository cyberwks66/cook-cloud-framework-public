package com.cooksys.cloud.sdk.util;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Misc file utilities
 *
 * @author Tim Davidson
 */
public interface FileUtil {

    /**
     * Reads a text file into a String
     *
     * @param filename Name of the file located in the classpath to read
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    String readFileFromClasspath(String filename) throws IOException, URISyntaxException;
}
