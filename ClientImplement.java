/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.Socket;
import javax.swing.JOptionPane;
/**
 *
 * @author Danh Cooper
 */
public class ClientImplement {
    String ServerIP;
    static public Socket socket;
    static public ClientGUI cGUI;
   
    public ClientImplement(String IP) {
		
        try {
            ServerIP = IP;
            socket = new Socket(ServerIP, 5000);
            cGUI = new ClientGUI(socket);
            
            new Thread(new sendThread(socket, 0)).start();
            new Thread(new receiveThread(socket, cGUI)).start();
            
            JOptionPane.showMessageDialog(null, "successfully connected to server!!!");

        } catch (Exception e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "can't connect to server!");
        }
    }
}
