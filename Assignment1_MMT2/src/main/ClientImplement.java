/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
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
    static public ClientGUI cGUI;
    static public ServerSocket clientListener;
    String name;

    public ClientImplement(String IP, String name) {

        try {
            this.name =name;
            ServerIP = IP;
            socket = new Socket(ServerIP, 5000);
            cGUI = new ClientGUI(socket, name);
            clientListener = new ServerSocket(5678);

            new Thread(new receiveThread(socket, cGUI)).start();
            new Thread(new sendThread(socket, name, 10)).start();
            Thread.sleep(100);
            new Thread(new sendThread(socket, 0)).start();
            
            new Thread(new ServerListeningThread(clientListener, cGUI)).start();
            System.out.println("da tao thread nghe cho client khac call");

            JOptionPane.showMessageDialog(null, "successfully connected to server!!!");

        } catch (Exception e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "can't connect to server!");
        }
    }
}

class ServerListeningThread implements Runnable {

    ServerSocket server;
    ClientGUI cGUI;

    ServerListeningThread(ServerSocket s, ClientGUI i) {
        server = s;
        cGUI = i;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket Client = server.accept();
                System.out.println("Create receiveThread on " + server.getLocalPort() + "...");
                new Thread(new receiveThread(Client, cGUI)).start();
            } catch (IOException e) {
                System.exit(0);
            }
        }
    }

}
