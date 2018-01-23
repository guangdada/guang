package com.ikoori.driverpos.param;

/**
 * 图片参数
 * @author zhulinfeng
 * @时间 2016年9月23日下午1:01:06
 *
 */
public class PagerImages {
	
	private int type;
	// 条码或二维码图片路径
	private String path;
	// 条码宽度
	private int width = 80;
	// 条码高度
	private int height = 30;
	// 打印位置
	private int align;
	// 换行
	private boolean line;
	
	public PagerImages() {
		
	}
	
	public PagerImages(String path, int width, int height) {
		this.type = 1;
		this.path = path;
		this.width = width;
		this.height = height;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
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

	
}
