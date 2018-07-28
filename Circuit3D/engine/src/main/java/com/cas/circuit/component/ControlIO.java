
package com.cas.circuit.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
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
public class ControlIO implements Savable {

	/**
	 * 揿钮分/揿钮合
	 */
	public static final String INTERACT_UNIDIR = "unidir";
	/**
	 * 按下不弹起按钮或上下拨动的开关
	 */
	public static final String INTERACT_CLICK = "click";
	/**
	 * 按下弹起型按钮
	 */
	public static final String INTERACT_PRESS = "press";
	/**
	 * 拨转型旋钮
	 */
	public static final String INTERACT_ROTATE = "rotate";

	
//	id 对于 ControlIO 并不是必要的，在
	@XmlAttribute
	private String id;
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

	@Setter
	private ElecCompDef elecCompDef;

	private List<ISwitch> switchElms = new ArrayList<>();

	public void setSpatial(Spatial spatial) {
		if (spatial == null) {
			String errMsg = String.format("ControlIO::没有找到开关[%s]的模型[%s]", name, mdlName);
			log.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		this.spatial = spatial;
		spatial.setUserData("entity", this);
	}

	public void addSwitch(ISwitch s) {
		switchElms.add(s);
	}

	/**
	 * 按钮松开
	 */
	public void unstuck() {
		spatial.move(0, (float) 1e-3, 0);
	}

	/**
	 * 按钮吸合
	 */
	public void absorbed() {
		spatial.move(0, (float) -1e-3, 0);
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		// nothing to save
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		// nothing to read
	}

}
