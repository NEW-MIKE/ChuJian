package com.glide.chujian.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketIO {
    private Socket mSock;
    private BufferedReader mBufReader;
    private OutputStream mOutStream;
    public boolean mIsTerminate = false;
    private boolean mIsConnected;
    private static final String TAG = "SocketIO";

    public final void setTerminate(boolean value) throws IOException {
        mIsTerminate = value;
        if (value) {
            if (mSock != null) {
                mSock.shutdownInput();
                mSock.shutdownOutput();
            }
        }
    }

    public boolean isTerminate(){
        return mIsTerminate;
    }
    public final boolean isConnected() {
        mIsConnected = (this.mSock != null);
        return mIsConnected;
    }

    public boolean isNetworkAlive(){
        if (mSock == null){
            return false;
        }else if (mSock.isConnected())
        {
            return true;
        }else {
            return false;
        }
    }
    public void connect(String host,int port) throws IllegalStateException, IOException {
        if (isConnected()){
            throw new IllegalStateException("Already connected!");
        }

        mSock = new Socket(host,port);
        if (mSock != null) {
            mBufReader = new BufferedReader(new InputStreamReader(mSock.getInputStream()));
            mOutStream = mSock.getOutputStream();
        }
    }

    public void disconnect() throws IOException {
        if (!isConnected()) {
            return;
        }

        mBufReader.close();
        mBufReader = null;
        mOutStream.close();
        mOutStream = null;
        if (mSock != null){
            mSock.close();
            mSock = null;
        }
    }

    public void writeLine(String s) throws IllegalStateException{
        if (!isConnected()) {
            throw new IllegalStateException("Is not connected!");
        }

        try {
            mOutStream.write((s+"\r\n").getBytes(StandardCharsets.UTF_8));
            mOutStream.flush();
        } catch (Exception e){

        }
    }

    public String readLine() throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException("Is not connected!");
        }

        String line = "";
        try {
            line = mBufReader.readLine();//工作在阻塞模式，舍弃循环
        } catch (Exception e) {
            e.printStackTrace();
            line = "";
        }

        return line;
    }
}
