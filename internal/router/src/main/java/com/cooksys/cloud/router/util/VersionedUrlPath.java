package com.cooksys.cloud.router.util;

import com.github.zafarkhaja.semver.ParseException;
import com.github.zafarkhaja.semver.Version;
import com.sun.jersey.api.uri.UriTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * URL path parser for specific version requested at the edge-proxy.  Request urls can have version appended to route
 * to that specific version in the format defined in TEMPLATE.
 *
 * Example: http://hello.cooksys.com/_1/0/2/_/hello
 *
 *
 * @author Tim Davidson
 */
public class VersionedUrlPath {
    private static final String TEMPLATE = "/_/{major}/{minor}/{patch}/_{remainder:.*}";
    public static final String MAJOR = "major";
    public static final String MINOR = "minor";
    public static final String PATCH = "patch";
    public static final String REMAINDER = "remainder";

    private Version version;
    private String pathRemainder;

    public static VersionedUrlPath valueOf(String path) throws IllegalArgumentException {
        final UriTemplate template = new UriTemplate(TEMPLATE);
        final Map<String, String> extractedVariables = new HashMap<>();

        if (!template.match(path, extractedVariables)) {
            throw new IllegalArgumentException("Path does not conform to template: " + template);
        }

        Version version = null;

        try {
            version = Version.valueOf(extractedVariables.get(MAJOR)
                    + "." + extractedVariables.get(MINOR) + "." + extractedVariables.get(PATCH));
        } catch (IllegalArgumentException | ParseException e) {
            throw new IllegalArgumentException("path does not conform to semantic version format");
        }

        return new VersionedUrlPath(version,extractedVariables.get(REMAINDER));
    }

    private VersionedUrlPath(Version version, String pathRemainder) {
        this.version = version;

        this.pathRemainder = pathRemainder;
    }

    public Version getVersion() {
        return version;
    }

    public String getPathRemainder() {
        if(pathRemainder.endsWith("/")) {
            // strip off trailing slash
            pathRemainder = pathRemainder.substring(0,pathRemainder.length()-1);
        }
        return pathRemainder;
    }

    public static void main(String[] args) {
        UriTemplate template = new UriTemplate("/docker/{remainder:*}");
        final Map<String, String> extractedVariables = new HashMap<>();
        if (!template.match("/docker/container/foo/info", extractedVariables)) {
            throw new IllegalArgumentException("Path does not conform to template: " + template);
        }
        System.out.println();

    }
}
