package main;

//class nhan am thanh
import java.awt.*;
<<<<<<< HEAD
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
=======
>>>>>>> origin/master
import java.io.IOException;

import javax.media.*;
import javax.swing.JFrame;

public class RTPReceive extends JFrame implements ControllerListener, Runnable  {

    String IP;
    String port;
//    public static void main(String[] args) throws NoPlayerException,
//			IOException {
////		new RTPReceive().init();
//	}

    public RTPReceive(String Ip, String port) {
       
            this.IP = Ip;
<<<<<<< HEAD
            this.port = port;        
            addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                System.out.println("Stop Player");
                stop();
                //new Thread(new sendThread(socket, 2)).start();
               // System.exit(0);
            }
        });
=======
            this.port = port;
            
>>>>>>> origin/master
       
    }

    Player player = null;

    public void init() throws NoPlayerException, IOException {
        setLayout(new BorderLayout());

        MediaLocator url;
        url = new MediaLocator("rtp://"+IP+":"+port+"/audio");
        player = Manager.createPlayer(url);
        player.addControllerListener(this);
        //player.start();
        setVisible(true);
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

<<<<<<< HEAD
    @Override
=======
>>>>>>> origin/master
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
        try{
            init();
            start();
        }catch(Exception ex){
            
        }
    }
}
