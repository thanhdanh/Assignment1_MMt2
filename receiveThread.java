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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
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
                model.addRow(new Object[]{table.getRowCount() + 1, list[i]});
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
                System.out.println("received from " + client + " request " + mess[0]);
                System.out.println(">>>>>>>tmp:" + tmp + " and mess[0]:" + mess[0]);
                switch (mess[0]) {
                    case "NEED_UPDATE": {
                        System.out.println("received update request! processing ... ... ...");
                        int i;
                        String[] list = new String[sGUI.clientList.getRowCount()];
                        String compare;
                        for (i = 0; i < sGUI.clientList.getRowCount(); i++) {
                            compare = sGUI.clientList.getValueAt(i, 1).toString() + ":" + sGUI.clientList.getValueAt(i, 2).toString();
                            System.out.println(compare);
                            if (!compare.equals(client.getRemoteSocketAddress().toString())) {
                                list[i] = sGUI.clientList.getValueAt(i, 1).toString();
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
                        writeInfoFileJTable(cGUI.clientList, listArray);
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
                            if (sGUI.clientList.getValueAt(i, 1).toString().equals(client.getInetAddress().toString())
                                    && sGUI.clientList.getValueAt(i, 2).toString().equals(
                                            String.valueOf(client.getPort()))) {
                                sGUI.clientList.setValueAt(mess[1], i, 3);
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
                        InetAddress b;
                        int a = JOptionPane.showConfirmDialog(cGUI, "do you accept call from " + client + " ???", "Call Alert!!!", JOptionPane.YES_NO_OPTION);
                        b = InetAddress.getLocalHost();
                        switch (a) {
                            case JOptionPane.YES_OPTION: {
                                System.out.println("accepted!");
                                // tra loi cho client kia accepted                             
                                new Thread(new sendThread(client, 8)).start();
                                // tao thread nghe nhan
                                new Thread(new RTPReceive(client.getInetAddress().toString(), "12468")).start();
                                //new Thread(new RTPSend(StringOf(b),"12478")).start();

                                break;
                            }
                            case JOptionPane.NO_OPTION: {
                                System.out.println("refused!");
                                // tra loi cho client kia refused
                                new Thread(new sendThread(client, 9)).start();
                                //do nothing
                                break;
                            }
                        }
                        System.out.println("gia tri tra ve cua a: " + a);
                        break;
                    }
                    case "OK": {
                        System.out.println("received OK from other client!!! setting up calling ... ... ...");
                        new Thread(new RTPReceive(client.getInetAddress().toString(), "24680")).start();
                        new Thread(new RTPSend(String.valueOf(client.getRemoteSocketAddress()),"24682")).start();
                        System.out.println("called " + client.getRemoteSocketAddress() + " from" + client.getInetAddress());

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
            } else {// server down
                JOptionPane
                        .showMessageDialog(
                                cGUI,
                                "Server has been shutdown unexpectedly!!!\n"
                                + "you can't update list from the server but you still can call your friends existing in your list\n"
                                + "you can try to connect to server later\n"
                                + "we're sorry for this\n");

            }
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

    private void liveDisapper(String ip, String port) {

    }

    private void xoarow(JTable table, int row) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.removeRow(row);
    }
}
