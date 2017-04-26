package com.cooksys.cloud.commons.util;

import org.springframework.boot.ExitCodeGenerator;

/**
 * Custom exit code generator that returns exit code 1 (Error)
 *
 * @author Tim Davidson
 */
public class ErrorExitCodeGenerator implements ExitCodeGenerator {

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.ExitCodeGenerator#getExitCode()
     */
    @Override
    public int getExitCode() {
        return 1;
    }

}