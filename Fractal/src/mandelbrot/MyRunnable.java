/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import org.apache.commons.math3.complex.Complex;
/**
 *
 * @author dobri
 */
public class MyRunnable implements Runnable{
    int thrIndex;

    public MyRunnable(int thrIndex) {
        this.thrIndex = thrIndex;
    }

    public static Complex z_iter(Complex z, Complex c) {
        //return z.multiply(z).add(c).exp();
        return z.multiply(c).cos().exp();
    }

    public static int z_check(Complex c) {

        Complex z0 = new Complex(0.0, 0.0);
        Complex z_prev = z0;
        Complex z_i = null;

        int steps = 0;

        Double d = null;

        for (int i = 0; i < 640; i++) {
            z_i = z_iter(z_prev, c);
            z_prev = z_i;
            d = new Double(z_prev.getReal());
            if (d.isInfinite() || d.isNaN()) {
                steps = i;
                break;
            }
        }
        return steps;
    }

    public void run() {
        if(!Mandelbrot.quiet)
            System.out.printf("Thread %d is starting\n", thrIndex);
        
        
        try {
            for (int row = thrIndex; row < Mandelbrot.height; row+= Mandelbrot.numThr) {
                for (int col = 0; col < Mandelbrot.width; col++) {

                    double c_re = (col - Mandelbrot.width / 2)
                            * (Mandelbrot.max_a - Mandelbrot.min_a) / Mandelbrot.width
                            + (Math.abs(Mandelbrot.max_a) - Math.abs(Mandelbrot.min_a)) / 2;
                    double c_im = (row - Mandelbrot.height / 2)
                            * (Mandelbrot.max_b - Mandelbrot.min_b) / Mandelbrot.height
                            + (Math.abs(Mandelbrot.max_b) - Math.abs(Mandelbrot.min_b)) / 2;

                    int r = z_check(new Complex(c_re, c_im));

                    if (r < 640) {
                        Mandelbrot.bi.setRGB(col, row, Mandelbrot.colors[r]);
                    } else {
                        Mandelbrot.bi.setRGB(col, row, 0);
                    }

                }
            }
        } catch (Exception e) {
        }
        
        if(!Mandelbrot.quiet)
            System.out.printf("Thread %d is finishing\n", thrIndex);
    }
}
