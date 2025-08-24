package object_detect_program;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import swiftbot.SwiftBotAPI;
import swiftbot.Button;
import swiftbot.ImageSize;

 
// this is the child class of Mode for Curious mode that are being called 

public class CuriousMode extends Mode {
	

	public CuriousMode(SwiftBotAPI swiftBot, String imageFilePath) {
		super(swiftBot, imageFilePath);
		 
	}

	private static final String CYAN = "\u001B[36m";    // Cyan text
	private static final String RED = "\u001B[31m";     // Red text
	private static final String GREEN = "\033[32m";
	private static final String BG_CYAN = "\033[46m"; // CBG YAN
	private static final String BG_BLUE = "\u001B[44m"; // Blue background
	private static final String RESET = "\u001B[0m";    // Reset color

	// using private as access modifier , as a part of better encapsulation   

	 
	public void run() { // method to run the functions of curious mode as the concept of Polymorphism 
		try {
			
			displayWelcome();
			WanderAround(); // calling the methods from the class 

			while (!ObjectDetect_Main.terminateCheck) 
			{ 				
				double distance = swiftBot.useUltrasound(); // keep storing and displaying distance  
				System.out.println(CYAN + "\n ==== "+RESET+" Current distance is " + distance + " cm "+CYAN +" ==== " + RESET);

				/* checking the bufferZone , instead of exactly 30 cm , for better execution of the 
				   program , its modified to between 29 and 31  */
				
				if (distance >= 29 && distance <= 31)  // BufferZone
				{  
					// if the object is inside the buffer zone , it should declare :
					
					System.out.println(CYAN + "\n >>>> "+RESET+" Object Detected ! "+CYAN +" <<<<" + RESET);

					for (int i = 0; i < 3; i++) 
					{ // For loop used for proper Blinking of the underlights as Green  
						swiftBot.fillUnderlights(new int[] { 0, 255, 0 });
						Thread.sleep(300);
						swiftBot.disableUnderlights(); 
					}

					ObjectDetect_Main.objectCount++; // Counting the object 

					System.out.println(CYAN + "\n |><| "+RESET+" Taking an image of the object "+CYAN +" |><|" + RESET+ "\n");
					BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_720x720);
					ImageIO.write(image, "png", new File(imageFilePath + "object_" + ObjectDetect_Main.objectCount+"_" +uniqueDate + ".png"));

					System.out.println(CYAN+"---------------------------------------"+RESET);
					System.out.println("|      Do you want to quit ?          |");
					System.out.println("|   Please press "+ GREEN +"'X'"+RESET+" button to exit   |");
					System.out.println(CYAN+"---------------------------------------\n"+RESET);  
 
					long startTime = System.currentTimeMillis();
					boolean[] quitPressed = { false }; // boolean to store button press by the user 

					swiftBot.disableButton(Button.X);
					swiftBot.enableButton(Button.X, () -> {
						swiftBot.disableButton(Button.X);
						quitPressed[0] = true; // means button is pressed
						ObjectDetect_Main.terminateCheck = true;
					});

					while (System.currentTimeMillis() - startTime < 5000)  // 5 seconds waiting for termination
					 {
						// if the button is pressed then the program goes to termination 
						if (quitPressed[0]) 
						{ // as user pressed X button , calling terminatation from main class 
							ObjectDetect_Main.terminateProgram();
							return;
						}
						Thread.sleep(100); // to smoothen the program 
					 } // termination while ends 

					Thread.sleep(1000); // waiting one more second 
					System.out.println(CYAN + "\n>>> "+ RESET+"Changing the direction "+ CYAN +" <<<" + RESET);
					ChangeDirection(); // method calling for Changing direction for new object 
 
				} // Buffer If ends

				else if (distance != swiftBot.useUltrasound())
					//  if the object is moved then it should adjust position
				{
					ObjectDetect_Main.AdjustPosition(distance); // calling the method adjust position 
				}
			} // main while loop ends 
			
		} // try block ends
		    catch (Exception e) 
		    {
			e.printStackTrace();
			System.out.println(RED + "\n Error initiating the Curious Mode " + RESET);
		    } // catch ends 
		
	} // run ends 

	// method for wandering around of curious mode
	public void WanderAround() { 
		try {
			int[] rgb = { 0, 0, 255 }; // Blue 
			swiftBot.fillUnderlights(rgb); // underlights as blue 
			
			System.out.println(BG_BLUE + ">>> Hi ! I am wandering around for an object <<<" + RESET);
			double distance = swiftBot.useUltrasound();
			
			swiftBot.move(40, 40, 3000);
			// Forward for 3 seconds at 40% speed

			if (distance >= 29 && distance <= 31) // if the object is already inside bufferzone 

			{
				swiftBot.disableUnderlights();
				swiftBot.move(0, 0, 1000);		// stops		
			}

			else {
				// Forward for 2 seconds at 40% speed
				swiftBot.move(40, 40, 2000);

				// Right for 3 seconds at good speed
				swiftBot.move(80, 10, 3000);

				// Forward for 3 seconds at 40% speed
				swiftBot.move(40, 40, 3000);

				// Right for 3 seconds at good speed
				swiftBot.move(90, 10, 3000);

				// Keep moving forward 2s at 35% speed
				swiftBot.move(35, 35, 2000);
 
				swiftBot.disableUnderlights();
				// Blue light turned off after the Wandering around 

			}
		} 
		catch (Exception e) {

			System.out.println(RED + "Error in wandering movement" + RESET);
		}

	} // Wander around ends

	private void displayWelcome() 
	{
		System.out.println("\n");
		System.out.println(" ____            _                  __  __           _      ");
		System.out.println("/  __|          (_)                |  \\/  |         | |     ");
		System.out.println("| |    _   _ _ __ _  ___  _   _ ___| \\  / | ___   __| | ___ ");
		System.out.println("| |   | | | | '__| |/ _ \\| | | / __| |\\/| |/ _ \\ / _` |/ _ \\");
		System.out.println("| |___| |_| | |  | | (_) | |_| \\__ \\ |  | | (_) | (_| |  __/");
		System.out.println("\\____/\\__,_|_|  |_|\\___/ \\__,_|___/_|  |_|\\___/ \\__,_|\\___|");
		System.out.println("\n");
		System.out.println(BG_CYAN + "*** Welcome to the  Curious Mode ***" + RESET);
		System.out.println("\n");
	} // display welcome ends
} // curious class ends 