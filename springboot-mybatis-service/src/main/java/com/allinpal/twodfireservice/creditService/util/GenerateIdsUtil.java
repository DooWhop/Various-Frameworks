package com.allinpal.twodfireservice.creditService.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流水号获取工具类
 * 
 * @author Shawn
 *
 */
public final class GenerateIdsUtil {
	/**
	 * 线程安全计数器
	 */
	private static AtomicInteger counter = new AtomicInteger(1);
	private static AtomicInteger keyCounter = new AtomicInteger(1);
	private static AtomicInteger traceCounter = new AtomicInteger(1);

	/**
	 * 根据ip获取流水号
	 * 
	 * @param ip
	 * @return
	 */
	public static String generateId(String ip) {
		String id = String.valueOf(System.currentTimeMillis());

		counter.compareAndSet(10000, 1);

		String[] sub2 = ip.split("\\.");
		String sub3 = String.format("%04d", counter.getAndIncrement());
		id = id + String.format("%03d", Integer.valueOf(sub2[2])) + String.format("%03d", Integer.valueOf(sub2[3]))
				+ sub3;

		return id;
	}

	/**
	 * 生成合同编号，贷款合同则type为HKHT
	 * 
	 * @param type
	 * @return
	 */
	public static synchronized String protocolId(String type) {
		String id = String.valueOf(System.currentTimeMillis());

		keyCounter.compareAndSet(10000, 1);

		String sub3 = String.format("%04d", new Object[] { keyCounter.getAndIncrement() });

		if("0".equals(type)){
			type = "WTKK";
		}else if("1".equals(type)){
			type = "KHFW";
		}else if("2".equals(type)){
			type = "KKSQS";
		}else if("3".equals(type)){
			type = "ZXCX";
		}else if("4".equals(type)){
			type = "DKHT";
		}
		
		id = type + id + sub3;

		return id;
	}

	public static synchronized String sysTraceUID() {
		String id = String.valueOf(System.currentTimeMillis());

		traceCounter.compareAndSet(10000, 1);

		String sub3 = String.format("%04d", new Object[] { traceCounter.getAndIncrement() });
		System.out.println("");
		id = id + "000000" + sub3;
		return id;
	}
}
