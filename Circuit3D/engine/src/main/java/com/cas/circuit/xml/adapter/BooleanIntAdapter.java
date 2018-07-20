package com.cas.circuit.xml.adapter;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanIntAdapter extends XmlAdapter<String, Boolean> {
	public Boolean unmarshal(String v) {
		if (v == null) {
			return false;
		}
		return DatatypeConverter.parseBoolean(v);
	}

	public String marshal(Boolean v) {
		if (v == null) {
			return "0";
		}
		if (v) {
			return "1";
		} else {
			return "0";
		}
	}

}