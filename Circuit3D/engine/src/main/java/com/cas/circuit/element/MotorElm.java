package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;
import com.jme3.scene.Spatial;

public class MotorElm extends CircuitElm {
	public static final int STATE_STATIC = 0, STATE_CW = 1, STATE_ACW = -1;

	protected int state = 0;

//	转子
	protected Spatial rotator;

	public MotorElm() {
	}

	public MotorElm(Function<String, Terminal> f, Map<String, String> params) {
	}

	@Override
	void buildInfo() {
	}

	@Override
	public boolean nonLinear() {
		return true;
	}
}
