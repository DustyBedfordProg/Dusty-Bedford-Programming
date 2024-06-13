package com.stratonica.util.sync.http;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.stratonica.util.Properties;

class DataLog {
	String log;
	
	public DataLog() {
		this.log = new String();
	}
	
	public synchronized void log(String msg) {
		log += ","+ msg;
	}
	public synchronized String getLog() {
		return log;
	}
}

public class JSONThreadClients extends JSONClient implements Runnable {

	static JSONClient client = new JSONClient();
	static String groupID = SyncBase.DEFAULT_GROUP;
	static String httpURL = SyncBase.DEFAULT_URL;
	static Random r = new Random();
	static int repeatCount = 15;
	static boolean setServerWait = false;

	DataLog report = null;
	
	public JSONThreadClients(DataLog log) {
		this.report = log;
	}
 
	public static void main(String args[]) {

		int clientCount = 5;

		//String report = "";
		
		DataLog log = new DataLog();

		try {

			Properties props = new Properties("client.txt");

			String groupID = props.get("group", SyncBase.DEFAULT_GROUP);
			String httpURL = props.get("url", SyncBase.DEFAULT_URL);

			boolean serverWait = props.getBoolean("server_wait", false);

			client.setURL(new URL(httpURL));

			client.deleteGroup(groupID);

			client.setClientCount(groupID, clientCount);

			if (serverWait) {
				System.out.println("Setting Server Wait");
				client.setGroupServerWait(groupID);
			}

			for (int n = 0; n < clientCount; n++) {

				JSONThreadClients cliTmp = new JSONThreadClients(log);
				Thread t = new Thread(cliTmp);
	
				t.start();
			}
			int waitCount = 120;
			do {
				try {
					Thread.sleep(1000);

				} catch (Exception e) {

				}
			} while (!log.getLog().endsWith("ZZZ") && --waitCount > -1);
			
			
			assert(waitCount > -1);
			
			String [] list = log.getLog().split(",");
			
			List<String> unsorted = new ArrayList<String>();
			List<String> sorted = new ArrayList<String>();
			for(int n = 0; n < list.length; n++) {
				if (!list[n].contentEquals("ZZZ")) {
					unsorted.add(list[n]);
					sorted.add(list[n]);
				}
			}
			
			Collections.sort(sorted);
			
			for(int n = 0; n < unsorted.size(); n++) {
				assert(unsorted.get(n).contentEquals(sorted.get(n)));
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void log(String msg) {
		report.log(msg);
	}

	public synchronized String getLogLast() {
		
		String [] tmp = report.getLog().split(",");
		if (tmp.length > 0) {
			System.out.println(tmp[tmp.length -1]);
			return tmp[tmp.length - 1];
		} else {
			return "";
		}
	}

	@Override
	public void run() {

		int round = 0;
		client.sendWaiting(groupID);

		for (int n = 0; n < repeatCount; n++) {
			System.out.println("Round:" + n);
			if (setServerWait) {
				client.waitingToRunAtServer(groupID);
			} else {
				client.waitingToRun(groupID);
			}
			String msg = formatNumber(++round) + " Round";
			log(msg);
			// System.out.println(msg);

			//if (!this.waitingToRunAtServer) {
				client.updateRunning(groupID);
			//}

			long t = (r.nextInt(5) + 1) * 250;
			try {
				Thread.sleep(t);
			} catch (Exception e) {
			}

			client.waitingOthers(groupID);

		}
		log("ZZZ");
	}

	String formatNumber(int number) {

		String zeroes = "00000000";
		String inputString = "" + number;
		if (inputString.length() >= zeroes.length()) {
			return inputString;
		} else {
			return zeroes.substring(0, zeroes.length() - inputString.length()) + inputString;
		}
	}

	public static void sleepRandom() {
		long t = r.nextInt(5) * 1000;
		try {
			Thread.sleep(10 * t);
		} catch (Exception e) {
		}
	}

}
