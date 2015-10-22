package main;

import java.io.IOException;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.format.AudioFormat;

public class voicechat {

	public String chosenIP;

	public voicechat(String IP) {
		chosenIP = IP;
	}

	public static void main(String[] args) throws NoPlayerException,
			IOException {
		// TODO Auto-generated method stub
		CaptureDeviceInfo di = null;
		Vector deviceList = CaptureDeviceManager.getDeviceList(new AudioFormat(
				"linear", 44100, 16, 2));
		if (deviceList.size() > 0) {
			di = (CaptureDeviceInfo) deviceList.firstElement();
		} else {
			System.out.println("no device detected!");
		}
		// Player p = Manager.createPlayer(di.getLocator());
		// p.start();
	}

}
