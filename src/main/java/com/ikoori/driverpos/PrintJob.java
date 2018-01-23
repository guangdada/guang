package com.ikoori.driverpos;

import java.util.Map;

/**
 * 打印任务
 * @author zhulinfeng
 * @时间 2016年9月23日下午12:59:57
 *
 */
public class PrintJob {
	/**
	 * 模板
	 */
	private String template;
	/**
	 * 任务类型：0=普通订单 1=预定单 2=支付订单 3=警告单 4=test单
	 */
	private int missionType;
	
	/**
	 * 需要打印的参数--可扩展
	 */
	private Map<String, Object> param;
	
	/**
	 * 打印机逻辑名称
	 */
	private String printerName;
	
	public PrintJob(String template, Map<String, Object> param, String printerName) {
		this.template = template;
		this.param = param;
		this.printerName = printerName;
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

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	
	
}
