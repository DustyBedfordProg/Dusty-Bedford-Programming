package com.stratonics.util.sync.http;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.stratonica.util.Properties;
import com.stratonica.util.sync.http.JSONClient;
import com.stratonica.util.sync.http.JSONThreadClients;
import com.stratonica.util.sync.http.SyncBase;

public class JSONServerTest {

	private Properties props = null;

	private String httpURL = "";

	private JSONClient client;

	private Random rnd;

	@BeforeTest
	public void initialize() throws MalformedURLException {

		props = new Properties("client.txt");

		httpURL = props.get("url", SyncBase.DEFAULT_URL);

		client = new JSONClient();

		client.setURL(new URL(httpURL));

		rnd = new Random();
	}
	
	public static void main(String [] args) throws MalformedURLException {
		
		JSONServerTest jst = new JSONServerTest();
		jst.initialize();
		jst.testGroupCreation();
		jst.testSync();
		
	}

	@Test
	public void testSync() {

		JSONThreadClients.main(null);
		
	}
	
	@Test
	public void testGroupCreation() {

		String groupBaseID = "test_group_";
		int max_groups = 5;

		for (int n = 0; n < max_groups; n++) {

			String groupID = groupBaseID + n;

			client.deleteGroup(groupID);

			verifyNoSuchGroup(groupID);

		}

		for (int n = 0; n < max_groups; n++) {
			int clientCount = rnd.nextInt(100);
			String groupID = groupBaseID + n;

			client.setClientCount(groupID, clientCount);

			for (int m = 0; m < clientCount; m++) {

				client.registerUser(groupID).get("result");
				verifyGroupStatus(groupID, "init", clientCount);

				client.sendWaiting(groupID);
				if (m < clientCount - 1) {
					verifyGroupStatus(groupID, "init", clientCount);
				} else {
					verifyGroupStatus(groupID, "go", clientCount);
				}
			}

		}

		for (int n = 0; n < max_groups; n++) {

			String groupID = groupBaseID + n;

			int userCount = getUserCount(groupID);

			for (int i = 0; i < userCount; i++) {
				client.updateRunning(groupID);
				if (i < userCount - 1) {
					verifyGroupStatus(groupID, "go", userCount);
				} else {
					verifyGroupStatus(groupID, "wait", userCount);
				}
			}

		}

		for (int n = 0; n < max_groups; n++) {
			String groupID = groupBaseID + n;
			int userCount = getUserCount(groupID);
			for (int i = 0; i < userCount; i++) {
				client.waitForOthers(groupID);
				if (i < userCount - 1) {
					verifyGroupStatus(groupID, "wait", userCount);
				} else {
					verifyGroupStatus(groupID, "go", userCount);
				}
			}
		}

		for (int n = 0; n < max_groups; n++) {
			String groupID = groupBaseID + n;
			client.deleteGroup(groupID);
			verifyNoSuchGroup(groupID);

		}
	}

	private int getUserCount(String groupID) {

		org.json.JSONArray result = client.allGroupInfo().getJSONArray("result");
		org.json.JSONObject data = result.getJSONObject(0);
		org.json.JSONArray groups = data.getJSONArray("Group");

		int index = -1;

		JSONObject group = null;

		for (int i = 0; i < groups.length(); i++) {

			group = groups.getJSONObject(i);
			if (group.getString("Name").contentEquals(groupID)) {
				index = i;
				break;
			}

		}
		assert(index > -1);

		data = group.getJSONObject("Data");

		return data.getInt("Count");
	}

	private void verifyNoSuchGroup(String groupID) {

		org.json.JSONArray result = client.allGroupInfo().getJSONArray("result");
		org.json.JSONObject data = result.getJSONObject(0);
		org.json.JSONArray groups = data.getJSONArray("Group");

		for (int i = 0; i < groups.length(); i++) {

			org.json.JSONObject group = groups.getJSONObject(i);
			assert(!group.getString("Name").contentEquals(groupID));

		}

	}

	public void verifyGroupStatus(String groupID, String expectedStatus, int expectedUserCount) {
		org.json.JSONArray result = client.allGroupInfo().getJSONArray("result");
		org.json.JSONObject data = result.getJSONObject(0);
		org.json.JSONArray groups = data.getJSONArray("Group");

		int index = -1;

		JSONObject group = null;

		for (int i = 0; i < groups.length(); i++) {

			group = groups.getJSONObject(i);
			if (group.getString("Name").contentEquals(groupID)) {
				index = i;
				break;
			}

		}
		assert(index > -1);

		data = group.getJSONObject("Data");
		String currentStatus = data.getString("Status");
		assert(currentStatus.contentEquals(expectedStatus));

		int currentUserCount = data.getInt("Count");
		assert(currentUserCount == expectedUserCount);
	}


}
