/*
 * Copyright (c) 2014 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>标题： CopyByTime</p>
 * <p>
 *    功能描述：
 *    
 * </p>
 * <p>创建日期：2017年8月14日 下午5:10:23 </p>
 * <p>作者：TaoCong</p>
 * <p>版本：1.0</p>
 */
public class CopyByTime extends CopyAbstract {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	boolean judgeParam(Object startParam, Object endParam) {
		Date startParamD = convertObject2Date(startParam);
		Date endParamD = convertObject2Date(endParam);
		return startParamD.getTime() < endParamD.getTime();
	}

	@Override
	Object getIntervalEndParam(Object startParam, Object endParam, int interval) {
		Date startParamD = convertObject2Date(startParam);
		Date endParamD = convertObject2Date(endParam);
		
		Date tmp = addDays(startParamD, interval);
		if (tmp.getTime() > endParamD.getTime()) {
			tmp = endParamD;
		}
		return sdf.format(tmp);
	}

	@Override
	Object getNewStartParam(Object intervalEndParam) {
		Date intervalEndParamD = convertObject2Date(intervalEndParam);
		return sdf.format(addOneSecond(intervalEndParamD));
	}
	
	/**
	 * 将参数对象转为日期：如果参数长度不够19位，说明时缺少时分秒，默认添加“00:00:00”
	 * @param param
	 * @return
	 * @author Taocong
	 * @date 2017年8月14日 下午5:52:36
	 */
	private Date convertObject2Date(Object param) {
		String paramStr = param.toString();
		paramStr = paramStr.trim();
		if (paramStr.length() != 19) {
			paramStr += " 00:00:00";
		}
		Date date = null;
		try {
			date = sdf.parse(paramStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	private Date addDays(Date date, int addNum) {
		Calendar c = Calendar.getInstance();
         
        c.setTime(date);
        c.add(Calendar.DATE, addNum);
        Date m = c.getTime();
        return m;
	}
	
	private Date addOneSecond(Date date) {
		Calendar c = Calendar.getInstance();
		
		c.setTime(date);
		c.add(Calendar.SECOND, 1);
		Date m = c.getTime();
		return m;
	}
	
	

}
