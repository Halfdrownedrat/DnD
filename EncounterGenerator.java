import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EncounterGenerator {
    //String[] dataFiles = {"names.txt", "creatures.txt"}; // Reokace this later with a way to auto detect all .txt files in the folder

    // Data Textfiles to strings
    static String[] names = readFromFile("names.txt");
    static String[] creatures = readFromFile("creatures.txt");


    // Other
    static boolean writeToFile = false;
        
        public static void main(String[] args) {
            if(args.length > 0){
                if(args[0].equals("write")){
                    writeToFile = true;
                }
            }
        
        TakeInputs();
    }

    public static void TakeInputs(){
        System.out.println("writeToFile: " + writeToFile);
        System.out.println("0 = Random Human, 1 = Complete Random, 2 = Clear Output 9 = Exit Programm");
        Creature cr = null; //Needs to be initialized or the compiler will complain
        Random rand = new Random();

        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        switch (choice) {
            case 0 -> {
                cr = new Creature(names[rand.nextInt(names.length)], "human");
                writeToFile(cr);
            }
            case 1 -> {
                cr = new Creature(names[rand.nextInt(names.length)], creatures[rand.nextInt(creatures.length)]);
                writeToFile(cr);

            }
            case 2 -> {
                clearFile();
                System.out.println("File Cleared");
                TakeInputs();
            }   
            case 9 -> System.exit(0);
            default -> {
                System.out.println("Invalid Input");
                TakeInputs();
            }
        }
        input.close();
    }
    
    public static void writeToFile(Creature creature) {
        System.out.println(creature.printValues());
        if (!writeToFile){
            TakeInputs();
            return; // Break out if wirteToFile is false
        } 
        try {
            Files.write(Paths.get("output.txt"), "\n".getBytes(), java.nio.file.StandardOpenOption.APPEND);
            Files.write(Paths.get("output.txt"), creature.printValues().getBytes(), java.nio.file.StandardOpenOption.APPEND);
            TakeInputs();

        } catch (IOException e) {}
    }

    public static void clearFile() {
        try {
            Files.write(Paths.get("output.txt"), "".getBytes());
        } catch (IOException e) {}
    }

    public static String[] readFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            return lines.toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }
}