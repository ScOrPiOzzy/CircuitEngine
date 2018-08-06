package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;
import com.cas.circuit.util.Util;

public class VoltageElm extends CircuitElm {
	static final int FLAG_COS = 2;
	int waveform;
	public static final int WF_DC = 0, WF_AC = 1, WF_SQUARE = 2, WF_TRIANGLE = 3, WF_SAWTOOTH = 4, WF_PULSE = 5, WF_VAR = 6;
	private double maxVoltage, bias, phaseShift, dutyCycle;

	private int frequency;

	public VoltageElm(int waveform) {
		super();
		this.waveform = waveform;
		maxVoltage = 220;
		frequency = 50;
		dutyCycle = .5;
		reset();
	}

	public VoltageElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
		String value = params.get("waveform");
		waveform = value == null ? 0 : Integer.parseInt(value);

		value = params.get("maxVoltage");
		maxVoltage = value == null ? 5 : Double.parseDouble(value);

		value = params.get("frequency");
		frequency = value == null ? 50 : Integer.parseInt(value);

		value = params.get("phaseShift");
		setPhaseShift(value == null ? 0 : Integer.parseInt(value));
	}

	@Override
	public void reset() {
		curcount = 0;
	}

	public double triangleFunc(double x) {
		if (x < Math.PI) {
			return x * (2 / Math.PI) - 1;
		}
		return 1 - (x - Math.PI) * (2 / Math.PI);
	}

	@Override
	public void stamp() {
		if (waveform == VoltageElm.WF_DC) {
			CircuitElm.sim.stampVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		} else {
			CircuitElm.sim.stampVoltageSource(nodes[0], nodes[1], voltSource);
		}
	}

	@Override
	public void doStep() {
		if (waveform != VoltageElm.WF_DC) {
			CircuitElm.sim.updateVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		}
	}

	public double getVoltage() {
		double w = 2 * Math.PI * CircuitElm.sim.getTimer() * frequency + phaseShift;

		switch (waveform) {
		case WF_DC:
			return maxVoltage + bias;
		case WF_AC:
			return Math.sin(w) * maxVoltage + bias;
		case WF_SQUARE:
			return bias + ((w % (2 * Math.PI) > (2 * Math.PI * dutyCycle)) ? -maxVoltage : maxVoltage);
		case WF_TRIANGLE:
			return bias + triangleFunc(w % (2 * Math.PI)) * maxVoltage;
		case WF_SAWTOOTH:
			return bias + (w % (2 * Math.PI)) * (maxVoltage / Math.PI) - maxVoltage;
		case WF_PULSE:
			return ((w % (2 * Math.PI)) < 1) ? maxVoltage + bias : bias;
		default:
			return 0;
		}
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public double getPower() {
		return -getVoltageDiff() * current;
	}

	@Override
	public double getVoltageDiff() {
		return volts[1] - volts[0];
	}

	@Override
	void buildInfo() {
		super.buildInfo();
		switch (waveform) {
		case WF_DC:
		case WF_VAR:
			info.add(0, "voltage source");
			break;
		case WF_AC:
			info.add(0, "A/C source");
			break;
		case WF_SQUARE:
			info.add(0, "square wave gen");
			break;
		case WF_PULSE:
			info.add(0, "pulse gen");
			break;
		case WF_SAWTOOTH:
			info.add(0, "sawtooth gen");
			break;
		case WF_TRIANGLE:
			info.add(0, "triangle gen");
			break;
		}
		info.add(String.format("I = %s", Util.getCurrentText(getCurrent())));
		info.add(String.format(((this instanceof RailElm) ? "V = %s" : "Vd = %s"), Util.getVoltageText(getVoltageDiff())));

		if (waveform != VoltageElm.WF_DC && waveform != VoltageElm.WF_VAR) {
			info.add(String.format("f = %s", Util.getUnitText(frequency, "Hz")));
			info.add(String.format("Vmax = ", Util.getVoltageText(maxVoltage)));
			if (bias != 0) {
				info.add(String.format("Voff = %s", Util.getVoltageText(bias)));
			} else if (frequency > 500) {
				info.add(String.format("wavelength = %s", Util.getUnitText(2.9979e8 / frequency, "m")));
			}
			info.add(String.format("P = %s", Util.getUnitText(getPower(), "W")));
		}
		if (getCurrent() != 0) {
			info.add(String.format("R = %s", Util.getUnitText(getVoltageDiff() / getCurrent(), "")));
		}
	}

	/**
	 * @param phaseShift 角度
	 */
	public void setPhaseShift(int phaseShift) {
		this.phaseShift = Math.toRadians(phaseShift);
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}
}
