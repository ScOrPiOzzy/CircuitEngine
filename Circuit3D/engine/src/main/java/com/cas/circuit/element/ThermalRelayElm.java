package com.cas.circuit.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;

import com.cas.circuit.component.Terminal;

/**
 * 热继电器
 * @author Administrator
 */
public class ThermalRelayElm extends RelayElmEx {

//	电热片端子
	private List<Terminal[]> heater;
	private double resistance;
	private double temperature;

	public ThermalRelayElm() {
		super();
	}

	public ThermalRelayElm(Unmarshaller u, Function<String, Terminal> f, Map<String, String> params) {
		String value = params.get("heater");
		String[] arr = value.split("\\|");

		heater = new ArrayList<>(arr.length);

		for (int i = 0; i < arr.length; i++) {
			String[] termid = arr[i].split(",");

			Terminal[] t = new Terminal[2];
			t[0] = f.apply(termid[0]);
			t[1] = f.apply(termid[1]);

			heater.add(t);
		}

		value = params.get("resistance");
		resistance = value == null ? resistance : Double.parseDouble(value);
		value = params.get("temperature");
		temperature = value == null ? temperature : Double.parseDouble(value);

		pairs = 2;
		poleCount = 0;

		posts = new ArrayList<>();
		setPosts(f, params.get("nc"));
		flag = poleCount;
		setPosts(f, params.get("no"));

//		coil1.setIndexInElm(posts.size());
//		posts.add(coil1);
//		coil1.setElm(this);
//
//		coil2.setIndexInElm(posts.size());
//		posts.add(coil2);
//		coil2.setElm(this);
//
//		setupPoles();
//		allocNodes();
	}

}
