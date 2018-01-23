package com.ikoori.driverpos.param;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.swing.ImageIcon;

import com.ikoori.util.ImageRequest;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;

/**
 * 驱动打印
 * @author zhulinfeng
 * @时间 2016年9月22日上午11:11:22
 *
 */
public class Printer implements Printable {
	
	private PrintPager printPager;
	
	public Printer() {
	}
	
	/**
	 * 打印任务
	 * @param printPager	页面对象
	 * @param printerName	打印机名称(Windows控制面板-->设备和打印机-->打印机名称) 支持共享打印机
	 */
	public void printJob(PrintPager printPager, String printerName){
		try{
			if(printPager==null)return;
			int printSize = 1;
			
			this.printPager = printPager;
			final PrinterJob pj = PrinterJob.getPrinterJob();//创建一个打印任务
	        PageFormat pf = PrinterJob.getPrinterJob().defaultPage();
	        Paper paper = pf.getPaper();
	        paper.setSize(printPager.pagerWidth, printPager.pagerHeight);
	        paper.setImageableArea(printPager.offsetX, printPager.offsetY, printPager.pagerWidth, printPager.pagerHeight);
	        pf.setPaper(paper);
			HashAttributeSet hs = new HashAttributeSet();
			hs.add(new PrinterName(printerName, null));
			// 获取打印服务对象
			PrintService[] printService = PrintServiceLookup.lookupPrintServices(null, hs);
			if (printService.length > 0) {
				pj.setPrintService(printService[0]);
			}
			
	        pj.setPrintable(this, pf);
	        for (int i = 0; i < printSize; i++) {
				pj.print(); 
			}
		} catch (Exception e){
			LogKit.error("打印异常", e);
		}
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		System.out.println("print pageIndex : " + pageIndex);
		if (pageIndex>0) return NO_SUCH_PAGE;
		Graphics2D g2 = (Graphics2D)graphics;
		g2.setColor(Color.black);
		int offSetY = printPager.offsetY;
		int offSetX = printPager.offsetX;
		g2.translate(offSetX, offSetY);
		//处理内容项
		offSetY = printBody(g2, pageFormat, offSetX, offSetY);
		
		return PAGE_EXISTS;
	}
	
	/**
	 * 
	 * @param g2			画笔
	 * @param pageFormat	页面
	 * @param offSetX		起始坐标x
	 * @param offSetY		起始坐标y
	 * @return
	 */
	private int printBody(Graphics2D g2, PageFormat pageFormat,int offSetX, int offSetY){
		List<_PagerBody> list = printPager.list;
		
		float[] dash = { 2.0f };
		
		// 设置打印线的属性。虚线="线+缺口+线+缺口+线+缺口……" 
		// 1.线宽 2.不同的线端 3.当两条线连接时，连接处的形状 4.缺口的宽度(默认10.0f) 5.虚线的宽度 6.偏移量
		g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dash, 0.0f));
		
		if (list!=null && list.size()>0) {
			for (_PagerBody body : list) {
				if (body.getImg()!=null) {
					PagerImages qrcode = body.getImg();
					if (StrKit.notBlank(qrcode.getPath())) {
						String path = ImageRequest.down(qrcode.getPath());
						/*ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(PathKit.getWebRootPath()+"/img/"+qrcode.getPath()));*/
						ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
						int drawWidth = (int)pageFormat.getImageableWidth();
						pageFormat.getImageableHeight();
						if (body.getAlign().equals(PrintAlignment.center)) {
							offSetX = (drawWidth-qrcode.getWidth())/2;
						} else if (body.getAlign().equals(PrintAlignment.right)) {
							offSetX = drawWidth-body.getImg().getWidth() - 20;
						}
						g2.drawImage(icon.getImage(), offSetX, offSetY+5, qrcode.getWidth(), qrcode.getHeight(), icon.getImageObserver());
					}
					if (body.isFeeLine()) {
						offSetX = printPager.offsetX;
						// 5=行间距
						offSetY+=qrcode.getHeight() + 5;
					}else{
						offSetX+=qrcode.getWidth()+printPager.offsetX;
						offSetY+=qrcode.getHeight()/2 + 5;
					}
					continue;
				}
				
				//设置字体
				Font font = new Font(printPager.fontFamily,body.getFontStyle(),body.getFontSize());
				g2.setFont(font);
				//字体高度  
				float heigth = font.getSize2D();
				
				String str = body.getContent();
				//宽度占比(将一行10等分)
				int width = body.getWidth();
				// 实际占的宽度
				int realWidth = (int) Math.floor(printPager.pagerWidth * width * 0.1);
				
				//文本宽度
				int strWidth = g2.getFontMetrics().stringWidth(str);
				
				if (body.getAlign().equals(PrintAlignment.center)) {
					offSetX += (printPager.pagerWidth - strWidth)/2;
				} else if (body.getAlign().equals(PrintAlignment.right)) {
					offSetX =  printPager.offsetX;
					// paperWidth = offsexX * 2 + 可以打印宽度
					offSetX += printPager.pagerWidth - strWidth - printPager.offsetX * 2;
				}
				
				// 不能超过设定的宽度或者页面宽度
				int maxWidth = realWidth == 0 ? printPager.pagerWidth : realWidth;
				String[] content = body.isDrawline() ? addDottedStr(g2.getFontMetrics(), str, maxWidth, strWidth)
						: getChangeStr(g2.getFontMetrics(), str, maxWidth, strWidth);
				for(int i = 0;i<content.length;i++){
					if(content[i] == null){
						continue;
					}
					//绘制文本
					g2.drawString(content[i], offSetX, offSetY+heigth);
					//换行
					if(content.length>1){
						offSetX = printPager.offsetX;
						// 最后一行不换行
						if(i != content.length - 1){
							offSetY += heigth;
						}
					}
				}
				
				if (body.isFeeLine()) {//换行
					offSetX = printPager.offsetX;
					offSetY += heigth;
				}else{ //不换行
					if(content.length>1){
						offSetX += realWidth;
					}else{
						offSetX += realWidth >= strWidth ? realWidth : strWidth;
					}
				}
			}
		}
		return offSetY;
	}
	
	/**
	 * 画虚线,补齐一行
	 * @param metrics 字体属性
	 * @param str 要打印的文本
	 * @param pageWidth 打印页面宽度
	 * @param strWidth 文本在page里的宽度--不同字体大小宽度不一，使用metrics.stringWidth(str)获取
	 * @return
	 * @date:   2017年12月29日 下午1:30:34 
	 * @author: chengxg
	 */
	private static String[] addDottedStr(FontMetrics metrics, String str, int pageWidth, int strWidth){
		int dotWidth = metrics.stringWidth("-");
		int count = (int) (Math.ceil((pageWidth - strWidth) * 1.0 / dotWidth) / 2) ;
		StringBuilder dottedStr = new StringBuilder("");
		for(int i = 0 ;i < count ; i++){
			dottedStr.append("-");
		}
		
		return new String[] {dottedStr.toString() + str + dottedStr.toString()};
	}
	
	/**
	 * 
	 * @param metrics		字体属性
	 * @param str			要打印的文本
	 * @param pageWidth		打印页面宽度
	 * @param strWidth		文本在page里的宽度--不同字体大小宽度不一，使用metrics.stringWidth(str)获取
	 * @return
	 */
	private static String[] getChangeStr(FontMetrics metrics, String str, int pageWidth, int strWidth){
//		int StrPixelWidth = strWidth; // 字符串长度（像素） str要打印的字符串
		int lineSize = (int) Math.ceil(strWidth * 1.0 / pageWidth);// 要多少行
		lineSize = lineSize==0?1:lineSize;
		// 存储换行之后每一行的字符串
		String tempStrs[] = new String[lineSize];
		if (pageWidth < strWidth) {// 页面宽度（width）小于 字符串长度
			StringBuilder sb = new StringBuilder();// 存储每一行的字符串
			int j = 0;
			int tempStart = 0;
			for (int i = 0; i < str.length(); i++) {
				char ch = str.charAt(i);
				sb.append(ch);
				int tempStrPi1exlWi1dth = metrics.stringWidth(sb.toString());
				if (tempStrPi1exlWi1dth > pageWidth) {
					tempStrs[j++] = str.substring(tempStart, i);
					tempStart = i;
					sb.delete(0, sb.length());
					sb.append(ch);
				}
				if (i == str.length() - 1) {// 最后一行
					tempStrs[j] = str.substring(tempStart);
				}
			}
		}else{
			//tempStrs[0] = str==null ? " " : str;
			tempStrs[0] = str;
		}
		return tempStrs;
	}
}
