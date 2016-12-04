package com.fenneclabs.steam.namer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

public class SteamSearcher {

	private static final String STEAM_DETAILS = "http://store.steampowered.com/api/appdetails?appids=%d&l=french";
	private final Index index;
	public static boolean lucene = true;
	public static boolean test = true;

	public SteamSearcher() throws UnirestException, IOException {
		Map<String, Integer> ids = new SteamList().build();
		index = lucene ? new LuceneIndex(ids) : new MapIndex(ids);
	}

	public String search(String searchTerm) throws Exception {

		Integer id = index.search(searchTerm);
		if (id == null) {
			return "";
		} else if (test) {
			return "http://store.steampowered.com/app/" + id;
		}

		StringBuilder details = new StringBuilder();
		String tmpInfo = null;

		int nb = 0;
		try {
			for (int i = 0; i < 3 && tmpInfo == null; i++) {
				GetRequest gameDetails = Unirest.get(String.format(STEAM_DETAILS, id));
				tmpInfo = gameDetails.asString().getBody().trim();

				if (tmpInfo == null || tmpInfo.isEmpty() || tmpInfo.equalsIgnoreCase("null") || !tmpInfo.startsWith("{")) {
					System.err.println(new Date() + " nb:" + nb + " try:" + i + " response is null, waiting .... for: "
							+ searchTerm);
					Thread.sleep(4 * 60000);
				} else {
					nb++;
					JsonNode res = gameDetails.asJson().getBody();
					JSONObject root = res.getObject().getJSONObject(String.valueOf(id));

					if (root.getBoolean("success")) {
						JSONObject data = root.getJSONObject("data");
						//String date = getString(data, "release_date", "date");
						List<String> categories = getList(data, "categories", "description");
						List<String> genres = getList(data, "genres", "description");
						List<String> os = getList(data, "platforms", true);

						String score = getString(data, "recommendations", "total");
						//String meta = getString(data, "metacritic", "score");

						details
						//.append(date).append(" ")
						.append(genres)
						//.append(categories)
						.append(" reco:").append(score)
						.append(" os:").append(os)
						//.append(" meta:").append(meta)
						;
						if(categories.contains("Cartes à échanger Steam")) {
							details.append(" cartes");
						}
						
					}
					tmpInfo = null;
					break;
				}
			}

		} catch (Throwable t) {
			System.err.println(tmpInfo + " " + searchTerm);
			t.printStackTrace(System.err);
		}

		return "[url=http://store.steampowered.com/app/" + id + "][b]Fiche Steam[/b][/url] " + details.toString();
	}

	private String getString(JSONObject data, String key, String value) {
		if (!data.has(key)) {
			return "";
		} else {
			JSONObject obj = data.getJSONObject(key);
			return obj.has(value) ? obj.opt(value).toString() : "";
		}
	}

	List<String> getList(JSONObject data, String name, String key) {
		List<String> res = new ArrayList<>();

		if (data.has(name)) {
			JSONArray array = data.getJSONArray(name);
			for (int i = 0; i < array.length(); i++) {
				JSONObject app = array.getJSONObject(i);
				if (app.has(key)) {
					res.add(app.getString(key));
				}
			}
		}

		return res;
	}
	
	/*			"platforms": {
"windows": true,
"mac": true,
"linux": false
},*/
	private List<String> getList(JSONObject data, String name, boolean b) {
		List<String> res = new ArrayList<>();

		if (data.has(name)) {
			JSONObject map = data.getJSONObject(name);
			String[] names = JSONObject.getNames(map);
			for(String os: names) {
				if(map.optBoolean(os)) {
					res.add(os);
				}
			}
		}

		return res;
	}


}
