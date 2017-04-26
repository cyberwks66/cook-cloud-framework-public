package helloFramework;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

// package com.cooksys.cloud.internal.schedule.service;
//import com.cooksys.cloud.internal.schedule.event.CreateNewServiceEvent;
//import com.cooksys.cloud.internal.schedule.event.DockerServiceOptions;
//import com.cooksys.cloud.internal.schedule.redis.ScheduleMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@Service
public class RabbitEventer {
    private static final Logger log = (Logger) LoggerFactory.getLogger(RabbitEventer.class);

 //   private final Map<String, ScheduledFuture> futureMap = new HashMap<>();
 //   private final RedisMap<String, ScheduleMap> futureRedisMap;

    private ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);

    @Autowired
 //Entry point   private RabbitTemplate rabbitTemplate;

//   @Autowired
//   public RabbitEventer(RedisMap<String, ScheduleMap> futureRedisMap) {
//        this.futureRedisMap = futureRedisMap;

//       this.futureRedisMap.entrySet().iterator().forEachRemaining(entry -> log.info(entry.getKey() + ":" + entry.getValue()));
//    }

    public String scheduleDeploy(Date startDateTime, String name, String incomingPort, String outgoingPort, String imageName, String replicas) {
        long startTime = startDateTime.toInstant().getEpochSecond() - Instant.now().getEpochSecond();

        log.info("startTime: " + startTime);

  // 
     
     
     
     
     
     
     
     
 

        return name + " event scheduled";
    }

//   public List<Map.Entry<String, ScheduledFuture>> getScheduledDeployments() {
//      futureMap.entrySet().iterator().forEachRemaining(entry -> log.info(entry.getKey() + ":" + entry.getValue()));

//        return futureMap.entrySet().stream().collect(Collectors.toList());
//    }

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}

	public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
	}

   

   
}