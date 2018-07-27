package com.cas.circuit.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringArrayAdapter extends XmlAdapter<String, String[]> {

	@Override
	public String[] unmarshal(String v) throws Exception {
		if (v == null) {
			return null;
		}
		String[] arr = v.split(",");

		String[] result = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}

		return result;
	}

	@Override
	public String marshal(String[] v) throws Exception {
		if (v == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			buf.append(v[i]);
			if (i < v.length - 1) {
				buf.append(",");
			}
		}
		return buf.toString();
	}

}
