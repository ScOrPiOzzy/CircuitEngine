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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.ILight;
import com.cas.circuit.ISwitch;
import com.cas.circuit.control.MotorControl;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.LEDElm;
import com.cas.circuit.element.MotorElm;
import com.cas.circuit.xml.CircuitExchange;
import com.cas.circuit.xml.adapter.MapAdapter;
import com.cas.circuit.xml.adapter.TerminalMapAdapter;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.scene.Node;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ElecCompDef")
public class ElecCompDef implements Savable {
	public static final String PARAM_KEY_SHELL = "shell";
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String model;
	@XmlAttribute
	private String desc;
	/**
	 * 元器件连接头
	 */
	@XmlElement(name = "Terminal")
	@XmlJavaTypeAdapter(TerminalMapAdapter.class)
	private Map<String, Terminal> terminalMap = new HashMap<>();
	/**
	 * 元器件逻辑类转换器
	 */
	@XmlElement(name = "CircuitExchange")
	private List<CircuitExchange> circuitExchangeList = new ArrayList<>();
	/**
	 * 描述元器件内部电路情况，比如集成电路中的铜线，但这种导线没有实体（spatial）
	 */
	@XmlElementWrapper(name = "Wires")
	@XmlElement(name = "Wire")
	private List<Wire> internalWireList = new ArrayList<>();

	@XmlElement(name = "ControlIO")
	private List<ControlIO> controlIOList = new ArrayList<>();

//	元器件上的指示灯
	@XmlElement(name = "LightIO")
	private List<LightIO> lightIOList = new ArrayList<>();

	@XmlElement(name = "Base")
	private Base base;

	@XmlElement(name = "RelyOn")
	private RelyOn relyOn;

	@XmlElement(name = "Params")
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, String> params = new HashMap<>();

//	@XmlElement(name = "Jack")
//	private List<Jack> jackList = new ArrayList<>();
//
////	===================================================================================
////	Key:电缆插孔的名字
//	private Map<String, Jack> jackMap = new LinkedHashMap<String, Jack>();
////	Key: id
////	存放所有的连接头
//	private Map<String, Terminal> terminalMap = new LinkedHashMap<String, Terminal>();
////	Key: id
////	存放所有连接头及插孔中的针脚
//	private Map<String, Terminal> termAndStich = new LinkedHashMap<String, Terminal>();
//
//	元器件模型
	private Node spatial;

	@Setter
	private ElecCompProxy proxy;

	private Map<String, CircuitElm> circuitElmMap = new HashMap<>();

	public ElecCompDef() {
	}

	/**
	 * jaxb 解析标签&lt;ElecCompDef&gt;开始前调用
	 */
	public void beforeUnmarshal(Unmarshaller u, Object parent) {
	}

	/**
	 * jaxb 解析标签&lt;ElecCompDef&gt;完成后调用
	 */
	public void afterUnmarshal(Unmarshaller u, Object parent) {
//		标记导线为元器件内部导线
		internalWireList.forEach(w -> {
			w.markInternal();
			w.bind(getTerminal(w.getTerm1Id()));
			w.bind(getTerminal(w.getTerm2Id()));
		});
//		
		terminalMap.values().forEach(t -> t.setElecCompDef(this));
		controlIOList.forEach(c -> c.setElecCompDef(this));

//		设置按钮与开关类元器件的关系
		controlIOList.forEach(c -> {
			String effect = c.getEffect();

			String[] switchName = null;
			if (effect.indexOf(',') != -1) {
				switchName = effect.split(",");
			} else {
				switchName = new String[] { effect };
			}
			for (int i = 0; i < switchName.length; i++) {
				CircuitElm elm = this.circuitElmMap.get(switchName[i]);
				if (elm == null) {
					log.error("配置文件内容有错误! 没有{}对应的开关性质的元器件", switchName[i]);
				} else if (!(elm instanceof ISwitch)) {
					log.error("配置文件内容有错误! {}不是一个包含开关的元器件", elm);
				} else {
					ISwitch s = (ISwitch) elm;

					s.setButton(c);
					c.addSwitch(s);
				}
			}
		});

		lightIOList.forEach(l -> {
			CircuitElm elm = this.circuitElmMap.get(l.getEffect());
			if (elm == null) {
				log.error("配置文件内容有错误! 没有{}对应的开关性质的元器件", l.getEffect());
			} else if (!(elm instanceof ILight)) {
				log.error("配置文件内容有错误! {}不是灯", elm);
			} else {
				ILight s = (ILight) elm;
				s.setLight(l);
			}
		});

	}

	public void bindModel(Node spatial) {
		this.spatial = spatial;
		spatial.setUserData("entity", this);
//		遍历元气件中所有插座
//		jackList.forEach(jack -> jack.setSpatial(spatial.getChild(jack.getMdlName())));
//		遍历元气件中所有连接头
		terminalMap.values().stream().filter(Terminal::notInternal).forEach(t -> t.setSpatial(spatial.getChild(t.getMdlName())));
//		遍历元气件中所有开关
		controlIOList.forEach(c -> c.setSpatial(spatial.getChild(c.getMdlName())));
//		遍历元气件中所有指示灯
		lightIOList.forEach(l -> l.setSpatial(spatial.getChild(l.getMdlName())));

		circuitExchangeList.forEach(ex -> {
			CircuitElm elm = ex.getCircuitElm();
			if (elm instanceof MotorElm) {
				MotorControl control = new MotorControl(getParam("rotator"));
				spatial.addControl(control);

				((MotorElm) elm).setControl(control);
			}
		});
	}

	/**
	 * @param key Terminal::getId
	 */
	public Terminal getTerminal(String key) {
		return terminalMap.get(key);
	}

	public String getParam(String key) {
		return getParam(key, null);
	}

	public String getParam(String key, String def) {
		return params.getOrDefault(key, def);
	}

	public void update() {
//		判断工作条件
//		1、灯泡亮
		lightIOList.forEach(light -> {
		});
//		2、电机转
	}

	public void putCircuitElm(String id, CircuitElm circuitElm) {
		circuitElmMap.put(id, circuitElm);
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
