/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author Danh Cooper
 */
public class ClientImplement {
    String ServerIP;
    static public Socket socket;
    ClientGUI cGUI = new ClientGUI();
    public ClientImplement(String IP) {
		
        try {
            ServerIP = IP;
            socket = new Socket(ServerIP, 5000);   
            
            cGUI.setVisible(true);
           // new Thread(new rthread(socket, namdau)).start();
            //new Thread(new sthread(socket, 0)).start();
				//namdau.setVisible(true);
            //});
           // clientListener = new ServerSocket(5678);
            //new Thread(new ServerListeningThread(clientListener, namdau)).start();
            JOptionPane.showMessageDialog(null, "successfully connected to server!!!");

        } catch (Exception e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "can't connect to server!");
        }
    }
}
