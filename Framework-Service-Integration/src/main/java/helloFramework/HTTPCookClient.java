package helloFramework;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//import org.apache.http.impl.client.DefaultHttpClient;



//@SuppressWarnings("deprecation")
public class HTTPCookClient {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		HTTPCookClient http = new HTTPCookClient();

		System.out.println("Send Http GET request");
		http.sendGet();

	
	}

	// HTTP GET request
	String sendGet() throws Exception {

	
		String url = "http://www.cooksys.com";  //INternal Address Only Change
	//	String subOutput = "";
		String tag = "<title>";	//setTimeout(writeOlark)
		String tag2 = "setTimeout(writeOlark)";
	//	int intIndex = 0;
		String ekho = "test";

	//	@SuppressWarnings({ "resource", "deprecation" })
	//	HttpClient client = new DefaultHttpClient();
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " +
                       response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		
		}
		System.out.println("Dats all folks  " + result.toString());
		ekho = result.toString();
		
		
		
		
		
		int intIndex = result.indexOf(tag2);
	//	int begPos = intIndex +tag.length() + 4;
		int begPos = intIndex + 4;
		
		
	//	int endPos = begPos +exp.length()  ;
		int endPos = begPos + 5  ;

	String	subOutput = result.substring((begPos),(endPos));


	// System.out.println( "Raw JSON: "  +output);		//one time only
	//	System.out.println(tag + ": "  +subOutput);		

			System.out.println(intIndex);
			System.out.println(subOutput);
		
		
	//	}
		return ekho; //subOutput for use with test class RSandlin
	}

	

}
