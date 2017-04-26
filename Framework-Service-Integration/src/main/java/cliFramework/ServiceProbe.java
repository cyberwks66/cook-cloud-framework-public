package cliFramework;

import java.io.BufferedReader;
import java.io.InputStreamReader;






public class ServiceProbe {
	
	
	
	
	String executeCommand(String command,  String exp) {

		StringBuffer output = new StringBuffer();  //old
		StringBuilder sb = new StringBuilder(); //new
		String subOutput = "";
		
		
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
				 
	//			 System.out.println(line);
				 
	//			 JSONObject json = new JSONObject(sb.toString()); //new	 
			}			


System.out.println("ServiceProbe.executeCommand");	

			
//int intIndex = output.indexOf(tag);
//int begPos = intIndex +tag.length() + 4;
//int endPos = begPos +exp.length()  ;


//subOutput = output.substring((begPos),(endPos));


// System.out.println( "Raw JSON: "  +output);		//one time only
//System.out.println(tag + ": "  +subOutput);		

			
			
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	//	return subOutput;

		
	

	   

		
	
	}	

}
