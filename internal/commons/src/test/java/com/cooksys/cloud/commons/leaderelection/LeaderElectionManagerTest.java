package com.cooksys.cloud.commons.leaderelection;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link LeaderElectionManager}
 *
 * @author Tim Davidson
 */
public class LeaderElectionManagerTest {
    @Test
    public void purgeInstancesTest() {
        final LeaderElectionManager manager = new LeaderElectionManager(null);

        final LocalDateTime expiredTime1 =  LocalDateTime.now().minusSeconds(15);
        final LocalDateTime expiredTime2 =  LocalDateTime.now().minusSeconds(30);
        final LocalDateTime nonExpiredTime1 =  LocalDateTime.now().minusSeconds(10);
        final LocalDateTime nonExpiredTime2 =  LocalDateTime.now().minusSeconds(14);
        final LocalDateTime nonExpiredTime3 =  LocalDateTime.now().minusSeconds(5);

        manager.clusterHeartbeatRegistry.put("exp1",expiredTime1);
        manager.clusterHeartbeatRegistry.put("exp2",expiredTime2);
        manager.clusterHeartbeatRegistry.put("non1",nonExpiredTime1);
        manager.clusterHeartbeatRegistry.put("non2",nonExpiredTime2);
        manager.clusterHeartbeatRegistry.put("non3",nonExpiredTime3);

        manager.purgeExpiredInstances();

        System.out.println(manager.clusterHeartbeatRegistry);
        Assert.assertTrue(manager.clusterHeartbeatRegistry.containsKey("non1"));
        Assert.assertTrue(manager.clusterHeartbeatRegistry.containsKey("non2"));
        Assert.assertTrue(manager.clusterHeartbeatRegistry.containsKey("non3"));
        Assert.assertFalse(manager.clusterHeartbeatRegistry.containsKey("exp1"));
        Assert.assertFalse(manager.clusterHeartbeatRegistry.containsKey("exp2"));


    }

    @Test
    public void leaderElectionTest() {
        final LeaderElectionManager manager = new LeaderElectionManager(null);

        manager.myUuid="2ae2f0f9-68f5-4a8e-b17f-fe0d0e8d4588";
        manager.clusterHeartbeatRegistry.put("2ae2f0f9-68f5-4a8e-b17f-fe0d0e8d4588",LocalDateTime.now()); // Me

        manager.clusterHeartbeatRegistry.put("0225108f-df18-4aa6-af6f-8e636a62f4ce",LocalDateTime.now()); //leader
        manager.clusterHeartbeatRegistry.put("2db92b22-388d-4446-af8a-5e0b5465a0a7",LocalDateTime.now());
        manager.clusterHeartbeatRegistry.put("3b114a88-109e-4986-8840-a3d2f2250ad8",LocalDateTime.now());

        Assert.assertFalse(manager.isLeader());

        manager.clusterHeartbeatRegistry.remove("0225108f-df18-4aa6-af6f-8e636a62f4ce"); // removing leader, now I should be at the top

        Assert.assertTrue(manager.isLeader());
    }

}