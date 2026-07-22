package com.eriklievaart.ws.dev;

public class DevCliException extends RuntimeException {

    public DevCliException(String msg) {
        super(msg);
    }

    public static void on(boolean condition, String message) throws DevCliException {
        if (condition) {
            throw new DevCliException(message);
        }
    }

    public static void unless(boolean condition, String message) throws DevCliException {
        if (!condition) {
            throw new DevCliException(message);
        }
    }
}
