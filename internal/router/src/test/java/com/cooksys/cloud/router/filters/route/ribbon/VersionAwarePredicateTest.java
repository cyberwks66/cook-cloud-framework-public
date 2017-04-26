package com.cooksys.cloud.router.filters.route.ribbon;

import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.github.zafarkhaja.semver.Version;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Tests load balancer logic that enforces semantic versioning
 */
public class VersionAwarePredicateTest {

    private DiscoveryEnabledServer server;
    private InstanceInfo instanceInfo;

    @Before
    public void setUp() {
        server = mock(DiscoveryEnabledServer.class);
        instanceInfo = mock(InstanceInfo.class);
        doReturn(instanceInfo).when(server).getInstanceInfo();
    }

    private void resetMocks() {
        reset(instanceInfo);
    }

    @Test
    public void testVersionNotSet() throws Exception {
        doReturn(new HashMap<>()).when(instanceInfo).getMetadata();
        RibbonFilterContext context = RibbonFilterContextFactory.getCurrentContext();
        context.setVersion(null);

        VersionAwarePredicate predicate = new VersionAwarePredicate();

        Assert.assertTrue(predicate.apply(server));
    }

    @Test
    public void testVersionMetadataMissing() throws Exception {
        doReturn(new HashMap<>()).when(instanceInfo).getMetadata();
        RibbonFilterContext context = RibbonFilterContextFactory.getCurrentContext();
        context.setVersion(Version.valueOf("1.2.3"));

        VersionAwarePredicate predicate = new VersionAwarePredicate();

        Assert.assertFalse(predicate.apply(server));

    }

    @Test
    public void testMajorAccuracy() throws Exception {
        Map<String, Boolean> instanceVersionShouldRouteMap = new HashMap<>();
        instanceVersionShouldRouteMap.put("1.2.1", true);
        instanceVersionShouldRouteMap.put("1.2.3", true);
        instanceVersionShouldRouteMap.put("1.3.0", true);
        instanceVersionShouldRouteMap.put("1.1.9", false);
        instanceVersionShouldRouteMap.put("2.1.0", false);

        evaluate("1.2.1", SemanticAccuracy.MAJOR, instanceVersionShouldRouteMap);
        resetMocks();
    }

    @Test
    public void testMinorAccuracy() throws Exception {
        Map<String, Boolean> instanceVersionShouldRouteMap = new HashMap<>();
        instanceVersionShouldRouteMap.put("1.2.1", true);
        instanceVersionShouldRouteMap.put("1.2.3", true);
        instanceVersionShouldRouteMap.put("1.3.0", false);
        instanceVersionShouldRouteMap.put("1.1.9", false);
        instanceVersionShouldRouteMap.put("2.1.0", false);

        evaluate("1.2.1", SemanticAccuracy.MINOR, instanceVersionShouldRouteMap);
        resetMocks();
    }

    @Test
    public void testPatchAccuracy() throws Exception {
        Map<String, Boolean> instanceVersionShouldRouteMap = new HashMap<>();
        instanceVersionShouldRouteMap.put("1.2.1", true);
        instanceVersionShouldRouteMap.put("1.2.3", false);
        instanceVersionShouldRouteMap.put("1.3.0", false);
        instanceVersionShouldRouteMap.put("1.1.9", false);
        instanceVersionShouldRouteMap.put("2.1.0", false);

        evaluate("1.2.1", SemanticAccuracy.PATCH, instanceVersionShouldRouteMap);
        resetMocks();
    }

    private void evaluate(String routeVersion, SemanticAccuracy accuracy, Map<String, Boolean> instanceVersionToShouldRouteMap) {
        HashMap<String, String> metadata = new HashMap<>();
        RibbonFilterContext context = RibbonFilterContextFactory.getCurrentContext();
        context.setVersion(Version.valueOf(routeVersion));
        context.setSemanticAccuracy(accuracy);
        VersionAwarePredicate predicate = new VersionAwarePredicate();

        for (Map.Entry<String, Boolean> entry : instanceVersionToShouldRouteMap.entrySet()) {
            metadata.put("version", entry.getKey());
            doReturn(metadata).when(instanceInfo).getMetadata();
            Assert.assertTrue(predicate.apply(server) == entry.getValue());
        }
    }
}