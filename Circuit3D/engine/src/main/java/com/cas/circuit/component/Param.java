package com.cas.circuit.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.NONE)
public class Param {
	@XmlAttribute
	private String key;

	@XmlAttribute
	private String value;
}
