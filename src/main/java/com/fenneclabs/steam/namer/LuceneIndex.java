package com.fenneclabs.steam.namer;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

public class LuceneIndex implements Index {

	private RAMDirectory index;
	private IndexWriter writer;
	private Analyzer analyzer;
	private IndexSearcher searcher;

	public LuceneIndex(Map<String, Integer> ids) throws IOException {
		index = new RAMDirectory();
		analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(index, conf);

		for (Entry<String, Integer> pair : ids.entrySet()) {
			add(String.valueOf(pair.getValue()), pair.getKey());
		}

		done();
	}

	public void add(String appId, String title) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Store.YES));
		doc.add(new StringField("appid", appId, Store.YES));
		writer.addDocument(doc);
	}

	public void done() throws IOException {
		writer.commit();
		writer.close();

		IndexReader reader = DirectoryReader.open(writer.getDirectory());
		searcher = new IndexSearcher(reader);
	}

	private static final String FUZZY = "~";

	public Integer search(String queryStr) {
		try {
			queryStr = queryStr.replace(" ", FUZZY + " ") + FUZZY;
			QueryParser parser = new QueryParser("title", analyzer);

			Query query = parser.parse(queryStr);

			TopDocs res = searcher.search(query, 10, Sort.RELEVANCE);

			if (res.totalHits > 0) {
				int docId = res.scoreDocs[0].doc;
				Document doc = searcher.doc(docId);
				return Integer.valueOf(doc.get("appid"));
			} else {
				return null;
			}
		} catch (Exception ex) {
			System.err.println("can't search:{" + queryStr + "}" + ex.getMessage());
			ex.printStackTrace(System.err);
			return null;
		}
	}
}
