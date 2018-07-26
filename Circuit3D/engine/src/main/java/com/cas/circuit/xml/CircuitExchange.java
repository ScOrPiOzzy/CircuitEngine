package com.cas.circuit.xml;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cas.circuit.component.ElecCompDef;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.xml.adapter.MapAdapter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
public class CircuitExchange {
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String type;

	@XmlElement(name = "Params")
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, String> params;

	private CircuitElm circuitElm;

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		ElecCompDef elecCompDef = (ElecCompDef) parent;
		Function<String, Terminal> f = (key) -> {
			Terminal term = elecCompDef.getTerminalMap().get(key);
			log.debug(String.format("%s:%s", key, term == null ? "Not Fount!" : term.toString()));
			return term;
		};

		try {
			Class<?> clazz = Class.forName(String.format("com.cas.circuit.element.%s", type));
			Constructor<?> c = clazz.getDeclaredConstructor(new Class[] { Function.class, Map.class });
			circuitElm = (CircuitElm) c.newInstance(f, params);
			elecCompDef.putCircuitElm(id, circuitElm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
