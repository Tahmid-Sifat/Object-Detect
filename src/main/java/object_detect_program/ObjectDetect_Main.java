package object_detect_program;
 
import java.awt.image.BufferedImage; 
import java.io.FileWriter;
import java.io.IOException;
import swiftbot.SwiftBotAPI; 
import swiftbot.Button;
import java.text.SimpleDateFormat;
import java.util.Date;

// This is the main class from where the program is structured 

public class ObjectDetect_Main {

	// Using static variables for global accessibility 
	// ( can be accessed from other classes{methods} ) and better convenience

	static SwiftBotAPI swiftBot;     // Declaring SwiftBot API for global usability 
	static String selectedMode = ""; // variable that will display Selected Mode
	static int objectCount = 0;      // for counting the number of objects encountered 
	static long startTime = System.currentTimeMillis(); // To calculate the duration and initiate timer 
	static String imageFilePath = "/data/home/pi/object_detect_program/Images_objectD/"; // inside the swiftBot
	static boolean terminateCheck = false; // flag to store termination press 

	//  start of main method  

	/*	Here, throws InterruptedException in the main method is used to handle potential 
	interruptions during thread operations like Thread.sleep() without catching the exception locally */

	public static void main(String[] args) throws InterruptedException 
	{
		try {

			swiftBot = new SwiftBotAPI(); // declaring the SwiftBot API as class object 

		} 
		catch (Exception e) 
		{
			/* 
			 * Outputs a warning if I2C is disabled. For a better error handling 
			 * This only needs to be turned on once 
			 */ 
			System.out.println("\n Error handling SwiftBot API ");
			System.out.println("\nI2C disabled!");
			System.out.println("Run the following command:");
			System.out.println("sudo raspi-config nonint do_i2c 0\n");
			System.exit(5);
		} 

		while(!terminateCheck) 
		{ 
			// an infinite loop to keep the program running until terminated otherwise 
			// Enable X button globally for termination , so that user can initiate termination 
			// any time during the program 
			swiftBot.disableButton(Button.X); // disabling the Button before performing the task 
			swiftBot.enableButton(Button.X, () -> {
				swiftBot.disableButton(Button.X); 
				System.out.println("\n| >>>>> X button pressed. Initiating termination...  <<<<< | \n");
				terminateCheck=true;
				terminateProgram(); // Call termination process

			});

			displayWelcomeScreen(); // The welcome user interface will be displayed 
			swiftBot.disableButton(Button.A);
			// calling the method QR scan 
			QRscan();

		} // while loop ends 

	} // main method ends

	// Method for scanning the QR code

	public static void QRscan() throws InterruptedException {

		// ANSI escape codes to make the UI better 
		// making them as final to make them constant , cause the colour codes are fixed and 
		// not to be changed 
		final String RED = "\033[31m";         // Red text
		final String CYAN = "\033[36m";        // Cyan text
		final String BG_CYAN = "\033[46m";     // Cyan background
		final String BG_RED = "\033[41m";      // Red background
		final String BG_BLUE = "\033[44m";     // Blue background
		final String RESET = "\033[0m";        // Reset to default color

		System.out.println(BG_BLUE+"                                                                        "+RESET);
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("--- Please press 'A' button on the bot to scan a QR code using Bot's camera ---");
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println(BG_BLUE+"                                                                        "+RESET);

		// Using a flag 1D Array to wait for the A button press , so that it can be updated 
		boolean[] aButtonPressed = { false };

		swiftBot.disableButton(Button.A); // Disabling Button A first to avoid multiple callbacks
		swiftBot.enableButton(Button.A, () -> {
			swiftBot.disableButton(Button.A); // Disable after press
			aButtonPressed[0] = true; // Set flag to true to its index 1 
		});

		// Wait for A button press
		while (!aButtonPressed[0]) { // This loop will run if A button is not pressed 
			Thread.sleep(100); // Small delay to avoid busy-waiting and smooth operation 
		}

		// Proceed with QR scanning

		System.out.println(CYAN+"\n<><><><><><><><><><><><><><><>"+RESET);
		System.out.println(CYAN+"<><>"+RESET+" Scanning in progress"+CYAN+" . . ."+RESET);
		System.out.println(CYAN+"<><><><><><><><><><><><><><><>\n"+RESET);

		long startTime = System.currentTimeMillis();
		long endTime = startTime + 20000; // 20 seconds in milliseconds

		System.out.println(BG_CYAN + " +++  Starting 20s timer to scan a QR code +++ " + RESET+ "\n");
 


		try {
			while (System.currentTimeMillis() <= endTime) { // This loop will run till 20 seconds 

				BufferedImage img = swiftBot.getQRImage(); 
				String QRin = swiftBot.decodeQRImage(img);  // storing the text inside QRin

				selectedMode=QRin; // transferring it to selected mode so that we can use it in termination

				int[] rgb = { 128, 0, 128 }; // purple
				swiftBot.fillUnderlights(rgb);

				if (QRin.isEmpty()) 
				{ // for QR validation 1
					System.out.println(BG_RED + "Invalid QR code , please scan again !" + RESET + "\n");
				} 
				else {
					System.out.println(BG_CYAN+" QR code found and decoded successfully "+RESET+ "\n");
					swiftBot.disableUnderlights(); // turning the lights off after decoding 

					System.out.println("\n");

					// The mode selection will be done as well as QR validation 2 with switch statement

					switch (QRin.toLowerCase())  // making the text all lower case to avoid case errors 
					{   
					case "curious":
						System.out.println(CYAN+"----------------------------------------"+RESET);
						System.out.println(CYAN+"| >>>>>>" +RESET+" Executing Curious Mode"+CYAN+" . . .  |"+RESET);
						System.out.println(CYAN+"----------------------------------------\n"+RESET);
						new CuriousMode(swiftBot, imageFilePath).run();
						// calling the class Curious mode to execute the mode , the class takes input 
						// as the api and imageFilePath 
						break; 
						// the switching stops 

					case "scaredy":
						System.out.println(CYAN+"----------------------------------------"+RESET);
						System.out.println(CYAN+"| >>>>>>" +RESET+" Executing Scaredy Mode"+CYAN+" . . . |"+RESET);
						System.out.println(CYAN+"----------------------------------------\n"+RESET);
						new ScaredyMode(swiftBot, imageFilePath).run();
						break;

					case "dubious":
						System.out.println(CYAN+"----------------------------------------"+RESET);
						System.out.println(CYAN+"| >>>>>>" +RESET+" Executing Dubious Mode"+CYAN+" . . . |"+RESET);
						System.out.println(CYAN+"----------------------------------------\n"+RESET);
						new DubiousMode(swiftBot, imageFilePath).run();
						break;
						// if the QR does not match any of the 3 modes , then we call the method again 

					default:
						System.out.println(BG_RED + "Invalid QR code , please scan again !" + RESET + "\n");
						QRscan(); // Recursive call to scan again
					} // switch ends 

					break; // the Loop breaks 
				} // else ends

				if (System.currentTimeMillis() > endTime) // 20s timer ends 
				{
					System.out.println(BG_RED + "\\n Times Up! Please Scan Again" + RESET + "\n");
				}

			} // Main while loop for scanning QR code ends

		} // try block ends 
		catch (Exception e) 
		{ // Handling any other error during the QR scan method 
			System.out.println(RED + "\n Error occured during QR code scan , please run the program again  " + RESET + "\n");
			e.printStackTrace(); // using it to print detailed information about an exception, including the stack trace
			System.exit(5); // The program ends 
		} // catch block ends

	} // QRScan Method ends 

	// Method for the termination process 

	public static void terminateProgram() 
	{
		final String RED = "\033[31m";     // Red text
		final String GREEN = "\033[32m";  // Green text
		final String BLUE = "\033[34m";  // Blue text
		final String YELLOW = "\033[33m";  // Yellow text
		final String RESET = "\033[0m";   // Reset to default color

		try {
			long endTime = System.currentTimeMillis();
			double duration = (endTime - startTime) / 1000.0 ;
			// creating a unique date so that the log files can be unique named 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String uniqueDate = dateFormat.format(new Date());
			String AbslogFilePath = "Object_Detect_Log_Swiftbot_"+uniqueDate+".txt";  
			String logFilePath = "/data/home/pi/object_detect_program/ "+AbslogFilePath; // inside SwiftBot
			// Calculating the total Duration in seconds

			// Creating log file , logFilePath as given at the beginning 
			FileWriter writer = new FileWriter(logFilePath); // Initiating writing log file 

			writer.write("Mode: " + selectedMode + "\n"); // from main method
			writer.write("Duration: " + duration + " seconds\n");
			writer.write("Objects Encountered: " + objectCount + "\n"); // from the classes 
			writer.write("Image File Path: " + imageFilePath + "\n"); 
			writer.write("Log File Path: " + logFilePath + "\n");
			writer.close();

			// Termination UI ASCII art
			System.out.println("\n");
			System.out.println(" _____                    _            _   _             ");
			System.out.println("|_   _|                  (_)          | | (_)            ");
			System.out.println("  | | ___ _ __ _ __ ___  _ _ __   __ _| |_ _  ___  _ __  ");
			System.out.println("  | |/ _ \\ '__| '_ ` _ \\| | '_ \\ / _` | __| |/ _ \\| '_ \\ ");
			System.out.println("  | |  __/ |  | | | | | | | | | | (_| | |_| | (_) | | | |");
			System.out.println("  \\_/\\___|_|  |_| |_| |_|_|_| |_|\\__,_|\\__|_|\\___/|_| |_|");
			System.out.println("\n");

			// Asking the user Termination options
			System.out.println(RED + ">>> "+RESET+" Choose how do you want to terminate "+RED+ "<<<\n" + RESET);
			System.out.println(RED + "--------------------------------------------------------------------------------" + RESET);
			System.out.println(RED +"|"+RESET+"    Press " + YELLOW + "'Y'" + RESET + " button to view the execution log          "+RED+"                      |"+RESET );
			System.out.println(RED +"|"+RESET+"    Press " + GREEN + "'X'" + RESET + " button to view only the log file path      "+RED+"                     |"+RESET );
			System.out.println(RED + "--------------------------------------------------------------------------------" + RESET+"\n");

			// Disable buttons before enabling
			swiftBot.disableButton(Button.Y);
			swiftBot.disableButton(Button.X);

			swiftBot.enableButton(Button.Y, () -> { // when user presses Y
				swiftBot.disableButton(Button.Y);

				swiftBot.fillUnderlights(new int[] { 255, 165, 0 }); // Orange light 				
				// Execution log display
				System.out.println(BLUE + "----------------------------------------------------------------------------------------" + RESET);
				System.out.println(BLUE + "| Mode of operation    : " + GREEN + selectedMode + BLUE + "                                                       |   " + RESET);
				System.out.println(BLUE + "| Duration of operation: " + GREEN + duration + " Seconds" + BLUE + "                                                |  " + RESET);
				System.out.println(BLUE + "| Objects Encountered  : " + GREEN + objectCount + BLUE + "                                                             |   " + RESET);
				System.out.println(BLUE + "| Image file path      : " + GREEN + imageFilePath + BLUE + "           |    " + RESET);
				System.out.println(BLUE + "----------------------------------------------------------------------------------------\n" + RESET);
				swiftBot.disableUnderlights();
				thankText();
				System.exit(0);
			}); 

			swiftBot.enableButton(Button.X, () -> { // when user presses X 
				swiftBot.disableButton(Button.X);

				swiftBot.fillUnderlights(new int[] { 255, 255, 0 }); // Yellow light 
				// Log file path display
				System.out.println(BLUE + "---------------------------------------------------------------------------------------------------------------------------" + RESET);
				System.out.println(BLUE + "| >>> " + GREEN + "Execution log file path : " + logFilePath + BLUE + "       |" + RESET);
				System.out.println(BLUE + "---------------------------------------------------------------------------------------------------------------------------\n" + RESET);
				swiftBot.disableUnderlights();
				thankText(); 
				System.exit(0);
			});

		} // try block ends 

		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println(RED + "Error handling the termination, please try again\n" + RESET);

		} // catch block ends 

	} // Termination ends



	// Method to adjust position based on distance
	// From the speed testing I did , at 35% speed of Swiftbot 
	// The velocity is 11.333 cm/s , so we are using the velocity formula 
	// distance = velocity x time to adjust position ( Maintaining the buffer zone for curious mode )

	public static void AdjustPosition(double distance) {
		try 
		{ 
			System.out.println(" \n >>> Adjusting position <<< ");
			swiftBot.fillUnderlights(new int[] { 0, 255, 0 }); // green 

			if(distance>100) // when the object is too far , the bot moves forward 
			{
				swiftBot.move( 50, 50, 2000 );
			}

			else if (distance > 31) // if the bot is far from buffer zone 
			{
				double s = distance-30;
				double t = s/(11.33333); // using the formula of velocity from test data 
				int time = (int) (t*1000); // converting the double to int as move method requires integer as time
				swiftBot.move( 35, 35, time ); // move forward to make it inside buffer
			}   
			else if (distance < 29)  // if the bot is inside buffer zone 
			{
				double s = 30-distance; 
				double t = s/(11.33333);
				int time = (int) (t*1000);
				swiftBot.move( -35, -35, time ); // move backward to make it inside buffer
			}

			swiftBot.disableUnderlights(); 
		} // try block ends 
		catch (Exception e) 
		{
			System.out.println("Error adjusting position");
		}

	} // Adjust position ends

	// Method to display Welcome UI
	public static void displayWelcomeScreen() {
		System.out.println("\n");
		// Object Detect ASCII art - with your preferred Detect style
		System.out.println("   ____  _     _           _                _____        _            _   ");
		System.out.println("  / __ \\| |   (_)         | |              |  __ \\      | |          | |  ");
		System.out.println(" | |  | | |__  _  ___  ___| |_             |  |  | _____| |_ ___  ___| |_ ");
		System.out.println(" | |  | | '_ \\| |/ _ \\/ __| __|            |  |  |/ _ \\ __/ _ \\/ __| __|");
		System.out.println(" | |__| | |_) | |  __/ (__| |_             |  |__/  __/ ||  __/ (__| |_ ");
		System.out.println("  \\____/|_.__/| |\\___|\\___|\\__|            |_____/\\___|\\__\\___|\\___|\\___|");
		System.out.println("             _/ |                                                         ");
		System.out.println("            |__/                                                         ");
		System.out.println("\n");

		// Welcome message in blue box
		final String CYAN = "\033[36m"; // Cyan text
		final String BG_CYAN = "\033[46m"; // CYAN TEXT
		final String RESET = "\033[0m";//   background reset

		System.out.println(BG_CYAN+"    *** Welcome to the  Object Detect  Program by SwiftBot :D ***   "+RESET);
		System.out.println("\n");

		// Button layout ASCII art using standard characters
		System.out.println("        ------              ");
		System.out.println("     _/        \\_           ");
		System.out.println("    |Y          B|           ");
		System.out.println("  _|  X        A  |_         ");
		System.out.println(" /                  \\        ");
		System.out.println("|                    |        ");
		System.out.println(" \\                  /        ");
		System.out.println("  |_              _|         ");
		System.out.println("    |            |           ");
		System.out.println("     \\__      __/           ");
		System.out.println("        ------              \n");

		// Button labels in boxes using standard ASCII
		System.out.println("    +--------+   +--------+");
		System.out.println("    |   Y    |   |    B   |");
		System.out.println("    +--------+   +--------+");
		System.out.println("    +--------+   +--------+");
		System.out.println("    |   X    |   |    A   |");
		System.out.println("    +--------+   +--------+\n");

		// Bottom description
		System.out.println(CYAN+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+RESET);
		System.out.println("++  The outline ( top view ) shows the approximate positions of      ++");
		System.out.println("+++            the four Buttons  X , Y , A , B of the bot           +++");
		System.out.println(CYAN+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+RESET);

		System.out.println("\n\n");
	} // Welcome screen ends

	// Method to display Thank you ASCII art
	public static void thankText()
	{

		System.out.println("\n");
		System.out.println(" _____ _                 _      __   __          ");
		System.out.println("|_   _| |               | |     \\ \\ / /          ");
		System.out.println("  | | | |__   __ _ _ __ | | __   \\ V /___  _   _ ");
		System.out.println("  | | | '_ \\ / _` | '_ \\| |/ /    \\ // _ \\| | | |");
		System.out.println("  | | | | | | (_| | | | |   <     | | (_) | |_| |");
		System.out.println("  \\_/ |_| |_|\\__,_|_| |_|_|\\_\\    \\_/\\___/ \\__,_|");
		System.out.println("\n");

	} // Thank text ends

} // main class ends 


