/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.DataSink;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.control.StreamWriterControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.JOptionPane;
import jmapps.util.StateHelper;

/**
 *
 * @author Danh Cooper
 */
public class Record {
    CaptureDeviceInfo di = null;
    Processor p = null;
    StateHelper sh = null;
    String url;
    public Record(String url){   
        this.url = url;
        
         File file = new File(this.url);
         
 	 Vector deviceList = CaptureDeviceManager.getDeviceList(new
 	 	 	 	 AudioFormat(AudioFormat.LINEAR, 44100, 16, 2));
 	 if (deviceList.size() > 0)
 	     di = (CaptureDeviceInfo)deviceList.firstElement();
 	 else 	     
 	     System.exit(-1);
         
    }
    public void start(){
        try {
 	     p = Manager.createProcessor(di.getLocator());
 	     sh = new StateHelper(p);
 	 } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "Errrot create processor "+ e);
             System.exit(0);
 	 }
 	 // Configure the processor
 	 if (!sh.configure(10000))
 	     System.exit(-1);
 	 // Set the output content type and realize the processor
 	 p.setContentDescriptor(new
                  FileTypeDescriptor(FileTypeDescriptor.WAVE));
 	 if (!sh.realize(10000))
 	     System.exit(-1);
 	 // get the output of the processor
 	 DataSource source = p.getDataOutput();
 	 // create a File protocol MediaLocator with the location of the
 	 // file to which the data is to be written
 	 MediaLocator dest = new MediaLocator("file://"+url);
 	 // create a datasink to do the file writing & open the sink to
 	 // make sure we can write to it.
 	 DataSink filewriter = null;
 	 try {
 	     filewriter = Manager.createDataSink(source, dest);
 	     filewriter.open();
 	 } catch (Exception e) {
 	     System.exit(-1); 	
 	 }
 	 // if the Processor implements StreamWriterControl, we can
 	 // call setStreamSizeLimit
 	 // to set a limit on the size of the file that is written.
 	 StreamWriterControl swc = (StreamWriterControl)
 	     p.getControl("javax.media.control.StreamWriterControl");
 	 //set limit to 5MB
 	 if (swc != null)
 	     swc.setStreamSizeLimit(5000000);
 
 	 // now start the filewriter and processor
 	 try {
 	     filewriter.start();
 	 } catch (IOException e) {
 	     System.exit(-1);
 	 }
 	 // Capture for 5 seconds
 	 sh.playToEndOfMedia(10000);
 	 sh.close();
 	 // Wait for an EndOfStream from the DataSink and close it...
 	 filewriter.close();
    }
    
}
