package cliFramework;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Framework {
	
	{

		
		
		
		Framework obj = new Framework();

//		String domainName = "hub.docker.com";

		
	//	String command = "docker node inspect moby --format \"{{ Status.State}}\"";
		String command = "docker node inspect moby ";
		
///////////////////////////////////////////////////	
		System.out.println("Issuing command");	
		String output = obj.executeCommand(command);
	//	System.out.println(output);	
	//	System.out.println("Full string ahead");	
		int intIndex = output.indexOf("Reachability");
   //	System.out.println("Index start " + intIndex);	

		String subOutput = output.substring(( intIndex+16),(intIndex+25));

		System.out.println("ManagerStatus.Reachability: " +subOutput);		
/////////////////////////////////////////////////////////
		
//		List<String> list = new ArrayList<String>();
//		JSONArray array = obj.getJSONArray("docker response");
//		for(int i = 0 ; i < array.length() ; i++){
//		    list.add(array.getJSONObject(i).getString("Type"));
//		}
		
			

	}
	String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
	private void Framework() {
		// TODO Auto-generated method stub
		
	}
public String Framework(String input){
	
	Framework obj = new Framework();

	String command = "docker node inspect moby --format \"{{ Status.State}}\"";
		 
		
///////////////////////////////////////////////////	
		System.out.println("Issuing command");	
		String output = obj.executeCommand(command);
	//	System.out.println(output);	
	//	System.out.println("Full string ahead");	
		int intIndex = output.indexOf("Reachability");
   //	System.out.println("Index start " + intIndex);	

		String subOutput = output.substring(( intIndex+16),(intIndex+25));

		System.out.println("ManagerStatus.Reachability: " +subOutput);	
	
return output;	
}

}
