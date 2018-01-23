package com.ikoori.printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ikoori.driverpos.DriverPos;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class DriverPosSimple extends BaseTest{
	
	@Override
	public void test() {
		try {
			DriverPos pos = new DriverPos();
			String json = readTxt(PathKit.getRootClassPath() + "/driverpos/simple.json", "utf-8");
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
		String posResult = "点菜成功！1001 无此菜 本次点菜3/3份 2成功，1失败合计XX元祝您用餐愉快a";
		keys.put("title", "网络订单");
		keys.put("brandName", "智慧餐厅");
		keys.put("shopName", "天山店");
		keys.put("tableNumb", "0002");
		keys.put("tableName", "外卖1");
		keys.put("orderId", "1609101220001");
		keys.put("dateTime", "2016-09-10 12:21:00");
		keys.put("allPrice", "66.88");
		keys.put("barCode","7255");
		keys.put("path", "1.png");
		keys.put("remark", "免葱，免辣");
		
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
				keys.put("warnMsg", "部分菜品未下成功请联系服务员人工处理");
			}
		}
		
		keys.put("posTitle", "收银软件下单结果");
		keys.put("posMsg", posResult);
		
		List<Map<String, Object>> goods = new ArrayList<Map<String,Object>>();
		List<Record> menu = Db.find("select * from menu");
		int count = 0;
		for (Record record : menu) {
			Map<String, Object> good = new HashMap<String, Object>();
			good.put("code", record.get("code"));
			good.put("name", record.get("name"));
			good.put("quantity", "1.0");
			good.put("price", record.get("price"));
			good.put("qrcode", "1.png");
			if(count!=1){
				good.put("remark", "免葱、免辣");
			}
			count++;
			goods.add(good);
		}
		
		template.put("goods", goods);
		template.put("keys", keys);
		String temp = JsonKit.toJson(template);
		System.out.println(temp);
		return temp;
	}
}
