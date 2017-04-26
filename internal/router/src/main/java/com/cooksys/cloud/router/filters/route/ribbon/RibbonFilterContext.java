package com.cooksys.cloud.router.filters.route.ribbon;

import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.github.zafarkhaja.semver.Version;

import java.util.List;

/**
 * Interface that defines what fields should be contained in the Ribbon Filter Context.  This context can be used
 * for determining server list filter criteria for ribbon's load-balancing rule engine.  This context is request scoped
 * and held in RibbonFilterContextFactory
 *
 * @author Tim Davidson
 */
public interface RibbonFilterContext {

    void setVersion(Version version);

    Version getVersion();

    void setSemanticAccuracy(SemanticAccuracy accuracy);

    SemanticAccuracy getSemanticAccuracy();

    void setExcludedVersions(List<Version> excludedVersions);

    List<Version> getExcludedVersions();

    void setProxyHost(String proxyHost);

    String getProxyHost();

}
