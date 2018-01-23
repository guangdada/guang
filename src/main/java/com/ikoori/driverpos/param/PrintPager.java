package com.ikoori.driverpos.param;

import java.awt.Font;
import java.util.List;

/**
 * 页面参数
 * @author zhulinfeng
 * @时间 2016年9月23日下午1:02:01
 *
 */
public class PrintPager {
	/** 字体样式(默认:SansSerif)*/
	public String fontFamily = "宋体";
	/** 起始位置横向偏移量(默认:10)*/
	public Integer offsetX = 5;
	/** 起始位置纵向偏移量(默认:10)*/
	public Integer offsetY = 10;
	/** 纸张宽度(默认:80 * 2.1)*/
	//public Integer pagerWidth = (int) (58 * 2.1) - (int)(offsetX * 2);
	public Integer pagerWidth = (int) (58 * 2.1);
	/** 纸张高度(默认:80 * 2.1)*/
	public Integer pagerHeight = (int) (1000 * 2);
	/** 字体大小(默认:12)*/
	public int fontSize	= 10;
	/** 打印内容项(默认:无)*/
	public List<_PagerBody> list;
	/** 字体样式(默认:平滑)*/
	public int fontStyle = Font.PLAIN;
	
	public PrintPager(int type){
		this.pagerWidth = (int)(type * 2.1);
	}
	
	private String driver;
	
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}
	public void setPagerWidth(Integer pagerWidth) {
		this.pagerWidth = pagerWidth;
	}
	public void setPagerHeight(Integer pagerHeight) {
		this.pagerHeight = pagerHeight;
	}
	public void setOffsetX(Integer offsetX) {
		this.offsetX = offsetX;
	}
	public void setOffsetY(Integer offsetY) {
		this.offsetY = offsetY;
	}
	public void setList(List<_PagerBody> list) {
		this.list = list;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
}
