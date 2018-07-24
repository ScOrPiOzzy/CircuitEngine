package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentText;
import static com.cas.circuit.util.Util.getUnitText;
import static com.cas.circuit.util.Util.getVoltageText;
import static com.cas.circuit.util.Util.pi;

import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;

import com.cas.circuit.component.Terminal;

public class VoltageElm extends CircuitElm {
	static final int FLAG_COS = 2;
	int waveform;
	public static final int WF_DC = 0, WF_AC = 1, WF_SQUARE = 2, WF_TRIANGLE = 3, WF_SAWTOOTH = 4, WF_PULSE = 5, WF_VAR = 6;
	double frequency, maxVoltage, freqTimeZero, bias, phaseShift, dutyCycle;

	public VoltageElm(int waveform) {
		super();
		this.waveform = waveform;
		maxVoltage = 220;
		frequency = 50;
		dutyCycle = .5;
		reset();
	}

	public VoltageElm(Unmarshaller u, Function<String, Terminal> f, Map<String, String> params) {
		super(u, f, params);
		String value = params.get("waveform");
		waveform = value == null ? 0 : Integer.parseInt(value);

		value = params.get("maxVoltage");
		maxVoltage = value == null ? 5 : Integer.parseInt(value);

		value = params.get("frequency");
		frequency = value == null ? 50 : Integer.parseInt(value);

		value = params.get("phaseShift");
		phaseShift = value == null ? 0 : Integer.parseInt(value);
	}

	@Override
	public void reset() {
		freqTimeZero = 0;
		curcount = 0;
	}

	public double triangleFunc(double x) {
		if (x < pi) {
			return x * (2 / pi) - 1;
		}
		return 1 - (x - pi) * (2 / pi);
	}

	@Override
	public void stamp() {
		if (waveform == WF_DC) {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		} else {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource);
		}
	}

	@Override
	public void doStep() {
		if (waveform != WF_DC) {
			sim.updateVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		}
	}

	public double getVoltage() {
		double w = 2 * pi * (sim.getT() - freqTimeZero) * frequency + phaseShift;

		switch (waveform) {
		case WF_DC:
			return maxVoltage + bias;
		case WF_AC:
			return Math.sin(w) * maxVoltage + bias;
		case WF_SQUARE:
			return bias + ((w % (2 * pi) > (2 * pi * dutyCycle)) ? -maxVoltage : maxVoltage);
		case WF_TRIANGLE:
			return bias + triangleFunc(w % (2 * pi)) * maxVoltage;
		case WF_SAWTOOTH:
			return bias + (w % (2 * pi)) * (maxVoltage / pi) - maxVoltage;
		case WF_PULSE:
			return ((w % (2 * pi)) < 1) ? maxVoltage + bias : bias;
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

	public void setPhaseShift(double phaseShift) {
		this.phaseShift = phaseShift;
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
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
		info.add(String.format("I = %s", getCurrentText(getCurrent())));
		info.add(String.format(((this instanceof RailElm) ? "V = %s" : "Vd = %s"), getVoltageText(getVoltageDiff())));

		if (waveform != WF_DC && waveform != WF_VAR) {
			info.add(String.format("f = %s", getUnitText(frequency, "Hz")));
			info.add(String.format("Vmax = ", getVoltageText(maxVoltage)));
			if (bias != 0) {
				info.add(String.format("Voff = %s", getVoltageText(bias)));
			} else if (frequency > 500) {
				info.add(String.format("wavelength = %s", getUnitText(2.9979e8 / frequency, "m")));
			}
			info.add(String.format("P = %s", getUnitText(getPower(), "W")));
		}
		if (getCurrent() != 0) {
			info.add(String.format("R = %s", getUnitText(getVoltageDiff() / getCurrent(), "")));
		}
	}
}
