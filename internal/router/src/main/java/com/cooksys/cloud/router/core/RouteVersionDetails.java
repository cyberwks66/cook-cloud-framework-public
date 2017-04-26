package com.cooksys.cloud.router.core;

import com.github.zafarkhaja.semver.Version;
import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * Model class for route version info
 *
 * @author Tim Davidson
 */
public class RouteVersionDetails {
    private Version version;
    private SemanticAccuracy semanticAccuracy;
    private List<Version> excludedVersions;


    public RouteVersionDetails() {
    }

    public RouteVersionDetails(Version version, SemanticAccuracy semanticAccuracy, List<Version> excludedVersions) {
        this.version = version;
        this.semanticAccuracy = semanticAccuracy;
        this.excludedVersions = excludedVersions;
    }

    public List<Version> getExcludedVersions() {
        return excludedVersions;
    }

    public RouteVersionDetails setExcludedVersions(List<Version> excludedVersions) {
        this.excludedVersions = excludedVersions;
        return this;
    }

    public Version getVersion() {
        return version;
    }

    public RouteVersionDetails setVersion(Version version) {
        this.version = version;
        return this;
    }

    public SemanticAccuracy getSemanticAccuracy() {
        return semanticAccuracy;
    }

    public RouteVersionDetails setSemanticAccuracy(SemanticAccuracy semanticAccuracy) {
        this.semanticAccuracy = semanticAccuracy;
        return this;
    }

    /**
     * Do not use IDE default equals() and Hashcode - only version field should be used for equality
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteVersionDetails that = (RouteVersionDetails) o;

        return version.equals(that.version);

    }

    @Override
    public int hashCode() {
        return version.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("version", version)
                .add("semanticAccuracy", semanticAccuracy)
                .add("excludedVersions", excludedVersions)
                .toString();
    }
}
