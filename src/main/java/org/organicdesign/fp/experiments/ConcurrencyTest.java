package org.organicdesign.fp.experiments;

import org.organicdesign.fp.experiments.ConcurrentXform;
import org.organicdesign.fp.experiments.IntRange;

public class ConcurrencyTest {

    private static void println(String s) { System.out.println(s); }

    private static void exit(String s) {
        println(s);
        println("For help, run with no arguments");
        System.exit(-1);
    }

    public static void main(String[] args) {
        if ( (args == null) || (args.length != 2) ) {
            println("Usage:");
            println(" This test takes 2 arguments:");
            println("     range - a number between 1 and " + Integer.MAX_VALUE + " Ideally big enough for 10 seconds of processing.");
            println("             range will be doubled by this program (from negative range to positive).");
            println("     numThreads - the number of threads to use for processing");
            println("                  1 means not to start any new threads");
            System.exit(-1);
        }
        int rMax = 0;
        try {
            rMax = Integer.valueOf(args[0]);
            if (rMax < 1) {
                exit("Range parameter must be > 0");
            }
        } catch (Exception e) {
            exit("Unable to parse range parameter (must be between 1 and " + Integer.MAX_VALUE + ")");
        }

        int numThreads = 0;
        try {
            numThreads = Integer.valueOf(args[1]);
            if (numThreads < 1) {
                exit("numThreads parameter must be > 0");
            }
        } catch (Exception e) {
            exit("Unable to parse numThreads parameter (must be between 1 and " + Integer.MAX_VALUE + ")");
        }

        println("");
        IntRange range = IntRange.of( (0 - rMax), rMax);
        ConcurrentXform cx = ConcurrentXform.of(numThreads, range);
        long startTime = System.currentTimeMillis();
        cx.toLinkedList();
        println("Time: " + (System.currentTimeMillis() - startTime));
        System.exit(0);
    }
}
