package helloFramework;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//import org.apache.http.impl.client.DefaultHttpClient;



//@SuppressWarnings("deprecation")
public class HTTPHelloClient {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		HTTPHelloClient http = new HTTPHelloClient();

		System.out.println("Hello Routing - Send Http GET request");
		http.sendGet();

	
	}

	// HTTP GET request
	String sendGet() throws Exception {

	
		String url = "http://localhost:8081/hello";
		

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

		System.out.println("" + result.toString());
		String ekho = result.toString();
		return ekho;
	}

	

}
