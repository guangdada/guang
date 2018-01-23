package com.ikoori.escp;

import java.util.Map;

public class PrintJob {

	private int missionType;//任务类型：0=普通订单 1=预定单 2=支付订单 3=警告单 4=test单
	private Map<String, Object> param;//需要打印的参数--可扩展
	private String ip;//posIP地址
	
	public PrintJob(int missionType, Map<String, Object> param, String ip) {
		this.missionType = missionType;
		this.param = param;
		this.ip = ip;
	}

	public int getMissionType() {
		return missionType;
	}

	public void setMissionType(int missionType) {
		this.missionType = missionType;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
