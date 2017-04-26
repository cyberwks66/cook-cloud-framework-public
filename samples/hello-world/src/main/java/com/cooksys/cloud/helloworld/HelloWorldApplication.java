package com.cooksys.cloud.helloworld;

import com.cooksys.cloud.sdk.CloudApplication;
import com.cooksys.cloud.sdk.CloudMicroservice;
import com.github.zafarkhaja.semver.Version;

/**
 * Central class to launch application with spring boot embedded server.
 * Can be used as a configuration class.
 *
 * @author Steven Bradley
 */
@CloudMicroservice
public class HelloWorldApplication {
    public static void main(String[] args) {
        String versionArg = null;

        for (String arg : args) {
            if (arg.startsWith("--version=")) {
                versionArg = arg.split("=")[1];
                break;
            }
        }

        final Version version = Version.valueOf(versionArg);
        CloudApplication.run(HelloWorldApplication.class, version, args);
    }
}