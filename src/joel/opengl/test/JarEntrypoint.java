package joel.opengl.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JarEntrypoint {

    public static void main(String[] args) {

        try {

            int input = 1;
            if (args.length != 0) {
                try {
                    input = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                }
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                while (true) {

                    System.out.println("Select project by inputting one of the following numbers:");
                    System.out.println("  1 - Basic Grid");
                    System.out.println("  2 - Mandelbrot Zoom");
                    System.out.println("  3 - Mandelbrot Zoom (float64)");
                    System.out.println("  4 - Server Test");
                    System.out.println("  5 - Client Test");
                    System.out.println("  6 - 3D Test");

                    String text = reader.readLine();
                    try {
                        input = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    if (input >= 1 && input <= 6) break;

                }
            }



            // Closing the reader also closes System.in which is annoying
//            reader.close();

            if (input == 1) {
                new BasicGrid().run();
            } else if (input == 2) {
                new MandelbrotZoom(false).run();
            } else if (input == 3) {
                new MandelbrotZoom(true).run();
            } else if (input == 4) {
                new ServerTest().run();
            } else if (input == 5) {
                new ClientTest().run();
            } else if (input == 6) {
                new Test3D().run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
