package com.stratonica.util.sync.http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.stratonica.util.Conversion;
import com.stratonica.util.Properties;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


public class JSONServer implements HttpHandler {

	private SyncServer theServer;
	long serverWaitTimeout = 10;
	long maxWaitIntervals = 9000;
	boolean verboseLogging = false;

	public JSONServer() {
		super();
		theServer = new SyncServer();
	}

	public static void main(String[] args) throws IOException {

		JSONServer js = new JSONServer();
		js.log("Reading properties file server.txt");
		Properties props = new Properties("server.txt");
		
		js.setVerboseLogging(props.getBoolean("verbose_logging", false));
		js.log("Verbose Logging:" + js.getVerboseLogging());

		
		int port = props.getInteger("port", 8000);
		js.log("Port is set at:" + port);
		

		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		js.log("HttpServer Created");
		
		
		js.setMaxWaitIntervals(props.getLong("max_wait_intervals", 9000));
		js.log("max_wait_intervals:" + js.getMaxWaitIntervals());
		
		js.setServerWaitTimeout(props.getLong("server_wait_timeout", 10));
		js.log("server_wait_timeout:" + js.getServerWaitTimeout());
		
		
		server.createContext("/json", new JSONServer());
		
		int maxThreads = props.getInteger("max_threads", 100);
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
		server.setExecutor(threadPoolExecutor);

		server.start();
		js.log("Server started on port " + port, true);
	}

	private long getServerWaitTimeout() {
		return this.serverWaitTimeout;
	}

	private long getMaxWaitIntervals() {
		return this.maxWaitIntervals;
	}

	private void setVerboseLogging(Boolean verbose) {
		verboseLogging = verbose;
		
	}
	public Boolean getVerboseLogging() {
		return verboseLogging;
	}

	private void setServerWaitTimeout(Long ms) {
		this.serverWaitTimeout = ms;
		
	}

	private void setMaxWaitIntervals(Long intervals) {
		this.maxWaitIntervals = intervals;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
			JSONObject requestJson = new JSONObject();
			try {
				requestJson = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			this.log(requestJson.toString(4));

			String command = requestJson.getString("command");
			String objectID = requestJson.getString("id");

			StratResponse stratResponse = processMessage(command + " " + objectID);
			
			this.log(stratResponse.getData().toString(4));

			exchange.sendResponseHeaders(stratResponse.getResponseCode(), 
					                     stratResponse.getData().toString().length());
			OutputStream os = exchange.getResponseBody();
			os.write(stratResponse.getData().toString().getBytes());
			os.close();
		}
	}

	private StratResponse processMessage(String msg) {

		this.log("Message:(" + msg + ")");
		String[] items = msg.split(" ");

		if (items.length < 1) {
			items = new String[2];
			items[0] = "status";
			items[1] = "default";
		}

		StratResponse response = new StratResponse();
		
		JSONObject rtn = new JSONObject();
		
		response.setData(rtn);
		
		switch (items[0]) {

		case "clear": // do this during test preparation
			clearGroup(items[1]);
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", items[1]);
			break;
		case "delete": // do this during test preparation
			deleteGroup(items[1]);
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", items[1]);
			break;
		case "new":
			addGroup(items[1]);
			Integer nextID = getNextID(items[1]);
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", items[1] + nextID);
			break;

		case "total":
			addGroup(items[1]);
			int total = setTotal(items[1], Conversion.toInteger(items[2]));
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", "" + total);
			break;
		case "waiting":
			int waiting = addWaitingUser(items[1]);
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", "" + waiting);
			break;
		case "running":
			int running = addRunningUser(items[1]);
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", "" + running);
			break;
		case "server_wait":
			setGroupServerWait(items[1]);
			break;
		case "status":
			rtn.append("status", SyncBase.SUCCESS);
			Boolean waitAtServer = groupWaitStatus(items[1]);
			String status = "";
			
			long intervals = maxWaitIntervals;
			int coun5 = 0;

			do {
				if (coun5 > 0) {
					this.log("**** Actually Waiting At SErver ****");
				}
				status = groupStatus(items[1]);
				intervals--;
				coun5++;
			} while (waitAtServer && intervals != 0 && sleep(serverWaitTimeout)
					&& (status == SyncBase.GROUP_STATUS_WAIT || status == SyncBase.GROUP_STATUS_INIT));

			if (waitAtServer) {
				if(intervals == 0) {
					response.setResponseCode(responseCode.TIMEOUT);
				}
				//running = addRunningUser(items[1]);		
			}
			
			rtn.append("result", status);
			break;
		case "all_group_info":
			rtn.append("status", SyncBase.SUCCESS);
			rtn.append("result", allGroups());
			break;
		default:
			rtn.append("status", SyncBase.ACK);
			rtn.append("result", "");
			response.setResponseCode(responseCode.BAD_REQUEST);
			break;

		}
		return response;
	}

	private Boolean groupWaitStatus(String group) {
		GroupInfo grp = getGroupInfoFromMap(group);

		if (grp != null) {
			return grp.getGroupServerWaiting();
		}
		return false;

	}

	private boolean setGroupServerWait(String group) {
		GroupInfo grp = getGroupInfoFromMap(group);

		if (grp != null) {
			grp.setGroupServerWaiting();
			return true;
		}
		return false;

	}

	private boolean sleep(long ms) {

		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
		return true;
	}

	public void log(String msg, Boolean notVerbose) {
		if (verboseLogging || notVerbose) {
			System.out.println(curTime() + ": " + msg);
		}
	}
	public void log(String msg) {
		log(msg, false);
	}

	private long curTime() {
		return (new Date()).getTime();
	}

	private synchronized int clearGroup(String group) {
		if (theServer.groupMap.get(group) != null) {
			theServer.groupMap.remove(group);
		}
		addGroup(group);
		return responseCode.OK;
	}

	private synchronized int deleteGroup(String group) {
		if (theServer.groupMap.get(group) != null) {
			theServer.groupMap.remove(group);
		} 
		return responseCode.OK;
	}

	private synchronized int addGroup(String group) {
		if (theServer.groupMap.get(group) == null) {
			theServer.groupMap.put(group, new GroupInfo());
		} 
		return responseCode.OK;
	}

	private synchronized int addRunningUser(String group) {
		GroupInfo grp = theServer.groupMap.get(group);
		return grp.addRunning();

	}

	private synchronized int addWaitingUser(String group) {
		GroupInfo grp = theServer.groupMap.get(group);
		return grp.addWaiting();
	}

	private synchronized GroupInfo getGroupInfoFromMap(String group) {
		return theServer.groupMap.get(group);
	}

	private String groupStatus(String group) {

		GroupInfo grp = getGroupInfoFromMap(group);

		if (grp != null) {
			return grp.getGroupStatus();
		}
		return "no_such_group_" + group;

	}

	private synchronized JSONObject allGroups() {

		JSONObject rtn = new JSONObject();

		Set<String> keySet = theServer.groupMap.keySet();
		if (keySet.size() > 0) {
			for (String key : keySet) {

				JSONObject groupObject = new JSONObject();
				groupObject.put("Name", key);

				GroupInfo info = theServer.groupMap.get(key);
				JSONObject stateObject = new JSONObject();
				stateObject.put("Status", info.getGroupStatus());
				stateObject.put("Count", info.getGroupCount());

				groupObject.put("Data", stateObject);
				rtn.append("Group", groupObject);
			}
		} else {
			rtn = new JSONObject();

			// Create an empty JSONArray
			JSONArray emptyList = new JSONArray();

			// Add the empty list to the JSONObject with the key "Group"
			rtn.put("Group", emptyList);
		}

		log(rtn.toString(4));

		return rtn;
	}

	private synchronized Integer getNextID(String group) {

		GroupInfo grpInfo = theServer.groupMap.get(group);
		++grpInfo.groupLastID;
		grpInfo = theServer.groupMap.get(group);
		log("** Last ID" + grpInfo.groupLastID);
		return grpInfo.groupLastID;
	}

	private synchronized int setTotal(String group, Integer count) {
		GroupInfo groupInfo = theServer.groupMap.get(group);
		if (groupInfo == null) {
			addGroup(group);
			groupInfo = theServer.groupMap.get(group);
		}
		groupInfo.setGroupCount(count);
		return groupInfo.getGroupCount();
	}

}
