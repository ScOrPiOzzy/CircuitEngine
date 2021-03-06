package com.cas.circuit.component;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.cas.circuit.IBroken;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
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
public class Wire implements Savable, IBroken {
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

	private boolean broken;

	public Wire() {
	}

	public Wire(boolean internal) {
		this.internal = internal;
	}

	public Wire(Terminal term1, Terminal term2) {
		bind(term1);
		bind(term2);
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

	public void markInternal() {
		internal = true;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		// nothing to save
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		// nothing to read
	}

	@Override
	public void setBroken(BrokenState state) {
		this.broken = BrokenState.OPEN == state;
		if (proxy != null) {
			proxy.setBroken(broken);
		}
	}

	@Override
	public String getKey() {
		if (term1 == null || term2 == null) {
			return null;
		} else {
			ElecCompDef def1 = term1.getElecCompDef();
			ElecCompDef def2 = term2.getElecCompDef();
			return String.format("elecComp:%s%s term:%s-elecComp:%s%s term:%s", def1.getName(), def1.getModel(), term1.getName(), def2.getName(), def2.getModel(), term2.getName());
		}
	}
	
	@Override
	public String getName() {
		return "导线";
		
	}

	@Override
	public String getDesc() {
		if (term1 == null || term2 == null) {
			return null;
		} else {
			ElecCompDef def1 = term1.getElecCompDef();
			ElecCompDef def2 = term2.getElecCompDef();
			return String.format("导线：%s%s 端子头【%s】 ←→ %s%s 端子头【%s】导线断开", def1.getName(), def1.getModel(), term1.getName(), def2.getName(), def2.getModel(), term2.getName());
		}
	}

	public void setCorrected(boolean corrected) {
		this.broken = false;
		proxy.setBroken(broken);
		proxy.setCorrected(corrected);
	}
}
