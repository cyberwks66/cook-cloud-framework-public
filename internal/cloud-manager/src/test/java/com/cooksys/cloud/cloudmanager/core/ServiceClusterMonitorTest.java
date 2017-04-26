package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.cloudmanager.configuration.CloudManagerProperties;
import com.cooksys.cloud.cloudmanager.core.ecs.Ec2Instance;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test class for {@link ServiceClusterMonitor}
 *
 * @author Tim Davidson
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceClusterMonitorTest {
    @Mock
    private CloudManagerProperties config;

    @Mock
    private ServiceClusterStateManager stateManager;

    @Mock
    private CooldownTimer cooldownTimer;

    @Mock
    private ServiceClusterScaler scaler;

    @Mock
    private LeaderElectionManager electionManager;

    @InjectMocks
    private ServiceClusterMonitor monitor;

    @Before
    public void setUp() {
        doReturn(true).when(electionManager).isLeader();
    }

    @Test
    public void scaleUpScenario1() throws Exception {
        setClusterState(ScaleState.NORMAL);
        setSpareNodesCount(1);
        setUnusedNodesCount(0);

        monitor.monitor();

        verifyAddNode();
    }

    @Test
    public void scaleUpScenario2() throws Exception {
        setClusterState(ScaleState.NORMAL);
        setSpareNodesCount(2);
        setUnusedNodesCount(0);

        monitor.monitor();

        verifyAddNode();
    }

    @Test
    public void scaleDownScenario1() throws Exception {
        setClusterState(ScaleState.NORMAL);
        setSpareNodesCount(1);
        setUnusedNodesCount(2);

        monitor.monitor();

        verifyStopNode(1);
    }

    @Test
    public void scaleDownScenario2() throws Exception {
        setClusterState(ScaleState.NORMAL);
        setSpareNodesCount(2);
        setUnusedNodesCount(4);

        monitor.monitor();

        verifyStopNode(1);
    }

    @Test
    public void doNothingScenario1() throws Exception {
        setClusterState(ScaleState.SCALING);
        setSpareNodesCount(2);
        setUnusedNodesCount(1);

        monitor.monitor();

        verifyDoNothing();
    }

    @Test
    public void doNothingScenario2() throws Exception {
        setClusterState(ScaleState.NORMAL);
        setSpareNodesCount(1);
        setUnusedNodesCount(1);

        monitor.monitor();

        verifyDoNothing();
    }

    @Test
    public void doNothingScenario3() throws Exception {
        setClusterState(ScaleState.NORMAL);
        setSpareNodesCount(2);
        setUnusedNodesCount(2);

        monitor.monitor();

        verifyDoNothing();
    }

    @Test
    public void doNothingScenario4() throws Exception {
        setClusterState(ScaleState.COOLDOWN);
        setSpareNodesCount(2);
        setUnusedNodesCount(1);

        monitor.monitor();

        verifyDoNothing();
    }


    private void setClusterState(ScaleState state) {
        doReturn(state).when(stateManager).getClusterState();
    }

    private void setSpareNodesCount(int count) {
        doReturn(count).when(config).getClusterNodeReserveCount();
    }

    private void setUnusedNodesCount(int count) {
        List<ServiceClusterNode> unusedNodes = new ArrayList<>();

        for(int i=0;i<count;i++) {
            unusedNodes.add(new Ec2Instance("instance" + i));
        }
        doReturn(unusedNodes).when(scaler).findUnusedClusterNodes();
    }

    private void verifyAddNode() {
        verify(scaler, times(1)).addClusterNode();
        verify(scaler,times(0)).stopClusterNode(any());
    }

    private void verifyStopNode(int count) {
        verify(scaler, times(0)).addClusterNode();
        verify(scaler,times(count)).stopClusterNode(any());
    }

    private void verifyDoNothing() {
        verify(scaler, times(0)).addClusterNode();
        verify(scaler,times(0)).stopClusterNode(any());
    }
}