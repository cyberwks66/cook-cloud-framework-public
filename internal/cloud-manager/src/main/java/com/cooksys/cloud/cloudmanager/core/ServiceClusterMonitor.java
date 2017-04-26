package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.cloudmanager.configuration.CloudManagerProperties;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tim Davidson on 4/19/17.
 */
@EnableScheduling
public class ServiceClusterMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ServiceClusterMonitor.class);

    private CloudManagerProperties config;
    private ServiceClusterStateManager stateManager;
    private CooldownTimer cooldownTimer;
    private ServiceClusterScaler scaler;
    private LeaderElectionManager electionManager;

    public ServiceClusterMonitor(CloudManagerProperties config, ServiceClusterStateManager stateManager, CooldownTimer cooldownTimer, ServiceClusterScaler scaler, LeaderElectionManager electionManager) {
        this.config = config;
        this.stateManager = stateManager;
        this.cooldownTimer = cooldownTimer;
        this.scaler = scaler;
        this.electionManager = electionManager;
    }

    @Scheduled(fixedRate = 60_000)
    public void monitor() {
        if (electionManager.isLeader()) {
            logger.info("monitor() thread: cluster state: " + stateManager.getClusterState());
            if (stateManager.getClusterState().equals(ScaleState.NORMAL)) {
                final List<ServiceClusterNode> unusedNodes = scaler.findUnusedClusterNodes();

                if (unusedNodes == null || unusedNodes.size() < config.getClusterNodeReserveCount()) {
                    stateManager.updateServiceClusterState(ScaleState.SCALING);
                    final int currentNodeCount = scaler.getCurrentClusterNodeCount();
                    try {
                        logger.info("scaling up cluster - currentNodeCount: " + currentNodeCount);
                        scaler.addClusterNode();
                        startScaleVerifyTask(currentNodeCount + 1);
                    } catch (RuntimeException e) {
                        logger.warn(e.getMessage(), e);
                        stateManager.updateServiceClusterState(ScaleState.NORMAL);
                    }

                } else if (unusedNodes.size() > config.getClusterNodeReserveCount()) {
                    stateManager.updateServiceClusterState(ScaleState.SCALING);
                    final int currentNodeCount = scaler.getCurrentClusterNodeCount();
                    try {
                        logger.info("scaling down cluster - currentNodeCount: " + currentNodeCount);
                        scaler.stopClusterNode(unusedNodes.get(0));
                        startScaleVerifyTask(currentNodeCount - 1);
                    } catch (RuntimeException e) {
                        logger.warn(e.getMessage(), e);
                        stateManager.updateServiceClusterState(ScaleState.NORMAL);
                    }

                }
            }
        }
    }

    private void startScaleVerifyTask(int desiredCount) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final int currentClusterNodeCount = scaler.getCurrentClusterNodeCount();
                logger.info("scaleVerifyTask thread: desiredCount=" + desiredCount + " currentCount=" + currentClusterNodeCount);
                if (desiredCount == currentClusterNodeCount) {
                    cooldownTimer.startServiceClusterCooldown();
                    this.cancel();
                }
            }
        }, 10_000, 10_000);
    }


}

