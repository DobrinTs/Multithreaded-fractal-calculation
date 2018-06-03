/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;
/**
 *
 * @author dobri
 */
public class Mandelbrot {

    static int width = 640;
    static int height = 480;

    static double min_a = -2.0;
    static double max_a = 2.0;
    static double min_b = -2.0;
    static double max_b = 2.0;

    static String outputFile = "zad17.png";

    static int numThr = 1;
    static Thread[] jobs;

    static boolean quiet = false;

    static int[] colors;
    static BufferedImage bi;

    public static void main(String[] args) {
        long timeOfStart = Calendar.getInstance().getTimeInMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s") || args[i].equals("-size")) {
                String[] parts = args[i + 1].split("x");
                width = new Integer(parts[0]);
                height = new Integer(parts[1]);
            }
            if (args[i].equals("-r") || args[i].equals("-rect")) {
                String[] parts = args[i + 1].split(":");
                min_a = new Double(parts[0]);
                max_a = new Double(parts[1]);
                min_b = new Double(parts[2]);
                max_b = new Double(parts[3]);
            }
            if (args[i].equals("-t") || args[i].equals("-tasks")) {
                numThr = new Integer(args[i + 1]);
            }
            if (args[i].equals("-o") || args[i].equals("-output")) {
                outputFile = args[i + 1];
            }
            if (args[i].equals("-q") || args[i].equals("-quiet")) {
                quiet = true;
            }
        }
        if(!quiet)
            System.out.printf("Threads used in current run: %d\n", numThr);

        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        colors = new int[640];
        for (int i = 0; i < 640; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        jobs = new Thread[numThr];

        for (int i = 1; i < numThr; i++) {

            MyRunnable r = new MyRunnable(i);
            Thread t = new Thread(r);
            t.start();
            jobs[i] = t;

        }
        new MyRunnable(0).run();
        for (int i = 1; i < numThr; i++) {

            try {
                jobs[i].join();
            } catch (InterruptedException e) {
            }
        }

        long timeOfEnd = Calendar.getInstance().getTimeInMillis();
        System.out.println("Time of calculation was: " 
                + (timeOfEnd - timeOfStart) + " ms.");
        if(!quiet)
            System.out.println("Exporting to file: " + outputFile);
        
        try {
            ImageIO.write(bi, "PNG", new File(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!quiet)
            System.out.println("DONE");
    }
    
}
