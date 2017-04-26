package com.cooksys.cloud.router.core;

import com.github.zafarkhaja.semver.Version;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by timd on 3/9/17.
 */
public class TrafficRatioManagerImplTest {

    private TrafficRatioManagerImpl manager;

    @Test
    public void testTrafficRatioBalancing() throws Exception {
        manager = new TrafficRatioManagerImpl();

        RouteVersionDetails details1 = new RouteVersionDetails(Version.valueOf("1.1.1"), SemanticAccuracy.PATCH,null);
        RouteVersionDetails details2 = new RouteVersionDetails(Version.valueOf("2.2.2"), SemanticAccuracy.PATCH,null);

        manager.putRatio("service", details1, 3);
        manager.putRatio("service", details2, 2);

        String[] sequence = {
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "2.2.2",
                "2.2.2",
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "2.2.2",
                "2.2.2"
        };

        assertSequence(sequence);
    }

    @Test
    public void testRemoveRatio() {

        manager = new TrafficRatioManagerImpl();

        RouteVersionDetails details1 = new RouteVersionDetails(Version.valueOf("1.1.1"), SemanticAccuracy.PATCH,null);
        RouteVersionDetails details2 = new RouteVersionDetails(Version.valueOf("2.2.2"), SemanticAccuracy.PATCH,null);

        manager.putRatio("service", details1, 3);
        manager.putRatio("service", details2, 2);

        // Remove a version from the manager
        manager.deleteRatio("service", new RouteVersionDetails(Version.valueOf("1.1.1"), SemanticAccuracy.PATCH,null));

        String[] sequence = {
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2",
                "2.2.2"
        };

        assertSequence(sequence);
    }

    @Test
    public void testModifyRatios() {
        manager = new TrafficRatioManagerImpl();

        RouteVersionDetails details1 = new RouteVersionDetails(Version.valueOf("1.1.1"), SemanticAccuracy.PATCH,null);
        RouteVersionDetails details2 = new RouteVersionDetails(Version.valueOf("2.2.2"), SemanticAccuracy.PATCH,null);

        manager.putRatio("service", details1, 4);
        manager.putRatio("service", details2, 1);

        String[] sequence1 = {
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "2.2.2",
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "2.2.2"
        };

        assertSequence(sequence1);

        manager.putRatio("service", new RouteVersionDetails(Version.valueOf("1.1.1"), SemanticAccuracy.PATCH,null), 1);

        String[] sequence2 = {
                "2.2.2",
                "1.1.1",
                "2.2.2",
                "1.1.1",
                "2.2.2",
                "1.1.1",
                "2.2.2",
                "1.1.1",
                "2.2.2",
                "1.1.1"
        };
        assertSequence(sequence2);
    }

    @Test
    public void testThreeVersions() {
        manager = new TrafficRatioManagerImpl();

        RouteVersionDetails details1 = new RouteVersionDetails(Version.valueOf("1.1.1"), SemanticAccuracy.PATCH,null);
        RouteVersionDetails details2 = new RouteVersionDetails(Version.valueOf("2.2.2"), SemanticAccuracy.PATCH,null);
        RouteVersionDetails details3 = new RouteVersionDetails(Version.valueOf("3.3.3"), SemanticAccuracy.PATCH,null);

        manager.putRatio("service", details1, 3);
        manager.putRatio("service", details2, 2);
        manager.putRatio("service", details3, 1);

        String[] sequence1 = {
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "2.2.2",
                "2.2.2",
                "3.3.3",
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "2.2.2",
                "2.2.2",
                "3.3.3",
                "1.1.1"
        };

        assertSequence(sequence1);

        manager.deleteRatio("service", new RouteVersionDetails(Version.valueOf("2.2.2"), SemanticAccuracy.PATCH,null));

        String[] sequence2 = {
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "3.3.3",
                "1.1.1",
                "1.1.1",
                "1.1.1",
                "3.3.3",
                "1.1.1"
        };

        assertSequence(sequence2);
    }

    private void assertSequence(String[] sequence) {
        for (String version : sequence) {
            assertTrue(manager.getNextRouteVersionDetails("service").getVersion().equals(Version.valueOf(version)));
        }
    }
}