package cn.edu.thss.iise.beehivez.server.index.petrinetindex.luceneindex;
//package cn.edu.thss.iise.beehivez.server.index.luceneindex;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.index.CorruptIndexException;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.queryParser.ParseException;
//import org.apache.lucene.queryParser.QueryParser;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.Searcher;
//import org.apache.lucene.search.TopDocCollector;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//public class LuceneIndex {
//
//	public static void insert(String url, String key, String path) {
//		Document document = new Document();
//		InputStream in = null;
//		try {
//			in = new FileInputStream(path);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return;
//		}
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		File file = new File(url);
//
//		document.add(new Field("name", key, Field.Store.YES,
//				Field.Index.NOT_ANALYZED));
//		try {
//			org.w3c.dom.Document doc = dbf.newDocumentBuilder().parse(in);
//			NodeList nl = doc.getDocumentElement().getElementsByTagName("text");
//			for (int i = 0; i < nl.getLength(); i++) {
//				document.add(new Field("content", nl.item(i).getTextContent(),
//						Field.Store.YES, Field.Index.TOKENIZED));
//			}
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			IndexWriter writer = new IndexWriter(file, new StandardAnalyzer(),
//					!file.exists(), IndexWriter.MaxFieldLength.LIMITED);
//			writer.addDocument(document);
//			writer.optimize();
//			writer.close();
//		} catch (CorruptIndexException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * +test -key java (test and java) or not key
//	 * 
//	 * @param key
//	 * @param pageIndex
//	 * @param pageSize
//	 * @return
//	 * @throws CorruptIndexException
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public static ArrayList<String> search(String url, String key,
//			int pageIndex, int pageSize) throws CorruptIndexException,
//			IOException, ParseException {
//		ArrayList<String> re = new ArrayList<String>();
//		IndexReader reader = IndexReader.open(url);
//		Searcher searcher = new IndexSearcher(reader);
//		Analyzer analyzer = new StandardAnalyzer();
//		QueryParser parser = new QueryParser("content", analyzer);
//		Query query = parser.parse(key);
//
//		int size = Math.max(pageIndex + 1, 5);
//
//		TopDocCollector collector = new TopDocCollector(size * pageSize);
//		searcher.search(query, collector);
//		ScoreDoc[] hits = collector.topDocs().scoreDocs;
//
//		int max = Math.min((pageIndex + 1) * pageSize, hits.length);
//		if (pageIndex * pageSize >= hits.length)
//			return re;
//		for (int i = pageIndex * pageSize; i < max; i++) {
//			Document doc = searcher.doc(hits[i].doc);
//			String name = doc.get("name");
//			re.add(name);
//		}
//
//		return re;
//	}
//
//	public static ArrayList<String> search(String url, String key) {
//
//		ArrayList<String> re = new ArrayList<String>();
//		try {
//			IndexReader reader = IndexReader.open(url);
//			Searcher searcher = new IndexSearcher(reader);
//			Analyzer analyzer = new StandardAnalyzer();
//			QueryParser parser = new QueryParser("content", analyzer);
//			Query query = parser.parse(key);
//
//			TopDocCollector collector = new TopDocCollector(Integer.MAX_VALUE);
//			searcher.search(query, collector);
//			ScoreDoc[] hits = collector.topDocs().scoreDocs;
//			for (ScoreDoc d : hits) {
//				Document doc = searcher.doc(d.doc);
//				String name = doc.get("name");
//				re.add(name);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		return re;
//	}
//}
