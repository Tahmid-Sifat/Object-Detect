package object_detect_program;

import java.util.Random;

import swiftbot.SwiftBotAPI;

public class DubiousMode extends Mode {
    

    public DubiousMode(SwiftBotAPI swiftBot, String imageFilePath) 
    {
		super(swiftBot, imageFilePath);	 
	}

	private static final String BLUE = "\u001B[34m";     // Blue text
	private static final String RED = "\033[31m";        // Red text
    private static final String CYAN = "\u001B[36m";     // Cyan text for arrows
    private static final String YELLOW = "\u001B[33m";   // Yellow  
    private static final String BG_BLUE = "\u001B[44m";  // Blue background
    private static final String RESET = "\u001B[0m";     // Reset color
  
    public void run() {
        displayWelcome();
        try {
            // Mode selection message
            System.out.println(BLUE + "--------------------------------" + RESET);
            System.out.println(BLUE + "|                              |" + RESET);
            System.out.println(YELLOW + "|   Randomly selecting between |" + RESET);
            System.out.println(YELLOW + "| Curious Mode " + BLUE + "or" + YELLOW + " Scaredy Mode |" + RESET);
            System.out.println(BLUE + "|                              |" + RESET);
            System.out.println(BLUE + "--------------------------------" + RESET);
            System.out.println("\n");

            Random rand = new Random();
            int mode = rand.nextInt(2) + 1; // Randomly select 1 or 2
            
            if (mode == 1) {
                System.out.println(BLUE + "-------------------------------------------------" + RESET);
                System.out.println(BLUE + "|" + CYAN + " >>>>>> Executing Curious Mode . . ." + BLUE + "           |" + RESET);
                System.out.println(BLUE + "-------------------------------------------------" + RESET);
                new CuriousMode(swiftBot,imageFilePath).run();
            } else {
                System.out.println(BLUE + "-------------------------------------------------" + RESET);
                System.out.println(BLUE + "|" + CYAN + " >>>>>> Executing Scaredy Mode . . ." + BLUE + "           |" + RESET);
                System.out.println(BLUE + "-------------------------------------------------" + RESET); 
                new ScaredyMode(swiftBot,imageFilePath).run();
            }
        } 
        catch (Exception e) 
        {
            System.out.println(RED + "\n Error initiating the Dubious Mode " + RESET);
        }
    } // run ends 

    private void displayWelcome() 
    {
        System.out.println("\n");
        System.out.println(" _____       _     _                  __  __           _      ");
        System.out.println("|  __ \\     | |   (_)                |  \\/  |         | |     ");
        System.out.println("| |  | |_   _| |__  _  ___  _   _ ___| \\  / | ___   __| | ___ ");
        System.out.println("| |  | | | | | '_ \\| |/ _ \\| | | / __| |\\/| |/ _ \\ / _` |/ _ \\");
        System.out.println("| |__| | |_| | |_) | | (_) | |_| \\__ \\ |  | | (_) | (_| |  __/");
        System.out.println("|_____/ \\__,_|_.__/|_|\\___/ \\__,_|___/_|  |_|\\___/ \\__,_|\\___|");
        System.out.println("\n");
        System.out.println(BG_BLUE + "*** Welcome to the  Dubious Mode ***" + RESET);
        System.out.println("\n");
    }
}