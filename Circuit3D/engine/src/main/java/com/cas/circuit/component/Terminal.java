package com.cas.circuit.component;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.CircuitNode;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.xml.adapter.AxisAdapter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
public class Terminal {
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String mdlName;
	@XmlAttribute
	@XmlJavaTypeAdapter(value = AxisAdapter.class)
	private Vector3f direction;
//	如果该端子是某个插孔上拓展出来的,则在对应插孔中哪一个针脚(编号)
	@XmlAttribute
	private String mark;
	@XmlAttribute(name = "num")
	private Integer limit;// 限制可连接导线的数量，要么是1，要么是2.
//	=============================================================================================
//	=============================================================================================
	@Setter
	private Spatial spatial;
	@Setter
//	如果该端子是在某个插孔中,则当插入电缆后,与之相连接的是哪个端子
	private Terminal contacted;
	@Setter
//	该端子上的连接的导线
	private List<Wire> wires = new ArrayList<>();
	@Setter
	private CircuitElm elm; // 连接头所属的单位元件
	@Setter
	private int indexInElm;
	@Setter
	private CircuitNode node;
	@Setter
	private Integer isoElectricNum;

	private ElecCompDef elecCompDef;

	public Terminal() {
	}

	public Terminal(String name) {
		this.name = name;
	}

	public void beforeUnmarshal(Unmarshaller u, Object parent) {
		this.elecCompDef = (ElecCompDef) parent;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
