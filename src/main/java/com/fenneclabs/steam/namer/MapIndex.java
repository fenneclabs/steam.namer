package com.fenneclabs.steam.namer;

import java.util.Map;

public class MapIndex implements Index {

	private final Map<String, Integer> ids;

	public MapIndex(Map<String, Integer> ids) {
		this.ids = ids;
	}

	public Integer search(String searchTerm) {

		if (searchTerm.length() < 3) {
			return null;
		}

		Integer id = ids.get(searchTerm);
		if (id == null) {
			searchTerm = searchTerm.replaceAll("[^A-Za-z0-9]", "");

			// Exact
			for (String key : ids.keySet()) {
				String tmpKey = key.replaceAll("[^A-Za-z0-9]", "");
				if (tmpKey.equalsIgnoreCase(searchTerm)) {
					id = ids.get(key);
					break;
				}
			}

			// Include
			if (id == null) {
				for (String key : ids.keySet()) {
					String tmpKey = key.replaceAll("[^A-Za-z0-9]", "");
					if (searchTerm.length() > 3 && tmpKey.contains(searchTerm)) {
						id = ids.get(key);
						break;
					}
				}
			}
		}

		return id;
	}

}
