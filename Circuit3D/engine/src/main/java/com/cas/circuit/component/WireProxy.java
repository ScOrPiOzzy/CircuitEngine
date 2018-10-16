package com.cas.circuit.component;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.xml.adapter.Vector3fAdapter;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;

@XmlAccessorType(XmlAccessType.NONE)
public class WireProxy {
//	元器件1的tagName
	@XmlAttribute
	private String comp1Uuid;
	@XmlAttribute
	private String ternimal1Id;

//	元器件2的tagName
	@XmlAttribute
	private String comp2Uuid;
	@XmlAttribute
	private String ternimal2Id;
	@XmlAttribute
	@XmlJavaTypeAdapter(value = Vector3fAdapter.class)
	private List<Vector3f> pointList = new ArrayList<>();

	/**
	 * 线号
	 */
	@XmlAttribute
	private String number;

	@XmlAttribute
//	@XmlJavaTypeAdapter(value = ColorRGBAAdapter.class)
	private String color;// = ColorRGBA.Black;

	/**
	 * 导线半径
	 */
	@XmlAttribute
	private String radius;
	
	@XmlAttribute
	private boolean broken;

	@XmlTransient
	private BitmapText tagNode;
	
	public String getComp1Uuid() {
		return comp1Uuid;
	}

	public void setComp1Uuid(String comp1Uuid) {
		this.comp1Uuid = comp1Uuid;
	}

	public String getTernimal1Id() {
		return ternimal1Id;
	}

	public void setTernimal1Id(String ternimal1Id) {
		this.ternimal1Id = ternimal1Id;
	}

	public String getComp2Uuid() {
		return comp2Uuid;
	}

	public void setComp2Uuid(String comp2Uuid) {
		this.comp2Uuid = comp2Uuid;
	}

	public String getTernimal2Id() {
		return ternimal2Id;
	}

	public void setTernimal2Id(String ternimal2Id) {
		this.ternimal2Id = ternimal2Id;
	}

	public void setPointList(List<Vector3f> pointList) {
		this.pointList = pointList;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<Vector3f> getPointList() {
		return pointList;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
//		同步显示
		this.tagNode.setText(number);
	}

	public void setTagNode(BitmapText tag) {
		this.tagNode = tag;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}
}
