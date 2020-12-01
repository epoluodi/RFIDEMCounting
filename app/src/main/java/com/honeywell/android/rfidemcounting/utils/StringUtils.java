package com.honeywell.android.rfidemcounting.utils;


import java.text.CollationKey;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StringUtils 
{

    private final static SimpleDateFormat dateFormatNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    /**
     * 获取当前时间--格式 2013-11-11 11:11
     *
     * @return
     */
    public static String getNormalTimeNow() {
        return dateFormatNormal.format(Calendar.getInstance().getTime());
    }

    /**
     * 格式化指定日期
     * @param date
     * @return
     */
    public static String getFormatTime(Date date){
            return dateFormatNormal.format(date);
    }

    /**
	 * 判断给定字符串是否空白串。
	 * 空白串是指由空格、制表符、回车符、换行符组成的字符串
	 * 若输入字符串为null或空字符串，返回true
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty( String input )
	{
		if ( input == null || "".equals( input.trim() ) || "null".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}

    /**
     * 数字去小数点后0
     * @param num
     * @return
     */
	public static String formatNum(double num) {

		if(num%1==0)
			return String.valueOf((int)num).trim();
		return String.valueOf(num).trim();
	}


    public static String formatInvestNum(double num){

        String str="";
        if(num!=0) {
            if (num % 1 == 0)
                str = String.valueOf((int) num).trim();
            else{
                str= String.valueOf(num).trim();
            }
        }
        return str;
    }

   public static List<String> sortList(List<String> mList){
       List<String> rList = new ArrayList<>();
       Collections.sort(mList, new Comparator<String>() {
           Collator collator = Collator.getInstance(Locale.CHINA);

           @Override
           public int compare(String o1, String o2) {
               CollationKey key1 = collator.getCollationKey(o1);
               CollationKey key2 = collator.getCollationKey(o2);
               return key1.compareTo(key2);
           }
       });

       for (String str : mList) {
           rList.add(str);
       }
       return rList;
   }
}