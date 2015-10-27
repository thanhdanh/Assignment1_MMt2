/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author Danh Cooper
 */
public class sendThread implements Runnable {

    Socket client;
    JTable tableList;
    public String[] b = new String[10];
    int command;
    public String c;
    public int stt, num;
    public sendThread(Socket so, String[] danhsachcapnhat, int cmd) throws IOException {

        this.client = so;
        command = cmd;
        b = danhsachcapnhat;
    }

    public sendThread(Socket so, int cmd) {
        this.client = so;
        command = cmd;
    }
    public sendThread(Socket so,String b,  int cmd) {
        this.client = so;
        command = cmd;
        this.c= b;
    }
    public sendThread(Socket so,int stt, int num,  int cmd) {
        this.client = so;
        command = cmd;
        this.stt=stt;
        this.num =num;
    }
    @Override
    public void run() {
        try {
            PrintStream ps;
            ps = new PrintStream(client.getOutputStream());
            switch (command) {

                case 0: {
                    ps.println("NEED_UPDATE END");
                    System.out.println("Send NEED_UPDATE request");
                    break;
                }
                case 1: { // CASE UPDATE CLIENT LIST-- sent FROM server 
                    int i;
                    String tmp = "";
                    for (i = 0; i < b.length; i++) {
                        tmp += b[i];
                        if (i != b.length - 1) {
                            tmp += ";";
                        }
                    }
                    ps.println("UPDATE " + tmp + " END");

                    break;
                }
                case 2: {
                    ps.println("BYE END");
                    break;
                }
                case 3: {
                    int i;
                    String tmp = "";
                    for (i = 0; i < b.length; i++) {
                        tmp += b[i];
                        if (i != b.length - 1) {
                            tmp += ",";
                        }
                    }
                    ps.println("LIVE_APPEAR " + tmp + " END");
                    System.out.println("sent from client live_appear");
                    break;
                }
                case 4: {
                    ps.println("NEED_UP_LI END");
                    System.out.println("sent from client need update live audio");

                    break;
                }
                case 5: {
                    int i;
                    String tmp = "";

                    for (i = 0; i < b.length; i++) {
                        tmp += b[i];
                        if (i != b.length - 1) {
                            tmp += ";";
                        }
                    }
                    ps.println("LI_UPDATE " + tmp + " END");
                    System.out.println("sent from server live list!!!");
                    break;
                }
                 case 6:{                   
                    ps.println("LIVE_DISAPPEAR END");
                    break;
                }
                case 7:{
                    ps.println("CALL "+c+" END");
                    break;
                }
                case 8:{
                    ps.println("OK END");
                    //System.out.println(" Chap nhan dc r");
                    break;
                }
                case 9:{
                    ps.println("NO END");

                    break;
                }
                case 10: {
                    ps.println("READY "+c+" END");
                    break;
                }
                case 11: {
                    ps.println("INVITE "+c+" END");
                    break;
                }
                case 12: {
                    ps.println("ACCEPT "+c+" END");
                    break;
                }
                case 13: {
                    ps.println("CONF "+stt + " "+num +" END");
                    break;
                }
                case 14:{
                    ps.println("CANCEL END");
                    break;
                }
                case 15:{
                    ps.println("NO_ANS END");
                    break;
                }
                    

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error tai send: " + e);
        }

    }
}
