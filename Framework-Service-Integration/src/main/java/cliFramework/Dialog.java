package cliFramework;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;


public class Dialog
{
  
	
	public static void main(String[] args)
  {
    // a jframe here isn't strictly necessary, but it makes the example a little more real
    JFrame frame = new JFrame("Framework Time Override");

    // prompt the user to enter their name
    String shour = JOptionPane.showInputDialog(frame, "Override Hour?");
    String sminute = JOptionPane.showInputDialog(frame, "Override Minute?");
    int hour = 0;
    int minute = 0;
    
    try {
        hour = Integer.parseInt(shour);
        minute = Integer.parseInt(sminute);
    } catch (NumberFormatException e) {
        // Not a number, display error message...
    }   
    
	//Creating LocalDateTime by providing input arguments
	LocalDateTime overrideDate = LocalDateTime.of(2016, Month.NOVEMBER, 11, hour, minute, 0);
	System.out.println("OverrideDateTime=  ");

	LocalDateTime dateTime = LocalDateTime.now();
			//default format
	//System.out.println("Default format of LocalDateTime="+dateTime);
			//specific format
	//System.out.println(dateTime.format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")));
	System.out.println(overrideDate.format(DateTimeFormatter.ofPattern("MMM/d/uuuu  HH:mm")));

    
    // get the user's input. note that if they press Cancel, 'name' will be null
   // System.out.printf("The time is " + hour +":"+ minute );
    System.exit(0);
  }
}