package helloFramework;

import java.util.concurrent.TimeUnit;



public class LaunchFramework {
	
	
	
	
	public void executeCommand() {

		String swarmInit = "docker swarm init";
		String networkInit = "docker network create --driver overlay swarm";
		String rabbitmqInit = "docker service create --name rabbitmq --network swarm --replicas 1 rabbitmq";
		String elasticsearchInit = "docker service create --name elasticsearch --network swarm --replicas 1 elasticsearch:2.4";
		String configserverInit = "docker service create --name configserver --network swarm --replicas 1 cooksysbeta/config-server:0.1.0";
		String discoveryInit = "docker service create --name discovery --network swarm --publish 8761:8761 --replicas 1 cooksysbeta/discovery:0.1.0";
		String routerInit = "docker service create --name router --network swarm --publish 8764:8764 --replicas 1 cooksysbeta/router:0.1.0";
	//	String kibanaInit = "docker service create --name kibana --network swarm --publish 80:5601 --replicas 1 kibana:4.5";
		String kibanaInit = "docker service create --name kibana --network swarm --publish 5601:5601 --replicas 1 kibana:4.5";
	 	String servicemanagerInit = "docker service create --name service-manager --network swarm --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock --publish 8080:8080 --replicas 1 cooksysbeta/service-manager:0.1.0-snapshot";
		String redisInit = "docker service create --name redis --network swarm --replicas 1 redis";
		String nodemanagerInit = "docker service create --name node-manager --network swarm --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock --mode global cooksysbeta/node-manager:0.1.0-snapshot";
		String helloworldInit = "docker service create --name hello-world --network swarm   --publish 8081:8080   --replicas 1 cooksysbeta/hello-world:0.1.0-snapshot";

		
		
		
		
		
		
		
		Process p;
		try {
			
			String commando = "";
			commando = swarmInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = networkInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = rabbitmqInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = elasticsearchInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = configserverInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = discoveryInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = routerInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = kibanaInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = servicemanagerInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = redisInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = nodemanagerInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			TimeUnit.SECONDS.sleep(10);
			commando = helloworldInit;
			p = Runtime.getRuntime().exec(commando);
			System.out.println("LaunchFramework.executeCommand " + commando + " ");	
			
			
			
	/*		p = Runtime.getRuntime().exec(swarmInit);
	 * 
     //		p.waitFor();
			p = Runtime.getRuntime().exec(networkInit);
			p = Runtime.getRuntime().exec(service1Init);
			p = Runtime.getRuntime().exec(service2Init);
			
	//		TimeUnit.SECONDS.sleep(60);
			p = Runtime.getRuntime().exec(service3Init);

			p = Runtime.getRuntime().exec(service4Init);
			p = Runtime.getRuntime().exec(service5Init);
			p = Runtime.getRuntime().exec(service6Init);
	        p = Runtime.getRuntime().exec(service7Init);
	  		p = Runtime.getRuntime().exec(service8Init);
			p = Runtime.getRuntime().exec(service9Init);
			
		*/               		





			
			
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	//	return output.toString();
		
		
	

	   

		
	
	}	

}
