// Goal:
// Mute files when not set to frequency, is more or less not possible
// Setting the current time stamp might to the clip if muting is not possible
// CurrentTime in sec mod FilesTime should set it to the current clip



import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.UnsupportedAudioFileException;


public class radio {
    // count variable tracks time. The tracking itself is done in the class CountUP
    static int count;
    public static void SetCount(int counterFromCount){
        count = counterFromCount;
    }

    static double frequency = Math.max(0, Math.min(1, 250)); // Clamping value between 0 and 250
    public static void main(String[] args) {
        // Startup Counter Threat
        Thread countUP = new Thread(new CountUP());
        countUP.setDaemon(true);
        countUP.start();
        Scanner scanner = new Scanner(System.in);

        // Startup Radio
        homeScreen(scanner);
    }

    public static void setFrequency(Scanner scanner) {
        System.out.println("Please enter a frequency between 0 and 250:");
        double input = scanner.nextDouble();
        frequency = Math.max(0, Math.min(input, 250)); // Clamp the value between 0 and 250
        homeScreen(scanner);
    }
    
    public static void dialFrequency() {
        System.out.println("Q to dial -10, E to dial +10");
        MyFrame frame = new MyFrame();
        frame.setKeyInputListener(keyChar -> {
            switch (keyChar) {
                case 'q':
                    frequency -= 10;
                    System.out.println(frequency);
                    break;
                case 'e':
                    frequency += 10;
                     System.out.println(frequency);
                    break;
                default:
                    System.out.println("Wrong Input: " + keyChar);
                    break;
            }
            CheckFrequency();
        });
    }
    
    public static void homeScreen(Scanner scanner) {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Current frequency: " + frequency);
        System.out.println("Press 0 to set frequency, 1 to dial frequency, or any other key to exit");
        CheckFrequency();

        int mode = scanner.nextInt();
        switch (mode) {
            case 0 -> setFrequency(scanner);
            case 1 -> dialFrequency();
            default -> System.out.println("Exiting...");
        }
    }
    
    public static void PlayMusic(String name){
        String musicPath = "./Audio Files/" + name;
        // StdAudio.play(musicPath);
        // Play In FG
        StdAudio.stopInBackground();// Stop the file to avoid ungodly mix of all audio files, not the perfect solution
        StdAudio.playInBackground(musicPath);// Plays file in background, allows interacting while file is running
        //Skip some seconds based on the current time mod length of the clip
        try {
            skipSeconds(count, musicPath);
        } catch (UnsupportedAudioFileException e) {}
    }
    
    public static void Ton(int multi){
        int sps = 44100;
        int hz = 110 * multi;
        double duration = 3.0;
        int N = (int) (sps * duration);
        double[] a = new double[N+1];   
        for (int i = 0; i <= N; i++){
            a[i] = Math.sin(2 * Math.PI * hz * i / sps);
        }
        StdAudio.play(a);
    }
    
    public static void CheckFrequency(){
        if(frequency > 20 && frequency <= 50) {
            Ton(2);
        } else if(frequency > 50 && frequency <= 100) {
            System.out.println("TNO");
            PlayMusic("TNO.wav");
        } else if(frequency > 100 && frequency <= 150) {
            System.out.println("JDTM");
            PlayMusic("JDTM.wav");
        } else if(frequency > 150 && frequency <= 200) {
            Ton(1);

        } else if(frequency > 200 && frequency <= 250) {{}
            System.out.println("HWGS");
            PlayMusic("HWGS.wav");
        } else {
            System.out.println("You are listening to a station that is not categorized");
        }
    }

    public static void skipSeconds(int secs, String filePath) throws UnsupportedAudioFileException {
        double length = 1;
        File file = new File(filePath); // Convert Filepath to File Object/Variable?!
        try {
            length = StdAudio.getWavFileDuration(file); // Get the length of the audio file in seconds
        } catch (UnsupportedAudioFileException | IOException e) {}
        
        int microSec = secs * 1000000; // Convert to micro Seconds
        double pos = microSec % length;
        // Code for Setting to current pos
        
    }
    
}
// Count Up every second using the Runnable interface, no clue how exactly that works tbh
class CountUP implements Runnable{
    int count = 0;

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(1000); // Sleep for one second (Pausing Threat)
            } catch (Exception e) {} 
            count++;
            //System.err.println(count); // Keine Ahnung wieso das auf standart error printed
            // Resetting count if it gets to high
            if (count >= Integer.MAX_VALUE - 10) {
                count = 0;
            }
            radio.SetCount(count);// Set the count value in the radio class to the counter in the CountUP class/Interface...
        }

    }

}