package com.stratonica.util;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Properties {
	
	HashMap<String, String> properties = null;
	HashMap<String, String> replacers = null;
	Set<String> keys = null;

	public Properties(String file) {
		addProperties(file);
	}

	public void addProperties(String file) {

		if (properties == null) {
			properties = new HashMap<String, String>();
		}
		List<String> lines = File.readFile(file);
		showList(lines);
		for (String line : lines) {
			if (line.toLowerCase().trim().startsWith("replacer=")) {
				line = line.substring(9);
				int index = line.indexOf("=");
				if (index < 0)
					continue;
				if (replacers == null) {
					replacers = new HashMap<String, String>();
				}
				String key = line.substring(0, index);
				String value = line.substring(index + 1);
				replacers.put(key, value);
				keys = replacers.keySet();
			} else if (!line.trim().startsWith("#")) {
				int i = line.indexOf("=");
				if (i > -1) {
					String key = line.substring(0, i);
					String value = line.substring(i + 1);
					if (keys != null) {
						for (String k : keys) {
							int index = value.indexOf("<" + k + ">");
							if (index > -1) {
								value = value.replace("<" + k + ">", replacers.get(k));
							}
						}
					}

					properties.put(key, value);
				}
			}
		}

	}

	public String get(String key, String defaultValue) {
		String value = null;
		return get(key, value, defaultValue);
	}

	public String get(String key, String value, String defaultValue) {
		String rtn = properties.get(key);
		if (rtn == null)
			return defaultValue;
		return rtn;
	}

	private void showList(List<String> lines) {
		for (String line : lines) {
			System.out.println(line);
		}

	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Properties props = new Properties("./resources/test.txt");
	}

	public int getInteger(String key, int defVal) {
		String val = get(key, null);
		if (val == null) return defVal;
		return Conversion.toInteger(val, defVal);
	}

	public Long getLong(String key, long defVal) {
		String val = get(key, null);
		if (val == null) return defVal;
		return Conversion.toLong(val, defVal);
		
	}

	public Boolean getBoolean(String key, Boolean defVal) {
		String val = get(key, null);
		if (val == null) return defVal;
		return Conversion.toBoolean(val, defVal);
	}

}
