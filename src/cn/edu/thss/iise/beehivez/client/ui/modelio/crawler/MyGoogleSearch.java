/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thss.iise.beehivez.client.ui.modelio.crawler;

import java.util.ArrayList;
import org.htmlparser.filters.*;
import org.htmlparser.*;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;

public class MyGoogleSearch {

	public static final String PNML = "pnml";

	/**
	 * ��ѯ�ļ����ͣ���"bpel"
	 */
	protected String query;

	/**
	 * ��ǰ��ʾҳ
	 */
	protected int pageIndex = 0;

	/**
	 * ÿҳ��ʾ�Ľ������
	 */
	protected int amountEachPage = 50;

	/**
	 * �Ƿ�����ҳ����ʾ����
	 */
	protected boolean isEnd = false;

	/**
	 * 
	 * @param fileType
	 *            �ļ�����
	 * @param pageSize
	 *            ÿҳ�������
	 */
	public MyGoogleSearch(String fileType, int pageSize) {
		query = fileType;
		amountEachPage = pageSize;
	}

	public MyGoogleSearch(String fileType) {
		this(fileType, 50);
	}

	public ArrayList<String> lastPage() {
		if (pageIndex - 2 * amountEachPage < 0)
			return null;
		pageIndex -= 2 * amountEachPage;
		return nextPage();
	}

	/**
	 * ��ȡ��һҳ
	 * 
	 * @return ���ҳ�е��ļ����ӣ���Ϊnull��û����һҳ��
	 */
	public ArrayList<String> nextPage() {
		if (isEnd)
			return null;
		ArrayList<String> page = new ArrayList<String>();
		String httpQuery = "http://www.google.cn/search?q=filetype:" + query
				+ "&hl=zh-CN&lr=&rlz=1G1GGLQ_ZH-CNCN339&num=" + amountEachPage
				+ "&newwindow=1&start=" + pageIndex + "&sa=N&filter=0";
		HttpFile hf = new HttpFile(httpQuery);
		// System.out.println(httpQuery);
		try {
			String httpContent = hf.getContent();
			Parser parser;
			NodeList nodelist;
			parser = Parser.createParser(httpContent, "UTF-8");
			NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
			nodelist = parser.parse(linkFilter);
			Node[] nodes = nodelist.toNodeArray();
			String line = "";
			String fileFilter = "." + query;
			boolean isHavePageLeft = false;
			for (int i = 0; i < nodes.length; i++) {
				LinkTag link = (LinkTag) nodes[i];
				line = link.getLink().trim();
				if (link.getLinkText().startsWith("��һҳ")) {
					isHavePageLeft = true;
				}
				if (checkLink(line)) {
					// System.out.println(line);
					page.add(line);
				}
				// if(line.startsWith("http://www.cs.le.ac.uk/"))
				// System.out.println(">>>>"+line);
			}
			isEnd = !isHavePageLeft;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		pageIndex += amountEachPage;

		return page;
	}

	private boolean checkLink(String line) {
		if (!line.startsWith("http") || line.startsWith("http://203.208.")
				|| line.startsWith("http://www.google.")
				|| line.startsWith("https://www.google.")
				|| line.startsWith("http://images.google.")
				|| line.startsWith("http://video.google.")
				|| line.startsWith("http://ditu.google.")
				|| line.startsWith("http://news.google.")
				|| line.startsWith("http://translate.google.")
				|| line.startsWith("http://wenda.tianya.cn/")
				|| line.startsWith("http://laiba.tianya.cn/"))
			return false;
		return true;
	}
}
