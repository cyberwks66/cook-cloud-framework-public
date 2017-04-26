package com.cooksys.airline;

import com.cooksys.cloud.sdk.CloudApplication;
import com.cooksys.cloud.sdk.CloudMicroservice;
import com.github.zafarkhaja.semver.Version;


@CloudMicroservice
public class Application {
    public static void main(String[] args) {
        String versionArg = null;

        for (String arg : args) {
            if (arg.startsWith("--version=")) {
                versionArg = arg.split("=")[1];
                break;
            }
        }

        final Version version = Version.valueOf(versionArg);

        CloudApplication.run(Application.class, version, args);
    }
}
