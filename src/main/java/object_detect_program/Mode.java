package object_detect_program;

import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import swiftbot.SwiftBotAPI;

// this is the parent/ super  class 
 
abstract public class Mode {
	
	// inheritance being used in Childs / sub classes 
	// we are inheriting swiftBot api , imageFilePath , changeDirection method in the child classes
	// so that we do not need to repeat the code structure inside the classses over and over again 
	
    protected SwiftBotAPI swiftBot;  
    protected String imageFilePath;  

    public Mode(SwiftBotAPI swiftBot, String imageFilePath) 
    {  
        this.swiftBot = swiftBot;
        this.imageFilePath = imageFilePath;
    }

        // creating a unique date so that the image files can be unique named 
 			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
 			String uniqueDate = dateFormat.format(new Date());
 			
    abstract void run(); // this method is a template for the child classes  
     
    public void ChangeDirection() 
	{
		try {
		 
			// It will randomly pick between Right or Left movement 

			Random random = new Random();
			int num = random.nextInt(2) + 1;
			
			if (num == 1) 
			   {
				swiftBot.move(80, 10, 3000); // Move right
				swiftBot.move(35, 35, 2000); // Move forward
			   } 
			else {
				swiftBot.move(10, 80, 3000); // Move left
				swiftBot.move(35, 35, 2000); // Move forward
			     }
		    } // Try ends  
		catch (Exception e) {
			System.out.println("Error changing direction");
		}
	} // Change direction ends 
}