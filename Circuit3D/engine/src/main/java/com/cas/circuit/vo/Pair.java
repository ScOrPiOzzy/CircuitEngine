package com.cas.circuit.vo;

import com.cas.circuit.component.Terminal;

import lombok.Data;

/**
 * 描述一对线圈或触点
 */
@Data
public class Pair {
//	元器件Elm: ID
	private String id;
	private Terminal term1;
	private Terminal term2;

	public Pair(String id, Terminal term1, Terminal term2) {
		this.id = id;
		this.term1 = term1;
		this.term2 = term2;
	}

	public String getDesc() {
		return String.format("[$s]-[%s]", term1.getName(), term2.getName());
	}
}
