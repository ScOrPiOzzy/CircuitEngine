package com.cas.circuit.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.CirSim;
import com.cas.circuit.ISwitch;
import com.cas.circuit.component.ControlIO;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.util.Util;
import com.cas.circuit.vo.Pair;
import com.cas.circuit.vo.Pair.Type;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SwitchElm extends CircuitElm implements ISwitch {
//	private boolean momentary;
	// position 0 == closed, position 1 == open
	int position = 0, posCount = 2;

	@Setter
	private ControlIO button;

	public SwitchElm() {
		super();
//		momentary = false;
		position = 0;
	}

	public SwitchElm(boolean mm) {
		super();
		position = (mm) ? 1 : 0;
//		momentary = mm;
	}

	public SwitchElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
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
			CirSim.ins.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
		}
	}

	@Override
	public int getVoltageSourceCount() {
		return (position == 1) ? 0 : 1;
	}

	@Override
	public void doSwitch(boolean pressed) {
		position++;
		if (position >= posCount) {
			position = 0;
		}
		CirSim.ins.needAnalyze();
	}

	@Override
	void buildInfo() {
		super.buildInfo();
		info.add(0, "switch (SPST)");
//		arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
		if (position == 1) {
			info.add(1, "open");
			info.add(String.format("Vd = %s", Util.getVoltageDText(getVoltageDiff())));
		} else {
			info.add(1, "closed");
			info.add(String.format("V = %s", Util.getVoltageText(volts[0])));
			info.add(String.format("I = %s", Util.getCurrentDText(getCurrent())));
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

	@Override
	public List<Pair> getContactorList() {
		List<Pair> postList = new ArrayList<>();
		postList.add(new Pair(id, term1, term2, position == 0 ? Type.NC : Type.NO));
		return postList;
	}

}
