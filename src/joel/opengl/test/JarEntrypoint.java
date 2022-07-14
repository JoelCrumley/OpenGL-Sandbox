package joel.opengl.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JarEntrypoint {

    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            int input = 0;
            while (true) {

                System.out.println("Select project by inputting one of the following numbers:");
                System.out.println("  1 - Basic Grid");
                System.out.println("  2 - Mandelbrot Zoom");
                System.out.println("  3 - Mandelbrot Zoom (float64)");

                String text = reader.readLine();
                try {
                    input = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (input >= 1 && input <= 3) break;

            }

            reader.close();

            if (input == 1) {
                new BasicGrid().run();
            } else if (input == 2) {
                new MandelbrotZoom(false).run();
            } else if (input == 3) {
                new MandelbrotZoom(true).run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
