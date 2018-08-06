package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.Diode;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.util.Util;

public class DiodeElm extends CircuitElm {
	Diode diode;
	static final int FLAG_FWDROP = 1;
	final double defaultdrop = .805904783;
	double fwdrop, zvoltage;

	public DiodeElm() {
		super();
	}

	public DiodeElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
		diode = new Diode(CircuitElm.sim);
		fwdrop = defaultdrop;
		zvoltage = 0;
		setup();
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	void setup() {
		diode.setup(fwdrop, zvoltage);
	}

	@Override
	public void reset() {
		diode.reset();
		volts[0] = volts[1] = curcount = 0;
	}

	@Override
	public void stamp() {
		diode.stamp(nodes[0], nodes[1]);
	}

	@Override
	public void doStep() {
		diode.doStep(volts[0] - volts[1]);
	}

	@Override
	void calculateCurrent() {
		current = diode.calculateCurrent(volts[0] - volts[1]);
	}

	@Override
	void buildInfo() {
		info.add("diode");
		info.add(String.format("I = %s", Util.getCurrentText(getCurrent())));
		info.add(String.format("Vd = %s", Util.getVoltageText(getVoltageDiff())));
		info.add(String.format("P = %s", Util.getUnitText(getPower(), "W")));
		info.add(String.format("Vf = %s", Util.getVoltageText(fwdrop)));
	}
}
