
/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cn.com.dataCopy.Main;

/**
 * <p>标题： HandlerRegister</p>
 * <p>
 *    功能描述：
 *    
 * </p>
 * <p>创建日期：2017年8月28日 上午10:30:43 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public class HandlerUtil {
	// key:handler中的getColumnName(); value:handler
	private static Map<String, Handler> columnHandlerMap = new HashMap<>();

	static {
		Collection<Handler> hanlders = Main.getContext().getBeansOfType(Handler.class).values();
		for (Handler handler : hanlders) {
			columnHandlerMap.put(handler.getColumnName(), handler);
		}
	}
	
	/**
	 * 根据数据源列名寻找处理器，并进行特殊处理
	 * @param sourceColumnName 数据源列名
	 * @param sourceColumnValue 数据源值
	 * @return
	 * @author Taocong
	 * @date 2017年8月28日 上午10:52:10
	 */
	public static Object handle(String sourceColumnName, Object sourceColumnValue) {
		Handler handler = columnHandlerMap.get(sourceColumnName);
		if (handler == null)
			return sourceColumnValue;
		
		return handler.handle(sourceColumnValue);
	}
	
}
