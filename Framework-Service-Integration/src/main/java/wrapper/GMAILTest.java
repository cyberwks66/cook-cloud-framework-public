package wrapper;

import helloFramework.CrunchifyJava;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import static org.junit.Assert.*;

public class GMAILTest {

	@Test
	public void generateAndSendEmailtest() {
		
	
		
		try {
			CrunchifyJava.generateAndSendEmail();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

}
