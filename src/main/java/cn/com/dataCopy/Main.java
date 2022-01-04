
/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

/**
 * <p>标题： Main</p>
 * <p>
 *    功能描述：
 *    
 * </p>
 * <p>创建日期：2017年8月14日 上午11:10:35 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public class Main {

	static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:*.xml");
	static JdbcTemplate source = (JdbcTemplate) context.getBean("sourceJdbcTemplate");
	static JdbcTemplate target = (JdbcTemplate) context.getBean("targetJdbcTemplate");
	static String targetTableName;
	static List<String> sourceCols;		// 数据源列名集合
	static Map<String, String> target_sourceMap;	// 目标表列名与数据源列名对应map（key:目标表列名，value:数据源表列名）
	static String insertSql;
	static String searchSql;
	static Object startParam;
	static Object endParam;
	static int interval;
	static String searchType;
	private static int threadAmount;			// 线程数量
	private static int onceInsertAmount;		// 一次向目标数据库插入的最大条数
	
	static {
		Properties confPro = PropsUtil.loadProps("conf.properties");
		searchType = (String) confPro.get("searchType");
		startParam = confPro.get("startParam");
		endParam = confPro.get("endParam");
		interval = Integer.valueOf((String)confPro.get("interval"));
		
		searchSql = (String) confPro.get("searchSql");
		targetTableName = (String) confPro.get("targetTableName");

		Properties columsPro = PropsUtil.loadProps("colums.properties");
		sourceCols = new ArrayList<String>(columsPro.stringPropertyNames());
		target_sourceMap = getTarget_sourceMap(columsPro);
		
		insertSql = getInsertSql(targetTableName, target_sourceMap.keySet());
		
		threadAmount = Integer.valueOf((String)confPro.get("threadAmount"));
		onceInsertAmount = Integer.valueOf((String)confPro.get("onceInsertAmount"));
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		CopyAbstract copy = null;
		if ("ID".equals(searchType)) {
			copy = new CopyById();
		} else if ("TIME".equals(searchType)) {
			copy = new CopyByTime();
		}
		
		copy.handle(source, target, target_sourceMap, startParam, endParam, interval, insertSql, searchSql);
		
		long endTime = System.currentTimeMillis();
		System.out.println("执行完成，总耗时：" + (endTime - startTime) / 1000 + "s");
	}

	private static String getInsertSql(String targetTableName, Set<String> targetCols) {
		StringBuffer sql = new StringBuffer("insert into " + targetTableName + "(");
		StringBuffer params = new StringBuffer();

		for (String col : targetCols) {
			sql.append(col);
			sql.append(",");
			params.append("?,");
		}

		sql.setCharAt(sql.length() - 1, ')');
		params.setCharAt(params.length() - 1, ')');

		sql.append(" VALUES(").append(params.toString());

		return sql.toString();
		
	}

	/**
	 * 将字段对应properties转为以目标表列名为key，数据源表列名为value的map
	 * @param columsPro
	 * @author Taocong
	 * @date 2017年8月14日 下午2:36:33
	 */
	private static Map<String, String> getTarget_sourceMap(Properties columsPro) {
		Map<String, String> map = new HashMap<>();
		
		Iterator<Entry<Object, Object>> it = columsPro.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String sourceCol = (String) entry.getKey();
			String targetCol = (String) entry.getValue();
			if (!StringUtils.isEmpty(targetCol)) {
				map.put(targetCol, sourceCol);
			}
		}
		
		return map;
	}

	public static int getThreadAmount() {
		return threadAmount;
	}

	public static int getOnceInsertAmount() {
		return onceInsertAmount;
	}
	
	public static ApplicationContext getContext() {
		return context;
	}
	
}
