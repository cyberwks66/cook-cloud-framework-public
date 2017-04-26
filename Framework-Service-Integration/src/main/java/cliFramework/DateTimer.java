package cliFramework;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class DateTimer {

	public static void main(String[] args) {
		

	
		String dts =  "2016-11-01T07:52:28.9131808Z";
		
				//Current Date
		LocalDate today = LocalDate.now();
		System.out.println("Current Date= " + today);
		
	
				//Creating LocalDate by providing input arguments
		LocalDate firstDay_2014 = LocalDate.of(2014, Month.JANUARY, 1);
		System.out.println("Specific Date= " + firstDay_2014);
				
				
				//Try creating date by providing invalid inputs
				//LocalDate feb29_2014 = LocalDate.of(2014, Month.FEBRUARY, 29);
				//Exception in thread "main" java.time.DateTimeException: 
				//Invalid date 'February 29' as '2014' is not a leap year
	
				//Current Date
		LocalDateTime todaytime = LocalDateTime.now();
		System.out.println("Current DateTime="+todaytime);
				
				//Current Date using LocalDate and LocalTime
		todaytime = LocalDateTime.of(LocalDate.now(), LocalTime.now());
		System.out.println("Current DateTime="+todaytime);
				
		
		
				//Creating LocalDateTime by providing input arguments
		LocalDateTime specificDate = LocalDateTime.of(2014, Month.JANUARY, 1, 10, 10, 30);
		System.out.println("Specific Date="+specificDate);

		LocalDateTime dateTime = LocalDateTime.now();
				//default format
		System.out.println("Default format of LocalDateTime="+dateTime);
				//specific format
		System.out.println(dateTime.format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")));
		System.out.println(dateTime.format(DateTimeFormatter.ofPattern("MMM/d/uuuu  HH:mm")));
	
	
	}

}
