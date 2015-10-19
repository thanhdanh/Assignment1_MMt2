/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.JTable;

/**
 *
 * @author Danh Cooper
 */
public class sendThread implements Runnable {

    PrintStream ps;
    Socket client;
    JTable tableList;
    public String[] b = new String[10];
    int command;

    public sendThread(Socket so, String[] danhsachcapnhat, int cmd) throws IOException {

        this.client = so;
        command = cmd;
        b = danhsachcapnhat;
    }

    public sendThread(Socket so, int cmd) {
        this.client = so;
        command = cmd;
    }

    @Override
    public void run() {
        try {
            switch (command) {
                case 1: { // CASE UPDATE CLIENT LIST-- sent FROM server 
                    int i;
                    ps = new PrintStream(client.getOutputStream());
                   
                    String tmp = "";
                    for (i = 0; i < b.length; i++) {
                        tmp += b[i];
                        if (i != b.length - 1) {
                            tmp += ";";
                        }
                    }
                    ps.println("UPDATE "+ tmp+ " END");
                    System.out.println("Send UPDATE");
                    //ps.println("END");
                    break;
                }
                case 0: {
                    ps = new PrintStream(client.getOutputStream());
                    ps.println("NEED_UPDATE END");
                    System.out.println("Send NEED UPDATE");
                    break;
                }

            }
        } catch (Exception e) {

        }

    }
}
