package com.fenneclabs.steam.namer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.mashape.unirest.http.exceptions.UnirestException;

public class Namer {

	private static final boolean URL_ONLY = false;
	private static final String RES_PATH = "d:/res_";

	public static void main(String[] args) throws Exception {
		//String[] files = { "liste_normale.txt", "liste_donnateurs.txt", "liste_speciale.txt", "liste_premium.txt" };
		
		String[] files = { "liste_new.txt" };
		SteamSearcher.lucene = true;
		SteamSearcher.test = false;

		Namer namer = new Namer();
		for (String file : files) {
			namer.process(file);
			namer.stats();
		}
	}

	int processed = 0;
	int found = 0;
	int notFound = 0;
	long start = System.currentTimeMillis();
	SteamSearcher searcher;

	public Namer() throws UnirestException, IOException {
		searcher = new SteamSearcher();
	}

	public void process(String name) throws Exception {
		TreeMap<String, String> resMap = new TreeMap<>();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(RES_PATH + name)));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(Namer.class.getResourceAsStream(name)))) {
			String line;

			while ((line = br.readLine()) != null) {
				processed++;
				String searchLine = line.trim();

				if (searchLine.length() > 2) {
					searchLine = clean(searchLine);
					if (searchLine.isEmpty()) {
						continue;
					}

					String res = searcher.search(searchLine);

					if (res == null) {
						notFound++;
						System.out.println(line);
						resMap.put(searchLine, line);
					} else {
						found++;
						if (URL_ONLY) {
							System.out.println(res);
							resMap.put(searchLine, res);
						} else {
							System.out.println(line + " " + res);
							resMap.put(searchLine, line + " " + res);
						}
					}

					writer.flush();
				}
			}

			for (Entry<String, String> en : resMap.entrySet()) {
				writer.write(en.getValue() + "\r\n");
			}

		} finally {
			writer.close();
		}
	}

	public void stats() {
		System.out.println("");
		System.out.println("");
		System.out.println("processed:" + processed);
		System.out.println("found:" + found);
		System.out.println("notFound:" + notFound);
		System.out.println("took:" + ((System.currentTimeMillis() - start) / 1000 / 60) + " minutes");
		System.out.println("");
		System.out.println("");
	}

	public static String clean(String searchTerm) {
		searchTerm = searchTerm.trim().toLowerCase();
		// Remove BBCode
		while (searchTerm.contains("[")) {
			StringBuilder sb = new StringBuilder();
			boolean inSideTag = false;

			for (char c : searchTerm.toCharArray()) {
				if (!inSideTag) {
					if (c == '[') {
						inSideTag = true;
					} else {
						sb.append(c);
					}
				} else if (c == ']') {
					inSideTag = false;
				}
			}
			searchTerm = sb.toString();
		}

		for (int i = 1; i < 5; i++) {
			searchTerm = searchTerm.replace("X" + i, "");
		}

		// process the line.
		int pos = searchTerm.indexOf('(');
		if (pos > 0) {
			searchTerm = searchTerm.substring(0, pos);
		}

		for (char c : "+-&|!(){}[]^'._\"~*?:\\/".toCharArray()) {
			searchTerm = searchTerm.replace("" + c, " ");
		}

		searchTerm = searchTerm.replaceAll(" +", " ");

		return searchTerm.trim();
	}
}
