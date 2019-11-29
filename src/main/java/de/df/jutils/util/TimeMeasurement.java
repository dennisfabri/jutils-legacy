/*
 * Created on 24.05.2005
 */
package de.df.jutils.util;

import java.io.PrintStream;

/**
 * TimeMeasurement is a class for measuring time periods of a long running task
 * and its subtasks. The maximum levels of subtasks has to be specified, because
 * times will be stored in an array for lower overhead of this class.
 */
public class TimeMeasurement {

    private PrintStream out = null;
    private long[]      nano;
    private int         level;

    public TimeMeasurement(PrintStream out, int levels) {
        this.out = out;
        nano = new long[levels];
        level = 0;
        update();
    }

    /**
     * Starts the measuring of a new subtask. Increases the level of tasks.
     * 
     * @param task
     */
    public void start(String task) {
        println(task + "...");
        start();
    }

    /**
     * Starts the measuring of a new subtask. Increases the level of tasks.
     */
    public void start() {
        update();
        level++;
        update();
    }

    /**
     * Updates the current time
     */
    private void update() {
        nano[level] = System.nanoTime();
    }

    /**
     * Notifies that the current subtask has finished. Assumes that the next
     * subtask of the same level starts immediatly.
     * 
     * @param task
     */
    public void finish(String task) {
        double x = ((double) System.nanoTime() - nano[level]) / 1000000.0;
        x = Math.floor(x) / 1000.0;
        println(task + ": " + x + " seconds");
        update();
    }

    /**
     * Notifies that that the task of the next higher level has finished.
     * Reduces the level of subtasks and assumes that the next subtast of the
     * same level starts immediatly.
     * 
     * @param task
     */
    public void quit(String task) {
        level--;
        finish(task);
    }

    private void println(String s) {
        if (out != null) {
            for (int x = 0; x < level; x++) {
                out.print("  ");
            }
            out.println(s);
        }
    }
}