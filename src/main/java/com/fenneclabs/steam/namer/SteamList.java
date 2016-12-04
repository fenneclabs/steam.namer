package com.fenneclabs.steam.namer;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SteamList {
	private static final String STEAM = "http://api.steampowered.com/ISteamApps/GetAppList/v0002/";
	private Map<String, Integer> ids = new HashMap<>();

	public Map<String, Integer> build() throws UnirestException {
		System.out.println("building steam list");
		JsonNode res = Unirest.get(STEAM).asJson().getBody();
		JSONArray list = res.getObject().getJSONObject("applist").getJSONArray("apps");

		for (int i = 0; i < list.length(); i++) {
			JSONObject app = list.getJSONObject(i);
			int appid = app.getInt("appid");
			String name = app.getString("name").trim().toLowerCase();
			ids.put(Namer.clean(name), appid);
		}
		System.out.println("done, found " + ids.size() + " items");
		System.out.println("");
		System.out.println("");

		return ids;
	}
}
