package com.ikoori.escp.params;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 模板配置参数
 *
 * @author SubLuLu
 */
public class PosTpl {

    private List<JSONObject> header;

    private List<Goods> goods;

    private List<JSONObject> warn;

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
