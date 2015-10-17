/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Danh Cooper
 */
public class ServerImplement implements Runnable {
    private ServerSocket socket;
    private int port = 5000;
    static ServerGUI frame;
    public Thread thread = null;
    InetAddress ip;
    
    public ServerImplement(ServerGUI frameName) {
        try {           
            socket = new ServerSocket(port);
            ip = InetAddress.getLocalHost();
            port = socket.getLocalPort();
            
            frame = frameName;
            frame.txtHost.setText(ip.getHostName());
            frame.txtIp.setText(ip.getHostAddress()); 
            frame.txtPort.setText(Integer.toString(port)); 
            
            frame.btnStart.setEnabled(false);
            frame.btnStop.setEnabled(true);
            frame.jLabel5.setText("Started");
            start();
            //System.out.println("server start???");

        } catch (Exception ioe) {
            JOptionPane.showMessageDialog(null, ioe);
        }
    }
    
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
    
    @Override
    public void run() {
        while (thread != null) {
            try {
                Socket client = socket.accept();
                writeInfoFileJTable(client, frame.clientList);
                
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    private void writeInfoFileJTable(Socket so, JTable table) {
        int i = table.getRowCount();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{Integer.toString(i+1),so.getInetAddress().toString(),
            String.valueOf(so.getPort())});
    }
    
}
