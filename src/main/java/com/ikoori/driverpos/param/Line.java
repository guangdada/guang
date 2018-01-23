package com.ikoori.driverpos.param;

/**
 * 文本配置参数
 * @author zhulinfeng
 * @时间 2016年9月23日下午1:02:22
 *
 */
public class Line {
	// 打印文本内容
    private String text;
    // 打印内容类型
    private int type;
    // 换行
    private boolean line;
    
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isLine() {
		return line;
	}
	public void setLine(boolean line) {
		this.line = line;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
