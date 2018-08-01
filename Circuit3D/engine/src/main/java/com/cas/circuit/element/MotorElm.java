package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;
import com.cas.circuit.control.MotorControl;

import lombok.Setter;

public class MotorElm extends CircuitElm {
	public static final int STATE_STATIC = 0, STATE_CW = 1, STATE_ACW = -1;
	public static final int F_STABLE = 1;
	protected int state = 0;
	protected int rad = 500; // 最大转速 转/分钟

	protected MotorControl control;

	protected int flag;

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

	public void setControl(MotorControl control) {
		this.control = control;
		control.setMax(rad);
	}
}
