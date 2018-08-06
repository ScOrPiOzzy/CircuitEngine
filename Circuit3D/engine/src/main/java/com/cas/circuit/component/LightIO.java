package com.cas.circuit.component;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.util.JmeUtil;
import com.cas.circuit.xml.adapter.ColorRGBAAdapter;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@XmlAccessorType(XmlAccessType.NONE)
public class LightIO implements Savable {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String mdlName;
	@XmlAttribute
	@XmlJavaTypeAdapter(ColorRGBAAdapter.class)
	private ColorRGBA glowColor;
	@XmlAttribute
	private String effect;

	private Spatial spatial;

	public void closeLight() {
		JmeUtil.uncolor(spatial);
	}

	public void openLight() {
		JmeUtil.color(spatial, glowColor, true);
	}

	public void setSpatial(Spatial spatial) {
		if (spatial == null) {
			String errMsg = String.format("没有找到LightIO::name为%s的模型%s", name, mdlName);
			LightIO.log.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		this.spatial = spatial;
		spatial.setUserData("entity", this);
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
