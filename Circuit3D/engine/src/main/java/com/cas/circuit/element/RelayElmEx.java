package com.cas.circuit.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.vo.Pair;
import com.cas.circuit.vo.Pair.Type;

import lombok.extern.slf4j.Slf4j;

/**
 * 继电器的拓展，能控制一组NO或NC的连接头的通断。
 */
@Slf4j
public class RelayElmEx extends RelayElm {

	protected int flag = 0;

	public RelayElmEx() {
	}

	public RelayElmEx(Function<String, Terminal> f, Map<String, String> params) {
		pairs = 2;
		poleCount = 0;
		posts = new ArrayList<>();
		setPosts(f, params.get("nc"));

		flag = poleCount;

		setPosts(f, params.get("no"));

		addCoils(f.apply(params.get("coil1"))//
				, f.apply(params.get("coil2")));

		String value = params.get("coilR");
		coilR = value == null ? coilR : Float.parseFloat(value);

		value = params.get("onCurrent");
		onCurrent = value == null ? onCurrent : Float.parseFloat(value);

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
		CirSim.ins.stampResistor(nodes[nCoil1], nodes[nCoil2], coilR);

		for (int p = 0; p != poleCount; p++) {
			CirSim.ins.stampNonLinear(nodes[p * pairs]);
			CirSim.ins.stampNonLinear(nodes[p * pairs + 1]);
		}
	}

	@Override
	public void doStep() {
//		NC
		for (int p = 0; p != flag; p++) {
			CirSim.ins.stampResistor(nodes[p * pairs], nodes[p * pairs + 1], i_position == 0 ? r_on : r_off);
		}

//		NO
		for (int p = flag; p != poleCount; p++) {
			CirSim.ins.stampResistor(nodes[p * pairs], nodes[p * pairs + 1], i_position == 1 ? r_on : r_off);
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

	@Override
	public List<Pair> getContactorList() {
		List<Pair> postList = new ArrayList<>();

//		NC
		for (int p = 0; p != flag; p++) {
			postList.add(new Pair(id, posts.get(p * pairs), posts.get(p * pairs + 1), Type.NC));
		}

//		NO
		for (int p = flag; p != poleCount; p++) {
			postList.add(new Pair(id, posts.get(p * pairs), posts.get(p * pairs + 1), Type.NO));
		}
		return postList;
	}

}
