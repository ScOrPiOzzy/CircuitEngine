package com.cas.circuit;

import java.util.ArrayList;
import java.util.List;

import com.cas.circuit.component.Terminal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitNode {
//	private int x = -1;
//	private int y = -1;
	private boolean internal;

	private int num;

	private List<Terminal> terminals = new ArrayList<>();

	public boolean hasTerminal(Terminal terminal) {
		return terminals.contains(terminal);
	}

	public void addTerminal(Terminal terminal) {
//		System.out.println(num + " - " + terminal);
		terminals.add(terminal);
		terminal.setNode(this);
	}

	public Terminal getTerminal(int index) {
		return terminals.get(index);
	}

	public int getTerminalSize() {
		return terminals.size();
	}
}
