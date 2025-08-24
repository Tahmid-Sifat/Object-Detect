package object_detect_program;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import swiftbot.SwiftBotAPI;
import swiftbot.Button;
import swiftbot.ImageSize;
 
//this is the child class of Mode for Curious mode that are being called

public class ScaredyMode extends Mode {
 
	public ScaredyMode(SwiftBotAPI swiftBot, String imageFilePath) {
		super(swiftBot, imageFilePath);
		 
	}
  
	private static final String YELLOW = "\u001B[33m";    // Yellow/gold text
	private static final String RED = "\u001B[31m";       // Red text
	private static final String GREEN = "\u001B[32m";     // Green text for 'X' in quit message
	private static final String BG_YELLOW = "\u001B[43m"; // Yellow background
	private static final String BG_BLUE = "\u001B[44m";   // Blue background
	private static final String RESET = "\u001B[0m";      // Reset color
 

	public void run() {
		try { 
			// same beginning as Curious mode 
			displayWelcome();
			WanderAround();

			while (!ObjectDetect_Main.terminateCheck) 
			{ 
				double distance = swiftBot.useUltrasound();
				System.out.println(YELLOW + "\n==== "+RESET+" Current distance is " + distance + " cm "+YELLOW +" ==== " + RESET);

				if (distance <= 50) // Checking if the object is within 50 cm zone
				{ 
					System.out.println(YELLOW + "\n>>>>"+RESET+" Object Detected ! "+YELLOW +" <<<<" + RESET);

					swiftBot.fillUnderlights(new int[] { 255, 0, 0 }); // Red light 
					Thread.sleep(400);
					swiftBot.disableUnderlights();

					ObjectDetect_Main.objectCount++;

					System.out.println(YELLOW + "\n|><|"+RESET+" Taking an image of the object "+YELLOW +" |><|" + RESET+ "\n");
					BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_720x720);
					ImageIO.write(image, "png", new File(imageFilePath + "\\object_" +  ObjectDetect_Main.objectCount+"_" +uniqueDate + ".png"));

					swiftBot.fillUnderlights(new int[] { 255, 0, 0 }); // Red light
					// here the swiftbot is turning 180 degree and move forward after detecting one object 
					swiftBot.move(-40, -40, 1500);
					swiftBot.move(100, -100, 800); // 180 degree turn 
					swiftBot.move(40, 40, 3000);
                    
					swiftBot.disableUnderlights();
					
					System.out.println(YELLOW+"---------------------------------------"+RESET);
					System.out.println("|      Do you want to quit ?          |");
					System.out.println("|   Please press "+ GREEN +"'X'"+RESET+" button to exit   |");
					System.out.println(YELLOW+"---------------------------------------\n"+RESET);

					long startTime = System.currentTimeMillis();
					boolean[] quitPressed = { false };

					swiftBot.disableButton(Button.X);
					swiftBot.enableButton(Button.X, () -> {
						quitPressed[0] = true;
						ObjectDetect_Main.terminateCheck = true;
					});

					while (System.currentTimeMillis() - startTime < 5000) 
					{
						if (quitPressed[0]) 
						{
							System.out.println(RED + "\n>>> Termination in progress . . ." + RESET);
							ObjectDetect_Main.terminateProgram();
							return;
						}
						Thread.sleep(100);
					}

					swiftBot.disableButton(Button.X);
				} // 50 cm zone checking ends  
				
				else if (swiftBot.useUltrasound() > 50) // if the object has moved or out of range
				{
					System.out.println(YELLOW + "\n>>> "+ RESET+"Changing the direction "+ YELLOW +" <<<" + RESET);
					ChangeDirection();
				}
			} // while loop ends 
		 } 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println(RED + "\n Error initiating the Scaredy Mode " + RESET);
		}
	} // run method ends 
	
	// Method for Wandering Around 
	public void WanderAround() { 
		try {
			int[] rgb = { 0, 0, 255 }; // Blue
			swiftBot.fillUnderlights(rgb); 
			System.out.println(BG_BLUE + ">>> Hi ! I am wandering around for an object <<<" + RESET);
			double distance = swiftBot.useUltrasound();
			
			swiftBot.move(40, 40, 3000);
			// Forward for 3 seconds at 40% speed

			if (distance <= 50)

			{
				swiftBot.disableUnderlights();
				swiftBot.move(0, 0, 1000);				
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
		} // try ends 
		catch (Exception e) {

			System.out.println(RED + "Error in wandering movement" + RESET);
		}

	} // Wander around ends

	private void displayWelcome() {
		System.out.println("\n");
		System.out.println(" _____                         _         __  __           _      ");
		System.out.println("/  ___|                       | |       |  \\/  |         | |     ");
		System.out.println("\\ `--.  ___ __ _ _ __ ___   __| |_   _  | \\  / | ___   __| | ___ ");
		System.out.println(" `--. \\/ __/ _` | '__/ _ \\ / _` | | | | | |\\/| |/ _ \\ / _` |/ _ \\");
		System.out.println("/\\__/ / (_| (_| | | | (_) | (_| | |_| | | |  | | (_) | (_| |  __/");
		System.out.println("\\____/ \\___\\__,_|_|  \\___/ \\__,_|\\__, | |_|  |_|\\___/ \\__,_|\\___|");
		System.out.println("                                 __/ |                           ");
		System.out.println("                                |___/                            ");
		System.out.println("\n");
		System.out.println(BG_YELLOW + "*** Welcome to the  Scaredy Mode ***" + RESET);
		System.out.println("\n");
	}
} // main class ends 