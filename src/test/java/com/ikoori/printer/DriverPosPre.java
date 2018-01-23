package com.ikoori.printer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ikoori.driverpos.DriverPos;
import com.ikoori.util.BarcodeUtil;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;

public class DriverPosPre extends BaseTest{
	
	@Override
	public void test() {
		try {
			URL url = DriverPosPre.class.getClassLoader().getResource("driverpos/preOrder.json");
			DriverPos pos = new DriverPos();
			//String json = readTxt(PathKit.getRootClassPath() + "/driverpos/preOrder.json", "utf-8");
			String json = readTxt(url.getFile(), "utf-8");
			System.out.println("json----" + json.replace("	", ""));
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
		keys.put("title", "网络订单");
		keys.put("brandName", "智慧餐厅");
		keys.put("shopName", "天山店");
		keys.put("tableNumb", "0002");
		keys.put("tableName", "外卖1");
		keys.put("orderId", "1609101220001");
		keys.put("dateTime", "2016-09-10 12:21:00");
		keys.put("allPrice", "66.88");
		keys.put("barCode","7255");
		keys.put("isPayoff", "未支付");
		String path = PathKit.getWebRootPath() + "/img";
		String imgPath = BarcodeUtil.generateImg(path, "7255", 12, false);
		
		//keys.put("path", imgPath.substring(imgPath.lastIndexOf(".")-4));
		keys.put("path", "logo.jpg");
		
		List<Map<String, Object>> goods = new ArrayList<Map<String,Object>>();
		/*List<Record> menu = Db.find("select * from menu");
		for (Record record : menu) {
			Map<String, Object> good = new HashMap<String, Object>();
			good.put("code", record.get("code"));
			good.put("name", record.get("name"));
			good.put("quantity", "1.0");
			good.put("price", record.get("price"));
			goods.add(good);
		}*/
		
		template.put("goods", goods);
		template.put("keys", keys);
		String temp = JsonKit.toJson(template);
		System.out.println(temp);
		return temp;
	}
}
