package com.cas.circuit.component;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.xml.adapter.StringArrayAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class Base {
	// 底座对应依赖元器件-1.针脚与孔对应类似插头插座
	private ElecCompDef relyOnPlug;
	// 底座对应依赖元器件-2.开关电阻逻辑对应
	private ElecCompDef relyOnResis;

	@XmlAttribute
	@XmlJavaTypeAdapter(StringArrayAdapter.class)
	private String[] pulgTermIds;
	@XmlAttribute
	@XmlJavaTypeAdapter(StringArrayAdapter.class)
	private String[] tenons;// 可嵌入榫头的元器件系列号

	public ElecCompDef getRelyOnPlug() {
		return relyOnPlug;
	}

	public void setRelyOnPlug(ElecCompDef relyOnPlug) {
		this.relyOnPlug = relyOnPlug;
	}

	public ElecCompDef getRelyOnResis() {
		return relyOnResis;
	}

	public void setRelyOnResis(ElecCompDef relyOnResis) {
		this.relyOnResis = relyOnResis;
	}

	public boolean checkMatched(ElecCompDef def) {
		int type = def.getRelyOn().getType();
		if (RelyOn.RELY_ON_TYPE_PLUG == type) {
			return Arrays.asList(pulgTermIds).containsAll(Arrays.asList(def.getRelyOn().getRelyIds()));
		} else if (RelyOn.RELY_ON_TYPE_RESIS == type) {
			String model = def.getModel();
			String tenon = model.substring(0, model.indexOf("-"));
			List<String> tenonList = Arrays.asList(tenons);
			return tenonList.contains(tenon);
		}
		return false;
	}

	public boolean isUseable(ElecCompDef elecCompDef) {
		int type = elecCompDef.getRelyOn().getType();
		if (RelyOn.RELY_ON_TYPE_PLUG == type) {
			return this.relyOnPlug != null;
		} else if (RelyOn.RELY_ON_TYPE_RESIS == type) {
			return this.relyOnResis != null;
		}
		return false;
	}
}
