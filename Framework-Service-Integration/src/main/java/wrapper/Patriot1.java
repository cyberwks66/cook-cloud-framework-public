package wrapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

//import org.apache.http.impl.client.DefaultHttpClient;
 

//@SuppressWarnings("deprecation")
public class Patriot1 {

	private final String USER_AGENT = "Mozilla/5.0";
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	boolean contentChanged = false;
	static boolean alertSent = false;
	static int counter = 0;
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("main method firing again=================>");
		bootstrap();
	
	}

	private static void bootstrap() throws Exception{
		Patriot1 http = new Patriot1();

		System.out.println("PINGing Cook Systems Website- Send Http GET request :"  + counter);
		counter++;
		TimeUnit.SECONDS.sleep(5);
		http.sendGet();
		TimeUnit.SECONDS.sleep(30);
		bootstrap();
	}
	
	
	
	// HTTP GET request
	String sendGet() throws Exception {

	
		String url = "http://www.cooksys.com";  //INternal Address Only Change
	//	String subOutput = "";
		String tag = "<title>";	//setTimeout(writeOlark
		String tag2 = "setTimeout(writeOlark";
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

			System.out.println("INDEX: "  +intIndex  + " Counter " + counter);
		//	System.out.println(subOutput);
			
			if(intIndex!=108908){
				contentChanged = true;
				System.out.println("\n\n ===> Change detected..alertSent= " +alertSent);
			}
			if((intIndex!=108908)&&(alertSent==false)){
				generateAndSendEmail();
				alertSent = true;
				
				System.out.println("\n\n ===> Email sent successfully. Check your email..");
				System.out.println("\n\n ===> alertSent toggled to true....alertSent= " +alertSent);
			}
			System.out.println("\n\n ===> All is well. contentChanged= " +contentChanged + " ..alertSent= " +alertSent);
	//	}
		return ekho; //Change to subOutput for use with test class RSandlin
	}
	
	
	

	public static void generateAndSendEmail() throws AddressException, MessagingException {
		 
		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");
 
		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("rmcclure@cooksys.com"));
		generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("ahiggins@cooksys.com"));
		generateMailMessage.setSubject("Website Change Detected");
		String emailBody = "Normal page markup appears to have changed " + "<br><br> Regards, <br>Robin";
		generateMailMessage.setContent(emailBody, "text/html");
		System.out.println("Mail Session has been created successfully..");
 
		// Step3
			System.out.println("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtp");
	 
			// Enter your correct gmail UserID and Password
			// if you have 2FA enabled then provide App Specific Password
			transport.connect("smtp.gmail.com", "FastrackD7", "1234!@#$");
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
		}

}
