package com.cas.circuit.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class FloatArrayAdapter extends XmlAdapter<String, float[]> {

	@Override
	public float[] unmarshal(String v) throws Exception {
//		List<String> rotateAngleStrs = StringUtil.split(po.getMotionParams());
//		for (String string : rotateAngleStrs) {
//			if (Util.isNumeric(string)) {
//				motionParams.add(Float.parseFloat(string));
//			}
//		}
		if (v == null) {
			return null;
		}
		String[] arr = v.split(",");

		float[] result = new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i] = Float.parseFloat(arr[i]);
		}

		return result;
	}

	@Override
	public String marshal(float[] v) throws Exception {
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
