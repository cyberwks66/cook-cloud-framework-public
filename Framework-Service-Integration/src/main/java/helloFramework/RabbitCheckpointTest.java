package helloFramework;

import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RabbitCheckpointTest {

	
	
	@Autowired
    private RabbitTemplate rabbitTemplate;

	
	
	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}





	public RabbitTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}



	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

}
