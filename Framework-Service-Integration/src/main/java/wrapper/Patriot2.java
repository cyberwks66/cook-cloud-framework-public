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
 

/*Developer Note: SMS/MMS  Sandlin2016*/

/* SMS
 * ##########@txt.att.net
 * ##########@vtext.com
 * ##########@tmomail.net
 * ##########@messaging.sprintpcs.com
 * ##########@message.alltell.com
 * ##########@myboostmobile.com
 * ##########@sms.mycricket.com
 * ##########@mymetropcs.com
 * ##########@messaging.nextel.com
 * ##########@tms.suncom.com
 * ##########@email.uscc.net
 * ##########@voicestream.net
 * 
 */
/* MMS
 * ##########@mms.att.net
 * ##########@vzwpix.com
 * ##########@mms.mycricket.com
 */


//@SuppressWarnings("deprecation")
public class Patriot2 {

	private final String USER_AGENT = "Mozilla/5.0";
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	boolean contentChanged = false;
	static boolean alertSent = false;
	static int counter = 0;
	
	
	public static void main(String[] args) throws Exception {
		System.out.println(">=================>Let's Rock-n-Roll");
		bootstrap();
	
	}

	private static void bootstrap() throws Exception{
		Patriot2 http = new Patriot2();

		System.out.println("Step 1 - PINGing Cook Systems Website- Send Http GET request :"  + counter);
		counter++;
		TimeUnit.SECONDS.sleep(5);
		http.sendGet();
		TimeUnit.SECONDS.sleep(30);
		bootstrap();
	}
	
	
	
	// Step 1 - HTTP GET request
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

		System.out.println("\n Step 2 - Sending 'GET' request to URL : " + url);
		TimeUnit.SECONDS.sleep(2);
		// Step 2 - Check Response Code
		System.out.println(" Step 3 - Checking Response Code");
		TimeUnit.SECONDS.sleep(2);
		System.out.println("Response Code : " +
                       response.getStatusLine().getStatusCode());
		TimeUnit.SECONDS.sleep(2);
		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		
		}
		System.out.println("Step 4 - Begin SCAN===>  " + result.toString());
		TimeUnit.SECONDS.sleep(2);
		
		
		
		
		
		
		
		int intIndex = result.indexOf(tag2);
	//	int begPos = intIndex +tag.length() + 4;
		int begPos = intIndex + 4;
		
		
	//	int endPos = begPos +exp.length()  ;
		int endPos = begPos + 5  ;

	String	subOutput = result.substring((begPos),(endPos));
//	System.out.println(" " + result.toString());
//	TimeUnit.SECONDS.sleep(3);
	System.out.println("\n\n SCAN Complete\n\n " );
	TimeUnit.SECONDS.sleep(5);
	System.out.println("Patriot Armed  " + result.toString());
	ekho = result.toString();
	TimeUnit.SECONDS.sleep(3);
		// System.out.println( "Raw JSON: "  +output);		//one time only
	//	System.out.println(tag + ": "  +subOutput);		

		//	System.out.println(subOutput);
			
//			System.out.println("\n\n ===> = " +contentChanged + " ..alertSent= " +alertSent + "\n Begin Page Scan");
//			TimeUnit.SECONDS.sleep(2);
//			System.out.print("=");
//		//	TimeUnit.SECONDS.sleep(1);
//			TimeUnit.MILLISECONDS.sleep(200);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=\n");
//			
//			TimeUnit.SECONDS.sleep(2);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=");
//			TimeUnit.SECONDS.sleep(1);
//			System.out.print("=>\n");
//			
//			
//				// Just "for" kicks and giggles...get it "for"
//				for (int i = 0; i < 40; i++) {
//				    System.out.print("=");
//				    TimeUnit.MILLISECONDS.sleep(200);
//				}
//				System.out.print("=>\n");
//				for (int i = 0; i < 40; i++) {
//				    System.out.print("=");
//				    TimeUnit.MILLISECONDS.sleep(200);
//				}
//				System.out.print("=>\n");
				
				
				
				
				
			
			//	if(intIndex==108908){
	System.out.println("IMAGE: "  +intIndex  + " Counter " + counter);
	TimeUnit.SECONDS.sleep(3);
			if(intIndex!=108912){
				contentChanged = true;
				
				
			System.out.println("\n\n ===> Step 5 - Change detected, contentChanged toggled= "  +contentChanged +   " ..alertSent= " +alertSent);
			TimeUnit.SECONDS.sleep(2);
			}
		//	if((intIndex==108908)&&(alertSent==false)){
			if((contentChanged==true)&&(alertSent==false)){	
				generateAndSendEmail();
			System.out.println("\n\n ===> Step 6 -  Preparing to send alert now, contentChanged= "  +contentChanged +   " ..alertSent= " +alertSent);
			TimeUnit.SECONDS.sleep(2);	
				alertSent = true;
						
				System.out.println("\n\n ===> Step 7 Email sent successfully. Check your email..");
				System.out.println("\n\n ===> alertSent toggled to true....alertSent= " +alertSent);
			}
			
	//	}
			
			System.out.println("\n\n ===> All is well. contentChanged= " +contentChanged + " ..alertSent= " +alertSent);

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
	//	generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("8179993356@txt.att.net"));
		generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("rsandlin@cooksys.com"));
	//	generateMailMessage.setSubject("Website Change Detected");
	//	String emailBody = "Normal page markup appears to have changed " + "<br><br> Regards, <br>Robin";
		generateMailMessage.setSubject("SMS Alert Test");
		String emailBody = "This is only a test " + "<br><br> Regards, <br>Robin";
		
		
		generateMailMessage.setContent(emailBody, "text/html");
		System.out.println("Mail Session has been created successfully..");
 
		// Step3
			System.out.println("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtp");
	 
			
			// if you have 2FA enabled then provide App Specific Password
			transport.connect("smtp.gmail.com", "FastrackD7", "1234!@#$");
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
		}
	
	

}
