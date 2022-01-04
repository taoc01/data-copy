/*
 * Copyright (c) 2015 中国国际图书贸易集团公司 
 * All rights reserved.
 *  
 */
package cn.com.dataCopy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>
 * 标题： 类名
 * </p>
 * <p>
 * 功能描述： 功能描述
 * </p>
 * <p>
 * 创建日期：2015年10月28日
 * </p>
 * <p>
 * 作者：Administrator
 * </p>
 * <p>
 * 版本：版本号
 * </p>
 */
public class PropsUtil {
	
    /**
     * 加载属性文件
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName){
        Properties prop = null;
        InputStream is = null;
        try{
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if(is == null){
                throw new FileNotFoundException(fileName + " file is not found");
            }
            prop = new Properties();
            prop.load(is);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(is != null){
                try{
                     is.close();
                 }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    /**
     * 读取字符型属性值
     * @param prop
     * @param key
     * @return
     */
    public static String getString(Properties prop, String key){
        return getString(prop,key,"");
    }

    /**
     * 读取属性值，带有默认字符串
     * @param prop
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Properties prop, String key, String defaultValue){
        String value = defaultValue;
        if(prop.containsKey(key)){
            value = prop.getProperty(key);
        }
        return value;
    }

}
