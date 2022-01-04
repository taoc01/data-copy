
/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy.handler;

/**
 * <p>标题： Handler</p>
 * <p>
 *    功能描述：
 *    		字段特殊处理接口
 *    <p>1.此接口的实现类，需添加spring注解或手动配置为bean</p>
 *    <p>2.getColumnName 方法中，返回字符串应为数据源对应的列名称（source_columnName）</p>
 * </p>
 * <p>创建日期：2017年8月28日 上午10:21:13 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public interface Handler {
	Object handle(Object sourceValue);
	
	String getColumnName();
}
