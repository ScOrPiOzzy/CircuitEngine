package com.cas.circuit.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;

import lombok.extern.slf4j.Slf4j;

/**
 * 继电器的拓展，能控制一组NO或NC的连接头的通断。
 */
@Slf4j
public class RelayElmEx extends RelayElm {

	protected int flag = 0;

	public RelayElmEx() {
		super();
	}

	public RelayElmEx(Function<String, Terminal> f, Map<String, String> params) {
		Terminal coil1 = f.apply(params.get("coil1"));
		Terminal coil2 = f.apply(params.get("coil2"));
		pairs = 2;
		poleCount = 0;
		posts = new ArrayList<>();
		setPosts(f, params.get("nc"));

		flag = poleCount;

		setPosts(f, params.get("no"));

		coil1.setIndexInElm(posts.size());
		posts.add(coil1);
		coil1.setElm(this);

		coil2.setIndexInElm(posts.size());
		posts.add(coil2);
		coil2.setElm(this);

		setupPoles();
		allocNodes();
	}

	public void setPosts(Function<String, Terminal> f, String value) {
		if (value == null) {
			return;
		}
		String[] arr = value.split("\\|");
		List<Terminal> termList = new ArrayList<>(arr.length * 2);

		for (int i = 0; i < arr.length; i++) {
			String[] termid = arr[i].split(",");
			termList.add(f.apply(termid[0]));
			termList.add(f.apply(termid[1]));

			poleCount++;
		}

		termList.forEach(t -> {
			t.setIndexInElm(posts.size());
			posts.add(t);
			t.setElm(this);
		});
	}

	@Override
	public void stamp() {
		// resistor from coil post 1 to coil post 2
		sim.stampResistor(nodes[nCoil1], nodes[nCoil2], coilR);

		for (int p = 0; p != poleCount; p++) {
			sim.stampNonLinear(nodes[p * pairs]);
			sim.stampNonLinear(nodes[p * pairs + 1]);
		}
	}

	@Override
	public void doStep() {
//		NC
		for (int p = 0; p != flag; p++) {
			sim.stampResistor(nodes[p * pairs], nodes[p * pairs + 1], i_position == 0 ? r_on : r_off);
		}

//		NO
		for (int p = flag; p != poleCount; p++) {
			sim.stampResistor(nodes[p * pairs], nodes[p * pairs + 1], i_position == 1 ? r_on : r_off);
		}
	}

	@Override
	void calculateCurrent() {
		double voltdiff = volts[nCoil1] - volts[nCoil2];
		coilCurrent = voltdiff / coilR;
//		NC
		for (int p = 0; p != flag; p++) {
			if (i_position == 0) {
				switchCurrent[p] = (volts[p * pairs] - volts[p * pairs + 1]) / r_on;
			} else {
				switchCurrent[p] = 0;
			}
		}

//		NO
		for (int p = flag; p != poleCount; p++) {
			if (i_position == 1) {
				switchCurrent[p] = (volts[p * pairs] - volts[p * pairs + 1]) / r_on;
			} else {
				switchCurrent[p] = 0;
			}
		}
	}
}
