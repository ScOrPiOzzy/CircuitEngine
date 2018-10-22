package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;
import com.cas.circuit.util.Util;

public class ResistorElm extends CircuitElm {
	public double resistance = 100;

	public ResistorElm() {
	}

	public ResistorElm(double r) {
		resistance = r;
	}

	public ResistorElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
		String value = null;
		value = params.get("resistance");
		resistance = value == null ? resistance : Double.parseDouble(value);
	}

	@Override
	void calculateCurrent() {
		current = (volts[0] - volts[1]) / resistance;
	}

	@Override
	public void stamp() {
		CircuitElm.sim.stampResistor(nodes[0], nodes[1], resistance);
	}

	@Override
	void buildInfo() {
		info.add("resistor");
		info.add(String.format("R = %s", Util.getUnitText(resistance, Util.ohmString)));
		super.buildInfo();
		info.add(String.format("p1=%s p2=%s", volts[0], volts[1]));
		info.add(String.format("P = %s", Util.getUnitText(getPower(), "W")));
	}

}
