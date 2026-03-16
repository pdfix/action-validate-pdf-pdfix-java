package net.pdfix;

import java.io.PrintStream;

public class ConsoleProgressBar implements Runnable {

    private boolean firstOutput = false;
    private final int outputLength = 60;
    private final int percentStart = 25;
    private volatile float progress;
    private final int totalUnits;
    private volatile String title;

    private final PrintStream out;

    private final long startTime;

    private final Thread thread;
    private volatile boolean running = true;
    private volatile boolean stopped = false;

    private final Object wakeLock = new Object();

    private static final int BAR_WIDTH = 20;
    private static final long UPDATE_INTERVAL_MS = 250; // ~4 updates/sec


    // Constructors
    public ConsoleProgressBar() {
        this("Progress", 100, true);
    }

    public ConsoleProgressBar(String title) {
        this(title, 100, true);
    }

    public ConsoleProgressBar(String title, int totalUnits) {
        this(title, totalUnits, true);
    }

    public ConsoleProgressBar(String title, int totalUnits, boolean useErrorStream) {
        this.title = title;
        this.totalUnits = totalUnits;
        this.progress = 0;
        this.startTime = System.currentTimeMillis();

        this.out = useErrorStream ? System.err : System.out;

        thread = new Thread(this, "ConsoleProgressBar");
        thread.setDaemon(true);
        thread.start();
    }

    // Check if progress bar is running
    public boolean isRunning() {
        return running;
    }

    // Check if progress bar thread ended
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void run() {
        while (running) {

            printProgress(firstOutput, false);
            firstOutput = false;

            if (progress >= totalUnits) {
                running = false;
                break;
            }

            try {
                synchronized (wakeLock) {
                    wakeLock.wait(UPDATE_INTERVAL_MS);
                }
            } catch (InterruptedException ignored) {}
        }

        printProgress(firstOutput, true);
        stopped = true;
    }

    // Set absolute progress
    public void setProgress(float value) {
        progress = value;
        wake();
    }

    // Change title
    public void setTitle(String newTitle) {
        if (newTitle.length() > (percentStart - 3)) {
            System.err.println("Title for progress bar that you have chosen is longer than allowed, cutting.");
            newTitle = newTitle.substring(0, percentStart - 3);
        }
        title = newTitle;
        wake();
    }

    // Stop thread on background
    public void stop() throws InterruptedException {
        running = false;
        wake();
        waitUntilFinished();
    }

    // Increment progress
    public void update(float delta) {
        progress += delta;
        wake();
    }

    // Wait till thread finishes
    public void waitUntilFinished() throws InterruptedException {
        thread.join();
    }


    private void printProgress(boolean isFirst, boolean isLast) {
        float percent = Math.min(progress / totalUnits, 1.0f);
        int percentInt = (int) (percent * 100);
        int filled = (int) (percent * BAR_WIDTH);
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;

        //Build the output string
        StringBuilder line = new StringBuilder(80);

        if (!isFirst) {
            line.append("\r");
        }

        line.append(String.format("%s :", title));

        for (int i = line.length(); i < percentStart; i++) {
            line.append(' ');
        }

        line.append(String.format("%3d%% [", percentInt));

        for (int i = 0; i < BAR_WIDTH; i++) {
            line.append(i < filled ? '█' : '-');
        }

        line.append(String.format("] %ds", elapsed));

        int padding = Math.max(0, outputLength - line.length());
        if (padding > 0) {
            line.append(" ".repeat(padding));
        }

        if (isLast) {
            line.append("\n");
        }

        //print it
        out.print(line.toString());
    }

    private void wake() {
        synchronized (wakeLock) {
            wakeLock.notifyAll();
        }
    }
}
