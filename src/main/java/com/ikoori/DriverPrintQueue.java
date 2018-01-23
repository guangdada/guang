package com.ikoori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ikoori.driverpos.PrintJob;
import com.ikoori.driverpos.PrintQueue;
import com.ikoori.driverpos.PrintThread;
import com.ikoori.util.ResponseBody;
import com.ikoori.util.WXPayUtil;
import com.ikoori.util._MappingKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;

public class DriverPrintQueue{
	static String signKey = "123456"; // 加密秘钥
	static String url = "http://localhost/web/ticket/getTemplate"; // 模板地址
	static Logger logger = LoggerFactory.getLogger(DriverPrintQueue.class);
	
	static{
		String path = DriverPrintQueue.class.getClassLoader().getResource("cache.db").getPath();
		logger.info(path);
		String dbUrl = "jdbc:sqlite:"+path;
		C3p0Plugin c3p0Plugin = new C3p0Plugin(dbUrl, "", "");
		c3p0Plugin.setDriverClass("org.sqlite.JDBC");
		c3p0Plugin.start();

		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		arp.setDialect(new Sqlite3Dialect());
		_MappingKit.mapping(arp);
		arp.start();
		
		logger.info("printThread is start : " + System.currentTimeMillis());
		
		//发起打印命令
		PrintThread.start();
	}
	
	/**
	 * @Title: 添加一个打印任务   
	 * @param storeNo 店铺编号
	 * @param orderNo 订单号
	 * @date:   2017年12月29日 下午4:39:10 
	 * @author: chengxg
	 */
	public static void addJob(String storeNo,String orderNo) {
		//mySleep(5);
		Map<String, Object> ticket = getCacheTemplate(storeNo);
		if(ticket == null){
			logger.info("no cache");
			ticket = getRemoteTemplate(storeNo);
			flushCache(ticket);
		}
		if(ticket == null){
			logger.info("没有查到模板");
			return;
		}
		String template = ticket.get("template").toString();
		String params = ticket.get("params").toString();
		Map<String, Object> param = jsonParam(orderNo,params);
		PrintQueue.add(new PrintJob(template, param, "BTP-2002CP(E)"));
		//mySleep(15);
	}
	
	public static void mySleep(int i) {
		try {
			logger.info("sleep" + i*1000 + "ms ...");
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据店铺编号获取小票模板
	 * @Title: getTemplate   
	 * @param storeNo
	 * @return
	 * @date:   2017年12月29日 下午6:19:51 
	 * @author: chengxg
	 */
	public static Map<String, Object> getCacheTemplate(String storeNo){
		Record record = Db.findById("ticket", "storeno", storeNo);
		if(record !=null){
			Map<String, Object> ticket = new HashMap<String, Object>();
			ticket.put("storeno", record.get("storeno"));
			ticket.put("template", record.get("template"));
			ticket.put("params", record.get("params"));
			return ticket;
		}
		return null;
	}
	

	/**
	 * 删除本地缓存
	 * @Title: deleteCache   
	 * @param storeno
	 * @date:   2017年12月30日 上午12:01:14 
	 * @author: chengxg
	 */
	public static void deleteCache(String storeno){
		Db.deleteById("ticket", "storeno", storeno);
	}
	
	/**
	 * 刷新本地缓存
	 * @Title: flushCache   
	 * @param template
	 * @return
	 * @date:   2017年12月29日 下午6:43:20 
	 * @author: chengxg
	 */
	public static boolean flushCache(Map<String, Object> ticket){
		boolean result = false;
		if(ticket != null){
			Record record = new Record();
			record.set("storeno", ticket.get("storeno"));
			record.set("template", ticket.get("template"));
			record.set("params", ticket.get("params"));
			result= Db.save("ticket", record);
		}
		return result;
	}
	
	/**
	 * 获得服务器小票模板数据
	 * @Title: getRemoteTemplate   
	 * @param storeNo
	 * @return
	 * @date:   2017年12月29日 下午6:43:37 
	 * @author: chengxg
	 */
	public static Map<String, Object> getRemoteTemplate(String storeNo){
		Map<String, Object> ticket = null;
		try {
			Map<String, String> queryParas = new HashMap<String,String>();
			queryParas.put("storeNo", storeNo);
			String sign = WXPayUtil.generateSignature(queryParas, signKey);
			queryParas.put("sign", sign);
			
			String result = HttpKit.post(url, queryParas, "");
			ResponseBody body = JSON.parseObject(result, ResponseBody.class);
			logger.info(body.getMsg());
			if(body.getCode().equals("200")){
				ticket = new HashMap<String, Object>();
				ticket.put("storeno", storeNo);
				ticket.put("template", body.getTemplate());
				ticket.put("params", body.getParams());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ticket;
	}
	
	
	/**
	 * 初始化参数
	 * @Title: jsonParam   
	 * @param orderNo
	 * @param params
	 * @return
	 * @date:   2017年12月29日 下午6:51:01 
	 * @author: chengxg
	 */
	public static Map<String, Object> jsonParam(String orderNo,String params){
		Map<String, Object> template = new HashMap<String, Object>();
		Map<String, Object> keys = JSON.parseObject(params, new TypeReference<HashMap<String,Object>>() {});
		String posResult = "点菜成功！1001 无此菜 本次点菜3/3份。其中2成功，1失败。合计XXXX元祝您用餐愉快！欢迎下次光临";
		keys.put("title", "网络订单");
		keys.put("brandName", "智慧餐厅");
		keys.put("shopName", "天山店");
		keys.put("tableNumb", "0002");
		keys.put("tableName", "外卖1");
		keys.put("orderId", "1609101220001");
		keys.put("dateTime", "2016-09-10 12:21:00");
		keys.put("allPrice", "66.88");
		keys.put("barCode","7255");
		keys.put("posTitle", "收银软件下单结果");
		keys.put("posMsg", posResult);
		
		List<Map<String, Object>> goods = new ArrayList<Map<String,Object>>();
/*		List<Record> menu = Db.find("select * from menu");
//		int count = 0;
		for (Record record : menu) {
			Map<String, Object> good = new HashMap<String, Object>();
			good.put("code", record.get("code"));
			good.put("name", record.get("name"));
			good.put("quantity", "1.0");
			good.put("price", record.get("price"));
//			good.put("qrcode", "1.png");
//			if(count!=1){
//				good.put("remark", "免葱、免辣");
//			}
//			count++;
			goods.add(good);
		}*/
		
		template.put("goods", goods);
		template.put("keys", keys);
		return template;
	}
	
	public static void main(String[] args) {
		//getRemoteTemplate("193096c4358e4e8a822fe63fdb7adf79");
		/*Map<String, Object> ticket = new HashMap<String, Object>();
		ticket.put("storeno", "storeno1");
		ticket.put("template", "template");
		ticket.put("params", "params");
		flushCache(ticket);*/
		
		//deleteCache("193096c4358e4e8a822fe63fdb7adf79");
		//System.out.println(getCacheTemplate("193096c4358e4e8a822fe63fdb7adf79"));
		
		//ImageRequest.down("http://localhost/files/2e03da02-b60c-495c-a4c4-1cfe7ed6cb92.jpg");
	}

}
