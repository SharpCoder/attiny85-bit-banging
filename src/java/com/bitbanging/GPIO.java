package com.bitbanging;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GPIO {

    final int gpioPin;
    final String exportPath;
    final String unexportPath;
    final String gpioDirectionPath;
    final String gpioPath;

    private Boolean asInput = null;
    private InputStream gpioIn;

    public GPIO(int rpiGPIO) {
        gpioPin = rpiGPIO;
        exportPath = "/sys/class/gpio/export";
        unexportPath = "/sys/class/gpio/unexport";
        gpioPath = "/sys/class/gpio/gpio" + rpiGPIO + "/value";
        gpioDirectionPath = "/sys/class/gpio/gpio" + rpiGPIO + "/direction";
    }

    public Integer read() {
        try {
            gpioIn = new FileInputStream(gpioPath);

            String strResult = org.apache.commons.io.IOUtils.toString(gpioIn, StandardCharsets.UTF_8);
            strResult = strResult.trim();
            Integer result = Integer.parseInt(strResult);
            gpioIn.close();
            return result;
        }catch (Exception error) {
            System.out.println("Error! " + error.getMessage());
            return -1;
        }
    }

    public void export() {
        try {
            OutputStream os = new FileOutputStream(exportPath);
            String pin = "" + gpioPin;
            os.write(pin.getBytes());
            os.flush();
            os.close();
        }catch(Exception error) {
            System.out.println("Error! " + error.getMessage());
        }
    }

    public void unexport() {
        try {
            OutputStream os = new FileOutputStream(unexportPath);
            String pin = "" + gpioPin;
            os.write(pin.getBytes());
            os.flush();
            os.close();
        }catch(Exception error) {
            System.out.println("Error! " + error.getMessage());
        }
    }

    public void setAsInput(Boolean isInput) {
        try {
            OutputStream os = new FileOutputStream(gpioDirectionPath);
            if (!isInput) {
                os.write("out".getBytes());
            } else {
                os.write("in".getBytes());
            }
            os.flush();
            os.close();
            asInput = isInput;
        }catch(Exception error) {
            System.out.println("Error! " + error.getMessage());
        }
    }

    public void setValue(boolean high) throws Exception {
        if (asInput == false) {
            try {
                OutputStream os = new FileOutputStream(gpioPath);
                if (high) {
                    os.write("1".getBytes());
                } else {
                    os.write("0".getBytes());
                }
                os.flush();
                os.close();
            }catch(Exception error) {
                System.out.println("Error! " + error.getMessage());
            }
        } else {
            throw new Exception("gpio" + gpioPin + " is not an output");
        }
    }


}

