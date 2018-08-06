package com.cas.circuit.element;

import com.cas.circuit.util.Util;

public class GroundElm extends CircuitElm {
	@Override
	public int getPostCount() {
		return 1;
	}

	static int bbb = 0;

	@Override
	public void setCurrent(int x, double c) {
		current = -c;
	}

	@Override
	public void stamp() {
		CircuitElm.sim.stampVoltageSource(0, nodes[0], voltSource, 0);
	}

	@Override
	public double getVoltageDiff() {
		return 0;
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	void buildInfo() {
		info.add("ground");
		info.add(String.format("I = %s", Util.getCurrentText(getCurrent())));
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return true;
	}
}
