import java.util.Scanner;

public class radio {
    static double frequency = Math.max(0, Math.min(1, 250)); // Clamping value between 0 and 250

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        homeScreen(scanner);
    }

    public static void setFrequency(Scanner scanner) {
        System.out.println("Please enter a frequency between 0 and 250:");
        double input = scanner.nextDouble();
        frequency = Math.max(0, Math.min(input, 250)); // Clamp the value between 0 and 250
        homeScreen(scanner);
    }

    public static void dialFrequency(Scanner scanner) {
        System.out.println("Dialing frequency at: " + frequency + " MHz...");
        // Add logic for dialing frequency if needed
    }

    public static void homeScreen(Scanner scanner) {
        System.out.println("Welcome to the radio prototype");
        System.out.println("Current frequency: " + frequency);

        if(frequency > 20 && frequency < 50) {
            Kammerton(2);
        } else if(frequency > 50 && frequency < 100) {
            System.out.println("TNO");
            PlayMusic("TNO.wav");
        } else if(frequency > 100 && frequency < 150) {
            System.out.println("JDTM");
            PlayMusic("JDTM.wav");
        } else if(frequency > 150 && frequency < 200) {
            Kammerton(1);

        } else if(frequency > 200 && frequency < 250) {
            System.out.println("HWGS");
            PlayMusic("HWGS.wav");
        } else {
            System.out.println("You are listening to a station that is not categorized");
        }

        System.out.println("Press 0 to set frequency, 1 to dial frequency, or any other key to exit");

        int mode = scanner.nextInt();
        switch (mode) {
            case 0 -> setFrequency(scanner);
            case 1 -> dialFrequency(scanner);
            default -> System.out.println("Exiting...");
        }
    }

    public static void PlayMusic(String name){
        StdAudio.play(name);
    }
    public static void Kammerton(int multi){
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
}
