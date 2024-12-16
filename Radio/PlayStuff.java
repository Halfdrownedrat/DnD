public class PlayStuff {
    public static void main(String[] args) {
        //TonSchritte();
        PlayFile("TNO.wav");
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
    public static void TonSchritte(){
        for (int i = 1; i < 10; i++) {
            Kammerton(i);
        }
    }
    public static void PlayFile(String name){
        StdAudio.play(name);
    }

}