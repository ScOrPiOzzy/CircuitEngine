package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentDText;
import static com.cas.circuit.util.Util.getVoltageDText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.ISwitch;
import com.cas.circuit.component.ControlIO;
import com.cas.circuit.component.Terminal;

import lombok.extern.slf4j.Slf4j;

// 0 = switch
// 1 = switch end 1
// 2 = switch end 2
// ...
// 3n   = coil
// 3n+1 = coil
// 3n+2 = end of coil resistor
/**
 * 继电器， 能控制COM-NC-NO一组连接头的通断
 * @author Administrator
 */
@Slf4j
public class RelayElm extends CircuitElm implements ISwitch {
	private static final int COM = 0, NC = 1, NO = 2;
	protected double r_on = .05;
	protected double r_off = 1e18;
	protected double onCurrent = .02;

	protected double coilCurrent, switchCurrent[];
	protected double d_position;
//	开关的位置
	protected int i_position;
//	poleCount  触点的对数，默认值:1
//	pairs  每一组触点的数量， 默认值:3
	protected int poleCount = 1;
	protected int pairs = 3;

//	线圈的阻值
	protected double coilR = 20;

	protected int nCoil1, nCoil2;
	protected List<Terminal> posts;

	protected double delta = 0;
	protected boolean lock;

	protected ControlIO button;

	protected boolean force;

	public RelayElm() {
		noDiagonal = true;
	}

	public RelayElm(Function<String, Terminal> f, Map<String, String> params) {
		String value = null;

		Terminal coil1 = f.apply(params.get("coil1"));
		Terminal coil2 = f.apply(params.get("coil2"));

		value = params.get("posts");
		if (value == null) {
			throw new RuntimeException("继电器配置有错");
		}
		String[] arr = value.split("\\|");
		List<List<Terminal>> termGroupedList = new ArrayList<>(arr.length);
		for (int i = 0; i < arr.length; i++) {
			String[] tid = arr[i].split(",");

			List<Terminal> termList = new ArrayList<>(3);
			termList.add(f.apply(tid[COM]));
			termList.add(f.apply(tid[NC]));
			termList.add(f.apply(tid[NO]));

			termGroupedList.add(termList);
		}

		value = params.get("coilR");
		coilR = value == null ? coilR : Float.parseFloat(value);

		value = params.get("onCurrent");
		onCurrent = value == null ? onCurrent : Float.parseFloat(value);

		setPosts(coil1, coil2, termGroupedList);
	}

	public void setPosts(Terminal coil1, Terminal coil2, List<List<Terminal>> termGroupedList) {
		poleCount = termGroupedList.size();

		posts = new ArrayList<>();

		for (List<Terminal> s : termGroupedList) {
			for (int j = 0; j < s.size(); j++) {
				Terminal t = s.get(j);
				t.setIndexInElm(posts.size());
				posts.add(t);
				t.setElm(this);
			}
		}

		coil1.setIndexInElm(posts.size());
		coil1.setElm(this);
		posts.add(coil1);

		coil2.setIndexInElm(posts.size());
		coil2.setElm(this);
		posts.add(coil2);

		setupPoles();
		allocNodes();
	}

	void setupPoles() {
		nCoil1 = pairs * poleCount;
		nCoil2 = nCoil1++;

		if (switchCurrent == null || switchCurrent.length != poleCount) {
			switchCurrent = new double[poleCount];
		}
	}

	@Override
	public Terminal getPostPoint(int n) {
		return posts.get(n);
	}

	@Override
	public int getPostCount() {
		return 2 + poleCount * pairs;
	}

	@Override
	public void reset() {
		super.reset();
		coilCurrent = 0;
		for (int i = 0; i != poleCount; i++) {
			switchCurrent[i] = 0;
		}
	}

	@Override
	public void stamp() {
		// resistor from coil post 1 to coil post 2
		sim.stampResistor(nodes[nCoil1], nodes[nCoil2], coilR);

		for (int p = 0; p != poleCount; p++) {
			sim.stampNonLinear(nodes[COM + p * pairs]);
			sim.stampNonLinear(nodes[NC + p * pairs]);
			sim.stampNonLinear(nodes[NO + p * pairs]);
		}
	}

	@Override
	public void startIteration() {
		if (force) {
			i_position = 1;
			return;
		}

		if (!lock) {
			// magic value to balance operate speed with reset speed semi-realistically
			double magic = 1.3;
			double pmult = Math.sqrt(magic + 1);
			double p = coilCurrent * pmult / onCurrent;
			double f = Math.abs(p * p) - 1.3;

			d_position = f;
			if (d_position < 0) {
				d_position = 0;
			}
		} else if (delta == 0 && delta == coilCurrent) {
			if (lock) {
				button.unstuck();
				lock = false;
			}
		}

		if (d_position > 1) {
			d_position = 1;
			if (!lock) {
				lock = true;
				button.absorbed();
			}
		}

		if (d_position < .1) {
			i_position = 0;
		} else if (d_position > .9) {
			i_position = 1;
		} else {
			i_position = 2;
		}
		delta = coilCurrent;
	}

	@Override
	public void doStep() {
		for (int p = 0; p != poleCount; p++) {
			sim.stampResistor(nodes[COM + p * pairs], nodes[NC + p * pairs], i_position == 0 ? r_on : r_off);
			sim.stampResistor(nodes[COM + p * pairs], nodes[NO + p * pairs], i_position == 1 ? r_on : r_off);
		}
	}

	@Override
	void calculateCurrent() {
		double voltdiff = volts[nCoil1] - volts[nCoil2];
		coilCurrent = voltdiff / coilR;

		// actually this isn't correct, since there is a small amount of current through the switch when off
		for (int p = 0; p != poleCount; p++) {
			if (i_position == 0) {
				switchCurrent[p] = (volts[COM + p * pairs] - volts[NC + p * pairs]) / r_on;
			} else if (i_position == 1) {
				switchCurrent[p] = (volts[COM + p * pairs] - volts[NO + p * pairs]) / r_on;
			} else if (i_position == 2) {
				switchCurrent[p] = 0;
			}
		}
	}

	@Override
	void buildInfo() {
		info.add(String.format(i_position == 0 ? "%s (off)" : i_position == 1 ? "%s (on)" : "%s", getClass().getSimpleName()));
		super.buildInfo();
		for (int i = 0; i != poleCount; i++) {
			info.add(String.format("I%d = %s", (i + 1), getCurrentDText(switchCurrent[i])));
		}
		info.add(String.format("coil I = %s", getCurrentDText(coilCurrent)));
		info.add(String.format("coil Vd = %s", getVoltageDText(volts[nCoil1] - volts[nCoil2])));
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return n1 / pairs == n2 / pairs;
	}

	// we need this to be able to change the matrix for each step
	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public void setButton(ControlIO c) {
		this.button = c;
	}

	@Override
	public void doSwitch(boolean pressed) {
		force = pressed;
	}
}
