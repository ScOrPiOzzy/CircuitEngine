package com.cas.circuit.xml.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, String>> {

	private DocumentBuilder documentBuilder;

	public MapAdapter() throws Exception {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public static class AdaptedMap {
		@XmlAnyElement
		public List<Element> elements = new ArrayList<>();
	}

	@Override
	public AdaptedMap marshal(Map<String, String> map) throws Exception {
		Document document = documentBuilder.newDocument();
		AdaptedMap adaptedMap = new AdaptedMap();
		for (Entry<String, String> entry : map.entrySet()) {
			Element element = document.createElement(entry.getKey());
			element.setTextContent(entry.getValue());
			adaptedMap.elements.add(element);
		}
		return adaptedMap;
	}

	@Override
	public Map<String, String> unmarshal(AdaptedMap adaptedMap) throws Exception {
		HashMap<String, String> map = new HashMap<>();
		for (Element element : adaptedMap.elements) {
			map.put(element.getLocalName(), element.getTextContent());
		}
		return map;
	}

}