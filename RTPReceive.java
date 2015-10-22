package main;

//class nhan am thanh
import java.awt.*;
import java.io.IOException;

import javax.media.*;
import javax.swing.JFrame;

public class RTPReceive extends JFrame implements ControllerListener  {

    String IP;
    String port;
//    public static void main(String[] args) throws NoPlayerException,
//			IOException {
////		new RTPReceive().init();
//	}

    public RTPReceive(String Ip, String port) {
        try {
            this.IP = Ip;
            this.port = port;
            init();
        } catch (Exception ex) {

        }
    }

    Player player = null;

    public void init() throws NoPlayerException, IOException {
        setLayout(new BorderLayout());

        MediaLocator url;
        url = new MediaLocator("rtp://"+IP+":"+port+"/audio");
        player = Manager.createPlayer(url);
        player.addControllerListener(this);
        player.start();
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
}
