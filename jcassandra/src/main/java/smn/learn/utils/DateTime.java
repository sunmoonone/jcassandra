package smn.learn.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime {
	/**
	 * format current time
	 * @param format if format is empty will use: yyyy-MM-dd hh:mm:ss
	 * @return the formatted time string
	 */
	public static String format(String format){
		if(format==null || format.isEmpty()){
			format="yyyy-MM-dd hh:mm:ss";
		}
		return new SimpleDateFormat(format).format(new Date());
	}
	/**
	 * @param format if format is empty will use: yyyy-MM-dd hh:mm:ss
	 * @param timestamp timestamp in millis
	 * @return the formatted time string
	 */
	public static String format(String format,long timestamp){
		if(format==null || format.isEmpty()){
			format="yyyy-MM-dd hh:mm:ss";
		}
		Date d=new Date();
		d.setTime(timestamp);
		return new SimpleDateFormat(format).format(d);
	}
	/**
	 * 
	 * @param format if format is empty will use: yyyy-MM-dd hh:mm:ss
	 * @param date
	 * @return the formatted time string
	 */
	public static String format(String format,Date date){
		if(format==null || format.isEmpty()){
			format="yyyy-MM-dd hh:mm:ss";
		}
		return new SimpleDateFormat(format).format(date);
	}
	/**
	 * get current time in seconds or in milliseconds since epoc time
	 * @param inseconds
	 * @return
	 */
	public static long time(boolean inseconds){
		Calendar cal=Calendar.getInstance();
		if(inseconds){
			return cal.getTimeInMillis()/1000;
		}
		return cal.getTimeInMillis();
	}
	/**
	 * 
	 * @param strtime
	 * @param format
	 * @return return unix timestamp in milliseconds
	 */
	public static long strtotime(String strtime,String format){
		try {
			Date date=new SimpleDateFormat(format).parse(strtime);
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			return cal.getTimeInMillis();
		} catch (ParseException e) {
			return 0L;
		}
	}
	/**
	 * 
	 * @param timestamp timestamp in seconds or in milliseconds
	 * @return if timestamp is 0 return the day start of current time
	 */
	public static long daystart(long timestamp){
		Calendar cal=Calendar.getInstance();	
		if(timestamp==0){
			timestamp=cal.getTimeInMillis();
		}else{
			String s=String.valueOf(timestamp);
			if(s.length()<13){
				timestamp*=1000;
			}
			cal.setTimeInMillis(timestamp);
		}
		
		
		long delta=cal.get(Calendar.HOUR_OF_DAY)*3600000+
		cal.get(Calendar.MINUTE)*60000+
		cal.get(Calendar.SECOND)*1000+
		cal.get(Calendar.MILLISECOND);
		
		return timestamp-delta;
	}
	/**
	 * 
	 * @param strtime
	 * @param format
	 * @return if encounters parse error return 0
	 */
	public static long daystart(String strtime,String format){
		long stamp=strtotime(strtime,format);
		if(stamp==0)return 0;
		return daystart(stamp);
	}
	/**
	 * 
	 * @param date
	 * @return if date is null return 0
	 */
	public static long daystart(Date date){
		if(date==null)return 0L;
		return daystart(date.getTime());
	}
	/**
	 * get day end of timestamp in millis
	 * @param timestamp
	 * @return if timestamp is 0 return day end of current time
	 */
	public static long dayend(long timestamp){
		Calendar cal=Calendar.getInstance();	
		if(timestamp==0){
			timestamp=cal.getTimeInMillis();
		}else{
			String s=String.valueOf(timestamp);
			if(s.length()<13){
				timestamp*=1000;
			}
			cal.setTimeInMillis(timestamp);
		}
		
		long delta=cal.get(Calendar.HOUR_OF_DAY)*3600000+
		cal.get(Calendar.MINUTE)*60000+
		cal.get(Calendar.SECOND)*1000+
		cal.get(Calendar.MILLISECOND);
		
		return timestamp-delta+(23*3600000+59*60000+59*1000+1000);
	}
	/**
	 * 
	 * @param strtime
	 * @param format
	 * @return if encounters parse error return 0
	 */
	public static long dayend(String strtime,String format){
		long stamp=strtotime(strtime,format);
		if(stamp==0)return 0;
		return dayend(stamp);
	}
	/**
	 * 
	 * @param date
	 * @return if date is null return 0
	 */
	public static long dayend(Date date){
		if(date==null)return 0L;
		return dayend(date.getTime());
	}
}
