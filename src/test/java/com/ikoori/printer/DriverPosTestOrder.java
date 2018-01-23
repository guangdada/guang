package com.ikoori.printer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ikoori.driverpos.DriverPos;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;

public class DriverPosTestOrder extends BaseTest{
	
	@Override
	public void test() {
		try {
			DriverPos pos = new DriverPos();
			String json = readTxt(PathKit.getRootClassPath() + "/driverpos/test.json", "utf-8");
			System.out.println(json.replace("	", ""));
			pos.print(json, jsonParam(), "BTP-2002CP(E)");
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public static String jsonParam(){
		Map<String, Object> template = new HashMap<String, Object>();
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put("title", "测试单");
		
		template.put("keys", keys);
		String temp = JsonKit.toJson(template);
		System.out.println(temp);
		return temp;
	}
}
