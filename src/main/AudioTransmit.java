/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.swing.JOptionPane;

/**
 *
 * @author Danh Cooper
 */
public class AudioTransmit implements Runnable {

    private MediaLocator locator;
    private String ipAddress;
    private String port;

    private Processor processor = null;
    private DataSink rtptransmitter = null;
    private DataSource dataOutput = null;

    public AudioTransmit(MediaLocator locator, String ipAddress, String port) {
        this.locator = locator;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Starts the transmission. Returns null if transmission started ok.
     * Otherwise it returns a string with the reason why the setup failed.
     */
    /**
     * Stops the transmission if already started
     */
    public void stop() {
        synchronized (this) {
            if (processor != null) {
                processor.stop();
                processor.close();
                processor = null;
                rtptransmitter.close();
                rtptransmitter = null;
            }
        }
    }

    private String createProcessor() {
        if (locator == null) {
            return "Locator is null";
        }
        DataSource ds;
        DataSource clone;
        try {
            ds = Manager.createDataSource(locator);
        } catch (Exception e) {
            return "Couldn't create DataSource";
        }
        try {
            processor = Manager.createProcessor(ds);
        } catch (IOException ex) {
            return "IOException creating processor";
        } catch (NoProcessorException ex) {
            return "Couldn't create processor";
        }
        boolean result = waitForState(processor, Processor.Configured);
        if (result == false) {
            return "Couldn't configure processor";
        }
        // Get the tracks from the processor
        TrackControl[] tracks = processor.getTrackControls();
        // Do we have atleast one track?
        if (tracks == null || tracks.length < 1) {
            return "Couldn't find tracks in processor";
        }
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
                // afmt.getSampleRate(), 
                // afmt.getSampleSizeInBits(),
                // afmt.getChannels());
                // 8000,4,1);

                tracks[i].setFormat(ulawFormat);
                System.err.println("Audio transmitted as:");
                System.err.println("  " + ulawFormat);
                // Assume succesful
                programmed = true;
            } else {
                tracks[i].setEnabled(false);
            }
        }
        //===================================================
        if (!programmed) {
            return "Couldn't find Audio track";
        }
        //===================================================
        ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
        processor.setContentDescriptor(cd);

        result = waitForState(processor, Controller.Realized);
        if (result == false) {
            return "Couldn't realize processor";
        }
        dataOutput = processor.getDataOutput();
        return null;
    }

    // Creates an RTP transmit data sink. This is the easiest way to create
    // an RTP transmitter. The other way is to use the RTPSessionManager API.
    // Using an RTP session manager gives you more control if you wish to
    // fine tune your transmission and set other parameters.
    private String createTransmitter() {
        // Create a media locator for the RTP data sink.
        // For example:
        //    rtp://129.130.131.132:42050/Audio
        String rtpURL = "rtp://" + ipAddress + ":" + port + "/audio";
        MediaLocator outputLocator = new MediaLocator(rtpURL);

        // Create a data sink, open it and start transmission. It will wait
        // for the processor to start sending data. So we need to start the
        // output data source of the processor. We also need to start the
        // processor itself, which is done after this method returns.
        try {
            rtptransmitter = Manager.createDataSink(dataOutput, outputLocator);
            rtptransmitter.open();
            rtptransmitter.start();
            dataOutput.start();
        } catch (Exception ioe) {
            return "Couldn't create RTP data sink";
        }
        return null;
    }
    private Integer stateLock = new Integer(0);
    private boolean failed = false;

    Integer getStateLock() {
        return stateLock;
    }

    void setFailed() {
        failed = true;
    }

    private synchronized boolean waitForState(Processor p, int state) {
        p.addControllerListener(new StateListener());
        failed = false;

        // Call the required method on the processor
        if (state == Processor.Configured) {
            p.configure();
        } else if (state == Processor.Realized) {
            p.realize();
        }

	// Wait until we get an event that confirms the
        // success of the method, or a failure event.
        // See StateListener inner class
        while (p.getState() < state && !failed) {
            synchronized (getStateLock()) {
                try {
                    getStateLock().wait();
                } catch (InterruptedException ie) {
                    return false;
                }
            }
        }

        if (failed) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void run() {
        String result;

        // Create a processor for the specified media locator
        // and program it to output RTP
        result = createProcessor();
        if (result != null) {
            JOptionPane.showMessageDialog(null, result);
        }

        // Create an RTP session to transmit the output of the
        // processor to the specified IP address and port no.
        result = createTransmitter();
        if (result != null) {
            processor.close();
            processor = null;
            JOptionPane.showMessageDialog(null, result);
        }

        // Start the transmission
        processor.start();
    }

    class StateListener implements ControllerListener {

        @Override
        public void controllerUpdate(ControllerEvent ce) {

	    // If there was an error during configure or
            // realize, the processor will be closed
            if (ce instanceof ControllerClosedEvent) {
                setFailed();
            }

	    // All controller events, send a notification
            // to the waiting thread in waitForState method.
            if (ce instanceof ControllerEvent) {
                synchronized (getStateLock()) {
                    getStateLock().notifyAll();
                }
            }
        }
    }

}
