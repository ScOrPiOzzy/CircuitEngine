
package com.cas.circuit.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cas.circuit.ISwitch;
import com.cas.circuit.util.JmeUtil;
import com.cas.circuit.xml.adapter.BooleanIntAdapter;
import com.cas.circuit.xml.adapter.FloatArrayAdapter;
import com.cas.circuit.xml.adapter.MapAdapter;
import com.cas.circuit.xml.adapter.StringArrayAdapter;
import com.cas.circuit.xml.adapter.UnsignedAxisAdapter;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jme3tools.navigation.StringUtil;
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
//	id 对于 ControlIO 并不是必要的，在
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String mdlName;
	@XmlAttribute
	private String linkageId;
	@XmlAttribute
	private String effect;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private String interact;
	/**
	 * 揿钮分/揿钮合
	 */
	public static final String INTERACT_UNIDIR = "unidir",
			/**
			 * 按下不弹起按钮或上下拨动的开关
			 */
			INTERACT_CLICK = "click",
			/**
			 * 按下弹起型按钮
			 */
			INTERACT_PRESS = "press",
			/**
			 * 拨转型旋钮
			 */
			INTERACT_ROTATE = "rotate";
//	表示开关状态： 0 ：表示断开， 1 表示闭合
	@XmlAttribute
	private Integer state = STATE_OFF;
	public static final Integer STATE_OFF = 0;
	public static final Integer STATE_ON = 1;

	@XmlElement(name = "Params")
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, String> params = new HashMap<>();

//	--------------------------------------------------------------------
	private Spatial spatial;

	@Setter
	private ControlIO linkage; // 两个按钮通过连杆连接

	@Setter
	private ElecCompDef elecCompDef;

	private List<ISwitch> switchElms = new ArrayList<>();

	public void setSpatial(Spatial spatial) {
		if (spatial == null) {
			String errMsg = String.format("ControlIO::没有找到开关[%s]的模型[%s]", name, mdlName);
			ControlIO.log.error(errMsg);
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
	public void off() {
		tranform("off");
		state = STATE_OFF;
	}

	/**
	 * 按钮吸合
	 */
	public void on() {
		tranform("on");
		state = STATE_ON;
	}

	private void tranform(String key) {
		Node parant = elecCompDef.getSpatial();
		String absorbedJson = params.get(key);
		if (absorbedJson != null) {
			JSONArray arr = JSON.parseArray(absorbedJson);
			arr.forEach(obj -> {
				JSONObject json = (JSONObject) obj;
				String mdlName = json.getString("mdlName");
				Spatial sp = parant.getChild(mdlName);
				String location = json.getString("location");
				if (location != null) {
					sp.setLocalTranslation(JmeUtil.parseVector3f(location));
				}
				String rotation = json.getString("rotation");
				if (rotation != null) {
					sp.setLocalRotation(JmeUtil.parseDegree(rotation));
				}
				String scale = json.getString("scale");
				if (scale != null) {
					sp.setLocalScale(JmeUtil.parseVector3f(scale));
				}
			});
		}
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
