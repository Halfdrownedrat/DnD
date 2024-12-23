// Goal:
// Mute files when not set to frequency, is more or less not possible
// Setting the current time stamp might to the clip if muting is not possible
// CurrentTime in sec mod FilesTime should set it to the current clip



import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class radio {
    static Clip currentClip = null;
    static String oldFile = "DoNotExist"; // Setting it to null causes problems becouse comparing with null does not work

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
                case 'q' -> {
                    frequency -= 10;
                    System.out.println(frequency);
                }
                case 'e' -> {
                    frequency += 10;
                    System.out.println(frequency);
                }
                default -> System.out.println("Wrong Input: " + keyChar);
            }
            CheckFrequency();
        });
    }
    
    public static void homeScreen(Scanner scanner) {
        clearScreen();
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Current frequency: " + frequency);
        System.out.println("Press 0 to set frequency, 1 to dial frequency, or any other key to exit");
        System.out.println("------------------------------------------------------------------------");
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
        //StdAudio.playInBackground(musicPath);// Plays file in background, allows interacting while file is running
        //Skip some seconds based on the current time mod length of the clip
        try {
            skipSeconds(count, musicPath);
        } catch (UnsupportedAudioFileException e) {
            // Could do the normal play logic here if it sometimes fails, not sure though
        }
    }
    
    public static void Ton(int multi){
        // Stop current file
        oldFile = "DoNotExists";
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
        }
        // Generate sound
        int sps = 44100;
        int hz = 110 * multi;
        double duration = 1.0;
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

// Skips to the specific timestamp in seconds, modulo the file's length (Basicly a fake loop, each radio station is just a long .wav files)
// Might be good to have this in my modified StdAudio.java so I can use it in other projects
// Currently creates ungodly mixes since it can play every audio channel at once
    public static void skipSeconds(int secs, String filePath) throws UnsupportedAudioFileException {
        double length = 1;
        File file = new File(filePath); // Convert Filepath to File Object/Variable?!
        
        try {
            length = StdAudio.getWavFileDuration(file); // Get the length of the audio file in seconds
        } catch (UnsupportedAudioFileException | IOException e) {}
        
        // Calculate the exact position in the file's duration
        double pos = secs % length;  // Use modulus to keep within the length of the audio
        long microSec = (long)(pos * 1000000); // Convert to microseconds

        // Start playback at the calculated position
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioStream.getFormat();
            long skipBytes = (long)(microSec * format.getFrameSize() * format.getFrameRate() / 1000000);
            audioStream.skip(skipBytes); // Skip to the calculated position in the stream
            // Set up the clip and start playback from this point
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            // Check if old and new clip are the same, if not, close old one
            if(currentClip != null && !oldFile.equals(filePath)){
                System.out.println("Other Channel closed");
                currentClip.stop();
                currentClip.close();
            }
            // Starts up new clip and updates old variables
            Clip clip = (Clip) AudioSystem.getLine(info);
            if (!oldFile.equals(filePath)){
                clip.open(audioStream);
                clip.start(); // Start playback from the new position
                currentClip = clip;
                oldFile = filePath;
            }   
           

        } catch (IOException | LineUnavailableException e) {}
    }

    // Should clear the console
    // Does not work on Windows, IDE Enviremont and some other things, Console has to support ANSCI escape codes
    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
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