package com.frida;

import java.util.concurrent.TimeUnit;
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("[*] Checking status of someTest()...");
        try {
            while(true) {
                boolean result = someTest();
                if(result) {
                    System.out.println("[*] Congrats, result has been changed!");
                }
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) { }
    }
    public static boolean someTest() {
        return false;
    }
}
