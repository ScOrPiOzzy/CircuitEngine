package com.cas.circuit.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.jme3.math.ColorRGBA;

public class ColorRGBAAdapter extends XmlAdapter<String, ColorRGBA> {

	@Override
	public ColorRGBA unmarshal(String v) throws Exception {
		if (v != null) {
			String[] arr = v.split(",");
			if (arr.length != 4) {
				throw new RuntimeException("无效的参数：" + v);
			}
			float[] param = new float[4];
			for (int i = 0; i < param.length; i++) {
				param[i] = Float.parseFloat(arr[i]);
			}
			return new ColorRGBA(param[0], param[1], param[2], param[3]);
		}
		return null;
	}

	@Override
	public String marshal(ColorRGBA color) throws Exception {
		if (color == null) {
			return null;
		}
		float[] param = color.getColorArray();
		return param[0] + "," + param[1] + "," + param[2] + "," + param[3];
	}

}