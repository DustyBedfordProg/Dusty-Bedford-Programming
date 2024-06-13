package com.stratonica.util.sync.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.stratonica.util.Properties;

public class JSONClient {
	
	private static final long MAX_SLEEP_TIME = 50;
	
	private String groupID;

	private URL url;
	
	private long maxWaitTimeSeconds = 3600;
	


	public static void main(String[] args) {
        try {

        	Properties props = new Properties("client.txt");
    		
    		String groupID = props.get("group", SyncBase.DEFAULT_GROUP);
    		String httpURL = props.get("url", SyncBase.DEFAULT_URL);
    		boolean serverWait = props.getBoolean("server_wait", false);

    		int clientCount = 2;
    		int repeatCount = 5;

    		if (args.length > 0) {
    			httpURL = args[0];
    		}
        	
        	JSONClient client = new JSONClient();
        	
        	client.setMaxWaitTimeSeconds(props.getLong("max_wait_time_seconds", 3600));
        	
        	client.setURL(new URL(httpURL));
        	        	
    		client.deleteGroup(groupID);
    		
    		client.setClientCount(groupID, clientCount);
    		
    		if (serverWait) {
    			client.setGroupServerWait(groupID);
    		}
    		
    		for (int n = 0; n < clientCount; n++) {
    			Object instanceID1 = client.registerUser(groupID).get("result");
    			System.out.println("NEW **& user id:" + instanceID1);
    		}
    		
    		for (int n = 0; n < clientCount ; n++) {
				client.sendWaiting(groupID);
			}
 
    		int count = 0;
    		
    		while (repeatCount-- > 0) {
    			
    			System.out.println("Count:" + ++count);
    			
    			for (int n = 0; n < clientCount; n++) {
    				if (serverWait) {
    					client.waitingToRunAtServer(groupID);
    				} else {
    					client.waitingToRun(groupID);
    				}
    			}

    			for (int n = 0; n < clientCount; n++) {
    				client.updateRunning(groupID);
    			}

    			for (int n = 0; n < clientCount; n++) {
    				client.waitingOthers(groupID);
    			}
       			
    		}

    		client.deleteGroup(groupID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void setURL(URL url) {
		this.url = url;
	}
	
	public JSONObject allGroupInfo() {
		return this.sendRequest(createJSONRequest("all_group_info", "test"));
	}
	
	public JSONObject waitingOthers(String groupID) {

		return waitForOthers(groupID);

	}
	public JSONObject waitForOthers(String groupID) {
		return waitForOthers(groupID, this.maxWaitTimeSeconds);
	}
	
	public JSONObject waitForOthers(String groupID, long timeoutseconds) {

		JSONObject resp = sendRequest(createJSONRequest("waiting", groupID));

		long count = timeoutseconds;
		
		String respValue = "";
		
		do {
			
			resp = sendRequest(createJSONRequest("status", groupID));
			respValue = resp.getJSONArray("result").getString(0);
			
		} while (--count > 0 && respValue.toLowerCase().contentEquals(SyncBase.GROUP_STATUS_RUN) && sleep(MAX_SLEEP_TIME));
		
		return resp;

	}
	
	public JSONObject updateRunning(String groupID) {

		return sendRunning(groupID);

	}

	public JSONObject waitingToRun(String groupID) {

		return waitToRun(groupID);

	}
	
	public JSONObject waitingToRunAtServer(String groupID) {

		return waitToRunAtServer(groupID);

	}

	public JSONObject waitToRunAtServer(String groupID) {

		return sendRequest(createJSONRequest("status", groupID)); 

	}

	public JSONObject waitToRun(String groupID) {
	
		JSONObject resp = null;
		String respValue = "";
		do {
		
			resp = sendRequest(createJSONRequest("status", groupID));
			respValue = (String) resp.getJSONArray("result").get(0); 

		} while (!respValue.toLowerCase().contentEquals(SyncBase.GROUP_STATUS_GO) && sleep(MAX_SLEEP_TIME));

		return resp;
	}

	public static boolean sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		return true;
	}
	
	private JSONObject sendRunning(String group) {
		return sendRequest(createJSONRequest("running", group));
	}
	
	public JSONObject sendWaiting(String group) {
		return sendRequest(createJSONRequest("waiting", group));
	}
	
	public JSONObject registerUser(String group) {
		return sendRequest(createJSONRequest("new", group));
	}
	
	public JSONObject setClientCount(String group, int clientCount) {
		return sendRequest(createJSONRequest("total", group + " " + clientCount));
	}
	
	public JSONObject setGroupServerWait(String group) {
		return sendRequest(createJSONRequest("server_wait", group + " true"));
	}
	
	public JSONObject deleteGroup() {
		return deleteGroup(this.groupID);
	}
	
	public JSONObject deleteGroup(String group) {
		return sendRequest(createJSONRequest("delete", group));
	}

	private JSONObject sendRequest(JSONObject requestJson) {

		try {
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);


			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				response.append(line);
			}

			br.close();

            conn.disconnect();

            return new JSONObject(response.toString());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JSONObject("{}");
	
	}

	private JSONObject createJSONRequest(String command, String objID) {

		JSONObject requestJson = new JSONObject();

		requestJson.put("command", command);
		requestJson.put("id", objID);
		
		return requestJson;

	}

	long getMaxWaitTimeSeconds() {
		return maxWaitTimeSeconds;
	}

	void setMaxWaitTimeSeconds(long maxWaitTimeSeconds) {
		this.maxWaitTimeSeconds = maxWaitTimeSeconds;
	}
	
}
