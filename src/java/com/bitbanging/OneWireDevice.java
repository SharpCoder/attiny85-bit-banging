package com.bitbanging;

/*
This is used to communicate over my proprietary one-wire PWM
binary signal mechanism. You can read a single, 10 bit number from any
GPIO pin on the raspberry pi.
 */
public class OneWireDevice {

    final long TIMING = 32;

    final private GPIO primary;
    private boolean paused = true;
    private int value;

    public OneWireDevice(int pin) {
        primary = new GPIO(pin);
        primary.export();
        primary.setAsInput(true);
        this.startThread();
    }

    public int getValue() {
        return value;
    }

    private void startThread() {
        Thread fresh = new Thread(() -> {

            Integer comm_bit;
            Integer pos = 0;
            int val = 0;
            boolean sync = false;

            while(true) {

                try {
                    comm_bit = primary.read();

                    if (!sync) {
                        if (comm_bit != 1) {
                            pos = 1;
                            Thread.sleep(0, 50);
                        } else if (pos++ >= 10) {
                            System.out.println("Synced");
                            sync = true;
                            pos = 0;
                            Thread.sleep(TIMING);
                        } else {
                            Thread.sleep(TIMING);
                        }
                    } else {
                        if (comm_bit == 1) {
                            val = val | (0x01 << pos);
                        }

                        if (++pos >= 10) {
                            this.value = val;
                            System.out.println("\n" + val);
                            val = 0;
                            pos = 0;
                            sync = false;
                        } else {
                            Thread.sleep(TIMING);
                        }
                    }
                } catch (Exception error) {
                    System.out.println("Error! " + error.getMessage());
                }
            }
        });
        fresh.start();
    }

    public void start() {
        paused = false;
    }

    public void stop() {
        paused = true;
    }

}