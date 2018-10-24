package com.cas.circuit.vo;

import com.cas.circuit.IBroken;
import com.cas.circuit.component.Terminal;

import lombok.Data;

/**
 * 描述一对线圈或触点
 */
@Data
public class Pair implements IBroken {
//	元器件Elm: ID
	private String id;
	private Terminal term1;
	private Terminal term2;
	private Type type;
	private BrokenState state = BrokenState.NORMAL;

	public enum Type {
		NO, NC, COIL;
	}

	public Pair(String id, Terminal term1, Terminal term2, Type type) {
		this.id = id;
		this.term1 = term1;
		this.term2 = term2;
		this.type = type;
	}

	public String getKey(String key) {
		return String.format("%s-%s-%s", key, term1.getId(), term2.getId()).replaceAll("/", "_");
	}

	@Override
	public String getName() {
		return String.format("[%s]-[%s]", term1.getName(), term2.getName());
	}

	@Override
	public String getDesc() {
		String desc = "正常";
		if (BrokenState.CLOSE == state) {
			desc = "始终闭合";
		} else if (BrokenState.OPEN == state) {
			desc = "始终断开";
		}
		return String.format("元器件：%s [%s]-[%s] %s", term1.getElecCompDef().getName(), term1.getName(), term2.getName(), desc);
	}

	@Override
	public void setBroken(BrokenState state) {
		this.state = state;
	}
}
