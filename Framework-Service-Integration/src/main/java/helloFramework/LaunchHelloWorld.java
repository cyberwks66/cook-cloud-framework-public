package helloFramework;









public class LaunchHelloWorld {
	
	
	
	
	public void executeCommand(String command, String address, String port) {

		
		String commandLongVersion = "" + command + ";" +address + ":"+ port +"";
		command = "docker service create --name hello-world --network swarm --replicas 1 cooksysbeta/hello-world:0.1.0-snapshoty ";
		
		Process p;
		try {
			
			
			
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			               		


System.out.println("LaunchHelloWorld.executeCommand");	




			
			
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	//	return output.toString();
		
		
	

	   

		
	
	}	

}
