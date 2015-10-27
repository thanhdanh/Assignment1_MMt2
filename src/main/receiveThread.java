/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.MediaLocator;
import javax.swing.DefaultListModel;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Danh Cooper
 */
public class receiveThread implements Runnable {

    PrintStream ps;
    Socket client;
//    JTable thisTable;
    ServerGUI sGUI;
    ClientGUI cGUI;
    public Thread thread = null;
    public callAlertForm cA;
    
    
    public receiveThread(Socket so) {
        client = so;
    }

    public receiveThread(Socket so, ServerGUI sgui) {
        this.client = so;
        this.sGUI = sgui;
        // this.thisTable = sGUI.clientList;
    }

    public receiveThread(Socket so, ClientGUI cgui) {
        this.client = so;
        this.cGUI = cgui;
        //this.thisTable = cGUI.clientList;
    }

    private void writeInfoFileJTable(JTable table, String[] list) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < list.length; i++) {
            if (!"same".equals(list[i])) {
                String[] tmp = list[i].split(",");

                model.addRow(new Object[]{table.getRowCount() + 1, tmp[0], tmp[1]});
            }
        }
    }

    private void xoatable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    @Override
    public void run() {
        BufferedReader receive = null;
        String[] mess = new String[100];
        try {
            receive = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean flag = true;
        try {
            String tmp = new String();
            while (flag) {
                tmp = receive.readLine();

                mess = tmp.split(" ");
                System.out.println(">>>>>>>tmp:" + tmp + " and mess[0]:" + mess[0]);
                System.out.println("received from " + client + " request " + mess[0]);

                switch (mess[0]) {
                    case "READY": {
                        String name = mess[1];
                        JTable table = sGUI.clientList;
                        int i = table.getRowCount();
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.addRow(new Object[]{Integer.toString(i + 1), name, client.getInetAddress().toString(),
                            String.valueOf(client.getPort()), ""});

                        break;
                    }
                    case "NEED_UPDATE": {
                        System.out.println("received update request! processing ... ... ...");
                        int i;
                        String[] list = new String[sGUI.clientList.getRowCount()];
                        String compare;
                        for (i = 0; i < sGUI.clientList.getRowCount(); i++) {
                            compare = sGUI.clientList.getValueAt(i, 2).toString() + ":" + sGUI.clientList.getValueAt(i, 3).toString();
                            System.out.println(compare);
                            if (!compare.equals(client.getRemoteSocketAddress().toString())) {
                                list[i] = sGUI.clientList.getValueAt(i, 1).toString() + "," + sGUI.clientList.getValueAt(i, 2).toString();
                            } else {
                                list[i] = "same";
                            }
                            System.out.println(list[i]);
                        }
                        new Thread(new sendThread(client, list, 1)).start();
                        System.out.println("updated list sent!!!");
                        break;
                    }
                    case "UPDATE": {
                        String list1 = mess[mess.length - 2];
                        System.out.println("danh sach nhan duoc: " + mess[mess.length - 2]);
                        System.out.println("list1 before split: " + list1);
                        System.out.println("received updated list! adding to table ... ... ...");
                        String[] listArray = list1.split(";");
                        for (int i = 0; i < listArray.length; i++) {
                            System.out.println("listArray[" + i + "]: " + listArray[i]);
                        }
                        xoatable(cGUI.clientList);
                        xoatable(cGUI.listClient);
                        writeInfoFileJTable(cGUI.clientList, listArray);
                        writeInfoFileJTable(cGUI.listClient, listArray);
                        System.out.println("added to table!!!");
                        break;
                    }
                    case "BYE": {
                        saybye2client(client.getInetAddress().toString());
                        System.out.println("client " + client.getInetAddress() + " is disconnecting ... ... ...");
                        receive.close();
                        client.close();
                        flag = false;
                        System.out.println("disconnected!!!");
                        break;
                    }

                    case "LIVE_APPEAR": {

                        for (int i = 0; i < sGUI.clientList.getRowCount(); i++) {
                            if (sGUI.clientList.getValueAt(i, 2).toString().equals(client.getInetAddress().toString())
                                    && sGUI.clientList.getValueAt(i, 3).toString().equals(
                                            String.valueOf(client.getPort()))) {
                                sGUI.clientList.setValueAt(mess[1], i, 4);
                            }
                        }

                        System.out.println("received Live appear! processing ... ... ...: " + mess[1]);

                        break;
                    }
                    case "NEED_UP_LI": {
                        System.out.println("received update live request!");
                        String[] temp = new String[sGUI.clientList.getRowCount()];
                        int j = 0;
                        for (int i = 0; i < sGUI.clientList.getRowCount(); i++) {

                            if (!"".equals(sGUI.clientList.getValueAt(i, 3).toString())) {
                                temp[j] = sGUI.clientList.getValueAt(i, 3).toString();
                                j = j + 1;
                            }
                        }
                        System.out.println(j);
                        if (temp[0] == null) {
                            temp[0] = "no_list";
                        }
                        new Thread(new sendThread(client, temp, 5)).start();
                        break;
                    }
                    case "LI_UPDATE": {
                        String list1 = mess[mess.length - 2];
                        if (!list1.contains("no_list")) {
                            System.out.println("danh sach nhan duoc: " + mess[mess.length - 2]);
                            System.out.println("list1 before split: " + list1);
                            System.out.println("received live updated list! adding to table ... ... ...");
                            String[] listArray = list1.split(";");
                            xoatable(cGUI.LAudioList);
                            for (int i = 0; i < listArray.length; i++) {

                                String[] temp = new String[3];
                                if (!"null".equals(listArray[i])) {

                                    temp = listArray[i].split(",");
                                    DefaultTableModel model = (DefaultTableModel) cGUI.LAudioList.getModel();
                                    model.addRow(new Object[]{i + 1, temp[1], temp[2], temp[0]});
                                    //System.out.println("listArray[" + i + "]: " + listArray[i]);
                                }
                            }
                            //writeInfoFileJTable(cGUI.LAudioList, listArray);
                            System.out.println("added to table!!!");
                        } else {
                            xoatable(cGUI.LAudioList);
                            System.out.println("No live list in server");
                        }
                        break;
                    }

                    case "LIVE_DISAPPEAR": {
                        for (int i = 0; i < sGUI.clientList.getRowCount(); i++) {
                            if (sGUI.clientList.getValueAt(i, 1).toString().equals(client.getInetAddress().toString())
                                    && sGUI.clientList.getValueAt(i, 2).toString().equals(String.valueOf(client.getPort()))) {
                                sGUI.clientList.setValueAt("", i, 3);
                            }
                        }

                        System.out.println("No live list in server");
                        break;
                    }
                    case "CALL": {
                        String b;
                        String name = mess[1];
                        b = InetAddress.getLocalHost().getHostAddress();
                        cA = new callAlertForm(client, b, name, false);
                        thread=new Thread(cA);
                        thread.start();
                        //O: calling;
                        //1: inviting;
                        break;
                    }
                    case "OK": {
                        String b;
                        b = InetAddress.getLocalHost().getHostAddress();
                        System.out.println("received OK from other client!!! setting up calling ... ... ...");
//                        new Thread(new RTPReceive(client.getInetAddress().toString(), "24680")).start();
//                        new Thread(new RTPSend(String.valueOf(client.getRemoteSocketAddress()), "24682")).start();

                        new Thread(new RTPReceive(b, "12478", 1)).start();
                       
                        cGUI.cF.flag = false;
                        
                        new Thread(new AudioTransmit(new MediaLocator("javasound://44100"), client.getInetAddress().toString().substring(1), "12468")).start();
                        System.out.println("called " + client.getRemoteSocketAddress() + " from" + client.getInetAddress());

                        break;
                    }
                    case "NO":{
                        JOptionPane.showMessageDialog(null, "Refused!!!");
                        break;
                    }
                    case "INVITE": {
                        String b;
                        String name = mess[1];
                        b = InetAddress.getLocalHost().getHostAddress();
                        cA = new callAlertForm(client, b, name,true);   
                        new Thread(cA).start();
                        break;
                    }
                    case "ACCEPT": {
                        String b;
                        String name = mess[1];                        
                        DefaultListModel table = new DefaultListModel();
                        table.addElement(name);
                        int i=0;
                        
                        while(i< cGUI.listConf.getModel().getSize()){
                            table.addElement(cGUI.listConf.getModel().getElementAt(i));
                            i++;
                        }
                        cGUI.listConf.setModel(table);
                        break;
                    }
                    case "CANCEL":{
                        cA.setVisible(false);
                        cA.flag=false;
                        thread.stop();
                        JOptionPane.showMessageDialog(null, "You have miss call");
                        
                        break;
                    }
                    case "NO_ANS":{
                        JOptionPane.showMessageDialog(null, "No answer");
                        break;
                    }
                    case "CONF": {
                        int stt = Integer.parseInt(mess[1]);
                        int num = Integer.parseInt(mess[2]);
                        int thisPort = 22230+stt*2;
                        String IP = "226.116.116.116";
                        
                        new Thread(new AudioTransmit(new MediaLocator("javasound://44100"), IP, Integer.toString(thisPort))).start();
                        for (int i=0; i<stt; i++){
                            new Thread(new RTPReceive(IP, Integer.toString(thisPort-(stt-i)*2), 0)).start();
                            
                        }
                        for (int i=stt+1; i<num; i++){
                            new Thread(new RTPReceive(IP, Integer.toString(thisPort+(i-stt)*2), 0)).start();
                            
                        }
                        break;
                    }

                }
            }
        } catch (IOException | HeadlessException ex) {
            flag = false;
            if (client.getLocalPort() == 5000) {
                // client down
                saybye2client(client.getRemoteSocketAddress().toString());
                try {
                    receive.close();
                    client.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else if(client.getLocalPort()==5678){
                
            }
            else{// server down
                JOptionPane
                        .showMessageDialog(
                                cGUI,
                                "Server has been shutdown unexpectedly!!!\n"
                                + "you can't update list from the server but you still can call your friends existing in your list\n"
                                + "you can try to connect to server later\n"
                                + "we're sorry for this\n");

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(receiveThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saybye2client(String a) {
        String cliadd;
        cliadd = a;
        for (int i = 0; i < sGUI.clientList.getRowCount(); i++) {
            if (cliadd.equals(sGUI.clientList.getValueAt(i, 1).toString())) {
                xoarow(sGUI.clientList, i);
            }
        }

    }

    private void xoarow(JTable table, int row) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.removeRow(row);
    }
}
