package com.ese.utils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public enum Utils {
    ;
    public final static boolean TRUE = true;



    public static boolean isNull(final Object object){
        return object == null;
    }

    public static boolean isEmpty(final String string){
        return StringUtils.isEmpty(string);
    }

    public static boolean isZero(final String string){
        return 0 == string.length();
    }

    public static boolean equals(final String string, final String string2){
        return StringUtils.equals(string, string2);
    }

    public static<T> List<T> safetyList(final List<T> list) {
        return !isNull(list) && list.size() > 0 ? list : (List<T>) Collections.EMPTY_LIST;
    }

    public static<T> boolean isSafetyList(final List<T> list){
        return !isNull(list) && !isZero(list.size());
    }

    public static<T> boolean isCollection(final Collection collection){
        return !isNull(collection) && !isZero(collection.size());
    }

    public static boolean isZero(int id){
        try {
            return id == 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static Date currentDate(){
       return Calendar.getInstance().getTime();
    }

    public static String getDocumentNo(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(currentDate());
    }

    public static int parseInt(Object input, int defaultValue){
        if(input == null)
            return defaultValue;
        else if (input instanceof Integer)
            return (Integer) input;
        else {
            String inputStr = input.toString();
            if(isEmpty(inputStr))
                return defaultValue;
            try{
                return Integer.parseInt(inputStr);
            }catch (ClassCastException e){
                return defaultValue;
            }
        }
    }

    public static String parseString(Object input, String defaultValue){
        if(input == null)
            return defaultValue;
        else if (input instanceof String)
            return (String) input;
        else {
            try{
                if(isEmpty(input.toString())){
                    return defaultValue;
                } else {
                    return input.toString();
                }
            } catch (ClassCastException e){
                return defaultValue;
            }
        }
    }

    public static Date parseDate(Object input, Date defaultValue){
        if(input == null)
            return defaultValue;
        else if (input instanceof Date)
            return (Date) input;
        else {
            try{
                if(isNull(input)){
                    return defaultValue;
                } else {
                    return null;
                }
            } catch (ClassCastException e){
                return defaultValue;
            }
        }
    }

    public static String convertToStringDDMMYYYY(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        if (Utils.isNull(date)){
            return "";
        } else {
            String dateString = simpleDateFormat.format(date);
            return dateString;
        }
    }
}
