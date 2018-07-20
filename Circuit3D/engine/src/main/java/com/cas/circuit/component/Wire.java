package com.cas.circuit.component;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 导线
 * @author sco_pra
 */
@Getter
@Setter
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
public class Wire {
	@XmlAttribute
	private String mark;
	@XmlAttribute
	private String term1Id;
	@XmlAttribute
	private String term2Id;

	private Terminal term1;
	private Terminal term2;

	private boolean internal = false;

	private String wireNum;

	private Spatial spatial;

	private WireProxy proxy;

	public Wire() {
	}

	/**
	 * jaxb 解析标签&lt;ElecCompDef&gt;完成后调用
	 */
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		log.info("afterUnmarshal");
	}

	/**
	 * 将导线绑到连接头上
	 */
	public void bind(Terminal term) {
		if (isBothBinded() || term == null) {
			return;
		}
		if (term1 == null) {
			term1 = term;
		} else if (term2 == null) {
			term2 = term;
		}

		List<Wire> wires = term.getWires();
		if (!wires.contains(this)) {
			wires.add(this);
		}
	}

	public void unbind(Terminal term) {
		if (term == null) {
			return;
		}

		term.getWires().remove(this);

		if (term1 == term) {
			term1 = null;
		} else if (term2 == term) {
			term2 = null;
		}
	}

	public void unbind() {
		if (term1 != null) {
			term1.getWires().remove(this);
		}
		if (term2 != null) {
			term2.getWires().remove(this);
		}

		unbind(term1);
		unbind(term2);
	}

	public Terminal getAnother(Terminal term) {
		return term == term1 ? term1 : term2;
	}

	public boolean isBothBinded() {
		return term1 != null && term2 != null;
	}

	public Spatial getSpatial() {
		return spatial;
	}

	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
		spatial.setUserData("entity", this);
	}

	@Override
	public String toString() {
		return "Wire [term1=" + term1 + ", term2=" + term2 + "]" + hashCode();
	}

	@Nonnull
	public WireProxy getProxy() {
		if (proxy == null) {
			proxy = new WireProxy();
		}
		return proxy;
	}

	public void setProxy(WireProxy proxy) {
		this.proxy = proxy;
	}
}
