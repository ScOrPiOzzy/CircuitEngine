package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentText;

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
		sim.stampVoltageSource(0, nodes[0], voltSource, 0);
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
	public void getInfo(String arr[]) {
		arr[0] = "ground";
		arr[1] = "I = " + getCurrentText(getCurrent());
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return true;
	}
}
