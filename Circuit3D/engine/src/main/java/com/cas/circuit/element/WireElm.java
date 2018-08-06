package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;
import com.cas.circuit.util.Util;

public class WireElm extends ResistorElm {

	public static boolean ideal = true;
	private static final double defaultResistance = 1E-06;

	public WireElm() {
		super();
		resistance = WireElm.defaultResistance;
	}

	public WireElm(int r) {
		super(r);
	}

	public WireElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
	}

	@Override
	void calculateCurrent() {
		if (!WireElm.ideal) {
			super.calculateCurrent();
		}
	}

	@Override
	public void stamp() {
		if (WireElm.ideal) {
			CircuitElm.sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
		} else {
			CircuitElm.sim.stampResistor(nodes[0], nodes[1], resistance);
		}
	}

	@Override
	public int getVoltageSourceCount() {
		if (WireElm.ideal) {
			return 1;
		} else {
			return super.getVoltageSourceCount();
		}
	}

	@Override
	void buildInfo() {
		info.add("wire");
		info.add(String.format("I = %s", Util.getCurrentDText(getCurrent())));
		info.add(String.format("V = %s", Util.getVoltageText(volts[0])));
	}

	@Override
	public double getPower() {
		if (WireElm.ideal) {
			return 0;
		} else {
			return super.getPower();
		}
	}

	@Override
	public double getVoltageDiff() {
		if (WireElm.ideal) {
			return volts[0];
		} else {
			return super.getVoltageDiff();
		}
	}

	@Override
	public boolean isWire() {
		return WireElm.ideal;
	}
}
