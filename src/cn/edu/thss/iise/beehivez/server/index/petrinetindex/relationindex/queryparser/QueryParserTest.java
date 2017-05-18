/**
 * 
 */
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex.queryparser;

import java.io.StringReader;

import org.apache.lucene.search.Query;

/**
 * @author Tao Jin
 * 
 */
public class QueryParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String query = "(((((\"3\">>\"4\"))) && !exist\"5\"));";
			StringReader sr = new StringReader(query);
			QueryParser qp = new QueryParser(sr);
			Query q = qp.parse();
			System.out.println(q.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
