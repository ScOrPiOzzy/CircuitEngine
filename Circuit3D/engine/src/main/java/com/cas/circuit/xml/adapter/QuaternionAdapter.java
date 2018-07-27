package com.cas.circuit.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.jme3.math.Quaternion;

public class QuaternionAdapter extends XmlAdapter<String, Quaternion> {

	@Override
	public Quaternion unmarshal(String v) throws Exception {
		if (v == null) {
			return null;
		}
		String[] arr = v.split(",");

		Quaternion quaternion = new Quaternion();
		float x = Float.parseFloat(arr[0]);
		float y = Float.parseFloat(arr[1]);
		float z = Float.parseFloat(arr[2]);
		float w = Float.parseFloat(arr[3]);

		quaternion.set(x, y, z, w);
		return quaternion;
	}

	@Override
	public String marshal(Quaternion v) throws Exception {
		if (v == null) {
			return null;
		}

		return v.getX() + "," + v.getY() + "," + v.getZ() + "," + v.getW();
	}

}
