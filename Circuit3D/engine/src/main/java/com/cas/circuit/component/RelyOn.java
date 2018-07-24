package com.cas.circuit.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.xml.adapter.QuaternionAdapter;
import com.cas.circuit.xml.adapter.StringArrayAdapter;
import com.cas.circuit.xml.adapter.Vector3fAdapter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@Getter
public class RelyOn {
	public static final int RELY_ON_TYPE_PLUG = 1;
	public static final int RELY_ON_TYPE_RESIS = 2;

	@XmlAttribute
	private int type;
	@XmlAttribute
	@XmlJavaTypeAdapter(Vector3fAdapter.class)
	private Vector3f localTranslation;
	@XmlAttribute
	@XmlJavaTypeAdapter(QuaternionAdapter.class)
	private Quaternion localRotation;
	@XmlAttribute
	@XmlJavaTypeAdapter(StringArrayAdapter.class)
	private String[] relyIds;
	// 依赖底座
	@Setter
	private ElecCompDef baseDef;
}
