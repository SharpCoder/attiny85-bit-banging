package com.bitbanging;

public class Service {

    public static int main(String[] args) {
        OneWireDevice device = new OneWireDevice(26);
        device.start();

        while(true) {
            int v = device.getValue();
            System.out.println(v);
            try {
                Thread.sleep(100);
            } catch(Exception error) {

            }
        }
    }

}
