package com.cas.circuit.element;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.cas.circuit.ILight;
import com.cas.circuit.component.LightIO;
import com.cas.circuit.component.Terminal;

import lombok.Setter;

/**
 * 熔断器
 */
public class FuseElm extends ResistorElm implements ILight {

	// 单位安培（A）
	@Setter
	private double ratedCurrent = 2;
	@Setter
	private LightIO light;

	private double normal = 1e-2, broken = 1e8;

	public FuseElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);

		normal = resistance;
	}

	@Override
	public void startIteration() {
		double q = Math.abs(current);
		if (q > Math.abs(ratedCurrent) * 1.5) {
			resistance = broken;
			Optional.ofNullable(light).ifPresent(l -> {
				l.openLight();
			});
		}
	}

	@Override
	public void reset() {
		super.reset();
		resistance = normal;
		Optional.ofNullable(light).ifPresent(l -> {
			l.closeLight();
		});
	}

}
