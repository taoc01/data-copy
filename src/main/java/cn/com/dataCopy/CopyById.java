
/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy;

/**
 * <p>标题： PageById</p>
 * <p>
 *    功能描述：
 *    
 * </p>
 * <p>创建日期：2017年8月14日 上午10:59:47 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public class CopyById extends CopyAbstract {

	@Override
	boolean judgeParam(Object startParam, Object endParam) {
		long startParamL = convertObj2Long(startParam);
		long endParamL = convertObj2Long(endParam);
		return startParamL < endParamL;
	}

	@Override
	Object getIntervalEndParam(Object startParam, Object endParam, int interval) {
		long startParamL = convertObj2Long(startParam);
		long endParamL = convertObj2Long(endParam);
		long tmp = startParamL + interval;
		if (tmp > endParamL) {
			tmp = endParamL;
		}
		return tmp;
	}

	@Override
	Object getNewStartParam(Object intervalEndParam) {
		long intervalEndParamL = convertObj2Long(intervalEndParam);
		return intervalEndParamL + 1;
	}

	private long convertObj2Long(Object param) {
		return Long.valueOf(param.toString());
	}
}
