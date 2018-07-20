package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentDText;
import static com.cas.circuit.util.Util.getVoltageDText;
import static com.cas.circuit.util.Util.getVoltageText;

import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;

import com.cas.circuit.ISwitch;
import com.cas.circuit.component.ControlIO;
import com.cas.circuit.component.Terminal;

import lombok.Setter;

public class SwitchElm extends CircuitElm implements ISwitch {
//	private boolean momentary;
	// position 0 == closed, position 1 == open
	int position, posCount;

	@Setter
	private ControlIO button;

	public SwitchElm() {
		super();
//		momentary = false;
		position = 0;
		posCount = 2;
	}

	public SwitchElm(boolean mm) {
		super();
		position = (mm) ? 1 : 0;
//		momentary = mm;
		posCount = 2;
	}

	public SwitchElm(Unmarshaller u, Function<Object, Terminal> f, Map<String, String> params) {
		super(u, f, params);
		String value = null;

//		value = params.get("momentary");
//		momentary = value != null ? Boolean.valueOf(value) : false;

		value = params.get("position");
		position = value != null ? Integer.valueOf(value) : 0;
	}

	@Override
	void calculateCurrent() {
		if (position == 1) {
			current = 0;
		}
	}

	@Override
	public void stamp() {
		if (position == 0) {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
		}
	}

	@Override
	public int getVoltageSourceCount() {
		return (position == 1) ? 0 : 1;
	}

	@Override
	public void doSwitch() {
		position++;
		if (position >= posCount) {
			position = 0;
		}
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "switch (SPST)";
//		arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
		if (position == 1) {
			arr[1] = "open";
			arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		} else {
			arr[1] = "closed";
			arr[2] = "V = " + getVoltageText(volts[0]);
			arr[3] = "I = " + getCurrentDText(getCurrent());
		}
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return position == 0;
	}

	@Override
	public boolean isWire() {
		return true;
	}
}
