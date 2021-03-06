package com.cas.circuit.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.CircuitNode;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.util.JmeUtil;
import com.cas.circuit.xml.adapter.AxisAdapter;
import com.cas.circuit.xml.adapter.BooleanIntAdapter;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
public class Terminal implements Savable {
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
	private Integer limit = 2;// 限制可连接导线的数量，要么是1，要么是2.
	@XmlAttribute
	@XmlJavaTypeAdapter(BooleanIntAdapter.class)
	private Boolean internal;// 是否为内部节点，不需要给用户接线, 默认肯定是false，表不是内部连接头
//	=============================================================================================
//	=============================================================================================
	@Setter
	private Spatial spatial;
//	@Setter
////	如果该端子是在某个插孔中,则当插入电缆后,与之相连接的是哪个端子
//	private Terminal contacted;
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

	@Setter
	private ElecCompDef elecCompDef;

	public Terminal() {
	}

	public Terminal(String name) {
		this.name = name;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		// nothing to save
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		// nothing to read
	}

	public void setSpatial(Spatial spatial) {
		if (spatial == null) {
			String errMsg = String.format("Terminal::没有找到连接头[%s]的模型[%s]", name, mdlName);
			Terminal.log.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		this.spatial = spatial;
		spatial.setUserData("entity", this);
	}

	public Vector3f getDirection() {
		if (direction == null) {
			direction = Vector3f.UNIT_Z;
		}
		return direction;
	}

	@Override
	public String toString() {
		return this.name;
	}

	double maxV = 0;

	public void voltageChanged(double c) {
		if (c > maxV) {
			maxV = c;
		}
//		System.out.println(getName() + ": "+maxV);
//		if (spatial != null) {
//			if (c > 380) {
//				System.out.println(name + Math.abs(c));
//			}
//			if (Math.abs(c) > 1) {
//				JmeUtil.color(spatial, new ColorRGBA(1, 0, 0, (float) (Math.abs(c) / maxV)), true);
//			} else {
//				JmeUtil.uncolor(spatial);
//			}
//		}
	}

	public boolean notInternal() {
		return internal == null || !internal;
	}

	public long getWireSize() {
		return wires.stream().filter(w -> !w.isInternal()).count();
	}

	public String getName() {
//		return String.format("name:%s, volt:%.8f", name, maxV);
		return name;
	}

}
