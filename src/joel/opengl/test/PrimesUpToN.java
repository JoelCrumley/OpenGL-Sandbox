package joel.opengl.test;

import joel.opengl.util.TimerUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrimesUpToN {

    private static final int MAX = 100;

    public void run() {

        TimerUtil.start();

        boolean[] arr = new boolean[MAX]; // arr[i] == true => i is not prime.
        for (int i = 2; i < MAX; i++) {
            if (arr[i]) continue;
            for (int n = 2*i; n >= 0 && n < MAX; n += i) arr[n] = true;
        }

        TimerUtil.end("Generated all primes up to " + MAX);

        int primes = 0;
        String path = "primesUpTo" + MAX + ".txt";
        try {

            File file = new File(path);
            file.delete();
            file.createNewFile();
            FileWriter fw = new FileWriter(file);

            int milestone = MAX / 100;
            int nextMilestone = milestone;

            TimerUtil.start(TimerUtil.Type.B);
            for (int p = 2; p < MAX; p++) {
                if (arr[p]) continue;
                primes++;
                fw.write(p + "\n");
                if (p > nextMilestone) {
                    TimerUtil.end(TimerUtil.Type.B, "Processed " + (p / milestone) + "% of primes");
                    nextMilestone += milestone;
                }
            }

            TimerUtil.start(TimerUtil.Type.B, "Flushing...");
            fw.flush();
            fw.close();
            TimerUtil.end(TimerUtil.Type.B, "Finished flushing");

        } catch (IOException e) {
            e.printStackTrace();
        }

        TimerUtil.end("Wrote " + primes + " primes to " + path);

    }

    public PrimesUpToN() { }
    public static void main(String[] args) {
        new PrimesUpToN().run();
    }

}
