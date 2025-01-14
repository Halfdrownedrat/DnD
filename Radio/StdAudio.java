/******************************************************************************
 *  Compilation:  javac StdAudio.java
 *  Execution:    java StdAudio
 *  Dependencies: none
 *
 *  Simple library for reading, writing, and manipulating audio.
 *  Extenden by Halfdrownedrat
 ******************************************************************************/

 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream; 
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.util.LinkedList;
 import javax.sound.sampled.AudioFileFormat;
 import javax.sound.sampled.AudioFormat;
 import javax.sound.sampled.AudioInputStream;
 import javax.sound.sampled.AudioSystem;
 import javax.sound.sampled.DataLine;
 import javax.sound.sampled.LineUnavailableException;
 import javax.sound.sampled.SourceDataLine;
 import javax.sound.sampled.UnsupportedAudioFileException;
 
  /* 
   * @author Robert Sedgewick
  *  @author Kevin Wayne
   * @author Halfdrownedrat
  */

 public final class StdAudio {
 
     /**
      *  The sample rate: 44,100 Hz for CD quality audio.
      */
     public static final int SAMPLE_RATE = 44100;
 
     private static final int BYTES_PER_SAMPLE = 2;       // 16-bit audio
     private static final int BITS_PER_SAMPLE = 16;       // 16-bit audio
     private static final int MAX_16_BIT = 32768;
     private static final int SAMPLE_BUFFER_SIZE = 4096;
 
     private static final int MONAURAL = 1;
     private static final int STEREO = 2;
     private static final boolean LITTLE_ENDIAN = false;
     private static final boolean BIG_ENDIAN    = true;
     private static final boolean SIGNED        = true;
     private static final boolean UNSIGNED      = false;
 
 
     private static SourceDataLine line;   // to play the sound
     private static byte[] buffer;         // our internal buffer
     private static int bufferSize = 0;    // number of samples currently in internal buffer
 
     // queue of background Runnable objects
     private static LinkedList<BackgroundRunnable> backgroundRunnables = new LinkedList<>();
 
     // for recording audio
     private static QueueOfDoubles recordedSamples = null;
     private static boolean isRecording = false;
 
     private StdAudio() {
         // can not instantiate
     }
 
     // static initializer
     static {
         init();
     }
 
     // open up an audio stream
     private static void init() {
         try {
             // 44,100 Hz, 16-bit audio, monaural, signed PCM, little endian
             AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONAURAL, SIGNED, LITTLE_ENDIAN);
             DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
             line = (SourceDataLine) AudioSystem.getLine(info);
             line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
 
             // the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
             // it gets divided because we can't expect the buffered data to line up exactly with when
             // the sound card decides to push out its samples.
             buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
         }
         catch (LineUnavailableException e) {
             System.out.println(e.getMessage());
         }
 
         // no sound gets made before this call
         line.start();
     }
 
     // get an AudioInputStream object from a file
     private static AudioInputStream getAudioInputStreamFromFile(String filename) {
         if (filename == null) {
             throw new IllegalArgumentException("filename is null");
         }
 
         try {
             // first try to read file from local file system
             File file = new File(filename);
             if (file.exists()) {
                 return AudioSystem.getAudioInputStream(file);
             }
 
             // resource relative to .class file
             InputStream is1 = StdAudio.class.getResourceAsStream(filename);
             if (is1 != null) {
                 return AudioSystem.getAudioInputStream(is1);
             }
 
             // resource relative to classloader root
             InputStream is2 = StdAudio.class.getClassLoader().getResourceAsStream(filename);
             if (is2 != null) {
                 return AudioSystem.getAudioInputStream(is2);
             }
 
             // from URL (including jar file)
             URI uri = new URI(filename);
             if (uri.isAbsolute()) {
                 URL url = uri.toURL();
                 return AudioSystem.getAudioInputStream(url);
             }
             else throw new IllegalArgumentException("could not read audio file '" + filename + "'");
         }
         catch (IOException | URISyntaxException e) {
             throw new IllegalArgumentException("could not read audio file '" + filename + "'", e);
         }
         catch (UnsupportedAudioFileException e) {
             throw new IllegalArgumentException("file of unsupported audio file format: '" + filename + "'", e);
         }
     }
 
     /**
      * Sends any queued samples to the sound card.
      */
     public static void drain() {
         if (bufferSize > 0) {
             line.write(buffer, 0, bufferSize);
             bufferSize = 0;
         }
         line.drain();
     }
 
 
     /**
      * Closes standard audio.
      */
 /*
     public static void close() {
         drain();
         line.stop();
     }
 */
     /**
      * Writes one sample (between –1.0 and +1.0) to standard audio.
      * If the sample is outside the range, it will be clipped
      * (rounded to –1.0 or +1.0).
      *
      * @param  sample the sample to play
      * @throws IllegalArgumentException if the sample is {@code Double.NaN}
      */
     public static void play(double sample) {
         if (Double.isNaN(sample)) throw new IllegalArgumentException("sample is NaN");
 
         // clip if outside [-1, +1]
         if (sample < -1.0) sample = -1.0;
         if (sample > +1.0) sample = +1.0;
 
         // save sample if recording
         if (isRecording) {
             recordedSamples.enqueue(sample);
         }
 
         // convert to bytes
         short s = (short) (MAX_16_BIT * sample);
         if (sample == 1.0) s = Short.MAX_VALUE;   // special case since 32768 not a short
         buffer[bufferSize++] = (byte) s;
         buffer[bufferSize++] = (byte) (s >> 8);   // little endian
 
         // send to sound card if buffer is full
         if (bufferSize >= buffer.length) {
             line.write(buffer, 0, buffer.length);
             bufferSize = 0;
         }
     }
 
     /**
      * Writes the array of samples (between –1.0 and +1.0) to standard audio.
      * If a sample is outside the range, it will be clipped.
      *
      * @param  samples the array of samples to play
      * @throws IllegalArgumentException if any sample is {@code Double.NaN}
      * @throws IllegalArgumentException if {@code samples} is {@code null}
      */
     public static void play(double[] samples) {
         if (samples == null) throw new IllegalArgumentException("argument to play() is null");
         for (int i = 0; i < samples.length; i++) {
             play(samples[i]);
         }
     }
 
     /**
      * Plays an audio file (in WAVE, AU, AIFF, or MIDI format) and waits for it to finish.
      * The file extension must be either {@code .wav}, {@code .au},
      * or {@code .aiff}.
      *
      * @param filename the name of the audio file
      * @throws IllegalArgumentException if unable to play {@code filename}
      * @throws IllegalArgumentException if {@code filename} is {@code null}
      */
     public static void play(String filename) {
 
         // may not work for streaming file formats
         if (isRecording) {
             double[] samples = read(filename);
             for (double sample : samples)
                 recordedSamples.enqueue(sample);
         }
 
         AudioInputStream ais = getAudioInputStreamFromFile(filename);
         SourceDataLine line = null;
         int BUFFER_SIZE = 4096; // 4K buffer
         try {
             AudioFormat audioFormat = ais.getFormat();
             DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
             line = (SourceDataLine) AudioSystem.getLine(info);
             line.open(audioFormat);
             line.start();
             byte[] samples = new byte[BUFFER_SIZE];
             int count;
             while ((count = ais.read(samples, 0, BUFFER_SIZE)) != -1) {
                 line.write(samples, 0, count);
             }
         }
         catch (IOException | LineUnavailableException e) {
             System.out.println(e);
         }
         finally {
             if (line != null) {
                 line.drain();
                 line.close();
             }
         }
     }
 
 
     /**
      * Reads audio samples from a file (in WAVE, AU, AIFF, or MIDI format)
      * and returns them as a double array with values between –1.0 and +1.0.
      * The file extension must be either {@code .wav}, {@code .au},
      * or {@code .aiff}.
      *
      * @param  filename the name of the audio file
      * @return the array of samples
      */
     public static double[] read(String filename) {
         // 4K buffer (must be a multiple of 2 for monaural or 4 for stereo)
         int READ_BUFFER_SIZE = 4096;
 
         // create AudioInputStream from file
         AudioInputStream fromAudioInputStream = getAudioInputStreamFromFile(filename);
         AudioFormat fromAudioFormat = fromAudioInputStream.getFormat();
 
         // normalize AudioInputStream to 44,100 Hz, 16-bit audio, monaural, signed PCM, little endian
         // https://docs.oracle.com/javase/tutorial/sound/converters.html
         AudioFormat toAudioFormat = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONAURAL, SIGNED, LITTLE_ENDIAN);
         if (!AudioSystem.isConversionSupported(toAudioFormat, fromAudioFormat)) {
             throw new IllegalArgumentException("system cannot convert from " + fromAudioFormat + " to " + toAudioFormat);
         }
         AudioInputStream toAudioInputStream = AudioSystem.getAudioInputStream(toAudioFormat, fromAudioInputStream);
 
         // extract the audio data and convert to a double[] with each sample between -1 and +1
         try {
             QueueOfDoubles queue = new QueueOfDoubles();
             byte[] bytes = new byte[READ_BUFFER_SIZE];
             int count;
             while ((count = toAudioInputStream.read(bytes, 0, READ_BUFFER_SIZE)) != -1) {
 
                 // little endian, monaural
                 for (int i = 0; i < count/2; i++) {
                     double sample = ((short) (((bytes[2*i+1] & 0xFF) << 8) | (bytes[2*i] & 0xFF))) / ((double) MAX_16_BIT);
                     queue.enqueue(sample);
                 }
 
                 // little endian, stereo (perhaps, for a future version that supports stereo)
                 /*
                 for (int i = 0; i < count/4; i++) {
                     double left  = ((short) (((bytes[4*i + 1] & 0xFF) << 8) | (bytes[4*i + 0] & 0xFF))) / ((double) MAX_16_BIT);
                     double right = ((short) (((bytes[4*i + 3] & 0xFF) << 8) | (bytes[4*i + 2] & 0xFF))) / ((double) MAX_16_BIT);
                     double sample = (left + right) / 2.0;
                     queue.enqueue(sample);
                 }
                 */
             }
             toAudioInputStream.close();
             fromAudioInputStream.close();
             return queue.toArray();
         }
         catch (IOException ioe) {
             throw new IllegalArgumentException("could not read audio file '" + filename + "'", ioe);
         }
     }
 
     /**
      * Saves the audio samples as an audio file (using WAV, AU, or AIFF format).
      * The file extension must be either {@code .wav}, {@code .au},
      * or {@code .aiff}.
      * The format uses a sampling rate of 44,100 Hz, 16-bit audio,
      * monaural, signed PCM, ands little Endian.
      *
      * @param  filename the name of the audio file
      * @param  samples the array of samples
      * @throws IllegalArgumentException if unable to save {@code filename}
      * @throws IllegalArgumentException if {@code samples} is {@code null}
      * @throws IllegalArgumentException if {@code filename} is {@code null}
      * @throws IllegalArgumentException if {@code filename} is the empty string
      * @throws IllegalArgumentException if {@code filename} extension is not
      *         {@code .wav}, {@code .au}, or {@code .aiff}.
      */
     public static void save(String filename, double[] samples) {
         if (filename == null) {
             throw new IllegalArgumentException("filename is null");
         }
         if (samples == null) {
             throw new IllegalArgumentException("samples[] is null");
         }
         if (filename.length() == 0) {
             throw new IllegalArgumentException("argument to save() is the empty string");
         }
 
         // assumes 16-bit samples with sample rate = 44,100 Hz
         // use 16-bit audio, monaural, signed PCM, little Endian
         AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, MONAURAL, SIGNED, LITTLE_ENDIAN);
         byte[] data = new byte[2 * samples.length];
         for (int i = 0; i < samples.length; i++) {
             int temp = (short) (samples[i] * MAX_16_BIT);
             if (samples[i] == 1.0) temp = Short.MAX_VALUE;   // special case since 32768 not a short
             data[2*i + 0] = (byte) temp;
             data[2*i + 1] = (byte) (temp >> 8);   // little endian
         }
 
 
         // now save the file
         try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             AudioInputStream ais = new AudioInputStream(bais, format, samples.length)) {
 
             if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                 if (!AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, ais)) {
                     throw new IllegalArgumentException("saving to WAVE file format is not supported on this system");
                 }
                 AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
             }
             else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                 if (!AudioSystem.isFileTypeSupported(AudioFileFormat.Type.AU, ais)) {
                     throw new IllegalArgumentException("saving to AU file format is not supported on this system");
                 }
                 AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
             }
             else if (filename.endsWith(".aif") || filename.endsWith(".aiff") || filename.endsWith(".AIF") || filename.endsWith(".AIFF")) {
                 if (!AudioSystem.isFileTypeSupported(AudioFileFormat.Type.AIFF, ais)) {
                     throw new IllegalArgumentException("saving to AIFF file format is not supported on this system");
                 }
                 AudioSystem.write(ais, AudioFileFormat.Type.AIFF, new File(filename));
             }
             else {
                 throw new IllegalArgumentException("file extension for saving must be .wav, .au, or .aif");
             }
         }
         catch (IOException ioe) {
             throw new IllegalArgumentException("unable to save file '" + filename + "'", ioe);
         }
     }
 
     /**
      * Stops the playing of all audio files in background threads.
      */
     public static synchronized void stopInBackground() {
         for (BackgroundRunnable runnable : backgroundRunnables) {
             runnable.stop();
         }
         backgroundRunnables.clear();
     }
 
     /**
      * Plays an audio file (in WAVE, AU, AIFF, or MIDI format) in its own
      * background thread. Multiple audio files can be played simultaneously.
      * The file extension must be either {@code .wav}, {@code .au},
      * or {@code .aiff}.
      *
      * @param filename the name of the audio file
      * @throws IllegalArgumentException if unable to play {@code filename}
      * @throws IllegalArgumentException if {@code filename} is {@code null}
      */
     public static synchronized void playInBackground(final String filename) {
         BackgroundRunnable runnable = new BackgroundRunnable(filename);
         new Thread(runnable).start();
         backgroundRunnables.add(runnable);
     }
 
     private static class BackgroundRunnable implements Runnable {
         private volatile boolean exit = false;
         private final String filename;
 
         public BackgroundRunnable(String filename) {
             this.filename = filename;
         }
 
         // https://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
         // play a wav or aif file
         // javax.sound.sampled.Clip fails for long clips (on some systems)
         public void run() {
             AudioInputStream ais = getAudioInputStreamFromFile(filename);
 
             SourceDataLine line = null;
             int BUFFER_SIZE = 4096; // 4K buffer
 
             try {
                 AudioFormat audioFormat = ais.getFormat();
                 DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                 line = (SourceDataLine) AudioSystem.getLine(info);
                 line.open(audioFormat);
                 line.start();
                 byte[] samples = new byte[BUFFER_SIZE];
                 int count;
                 while (!exit && (count = ais.read(samples, 0, BUFFER_SIZE)) != -1) {
                     line.write(samples, 0, count);
                 }
             }
             catch (IOException | LineUnavailableException e) {
                 System.out.println(e);
             }
             finally {
                 if (line != null) {
                     line.drain();
                     line.close();
                 }
                 backgroundRunnables.remove(this);
             }
         }
 
         public void stop() {
             exit = true;
         }
     }
 
 
    
     /**
      * Turns on audio recording.
      */
     public static void startRecording() {
         if (!isRecording) {
             recordedSamples = new QueueOfDoubles();
             isRecording = true;
          }
          else {
              throw new IllegalStateException("startRecording() must not be called twice in a row");
          }
     }
 
     /**
      * Turns off audio recording and returns the recorded samples.
      * @return the array of recorded samples
      */
     public static double[] stopRecording() {
         if (isRecording) {
             double[] results = recordedSamples.toArray();
             isRecording = false;
             recordedSamples = null;
             return results;
         }
         else {
             throw new IllegalStateException("stopRecording() must be called after calling startRecording()");
         }
     }
 
 
    /***************************************************************************
     * Helper class for reading and recording audio.
     ***************************************************************************/
     private static class QueueOfDoubles {
         private static final int INIT_CAPACITY = 16;
         private double[] a;   // array of doubles
         private int n;        // number of items in queue
 
         // create an empty queue
         public QueueOfDoubles() {
             a = new double[INIT_CAPACITY];
             n = 0;
         }
 
         // resize the underlying array holding the items
         private void resize(int capacity) {
             assert capacity >= n;
             double[] temp = new double[capacity];
             for (int i = 0; i < n; i++)
                 temp[i] = a[i];
             a = temp;
         }
 
         // enqueue item onto the queue
         public void enqueue(double item) {
             if (n == a.length) resize(2*a.length);    // double length of array if necessary
             a[n++] = item;                            // add item
         }
 
 
         // number of items in queue
         public int size() {
             return n;
         }
 
         // return the items as an array of length n
         public double[] toArray() {
             double[] result = new double[n];
             for (int i = 0; i < n; i++)
                 result[i] = a[i];
             return result;
         }
 
     }

     // Custom Stuff
     //
     //
     public static double getWavFileDuration(File file) throws UnsupportedAudioFileException, IOException {
        // Open the audio file
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {

            // Get the format of the audio file
            AudioFormat format = audioInputStream.getFormat(); // Format is only .wav, so it could be done more effictly

            // Get the number of frames in the audio file
            long frames = audioInputStream.getFrameLength();

            // Calculate the duration in seconds
            float frameRate = format.getFrameRate();
            double durationInSeconds = (frames / frameRate);

            return durationInSeconds;
        }
    }
    

 }