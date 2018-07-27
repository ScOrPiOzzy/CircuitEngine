package com.cas.circuit.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.jme3.math.Vector3f;

public class AxisAdapter extends XmlAdapter<String, Vector3f> {

	@Override
	public Vector3f unmarshal(String v) throws Exception {
		if (v == null) {
			return Vector3f.UNIT_Z;
		}
		if ("X+".equalsIgnoreCase(v)) {
			return Vector3f.UNIT_X;
		} else if ("Y+".equalsIgnoreCase(v)) {
			return Vector3f.UNIT_Y;
		} else if ("Z+".equalsIgnoreCase(v)) {
			return Vector3f.UNIT_Z;
		} else if ("X-".equalsIgnoreCase(v)) {
			return Vector3f.UNIT_X.negate();
		} else if ("Y-".equalsIgnoreCase(v)) {
			return Vector3f.UNIT_Y.negate();
		} else if ("Z-".equalsIgnoreCase(v)) {
			return Vector3f.UNIT_Z.negate();
		}

		return null;
	}

	@Override
	public String marshal(Vector3f v) throws Exception {
		if (v == null) {
			return null;
		}
		if (Vector3f.UNIT_X.equals(v)) {
			return "X+";
		} else if (Vector3f.UNIT_Y.equals(v)) {
			return "Y+";
		} else if (Vector3f.UNIT_Z.equals(v)) {
			return "Z+";
		} else if (Vector3f.UNIT_X.negate().equals(v)) {
			return "X-";
		} else if (Vector3f.UNIT_Y.negate().equals(v)) {
			return "Y-";
		} else if (Vector3f.UNIT_Z.negate().equals(v)) {
			return "Z-";
		}

		return null;
	}

}
