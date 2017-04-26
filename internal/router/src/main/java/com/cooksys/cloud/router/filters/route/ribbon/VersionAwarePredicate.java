package com.cooksys.cloud.router.filters.route.ribbon;

import com.github.zafarkhaja.semver.Version;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.List;
import java.util.Map;

/**
 * Predicate that enforces semantic versioning.  Gives fine grained control at Major, minor, and patch levels - passing
 * in semantic version acuracy will tell the predicate how finely to filter the server list.
 *
 * MAJOR Accuracy:
 *  - Only filter same Major version
 *  - Request minor version must be less than or equal to registered instance version
 *  - If these two rules pass, filter any patch version
 *
 * MINOR Accuracy:
 *  - Only filter same Major/Minor version
 *  - Filter any patch version
 *
 * PATCH Accuracy:
 *  - Filter exact Major/Minor/Patch version
 *
 * Note: if version does not exist in metadata, ignores the service entry.  If version is not supplied to the
 * Ribbon filter context, then it does not filter anything.
 *
 * @author Tim Davidson
 */
public class VersionAwarePredicate extends DiscoveryEnabledPredicate {

    public static final String VERSION = "version";

    @Override
    protected boolean apply(DiscoveryEnabledServer server) {
        final RibbonFilterContext context = RibbonFilterContextFactory.getCurrentContext();
        final Map<String, String> metadata = server.getInstanceInfo().getMetadata();

        // in node-proxy mode, context.proxyHost should be set to my hostname/IP - if the service instance does not
        // reside on my host, ignore it
        if(context.getProxyHost() != null) {
            String proxyHostMetadata = metadata.get("proxyHost");
            if(proxyHostMetadata==null || proxyHostMetadata.isEmpty() || !context.getProxyHost().equalsIgnoreCase(proxyHostMetadata)) {
                return false;
            }
        }

        // If version is not set, just ignore this rule
        if(context.getVersion() == null) {
            return true;
        }

        final String metadataVersion = metadata.get(VERSION);
        if(metadataVersion==null || metadataVersion.isEmpty()) {
            // if version is not supplied in metadata, we will never forward a request to it
            return false;
        }

        final Version instanceVersion = Version.valueOf(metadataVersion);
        final Version routeVersion = context.getVersion();

        final List<Version> excludedVersions = context.getExcludedVersions();
        if(excludedVersions!=null && !excludedVersions.isEmpty()) {
            if(excludedVersions.contains(instanceVersion)) {
                return false;
            }
        }

        switch(context.getSemanticAccuracy()) {
            case MAJOR:
                if(instanceVersion.getMajorVersion() != routeVersion.getMajorVersion()) {
                    return false;
                }
                if(routeVersion.getMinorVersion() > instanceVersion.getMinorVersion()) {
                    return false;
                }
                break;
            case MINOR:
                if(instanceVersion.getMajorVersion() != routeVersion.getMajorVersion()) {
                    return false;
                }
                if(instanceVersion.getMinorVersion() != routeVersion.getMinorVersion()) {
                    return false;
                }
                break;
            default: //PATCH
                if(!instanceVersion.equals(routeVersion)) {
                    return false;
                }
        }
        return true;
    }
}
