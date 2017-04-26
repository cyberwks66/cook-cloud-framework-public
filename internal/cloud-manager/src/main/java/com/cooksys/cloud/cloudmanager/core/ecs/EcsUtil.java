package com.cooksys.cloud.cloudmanager.core.ecs;

import com.cooksys.cloud.commons.event.cloudmanager.Service;
import com.github.zafarkhaja.semver.ParseException;
import com.github.zafarkhaja.semver.Version;

/**
 * Amazon EC2 Utility class
 *
 * @author Tim Davidson
 */
public class EcsUtil {
    public static String formatServiceIdentifier(String serviceId, String version) throws RuntimeException {
        final Version semanticVersion;
        try {
            semanticVersion=Version.valueOf(version);
        } catch (IllegalArgumentException | ParseException e) {
            throw new RuntimeException(e.getMessage());
        }

        return serviceId + "_"
                + semanticVersion.getMajorVersion()
                + "_"
                + semanticVersion.getMinorVersion()
                + "_"
                + semanticVersion.getPatchVersion();
    }

    public static Service parseServiceIdentifier(String serviceIdentifier) {
        final String[] splitId = serviceIdentifier.split("_");

        return new Service(splitId[0],splitId[1] + "." + splitId[2] + "." + splitId[3]);
    }



}
