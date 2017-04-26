package com.cooksys.cloud.selftest.monkey;

/**
 * CPU Hog algorithm for self-test
 *
 * @author Randall McClure
 */
public class CpuHogMonkey extends Thread {
    // private int maxInt = 2^31 -1;
    private final int maxInt = 1000000;

    private boolean running;

    public CpuHogMonkey() {
        super();
        setPriority(MAX_PRIORITY);
    }

    public void stopThread() {
        running = false;

    }

    public void runAllIntegers() {
        boolean res = false;
        for (int i = 0; i < maxInt; i++) {
            res = isPrime(i);
            if (res) {
                System.out.println(i);
            }

            if (!running) {
                break;
            }
        }
    }

    private boolean isPrime(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        running = true;

        while (running) {
            runAllIntegers();
        }
    }

}
