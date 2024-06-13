package com.stratonica.util.sync.http;

import java.util.HashMap;
import java.util.Date;
 
/**
 * Sync Server, based upon server code at:
 * 
 * @author www.codejava.net
 */
public class SyncServer {
	
	HashMap<String, GroupInfo> groupMap = new HashMap<String, GroupInfo>();

	public SyncServer() {
	}

	public void log(String msg) {
		System.out.println(curTime() + ": " + msg);
	}

	private long curTime() {
	     return (new Date()).getTime();
	}

}