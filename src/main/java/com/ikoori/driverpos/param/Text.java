package com.ikoori.driverpos.param;

/**
 * 文本配置参数
 * @author zhulinfeng
 * @时间 2016年9月23日下午1:02:22
 *
 */
public class Text {

    // 打印内容类型
    private int type;
    // 对齐方式 居左、居中、居右
    private int align;
    // 换行
    private boolean line;
    // 打印文本内容
    private String text;
    // 文本字体大小
    private int size;
    // 文本是否加粗
    private boolean bold;
    
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAlign() {
		return align;
	}
	public void setAlign(int align) {
		this.align = align;
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
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}

}
