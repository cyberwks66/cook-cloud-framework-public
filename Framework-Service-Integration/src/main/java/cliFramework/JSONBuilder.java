package cliFramework;

import java.io.BufferedReader;
import java.io.InputStreamReader;






public class JSONBuilder {
	
	String executeCommand(String command) {

		StringBuffer output = new StringBuffer();  //old
		StringBuilder sb = new StringBuilder(); //new
		
		
		Process p;
		try {
			
			
			
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");  //old
	//			 sb.append(line);  //new
				 
				 System.out.println(line);
				 
	//			 JSONObject json = new JSONObject(sb.toString()); //new	 
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
		
		
		
	

	   

		
	
	}	

}
