package com.cas.circuit;

import com.cas.circuit.effect.ParticleEffect;
import com.cas.circuit.element.CircuitElm;

public interface ICircuitEffect {
	void setElecCompParticleEffect(CircuitElm elm, ParticleEffect effect);
}
