package com.cas.circuit.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.jme3.math.Vector3f;

public class Vector3fAdapter extends XmlAdapter<String, Vector3f> {

	@Override
	public Vector3f unmarshal(String v) throws Exception {
		if (v == null) {
			return null;
		}
		String[] arr = v.split(",");

		Vector3f v3f = new Vector3f();
		v3f.x = Float.parseFloat(arr[0]);
		v3f.y = Float.parseFloat(arr[1]);
		v3f.z = Float.parseFloat(arr[2]);
		return v3f;
	}

	@Override
	public String marshal(Vector3f v) throws Exception {
		if (v == null) {
			return null;
		}
		return v.x + "," + v.y + "," + v.z;
	}

}
