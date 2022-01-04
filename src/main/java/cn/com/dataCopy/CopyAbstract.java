
/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * <p>标题： PageAbstract</p>
 * <p>
 *    功能描述：
 *    
 * </p>
 * <p>创建日期：2017年8月14日 上午10:56:42 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public abstract class CopyAbstract {
	public void handle(JdbcTemplate source, JdbcTemplate target, Map<String, String> target_sourceMap, Object startParam,
			Object endParam, int interval, String insertSql, String searchSql) {
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Main.getThreadAmount());
		while (true) {
			if (!judgeParam(startParam, endParam)) {
				break;
			}

			// 获取间隔区间后的结果作为一次查询的结束条件
			Object intervalEndParam = getIntervalEndParam(startParam, endParam, interval);

			MyThread thread = new MyThread(source, target, target_sourceMap, startParam, intervalEndParam, insertSql, searchSql);

			pool.execute(thread);

			while (pool.getQueue().size() > 4) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			startParam = getNewStartParam(intervalEndParam);
		}

		pool.shutdown();
		while (true) {
			if (pool.isTerminated())
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断条件区间是否有效
	 * @param startParam
	 * @param endParam
	 * @return
	 * @author Taocong
	 * @date 2017年8月14日 下午3:33:53
	 */
	abstract boolean judgeParam(Object startParam, Object endParam);

	/**
	 * 获取间隔区间后的结果作为一次查询的结束条件
	 * @param startParam	开始条件
	 * @param endParam		结束条件
	 * @param interval		间隔
	 * @return
	 * @author Taocong
	 * @date 2017年8月14日 下午3:31:58
	 */
	abstract Object getIntervalEndParam(Object startParam, Object endParam,  int interval);
	
	/**
	 * 获取下一次的开始条件
	 * @param intervalEndParam	本次的结束条件
	 * @return
	 * @author Taocong
	 * @date 2017年8月14日 下午3:31:43
	 */
	abstract Object getNewStartParam(Object intervalEndParam);
}
