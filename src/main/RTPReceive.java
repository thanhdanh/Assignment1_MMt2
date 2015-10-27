package main;

//class nhan am thanh
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.net.Socket;

import javax.media.*;
import javax.swing.JFrame;

public class RTPReceive extends JFrame implements ControllerListener, Runnable {

    String IP;
    String port;
    int type;
    public Socket socket;
//    public static void main(String[] args) throws NoPlayerException,
//			IOException {
////		new RTPReceive().init();
//	}

    public RTPReceive(String Ip, String port, int type) {

        this.IP = Ip;
        this.port = port;
        this.type = type;

    }
    public RTPReceive(String Ip, String port, int type, Socket so) {

        this.IP = Ip;
        this.port = port;
        this.type = type;
        this.socket = so;
    }

    Player player = null;

    public void init() throws NoPlayerException, IOException {
        setLayout(new BorderLayout());
        MediaLocator url;
        System.out.println(IP);
        url = new MediaLocator("rtp://" + IP + ":" + port + "/audio");
        player = Manager.createPlayer(url);
        player.addControllerListener(this);
        //player.start();
        setVisible(true);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                System.out.println("Stop player");
                stop();
                if (type == 1) {
                    new Thread(new sendThread(socket, 10)).start();        
                }
                //new Thread(new sendThread(socket, 2)).start();
                // System.exit(0);
            }
        });
    }

    public void start() {
        player.start();
    }

    public void stop() {
        player.stop();
        player.deallocate();
    }

    public void destroy() {
        player.close();
    }

    @Override

    public synchronized void controllerUpdate(ControllerEvent event) {
        if (event instanceof RealizeCompleteEvent) {
            Component comp;
            if ((comp = player.getVisualComponent()) != null) {
                add("Center", comp);
            }
            if ((comp = player.getControlPanelComponent()) != null) {
                add("South", comp);
            }
            validate();
        }
    }

    @Override
    public void run() {
        try {
            init();
            start();
        } catch (Exception ex) {

        }
    }
}
