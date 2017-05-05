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

package cn.edu.thss.iise.beehivez.client.ui.similaritymetric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.processmining.framework.models.petrinet.PetriNet;

import cn.edu.thss.iise.beehivez.client.ui.modelio.DragAndDropGestureListener;
import cn.edu.thss.iise.beehivez.client.ui.modelio.DragAndDropTargetListener;
import cn.edu.thss.iise.beehivez.client.ui.modelio.VisualFrame;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.util.DataNode;
import cn.edu.thss.iise.beehivez.util.DatabaseModelTree;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

public class SimilarityMetricUI extends JPanel implements ChangeListener {
	ResourcesManager resourcesManager = new ResourcesManager();

	// ��һ������
	JSplitPane splitpane = null;
	JScrollPane leftpanel = null;
	JPanel tabrightpanel = null;

	JPanel panelModelChoose = new JPanel();

	// �ڶ�������
	JSplitPane modelShowPane = null;
	JPanel panelSimilarityMeasure = null;

	// ģ����ʾ���
	JPanel panelModel1;
	JPanel panelModel2;

	// �Ҽ�ˢ�²˵�
	JPopupMenu menu = null;
	JMenuItem openAsModel1 = null;
	JMenuItem openAsModel2 = null;

	// ���������ʾ����
	JTextArea taMeasureResult;

	// ѡ�������ģ�͵�id��
	long processid_1 = -1;
	long processid_2 = -1;

	// ��ǰ�������,Ĭ��Ϊģ��ѡ��,1Ϊģ�Ͷ���
	int currentTabPane = 0;

	// ��ǰ������ģ��,Ĭ��Ϊģ��1�� 2Ϊģ��2
	int currentModelSelect = 1;

	// ģ��Ŀ¼��
	JTree dataTree;
	DefaultTreeModel model = null;

	// // �㷨����
	Vector<String> algorithmList;
	// ��ǰѡ���㷨
	int currentAlgorithm = 0;
	// �㷨�б�ؼ�
	JComboBox cbAlgorithmSelect;

	/**
	 * ProcessExplorerFramePlugin ���캯��
	 */
	public SimilarityMetricUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		// �����㷨�б�
		loadAlgorithmList();

		// ����splitpane�����������

		// ��ʼ������壬�����ļ�ģ�Ͳ���
		try {
			leftpanel = create_leftpanel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// �������ʾѡ��ģ�Ϳ��ӻ��ṹ��Ϣ,һ��ʼrightpanel�ǿյ�
		tabrightpanel = create_rightpanel();

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftpanel,
				tabrightpanel);
		splitpane.setContinuousLayout(true);
		splitpane.setOneTouchExpandable(true);
		
		
		splitpane.setDividerLocation(0.5);
		splitpane.setDividerLocation(230);	
		this.setLayout(new BorderLayout());
		this.add(splitpane, BorderLayout.CENTER);
	}

	/*
	 * �����Զ����㷨�б����
	 */
	void loadAlgorithmList() {
		algorithmList = new Vector<String>();
		// �����Զ����㷨�����������
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.JaccardTARSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.JaccardStructureSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.LabelFreeTARSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.ContextBasedSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.CausalFootprintSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.BPSSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity_Wang");
		algorithmList
				.add("cn.edu.thss.iise.beehivez.server.metric.ExtensiveTARSimilarity");
	}

	/**
	 * ����ģ�Ϳ��ӻ�չʾģ��
	 * 
	 * @return
	 */
	protected JPanel create_rightpanel() {
		
		/*
		tabrightpanel = new JTabbedPane();
		tabrightpanel.addTab(resourcesManager.getString("ProcessSimilarityMetric.sm.choosemodels"), create_ModelChoose());
		tabrightpanel.addTab(resourcesManager.getString("ProcessSimilarityMetric.sm.sm"),
				create_paneSimilarityMeasure());
		tabrightpanel.getModel().addChangeListener(this);
		*/
		return create_ModelChoose();
	}

	// ������ǩ�л���Ӧ
	public void stateChanged(ChangeEvent e) {
		SingleSelectionModel model = (SingleSelectionModel) e.getSource();

		if (model.getSelectedIndex() == 0) {
			// ��һ����ǩҳ��ģ��ѡ��
			currentTabPane = 0;
		} else if (model.getSelectedIndex() == 1) {
			// �ڶ�����ǩҳ��ģ�Ͷ���
			currentTabPane = 1;
		}
	}

	/**
	 * ����ģ��ѡ�����
	 * 
	 * @return
	 */
	protected JPanel create_ModelChoose() {

		JPanel paneModelChoose = new JPanel();
		//paneModelChoose.setLayout(new BorderLayout());
		paneModelChoose.setLayout(null);
		// ģ��1
		panelModel1 = new JPanel();
		// ģ��2
		panelModel2 = new JPanel();
		// ģ����ʾ
		modelShowPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelModel1,
				panelModel2);

		modelShowPane.setContinuousLayout(true);
		modelShowPane.setOneTouchExpandable(true);
		modelShowPane.setDividerLocation(180);
		modelShowPane.disable();
		modelShowPane.setBounds(10, 5, 520, 370);
		// ģ��ѡ��
		paneModelChoose.add(modelShowPane);
		DropTarget dropTarget = new DropTarget(modelShowPane,
				new SimilarityDragAndDropTargetListener(this));
		// ����ģ�͵�ѡ��ť��ָʾ��ǰ˫����Ҫ�򿪵�ģ������ģ��1����ģ��2
		/*
		JPanel paneSelectRadio = new JPanel();
		paneSelectRadio.setLayout(new FlowLayout());
		ButtonGroup group = new ButtonGroup();
		// ģ��1��ť
		JRadioButton radioP1, radioP2;
		JLabel lb = new JLabel(resourcesManager.getString("ProcessSimilarityMetric.sm.tcm"));
		paneSelectRadio.add(lb);
		radioP1 = (JRadioButton) paneSelectRadio.add(new JRadioButton(
				resourcesManager.getString("ProcessSimilarityMetric.sm.tfm")));
		if (currentModelSelect == 1)
			radioP1.setSelected(true);
		// ����¼���Ӧ����
		radioP1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				currentModelSelect = 1;
			}

		});

		// ģ��2��ť
		group.add(radioP1);
		radioP2 = (JRadioButton) paneSelectRadio.add(new JRadioButton(
				resourcesManager.getString("ProcessSimilarityMetric.sm.tsm")));
		group.add(radioP2);
		radioP2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				currentModelSelect = 2;
			}

		});

		//paneModelChoose.add(paneSelectRadio, BorderLayout.SOUTH);
		paneSelectRadio.setBounds(5,310,300,20);
		//paneModelChoose.add(paneSelectRadio);
		*/
		taMeasureResult = new JTextArea();
		taMeasureResult.setBackground(Color.WHITE);
		taMeasureResult.setBounds(10,385,520,20);
		paneModelChoose.add(taMeasureResult);
		
		JPanel panelctrl = new JPanel();
		panelctrl.setLayout(new FlowLayout());
		// ѡ���㷨
		JLabel lb = new JLabel(resourcesManager.getString("ProcessSimilarityMetric.sm.cmma"));
		panelctrl.add(lb);
		cbAlgorithmSelect = new JComboBox();
		panelctrl.add(cbAlgorithmSelect);
		for (int i = 0; i < algorithmList.size(); i++) {

			// ͨ���䷽���õ��㷨����
			try {
				// �����ʵ��
				Class algorithmClass;
				algorithmClass = Class.forName(algorithmList.get(i));
				Object algorithmObj = null;
				algorithmObj = algorithmClass.newInstance(); // �����Ա���

				// �õ��෽��
				Method method = algorithmClass.getMethod("getName", null);

				// �����෽��
				Object result = method.invoke(algorithmObj, null);

				cbAlgorithmSelect.addItem((String) result);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		cbAlgorithmSelect.setEditable(false);
		cbAlgorithmSelect.setSelectedItem(currentAlgorithm);

		cbAlgorithmSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentAlgorithm = ((JComboBox) (e.getSource()))
						.getSelectedIndex();
			}
		});

		// ��ʼ��ť
		// ִ�ж���
		JButton btStart = new JButton(resourcesManager.getString("ProcessSimilarityMetric.sm.start"));
		btStart.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// ���ж��Ƿ�ѡ����ģ��
				if (processid_1 <= 0 || processid_2 <= 0) {
					taMeasureResult.setText(resourcesManager.getString("ProcessSimilarityMetric.sm.pcmf"));
					return;
				}
				// ����petrinet

				PetriNet petri1 = null;
				PetriNet petri2 = null;
				try {
					DataManager dm = DataManager.getInstance();
					petri1 = dm.getProcessPetriNet(processid_1);
					petri2 = dm.getProcessPetriNet(processid_2);

				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				// ͨ���䷽�����ö����㷨
				try {
					// �����ʵ��
					Class algorithmClass;
					String algorithmName = algorithmList.get(cbAlgorithmSelect
							.getSelectedIndex());
					algorithmClass = Class.forName(algorithmName);
					Object algorithmObj = null;
					algorithmObj = algorithmClass.newInstance(); // �����Ա���

					// �õ��෽��
					Class ptypes[] = new Class[2];
					ptypes[0] = Class
							.forName("org.processmining.framework.models.petrinet.PetriNet");
					ptypes[1] = Class
							.forName("org.processmining.framework.models.petrinet.PetriNet");

					Method method = algorithmClass.getMethod("similarity",
							ptypes);
					// ��������
					Object args[] = new Object[2];
					args[0] = petri1;
					args[1] = petri2;
					// �����෽��
					Object result = method.invoke(algorithmObj, args);
					if (algorithmName
							.equals("cn.edu.thss.iise.beehivez.server.metric.BPSSimilarity")
							|| algorithmName
									.equals("cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity")) {
						File file = new File((String) result);
						long size = file.length();
						char[] info = new char[(int) size];
						BufferedReader reader = new BufferedReader(
								new FileReader(file));
						reader.read(info);
						reader.close();
						taMeasureResult.setText(String.valueOf(info));
					} else {
						taMeasureResult.setText("similarity = "
								+ String.valueOf(result));
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});

		panelctrl.add(btStart);
		panelctrl.setBounds(10,410,520,30);
		paneModelChoose.add(panelctrl);
		//panelSimilarityMeasure.add(panelctrl, BorderLayout.SOUTH);
		
		return paneModelChoose;

	}

	// ���ģ��id����ģ�Ϳ��ӻ����
	protected JPanel create_modelVisualPane(long process_id) {
		// ���idӦ�Ǵ���0������
		if (process_id > 0) {
			DataManager dm = DataManager.getInstance();
			String modelType = dm.getProcessType(process_id);
			InputStream modelDefinition = dm.getProcessDefinitionInputStream(process_id);
			VisualFrame visualframe = new VisualFrame(modelType,
					modelDefinition);
			return visualframe.getJContentPane();
		}
		return new JPanel();
	}

	// ����ģ�Ͷ���ģ��
	protected JPanel create_paneSimilarityMeasure() {
		/*
		panelSimilarityMeasure = new JPanel();
		panelSimilarityMeasure.setLayout(new BorderLayout());
		// ���������ʾ����

		taMeasureResult = new JTextArea();
		taMeasureResult.setBackground(Color.WHITE);
		panelSimilarityMeasure.add(new JScrollPane(taMeasureResult),
				BorderLayout.CENTER);
		// �����㷨ѡ��Ϳ��Ʋ���
		JPanel panelctrl = new JPanel();
		panelctrl.setLayout(new FlowLayout());
		// ѡ���㷨
		JLabel lb = new JLabel(resourcesManager.getString("ProcessSimilarityMetric.sm.cmma"));
		panelctrl.add(lb);
		cbAlgorithmSelect = new JComboBox();
		panelctrl.add(cbAlgorithmSelect);
		for (int i = 0; i < algorithmList.size(); i++) {

			// ͨ���䷽���õ��㷨����
			try {
				// �����ʵ��
				Class algorithmClass;
				algorithmClass = Class.forName(algorithmList.get(i));
				Object algorithmObj = null;
				algorithmObj = algorithmClass.newInstance(); // �����Ա���

				// �õ��෽��
				Method method = algorithmClass.getMethod("getName", null);

				// �����෽��
				Object result = method.invoke(algorithmObj, null);

				cbAlgorithmSelect.addItem((String) result);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		cbAlgorithmSelect.setEditable(false);
		cbAlgorithmSelect.setSelectedItem(currentAlgorithm);

		cbAlgorithmSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentAlgorithm = ((JComboBox) (e.getSource()))
						.getSelectedIndex();
			}
		});

		// ��ʼ��ť
		// ִ�ж���
		JButton btStart = new JButton(resourcesManager.getString("ProcessSimilarityMetric.sm.start"));
		btStart.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// ���ж��Ƿ�ѡ����ģ��
				if (processid_1 <= 0 || processid_2 <= 0) {
					taMeasureResult.setText(resourcesManager.getString("ProcessSimilarityMetric.sm.pcmf"));
					return;
				}
				// ����petrinet

				PetriNet petri1 = null;
				PetriNet petri2 = null;
				try {
					DataManager dm = DataManager.getInstance();
					petri1 = dm.getProcessPetriNet(processid_1);
					petri2 = dm.getProcessPetriNet(processid_2);

				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				// ͨ���䷽�����ö����㷨
				try {
					// �����ʵ��
					Class algorithmClass;
					String algorithmName = algorithmList.get(cbAlgorithmSelect
							.getSelectedIndex());
					algorithmClass = Class.forName(algorithmName);
					Object algorithmObj = null;
					algorithmObj = algorithmClass.newInstance(); // �����Ա���

					// �õ��෽��
					Class ptypes[] = new Class[2];
					ptypes[0] = Class
							.forName("org.processmining.framework.models.petrinet.PetriNet");
					ptypes[1] = Class
							.forName("org.processmining.framework.models.petrinet.PetriNet");

					Method method = algorithmClass.getMethod("similarity",
							ptypes);
					// ��������
					Object args[] = new Object[2];
					args[0] = petri1;
					args[1] = petri2;
					// �����෽��
					Object result = method.invoke(algorithmObj, args);
					if (algorithmName
							.equals("cn.edu.thss.iise.beehivez.server.metric.BPSSimilarity")
							|| algorithmName
									.equals("cn.edu.thss.iise.beehivez.server.metric.BTSSimilarity")) {
						File file = new File((String) result);
						long size = file.length();
						char[] info = new char[(int) size];
						BufferedReader reader = new BufferedReader(
								new FileReader(file));
						reader.read(info);
						reader.close();
						taMeasureResult.setText(String.valueOf(info));
					} else {
						taMeasureResult.setText("similarity = "
								+ String.valueOf(result));
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});

		panelctrl.add(btStart);

		panelSimilarityMeasure.add(panelctrl, BorderLayout.SOUTH);

		return panelSimilarityMeasure;
		*/
		return new JPanel();
	}

	// ��������壬ģ�͵�����
	protected JScrollPane create_leftpanel() throws Exception {

		DatabaseModelTree dbmt = new DatabaseModelTree();
		dataTree = dbmt.createDbmTree();
		dataTree.setEditable(true);
		
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(dataTree,
				DnDConstants.ACTION_MOVE, new SimilarityDragAndDropGestureListener());
		/*
		dataTree.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// ���˫��ѡ�д�ģ��
				if (e.getClickCount() == 2) {
					// ���ǰ��ѡ��ģ��ģ�飬�򲻴���
					if (currentTabPane != 0)
						return;

					TreePath path = dataTree.getPathForLocation(e.getX(), e
							.getY());
					if (path == null)
						return;
					dataTree.setSelectionPath(path);

					long process_id = ((DataNode) path.getLastPathComponent())
							.getProcess_id();
					if (process_id < 0)
						return;

					// ��Ӧģ��1
					if (currentModelSelect == 1) {
						processid_1 = process_id;
						panelModel1 = create_modelVisualPane(processid_1);
						modelShowPane.setLeftComponent(panelModel1);
					}
					// ��Ӧģ��2
					else {
						processid_2 = process_id;
						panelModel2 = create_modelVisualPane(processid_2);
						modelShowPane.setRightComponent(panelModel2);
					}
					modelShowPane.updateUI();

				}
			}

		});
		*/
		return new JScrollPane(dataTree);
	}

	public void freshTree() {
		// TODO Auto-generated method stub
		DatabaseModelTree dbmt = new DatabaseModelTree();
		model = (DefaultTreeModel) dbmt.createDbmTree().getModel();
		dataTree.setModel(model);

	}

}
