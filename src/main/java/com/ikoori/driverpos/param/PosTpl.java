package com.ikoori.driverpos.param;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 模板配置参数
 * @author zhulinfeng
 * @时间 2016年9月23日下午1:01:30
 *
 */
public class PosTpl {

	//头文本参数
    private List<JSONObject> header;
    //商品属性
    private List<Goods> goods;
    //警告信息
    private List<JSONObject> warn;
    //提示信息
    private List<JSONObject> msg;

	public List<JSONObject> getWarn() {
		return warn;
	}
	
	public void setWarn(List<JSONObject> warn) {
		this.warn = warn;
	}

    public List<JSONObject> getHeader() {
        return header;
    }

    public void setHeader(List<JSONObject> header) {
        this.header = header;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

	public List<JSONObject> getMsg() {
		return msg;
	}

	public void setMsg(List<JSONObject> msg) {
		this.msg = msg;
	}

    
}
