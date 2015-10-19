/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Danh Cooper
 */
public class receiveThread implements Runnable {

    PrintStream ps;
    Socket client;
    JTable thisTable;
    ServerGUI sGUI;
    ClientGUI cGUI;

    public receiveThread(Socket so) {
        client = so;
    }

    public receiveThread(Socket so, ServerGUI sgui) {
        client = so;
        sGUI = sgui;
        thisTable = sGUI.clientList;
    }

    public receiveThread(Socket so, ClientGUI cgui) {
        client = so;
        cGUI = cgui;
        thisTable = cGUI.clientList;
    }

    private void writeInfoFileJTable(JTable table, String[] list) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < list.length; i++) {
            model.addRow(new Object[]{i + 1, list[i]});
        }
    }

    private void xoatable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    @Override
    public void run() {
        try {
            BufferedReader receive = null;
            String[] mess = new String[50];
            receive = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean flag = true;

            String tmp = receive.readLine();

            
                mess = tmp.split(" ");
                switch (mess[0]) {
                    case ("NEED_UPDATE"): {                        
                        int i;
                        String[] list = new String[thisTable.getRowCount()];
                        for (i = 0; i < thisTable.getRowCount(); i++) {
                            list[i] = thisTable.getValueAt(i, 1).toString();                        }                        
                        new Thread(new sendThread(client, list, 1)).start();
                        System.out.println("SEND UPDATE");
                        break;
                    }
                    case ("UPDATE"): {                        
                        String list1 = receive.readLine();
                        String[] listArray = list1.split(";");
                        xoatable(thisTable);                        
                        writeInfoFileJTable(thisTable, listArray);                       
                        break;
                    }
                }
           

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

}
