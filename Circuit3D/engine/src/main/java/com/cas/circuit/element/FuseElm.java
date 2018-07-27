package com.cas.circuit.element;

import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;

/**
 * 熔断器
 */
public class FuseElm extends ResistorElm {

	public FuseElm() {
		super();
	}

	public FuseElm(int r) {
		super(r);
	}

	public FuseElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);
	}

}
