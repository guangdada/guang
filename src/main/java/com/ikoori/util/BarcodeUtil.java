package com.ikoori.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * 生成 条码 工具类
 * @author shich
 *
 */
public class BarcodeUtil {
	/**
	 * 生成 条码
	 * @param out    	输出流
	 * @param code   	条码内容
	 * @param height 	高度
	 * @param dpi	 	分辨率
	 * @param showFont  是否显示字体
	 */
	public static void generate(OutputStream out, String code, int height, int dpi,
			boolean showFont) {
		try {
			Code128Bean bean = new Code128Bean();
			bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); // makes the narrow
			bean.setHeight(height);
			bean.doQuietZone(false);
			if (showFont) {
				bean.setFontSize(2);
			} else {
				bean.setFontSize(0);
			}

			BitmapCanvasProvider canvas = new BitmapCanvasProvider(out,
					"image/jpeg", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
			bean.generateBarcode(canvas, code);
			canvas.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据配置文件中的  dpi 生成 条码
	 * @param out
	 * @param code
	 * @param height
	 * @param showFont
	 */
	public static void generate(OutputStream out, String code, int height,
			boolean showFont) {
		try {
			int dpi = 150;
			generate(out, code, height, dpi, showFont);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据 指定 路径生成条码图片
	 * @param path
	 * @param code
	 * @param height
	 * @param showFont
	 * @return
	 */
	public static String generateImg(String path, String code, int height,
			boolean showFont) {
		OutputStream out = null;
		File outputFile = new File(path);
		if (!outputFile.exists()) {
			outputFile.mkdirs();
		}
		String filePath = outputFile.getAbsolutePath() + File.separator + code
				+ ".png";
		try {
			out = new FileOutputStream(filePath);
			generate(out, code, height, showFont);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return filePath;
	}
}