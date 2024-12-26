import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class PlayerGenerator {
    static final String[] DiceStats = {"STR", "DEX", "CON", "INT", "WIS", "CHA", "AC"};
    static String[] DiceStats_Input = new String[DiceStats.length];  // reset arrays
    static final String[] GeneralInfo = {"Name", "Gender", "Sex", "Age", "Height", "Weight"};
    static String[] GeneralInfo_Input = new String[GeneralInfo.length];  // reset arrays
    static final String[] BackgroundInfo = {"Former Profession", "Family status", "Background"};
    static String[] BackgroundInfo_Input = new String[BackgroundInfo.length];  // reset arrays

    public static void main(String[] args) {
        menu();
    }

    public static void menu() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("0: Write ouput to file.1: Start again.2: Display character infos.3: Get tips/ help. Anything else to exit.");
                int choice = -1;
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                } else {
                    System.err.println("Invalid input. Please enter a valid number.");
                    scanner.next(); // Consume the invalid input
                    continue;
                }

                // Consume the newline character left by nextInt()
                scanner.nextLine();

                switch (choice) {
                    case 0 -> {
                        // Write current Character to the file output.md
                        writeFile();
                        return; // Exit the program after writing to file
                    }
                    case 1 -> {
                        // Create new character destroying the old one (the file doesnt get overwritten yet)
                        clearScreen();
                        createCharacter(scanner);
                    }
                    case 2 -> {
                        // Print current temporary values
                        displayCharacterInfo();
                        menu();

                    }
                    case 3 ->{
                        // Explanation for data
                        clearScreen();
                        System.out.println(textBlock());
                    }
                    default -> {
                        // Exit the programm
                        System.out.println("Exiting...");
                        return;
                    }
                }
            }
        }
    }
    public static String textBlock(){
        return """
                STR: Breaking things, lifting heavy objects and using meleeweopens...
                Dex: Hitting ranged targets, lockpicking, balancing on a ledge...
                CON: Sprinting for long times, holding breath, resistance to poisen...
                INT: Magic, Castign spells...
                WIS: Detecting things, Cleric spells, (In german it would be closer to "Warnehmung")...
                CHA: Charming opponents, trading, negotiation...
                ---
                AC : Blocking/ Evading incoming damage. (Armor Class) 
                ---
                Difference Gender <-> Sex: Sex is the biological identity. Gender what you identify as. In most cases that will be the same.
                Former Profession: The fictional job you had before the events of the campaign started
                Familiy Status: Married, parents alive, stuff like that
                Background: All the lore related things that dont fit anywhere else.
                """;

    }

    public static void clearScreen() {
        // Platform-specific screen clearing
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void clearFile() {
        try {
            Files.write(Paths.get("output.md"), "".getBytes());
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }
    }

    public static void createCharacter(Scanner scanner) {
        // Getting all the inputs
        for (int i = 0; i < DiceStats.length; i++) {
            System.out.println("Please Enter your value for: " + DiceStats[i]);
            DiceStats_Input[i] = scanner.nextLine();  // Use nextLine() to read the full input
            System.out.println("You entered " + DiceStats_Input[i] + " as your value.");
        }

        for (int i = 0; i < GeneralInfo.length; i++) {
            System.out.println("Please Enter your value for: " + GeneralInfo[i]);
            GeneralInfo_Input[i] = scanner.nextLine();  // Use nextLine() to read the full input
            System.out.println("You entered " + GeneralInfo_Input[i] + " as your value.");
        }

        for (int i = 0; i < BackgroundInfo.length; i++) {
            System.out.println("Please Enter your value for: " + BackgroundInfo[i]);
            BackgroundInfo_Input[i] = scanner.nextLine();  // Use nextLine() to read the full input
            System.out.println("You entered " + BackgroundInfo_Input[i] + " as your value.");
        }

        clearScreen(); // Clear the screen after character creation
        displayCharacterInfo(); // Display all the info
    }

    public static void displayCharacterInfo() {
        clearScreen();
        System.out.println("Your values are: \n" + Arrays.toString(DiceStats) + "\n" + Arrays.toString(DiceStats_Input));
        System.out.println("Your General Info is \n" + Arrays.toString(GeneralInfo) + "\n" + Arrays.toString(GeneralInfo_Input));
        System.out.println("Your Background Info is: \n" + Arrays.toString(BackgroundInfo) + "\n" + Arrays.toString(BackgroundInfo_Input));
    }

    public static void writeFile() {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("# Character Info:\n\n");

        fileContent.append("## Dice Stats:\n");
        for (int i = 0; i < DiceStats.length; i++) {
            fileContent.append(DiceStats[i]).append(": ").append(DiceStats_Input[i]).append("\n");
        }

        fileContent.append("\n## General Info:\n");
        for (int i = 0; i < GeneralInfo.length; i++) {
            fileContent.append(GeneralInfo[i]).append(": ").append(GeneralInfo_Input[i]).append("\n");
        }

        fileContent.append("\n## Background Info:\n");
        for (int i = 0; i < BackgroundInfo.length; i++) {
            fileContent.append(BackgroundInfo[i]).append(": ").append(BackgroundInfo_Input[i]).append("\n");
        }

        try {
            Files.write(Paths.get("output.md"), fileContent.toString().getBytes());
            System.out.println("Character information has been written to output.md.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
