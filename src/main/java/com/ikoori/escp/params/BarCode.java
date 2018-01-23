package com.ikoori.escp.params;


/**
 * 条形码配置参数
 *
 * @author SubLuLu
 */
public class BarCode {

    // 打印内容类型
    private int type;
    // 条形码数字
    private String text;
    // 对齐方式 居左、居中、居右
    private int format;
    // 空行行数
    private int line;
    // 宽
    private int width;
    // 高
    private int height;
    // 字体显示 1=上方 2=下方 
    private int fontShow;
    
    private int posType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
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

	public int getFontShow() {
		return fontShow;
	}

	public void setFontShow(int fontShow) {
		this.fontShow = fontShow;
	}

	public int getPosType() {
		return posType;
	}

	public void setPosType(int posType) {
		this.posType = posType;
	}

    
}
