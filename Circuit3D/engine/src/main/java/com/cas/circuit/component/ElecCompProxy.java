package com.cas.circuit.component;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.xml.adapter.QuaternionAdapter;
import com.cas.circuit.xml.adapter.Vector3fAdapter;
import com.jme3.font.BitmapText;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

@XmlAccessorType(XmlAccessType.NONE)
public class ElecCompProxy {
//	元器件信息ID
	@XmlAttribute
	private Integer id;
//	元器件ID
	@XmlAttribute
	private String uuid;
//	元器件Tag
	@XmlAttribute
	private String tagName;
//	底座编号
	@XmlElement
	private String baseUuid;

//	摆放位置
	@XmlAttribute
	@XmlJavaTypeAdapter(value = Vector3fAdapter.class)
	private Vector3f location = Vector3f.ZERO;

//	旋转量
	@XmlAttribute
	@XmlJavaTypeAdapter(value = QuaternionAdapter.class)
	private Quaternion rotation = Quaternion.ZERO;

	@XmlTransient
	private BitmapText tagNode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
		}
		return uuid;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
//		同步显示
		this.tagNode.setText(tagName);
	}

	public String getBaseUuid() {
		return baseUuid;
	}

	public void setBaseUuid(String baseUuid) {
		this.baseUuid = baseUuid;
	}

	public Vector3f getLocation() {
		return location;
	}

	public void setLocation(Vector3f location) {
		this.location = location;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public void setTagNode(BitmapText tag) {
		this.tagNode = tag;
	}

}
