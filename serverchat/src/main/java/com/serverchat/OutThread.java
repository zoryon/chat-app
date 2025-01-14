package com.serverchat;

import java.io.DataOutputStream;

public class OutThread extends Thread{
    private DataOutputStream out;
    private String data;
    private int sleepMillisecond;


    public OutThread(DataOutputStream output, String dataToSend){
        this.out = output;
        this.data = dataToSend;
        this.sleepMillisecond = 0;
    }

    public OutThread(DataOutputStream output, String dataToSend,int sleepMillisecond){
        this.out = output;
        this.data = dataToSend;
        this.sleepMillisecond = sleepMillisecond;
    }

    //this function allow everyone who has
    //a working instance of OutThread to send data in the socket
        public void run() {
            try {
                if(this.sleepMillisecond != 0){
                    super.sleep(sleepMillisecond);
                }
                this.out.writeBytes(data + System.lineSeparator());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    } 
}
