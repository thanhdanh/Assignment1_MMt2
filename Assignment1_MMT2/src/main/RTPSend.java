package main;
// class truyen am thanh



import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

public class RTPSend  implements Runnable {
    static Processor p;
    static DataSink rtptransmitter;
            
    String IP, Port;
    
    public RTPSend(String ip, String port){
        this.IP = ip;
        this.Port = port;
        
    }

    static void stop() {
        if (p != null) {
            p.stop();
            p.close();
            p = null;
            rtptransmitter.close();
            rtptransmitter = null;
            System.out.println("End!");
            System.exit(0);
        }
    }

    @Override
    public void run() {
       try{
        //String url = "file://E:/music/audioclip.wav";//
        
        String url = "javasound://44100";
        DataSource dataOutput = null;
        DataSource ds = Manager.createDataSource(new MediaLocator(url));
        p = Manager.createProcessor(ds);
        p.configure();
        while (p.getState() < p.Configured) {
            Thread.sleep(50);
        }
        TrackControl[] tracks = p.getTrackControls();
        boolean programmed = false;
        AudioFormat afmt;
// Search through the tracks for a Audio track
        for (int i = 0; i < tracks.length; i++) {
            Format format = tracks[i].getFormat();
            if (tracks[i].isEnabled()
                    && format instanceof AudioFormat
                    && !programmed) {
                afmt = (AudioFormat) tracks[i].getFormat();
                AudioFormat ulawFormat = new AudioFormat(AudioFormat.DVI_RTP);
                tracks[i].setFormat(ulawFormat);
                System.err.println("Audio transmitted as:");
                System.err.println(" " + ulawFormat);
// Assume succesful
                programmed = true;
            } else {
                tracks[i].setEnabled(false);
            }
        }
        if (!programmed) {
            System.out.println("Couldn't find Audio track");
        }
        ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
        p.setContentDescriptor(cd);
        p.realize();
        while (p.getState() < p.Realized) {
            Thread.sleep(50);
        }
        dataOutput = p.getDataOutput();
        String rtpURL = "rtp://"+IP+":"+Port+"/audio";
        MediaLocator outputLocator = new MediaLocator(rtpURL);
        rtptransmitter = Manager.createDataSink(dataOutput, outputLocator);
        rtptransmitter.open();
        rtptransmitter.start();
        dataOutput.start();
        p.start();
        Thread.sleep(60000);
        stop();
        }catch(Exception ex){
            
        }
    }
}
