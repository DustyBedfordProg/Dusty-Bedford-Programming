package Stratonica.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Stats001ApplicationController {


    @GetMapping("/api/data")
    @ResponseBody
    public List<MyData> getData() {
    	return GetJSON.sendPostRequest();
    }
}
class GetJSON {
	
	private static org.json.JSONObject createJSONRequest(String command, String objID) {
		org.json.JSONObject requestJson = new org.json.JSONObject();
		requestJson.put("command", command);
		requestJson.put("id", objID);
		return requestJson;
	
	}

    // Method to send POST request and return response
    public static List<MyData> sendPostRequest() {
        String response = ""; // Response from the server
        
        List<MyData> rtn = new ArrayList<MyData>();
        
        try {
        	
        	java.net.URL url = new java.net.URL("http://localhost:8010/json");
        	java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

        	conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
        
			org.json.JSONObject requestJson = createJSONRequest("all_group_info", "test");

			// Send JSON request
			java.io.OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
		

			// Read JSON response
			java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));

			String line;
			while ((line = br.readLine()) != null) {
				response += line;
			}
	
			br.close();
            conn.disconnect();
	        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JSONObject response1 = new JSONObject(response);
        

		org.json.JSONArray result = response1.getJSONArray("result");
		org.json.JSONObject interim = result.getJSONObject(0);
		org.json.JSONArray values = interim.getJSONArray("Group");
		
		for (int i = 0; i < values.length(); i++) {
			
			
			org.json.JSONObject group = values.getJSONObject(i);
			String name = group.getString("Name");
			org.json.JSONObject data = group.getJSONObject("Data");
			String status = data.getString("Status");

			rtn.add(new MyData(name, status, data.getInt("Count")));
		}
        
        return rtn;
    }
}
class MyData {
    private String group;
    private String state;
    private int users;

    public MyData(String group, String state, int users) {
        this.group = group;
        this.state = state;
        this.users = users;
    }

    // Getters and Setters
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }
}