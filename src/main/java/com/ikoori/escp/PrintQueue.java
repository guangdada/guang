package com.ikoori.escp;

import java.util.concurrent.LinkedBlockingQueue;

public class PrintQueue {
	
	/**
	 * 任务队列--阻塞队列--默认长度限制（ Integer.MAX_VALUE）
	 */
	private static LinkedBlockingQueue<PrintJob> queue = new LinkedBlockingQueue<PrintJob>();
	
	/**
	 * 添加打印任务至队列
	 * @param printJob
	 * @return
	 */
	public static boolean add(PrintJob printJob){
		return queue.add(printJob);
	}
	
	/**
	 * 获取打印任务，无任务时挂起
	 * @return
	 * @throws InterruptedException
	 */
	public static PrintJob take() throws InterruptedException{
		return queue.take();
	}

}
