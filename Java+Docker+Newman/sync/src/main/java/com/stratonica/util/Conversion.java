package com.stratonica.util;

import java.util.HashMap;
import java.util.List;

public class Conversion {

	public static Integer toInteger(String data) {
		
		return toInteger(data, null);
	}
	
	public static Integer toInteger(String val, Integer defVal) {
		try {
			return Integer.parseInt(val);
		} catch(Exception e) {
			return defVal;
		}
	}

	public static Long toLong(String val, Long defVal) {
		try {
			return Long.parseLong(val);
		} catch(Exception e) {
			return defVal;
		}
	}


	public static Boolean toBoolean(String data) {
		return toBoolean(data, false);
	}
	
	public static String removeNonAlphanumeric(String str) {
	    if (str == null) {
	        return "";
	    }
	    StringBuilder sb = new StringBuilder();
	    for (char c : str.toCharArray()) {
	        if (Character.isLetterOrDigit(c) || c == '_') {
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}
	public static HashMap<String, String> toMap(List<String> elementAttributes) {

		HashMap<String, String> rtn = new HashMap<String, String>();
		for (String attr : elementAttributes) {
			
			String[] items = attr.split("=", 2);
			String key = attr;
			String value = null;
			if (items.length > 1) {
				 key = items[0];
				 value = items[1];
			} 
			rtn.put(key, value);
		}	
		return rtn;
	}

	public static Boolean toBoolean(String data, Boolean defVal) {
		if (data == null || data.length() == 0) return defVal;
		char ch = data.trim().toLowerCase().charAt(0);
		if (ch == 'y' || ch == 't') return true;
		return false;
	}


}
