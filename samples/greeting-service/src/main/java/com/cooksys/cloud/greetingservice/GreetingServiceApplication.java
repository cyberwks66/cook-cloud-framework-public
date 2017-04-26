package com.cooksys.cloud.greetingservice;

import com.cooksys.cloud.sdk.CloudMicroservice;
import com.cooksys.cloud.sdk.CloudApplication;
import com.github.zafarkhaja.semver.Version;

@CloudMicroservice
public class GreetingServiceApplication {


    public static void main(String[] args) {
        String versionArg = null;

        for (String arg : args) {
            if (arg.startsWith("--version=")) {
                versionArg = arg.split("=")[1];
                break;
            }
        }

        final Version version = Version.valueOf(versionArg);
        CloudApplication.run(GreetingServiceApplication.class, version, args);
    }
}
