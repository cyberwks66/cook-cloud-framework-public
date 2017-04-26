package com.cooksys.cloud.router.filters.route.ribbon;

import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * Default implementation of RibbonFilterContext
 *
 * @author Tim Davidson
 */
public class DefaultRibbonFilterContext implements RibbonFilterContext {

    private Version version;
    private SemanticAccuracy semanticAccuracy;
    private List<Version> excludedVersions;
    private String proxyHost;

    @Override
    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public void setSemanticAccuracy(SemanticAccuracy accuracy) {
        this.semanticAccuracy = accuracy;
    }

    @Override
    public SemanticAccuracy getSemanticAccuracy() {
        return semanticAccuracy;
    }

    @Override
    public void setExcludedVersions(List<Version> excludedVersions) {
        this.excludedVersions = excludedVersions;
    }

    @Override
    public List<Version> getExcludedVersions() {
        return excludedVersions;
    }

    @Override
    public void setProxyHost(String proxyHost) {
        this.proxyHost=proxyHost;
    }

    @Override
    public String getProxyHost() {
        return proxyHost;
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
