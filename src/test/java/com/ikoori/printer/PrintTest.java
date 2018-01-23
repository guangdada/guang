package com.ikoori.printer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ikoori.escp.EscPos;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class PrintTest extends BaseTest{
	
	@Override
	public void test() {
		long start = 0;
		try {
			start = System.currentTimeMillis();
			System.out.println("=======begin print job=========");
			EscPos pos = EscPos.getInstance("192.168.0.50");
			String json = readTxt(PathKit.getRootClassPath() + "/escpos/JsonTemplate.json", "utf-8");
			System.out.println(json);
			pos.print(json, jsonParam());
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
		} finally {
			long end = System.currentTimeMillis();
			System.out.println("core time:" + (end - start));
			System.out.println("=======end print job=========");
		}
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static String jsonParam(){
		Map<String, Object> template = new HashMap<String, Object>();
		Map<String, Object> keys = new HashMap<String, Object>();
		String posResult = "点菜成功！1001 无此菜";
		keys.put("title", "网络订单");
		keys.put("brandName", "智慧餐厅");
		keys.put("shopName", "天山店");
		keys.put("tableNumb", "0002");
		keys.put("tableName", "外卖1");
		keys.put("orderId", "1609101220001");
		keys.put("dateTime", "2016-09-10 12:21:00");
		keys.put("allPrice", "66.88");
		keys.put("barCode","7255");
		
		if(!posResult.contains("成功")){
			if(posResult.contains("重单")){
				keys.put("warnTitle", "提    示");
				keys.put("warnMsg", "该订单已处理");
			}else{
				keys.put("warnTitle", "异常提示");
				keys.put("warnMsg", "自动下单失败请人工处理");
			}
		}else{
			if(posResult.contains("无此") || posResult.contains("沽清")
					|| posResult.contains("不存在")){
				keys.put("warnTitle", "异常提示");
				keys.put("warnMsg", "部分菜品未下成功人工处理");
			}
		}
		
		keys.put("posTitle", "收银软件下单结果");
		keys.put("posMsg", posResult);
		
		
		List<Map<String, Object>> goods = new ArrayList<Map<String,Object>>();
		List<Record> menu = Db.find("select * from menu");
		for (Record record : menu) {
			Map<String, Object> good = new HashMap<String, Object>();
			good.put("code", record.get("code"));
			good.put("name", record.get("name"));
			good.put("quantity", "1.0");
			good.put("price", record.get("price"));
			goods.add(good);
		}
		
		template.put("keys", keys);
		template.put("goods", goods);
		template.put("keys", keys);
		template.put("source", "DP");
		String temp = JsonKit.toJson(template);
		System.out.println(temp);
		return temp;
	}
	
	public static String readTxt(String filePathAndName, String encoding)
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
		} catch (IOException es) {
			st = "";
		}
		return st;
	}
}
