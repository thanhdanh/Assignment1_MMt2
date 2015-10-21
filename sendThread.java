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
        	PrintStream ps;
        	ps = new PrintStream(client.getOutputStream());
        	switch (command) {
                
                case 0: {
                    ps.println("NEED_UPDATE END ");
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
                    ps.println("UPDATE "+ tmp+ " END ");
                    
                    break;
                }
                 case 2:{
                	 ps.println("BYE END"); 
                	 break;
                  }

            }
        
        } catch (Exception e) {

        }

    }
}
