package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.LightIO;
import com.cas.circuit.component.Terminal;

import lombok.Setter;

public class LEDElm extends DiodeElm {
	@Setter
	private LightIO light;

	public LEDElm() {
		super();
		fwdrop = 2.1024259;
		setup();
	}

	public LEDElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
	}

	@Override
	void buildInfo() {
		info.add("LED");
		super.buildInfo();
	}

}
