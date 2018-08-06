package com.cas.circuit.xml.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.cas.circuit.component.Terminal;

public class TerminalMapAdapter extends XmlAdapter<Terminal[], Map<String, Terminal>> {
	@Override
	public Terminal[] marshal(Map<String, Terminal> map) throws Exception {
		List<Terminal> beans = new ArrayList<>(map.size());
		Iterator<Entry<String, Terminal>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			beans.add(it.next().getValue());
		}
		return beans.toArray(new Terminal[beans.size()]);
	}

	@Override
	public Map<String, Terminal> unmarshal(Terminal[] beans) throws Exception {
		Map<String, Terminal> map = new HashMap<>();
		for (Terminal bean : beans) {
			map.put(bean.getId(), bean);
		}
		return map;
	}
}