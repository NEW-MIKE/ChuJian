package com.glide.chujian.util;

public class GuiderException extends Exception{
    private String st;

    public GuiderException(String st) {
        super(st);
        this.st = st;
    }
}
