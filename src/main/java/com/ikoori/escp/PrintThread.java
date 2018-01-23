package com.ikoori.escp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;

public class PrintThread {
	
	public static void start(){
			new Thread(new Runnable() {
				public void run() {
					while(true){
						try {
							PrintJob printJob = PrintQueue.take();
							EscPos pos = printer(printJob.getIp());
							System.out.println("begin print job...");
							pos.print(jsonTemplateByMissionType(printJob.getMissionType()), JsonKit.toJson(printJob.getParam()));
							System.out.println("end print job...");
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}, "print_thread_1").start();
	}
	
	private static EscPos printer(String ip){
		EscPos pos = null;
		try {
			System.out.println("get the printer pos...");
			pos = EscPos.getInstance(ip);
		} catch (ConnectException e) {
			System.out.println("the printer have not connect... please check the power is on");
		} catch (UnknownHostException ne) {
			System.out.println("the printer's IP address is not found...");
		} catch (SocketException e){
			System.out.println("Internet is broken...");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("printer error" + e);
		}
		return pos;
	}
	
	private static String jsonTemplateByMissionType(int missionType) throws IOException{
		String template = "";
		String path = PathKit.getRootClassPath() + "/escpos/";
		String pix = ".json";
		switch (missionType) {
		case 0:
			template = path + "JsonTemplate" + pix;
			break;
		case 1:
			template = path + "localhost" + pix;
			break;
		case 2:
			template = path + "pay" + pix;
			break;
		case 3:
			template = path + "warn" + pix;
			break;
		case 4:
			template = path + "test" + pix;
			break;

		default:
			template = path + "JsonTemplate" + pix;
			break;
		}
		String json = readTxt(template, "utf-8");
		
		return json;
	}
	
	private static String readTxt(String filePathAndName, String encoding)
			throws IOException {
		encoding = encoding.trim();
		StringBuffer str = new StringBuffer("");
		String st = "";
		try {
			FileInputStream fs = new FileInputStream(filePathAndName);
			InputStreamReader isr;
			if (encoding.equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding);
			}
			BufferedReader br = new BufferedReader(isr);
			try {
				String data = "";
				while ((data = br.readLine()) != null) {
					str.append(data);
				}
			} catch (Exception e) {
				str.append(e.toString());
			}
			st = str.toString();
			if (st != null && st.length() > 1)
				st = st.substring(0, st.length());
			
			isr.close();
			br.close();
		} catch (IOException es) {
			st = "";
		}
		return st;
	}
}
