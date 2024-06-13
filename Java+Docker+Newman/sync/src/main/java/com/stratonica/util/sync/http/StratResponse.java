package com.stratonica.util.sync.http;

import org.json.JSONObject;

public class StratResponse {
	
	private JSONObject data = null;
	private int responseCode = 200;
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	

}
