package com.ikoori.driverpos;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 打印队列
 * @author zhulinfeng
 * @时间 2016年9月23日下午1:00:03
 *
 */
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
		System.out.println("add print job...");
		return queue.add(printJob);
	}
	
	/**
	 * 获取打印任务，无任务时挂起
	 * @return
	 * @throws InterruptedException
	 */
	public static PrintJob take() throws InterruptedException{
		System.out.println("take a print job...");
		return queue.take();
	}

}
