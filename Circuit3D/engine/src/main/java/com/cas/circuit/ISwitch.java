package com.cas.circuit;

import com.cas.circuit.component.ControlIO;

public interface ISwitch {

	void setButton(ControlIO c);

	void doSwitch(boolean pressed);

}
