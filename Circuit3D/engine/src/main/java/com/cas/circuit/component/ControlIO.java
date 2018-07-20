
package com.cas.circuit.component;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.ISwitch;
import com.cas.circuit.xml.adapter.BooleanIntAdapter;
import com.cas.circuit.xml.adapter.FloatArrayAdapter;
import com.cas.circuit.xml.adapter.StringArrayAdapter;
import com.cas.circuit.xml.adapter.UnsignedAxisAdapter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 包括:按钮(button) 和 旋钮(switch)
 */
@Getter
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
public class ControlIO {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String mdlName;
	@XmlAttribute
	private String effect;
	@XmlAttribute
	private String controlModName;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private String interact;
	@XmlAttribute
	private Integer defSwitch;
	@XmlAttribute
	@XmlJavaTypeAdapter(StringArrayAdapter.class)
	private String[] switchIn;
	@XmlAttribute
	private String motion;
	@XmlAttribute
	@XmlJavaTypeAdapter(FloatArrayAdapter.class)
	private float[] motionParams;
	@XmlAttribute
	@XmlJavaTypeAdapter(UnsignedAxisAdapter.class)
	private Vector3f axis;
	@XmlAttribute
	private float speed = 4;
	@XmlAttribute
	@XmlJavaTypeAdapter(BooleanIntAdapter.class)
	private Boolean smooth = Boolean.FALSE;

	@XmlElement(name = "Param")
	@XmlElementWrapper(name = "Params")
	private List<Param> params = new ArrayList<>();

//	--------------------------------------------------------------------
	private Spatial spatial;

	private ElecCompDef elecCompDef;

	@Setter
	private ISwitch switchElm;

	public void beforeUnmarshal(Unmarshaller u, Object parent) {
		log.info("afterUnmarshal");
		this.elecCompDef = (ElecCompDef) parent;
	}

	public void setSpatial(Spatial spatial) {
		if (spatial == null) {
			String errMsg = String.format("没有找到ControlIO::name为%s的模型%s", name, mdlName);
			log.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		this.spatial = spatial;
		spatial.setUserData("entity", this);
	}

}
