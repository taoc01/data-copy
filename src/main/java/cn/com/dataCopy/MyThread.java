
/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import cn.com.dataCopy.handler.HandlerUtil;

/**
 * <p>标题： MyThread</p>
 * <p>
 *    功能描述：
 *    		线程类：执行数据查询与插入
 * </p>
 * <p>创建日期：2017年8月14日 上午9:51:13 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public class MyThread implements Runnable {
	
	private final static String SEARCH_START_REPLACE_STR = "#_START_#";
	private final static String SEARCH_END_REPLACE_STR = "#_END_#";

	private JdbcTemplate target;
	private JdbcTemplate source;
	private Object startParam;
	private Object endParam;
	private String insertSql;
	private String searchSql;
	private Map<String, String> target_sourceMap;

	public MyThread(JdbcTemplate source, JdbcTemplate target, Map<String, String> target_sourceMap, Object startParam, Object endParam,
			String insertSql, String searchSql) {
		this.source = source;
		this.target = target;
		this.target_sourceMap = target_sourceMap;
		this.startParam = startParam;
		this.endParam = endParam;
		this.insertSql = insertSql;
		this.searchSql = searchSql;
		
		initSearchSql();
	}

	/**
	 * 将参数占位替换为完成的sql
	 * @author Taocong
	 * @date 2017年8月14日 上午10:14:44
	 */
	private void initSearchSql() {
		if (StringUtils.isEmpty(searchSql))
			return;
		searchSql = searchSql.replace(SEARCH_START_REPLACE_STR, String.valueOf(startParam));
		searchSql = searchSql.replace(SEARCH_END_REPLACE_STR, String.valueOf(endParam));
	}

	@Override
	public void run() {
		System.out.println("执行id:" + startParam + "~" + endParam);
		List<Map<String, Object>> sourceList = search(source);
		save2Target(target, target_sourceMap, sourceList, insertSql);
	}

	List<Map<String, Object>> search(JdbcTemplate source) {
		return source.queryForList(searchSql);
	}

	private void save2Target(JdbcTemplate target, Map<String, String> target_sourceMap, List<Map<String, Object>> sourceList,
			String insertSql) {
		List<Object[]> batchArgs = new ArrayList<>(Main.getOnceInsertAmount());

		int offset = 0;
		long cur = System.currentTimeMillis();
		for (Map<String, Object> sourceObjs : sourceList) {

			List<Object> targetObjs = new ArrayList<>();
			Set<String> targetCols = target_sourceMap.keySet();
			for (String col : targetCols) {
				String sourceColumnName = target_sourceMap.get(col);
				Object sourceValue = sourceObjs.get(sourceColumnName);
				Object targetValue = HandlerUtil.handle(sourceColumnName, sourceValue);
				targetObjs.add(targetValue);
			}
			batchArgs.add(targetObjs.toArray(new Object[targetCols.size()]));

			if (++offset > Main.getOnceInsertAmount()) {
				batchUpdate(target, insertSql, batchArgs);
				batchArgs.clear();
				offset = 0;
				long now = System.currentTimeMillis();
				System.out.println("commit:" + (now - cur));
				cur = now;
			}
		}
		if (batchArgs.size() > 0) {
			batchUpdate(target, insertSql, batchArgs);
		}
	}

	private void batchUpdate(JdbcTemplate target, String insertSql, List<Object[]> batchArgs) {
		try {
			target.batchUpdate(insertSql, batchArgs);
		} catch (Exception e) {
			e.printStackTrace();
			writeError(batchArgs, e.getMessage());
		}
	}

	private void writeError(List<Object[]> batchArgs, String errorMessage) {
		try {
			StringBuilder totalStrB = new StringBuilder();
			for (Object[] objects : batchArgs) {
				StringBuilder strB = new StringBuilder("异常数据");
				for (Object object : objects) {
					strB.append(object + ",");
				}
				totalStrB.append(strB).append("\r\n");
				System.out.println(strB.toString());
			}
			
			String classPath = this.getClass().getResource("/").toString().substring(6);
			String fileName = classPath + "error.txt";
			writeError2File(fileName, errorMessage + "\r\n" + totalStrB.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeError2File(String fileName, String errorMessage) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw =  new FileWriter(file, true);
			fw.write(errorMessage);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
