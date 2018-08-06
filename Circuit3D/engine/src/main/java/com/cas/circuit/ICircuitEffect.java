package com.cas.circuit;

import com.cas.circuit.effect.ParticleEffect;
import com.cas.circuit.element.CircuitElm;

public interface ICircuitEffect {

	void addElecCompEffect(CircuitElm elm, ParticleEffect effect);

	void removeElecCompEffect(CircuitElm elm);

}
