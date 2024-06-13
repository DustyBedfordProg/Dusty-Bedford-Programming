package com.stratonica.util.sync.http;

public class GroupInfo {
	
	Integer groupLastID = 0;
	Integer groupCount = null;
	Integer waiting = 0;
	Integer running = 0;
	Boolean serverWait = false;
	String groupStatus = "init";
	
	public synchronized int addWaiting() {
		--running;
		++waiting;
		if (waiting == groupCount) {
			setGroupStatus(SyncBase.GROUP_STATUS_GO);
		}
		return waiting;
	}
	public synchronized int addRunning() {
		++running;
		--waiting;
		if (waiting == 0) {
			setGroupStatus(SyncBase.GROUP_STATUS_WAIT);
			//setGroupStatus(SyncBase.GROUP_STATUS_RUN);
		}
		return running;
	}

	public synchronized void setGroupStatus(String status) {
		groupStatus = status;
	}
	
	public synchronized void setGroupServerWaiting() {
		serverWait = true;
	}
	
	public synchronized Integer getNextID() {
		return ++groupLastID;
	}
	
	public synchronized Integer getGroupCount() {
		return groupCount;
	}
	
	public synchronized void setGroupCount(Integer count) {
		if (groupCount == null) {
			groupCount = count;
			running = count;
		}
	}

	public synchronized String getGroupStatus() {
		return groupStatus;
	}
	public synchronized Boolean getGroupServerWaiting() {
		return this.serverWait;
	}

}
