package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentDText;
import static com.cas.circuit.util.Util.getVoltageText;

import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;

import com.cas.circuit.component.Terminal;

public class WireElm extends ResistorElm {

	public static boolean ideal = true;
	private static final double defaultResistance = 1E-06;

	public WireElm() {
		super();
		resistance = defaultResistance;
	}

	public WireElm(int r) {
		super(r);
	}

	public WireElm(Unmarshaller u, Function<String, Terminal> f, Map<String, String> params) {
		super(u, f, params);
	}

	@Override
	void calculateCurrent() {
		if (!ideal) {
			super.calculateCurrent();
		}
	}

	@Override
	public void stamp() {
		if (ideal) {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
		} else {
			sim.stampResistor(nodes[0], nodes[1], resistance);
		}
	}

	@Override
	public int getVoltageSourceCount() {
		if (ideal) {
			return 1;
		} else {
			return super.getVoltageSourceCount();
		}
	}

	@Override
	void buildInfo() {
		info.add("wire");
		info.add(String.format("I = %s", getCurrentDText(getCurrent())));
		info.add(String.format("V = %s", getVoltageText(volts[0])));
	}

	@Override
	public double getPower() {
		if (ideal) {
			return 0;
		} else {
			return super.getPower();
		}
	}

	@Override
	public double getVoltageDiff() {
		if (ideal) {
			return volts[0];
		} else {
			return super.getVoltageDiff();
		}
	}

	@Override
	public boolean isWire() {
		return ideal;
	}
}
