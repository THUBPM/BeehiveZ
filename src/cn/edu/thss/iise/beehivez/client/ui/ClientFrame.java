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

package cn.edu.thss.iise.beehivez.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import cn.edu.thss.iise.beehivez.client.ui.customizableloggenerator.CustomizableLogGeneratorPlugin;
import cn.edu.thss.iise.beehivez.client.ui.featureextraction.FeatureExtractionPlugin;
import cn.edu.thss.iise.beehivez.client.ui.metric.DlgFrequentGraph;
import cn.edu.thss.iise.beehivez.client.ui.metric.DlgLabelStatistics;
import cn.edu.thss.iise.beehivez.client.ui.metric.DlgModelMetrics;
import cn.edu.thss.iise.beehivez.client.ui.miningevaluate.MiningEvaluatePlugin;
import cn.edu.thss.iise.beehivez.client.ui.modelio.DlgModelBatchGenerator2DB;
import cn.edu.thss.iise.beehivez.client.ui.modelio.DlgModelBatchImport;
import cn.edu.thss.iise.beehivez.client.ui.modelio.DlgModelGenerating2File;
import cn.edu.thss.iise.beehivez.client.ui.modelio.DlgQueryModelGenerator;
import cn.edu.thss.iise.beehivez.client.ui.modelio.ModelIOFramePlugin;
import cn.edu.thss.iise.beehivez.client.ui.modelio.crawler.DlgCrawler;
import cn.edu.thss.iise.beehivez.client.ui.modelquery.ModelQueryPlugin;
import cn.edu.thss.iise.beehivez.client.ui.modelrefactoring.ModelRefactoringPlugin;
import cn.edu.thss.iise.beehivez.client.ui.similaritymetric.SimilarityMetricPlugin;
import cn.edu.thss.iise.beehivez.server.datamanagement.DataManager;
import cn.edu.thss.iise.beehivez.server.parameter.DlgSystemConfig;
import cn.edu.thss.iise.beehivez.server.parameter.GlobalParameter;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;

/**
 * Main class of the ProcessManger tool. All function plugins are loaded and
 * shown here.
 * 
 * @version 1.00 09/02/25
 * @author Haiping Zha
 * 
 *         edited by JinTao 2009.9.6
 */

public class ClientFrame extends JPanel {

	private static final long serialVersionUID = 1L;

	// look and feeling
	private static final String mac = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
	private static final String metal = "javax.swing.plaf.metal.MetalLookAndFeel";
	private static final String motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private static final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static final String gtk = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

	// UI style
	private String currentLookAndFeel = metal;

	// list of functionplugins
	private ArrayList<FunctionFramePlugin> moduleList = new ArrayList<FunctionFramePlugin>();

	// properties file of frame plugin
	private static final String framePluginConfigureFile = "framePlugin.ini";

	// size of frame
	private final int PREFERRED_WIDTH = 800;
	private final int PREFERRED_HEIGHT = 600;

	// resource access
	ResourcesManager resourcesManager;

	private JPanel functionPanel = null;

	// about dialog
	private JDialog aboutBox = null;

	// text field in status bar
	private JTextField statusField = null;

	// tool bar
	private ToggleButtonToolBar toolbar = null;
	private ButtonGroup toolbarGroup = new ButtonGroup();

	// System menu
	private JMenuBar menuBar = null;
	private JMenu lafMenu = null;

	private JMenu optionsMenu = null;
	private ButtonGroup lafMenuGroup = new ButtonGroup();

	// pop menu
	private JPopupMenu popupMenu = null;
	private ButtonGroup popupMenuGroup = new ButtonGroup();

	// main frame of the program
	private JFrame frame = null;

	// tabbedpane contains
	private JTabbedPane tabbedPane = null;

	// contentPane cache
	Container contentPane = null;

	private static ClientFrame instance = new ClientFrame();

	// implement singleton pattern
	public static ClientFrame getInstance() {
		return instance;
	}

	private int selectedModuleIndex;

	/**
	 * entrance of program
	 */
	public void start() {
		resourcesManager = new ResourcesManager();

		//
		frame = createFrame(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration());

		// set layout
		setLayout(new BorderLayout());

		// set default size
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		// initialize
		initialize();

		// Shown the UI. Must do this on the GUI thread using invokeLater.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showApp();
			}
		});

		// load the function plugins
		this.loadModules();

		DataManager dm = DataManager.getInstance();
		long nModels = dm.getNumberOfModels();
		GlobalParameter.setNModels(nModels);
		setStatus(resourcesManager.getString("Status.numberOfModels") + nModels);

	}

	private void initialize() {
		menuBar = createMenus();

		frame.setJMenuBar(menuBar);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				DataManager dm = DataManager.getInstance();
				dm.close();
				System.out.println("Welcome to use BeehiveZ again");
				System.exit(0);
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		popupMenu = createPopupMenu();

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		add(top, BorderLayout.NORTH);

		ToolBarPanel toolbarPanel = new ToolBarPanel();
		toolbarPanel.setLayout(new BorderLayout());
		toolbar = new ToggleButtonToolBar();
		toolbarPanel.add(toolbar, BorderLayout.CENTER);
		top.add(toolbarPanel, BorderLayout.SOUTH);
		toolbarPanel.addContainerListener(toolbarPanel);

		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

		statusField = new JTextField("");
		statusField.setEditable(false);
		add(statusField, BorderLayout.SOUTH);

		functionPanel = new JPanel();
		functionPanel.setLayout(new BorderLayout());
		functionPanel.setBorder(new EtchedBorder());
		tabbedPane.addTab(resourcesManager.getString("Status.loading"),
				functionPanel);
	}

	private JMenuBar createMenus() {
		JMenuItem mi;
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName(
				resourcesManager.getString("MenuBar.accessible_description"));

		// file menu
		JMenu fileMenu = (JMenu) menuBar.add(new JMenu(resourcesManager
				.getString("FileMenu.file_label")));
		fileMenu.setMnemonic(resourcesManager
				.getMnemonic("FileMenu.file_mnemonic"));
		fileMenu.getAccessibleContext().setAccessibleDescription(
				resourcesManager.getString("FileMenu.accessible_description"));

		createMenuItem(fileMenu, "FileMenu.about_label",
				"FileMenu.about_mnemonic",
				"FileMenu.about_accessible_description", new AboutAction(this));

		fileMenu.addSeparator();

		createMenuItem(fileMenu, "FileMenu.exit_label",
				"FileMenu.exit_mnemonic",
				"FileMenu.exit_accessible_description", new ExitAction(this));

		// new menu for model management
		JMenu menuModelManagement = (JMenu) menuBar.add(new JMenu(
				resourcesManager.getString("ModelManagementMenu")));
		JMenu menuModelIO = (JMenu) menuModelManagement.add(new JMenu(
				resourcesManager.getString("ProcessIOFramePlugin.name")));
		JMenuItem menuModelImport = menuModelIO.add(new JMenuItem(
				resourcesManager.getString("FileMenu.import_label")));
		menuModelImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);
						DlgModelBatchImport dlg = new DlgModelBatchImport(null);
						dlg.setVisible(true);
						ClientFrame.getInstance().refreshStatus();
						((ModelIOFramePlugin) module).freshTree();
						break;
					}
				}
			}

		});

		JMenuItem menuItemExportAllPNML = menuModelIO
				.add(new JMenuItem(
						resourcesManager
								.getString("ProcessIOFramePlugin.rightmenu.exportAllPNMLModels")));
		menuItemExportAllPNML.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);

						JValidateFileChooser fileChooser = new JValidateFileChooser(
								GlobalParameter.getQueryObjectPath());
						fileChooser
								.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
						fileChooser.rescanCurrentDirectory();
						int choose = fileChooser.showSaveDialog(null);
						String savePath = null;
						DataManager dm = DataManager.getInstance();
						if (choose == JFileChooser.APPROVE_OPTION) {
							savePath = fileChooser.getSelectedFile()
									.getAbsolutePath();
							dm.exportAllPNMLModels(savePath);
							JOptionPane.showMessageDialog(null,
									"export to file successfully!");
						}

						break;
					}
				}
			}

		});

		JMenuItem menuItemExportAllYAWL = menuModelIO
				.add(new JMenuItem(
						resourcesManager
								.getString("ProcessIOFramePlugin.rightmenu.exportAllYAWLModels")));
		menuItemExportAllYAWL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);

						JValidateFileChooser fileChooser = new JValidateFileChooser(
								GlobalParameter.getQueryObjectPath());
						fileChooser
								.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
						fileChooser.rescanCurrentDirectory();
						int choose = fileChooser.showSaveDialog(null);
						String savePath = null;
						DataManager dm = DataManager.getInstance();
						if (choose == JFileChooser.APPROVE_OPTION) {
							savePath = fileChooser.getSelectedFile()
									.getAbsolutePath();
							dm.exportAllYAWLModels(savePath);
							JOptionPane.showMessageDialog(null,
									"export to file successfully!");
						}

						break;
					}
				}

			}

		});

		JMenuItem menuItemDeleteAllProcess = menuModelIO
				.add(new JMenuItem(
						resourcesManager
								.getString("ProcessIOFramePlugin.rightmenu.deleteAllModels")));
		menuItemDeleteAllProcess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);

						DataManager dm = DataManager.getInstance();
						dm.delAllProcess();
						ClientFrame.getInstance().refreshStatus();
						((ModelIOFramePlugin) module).freshTree();

						break;
					}
				}
			}
		});

		JMenu menuModelGeneration = (JMenu) menuModelManagement.add(new JMenu(
				resourcesManager.getString("ModelGenerationMenu")));
		JMenuItem menuGenerateModels2DB = menuModelGeneration
				.add(new JMenuItem(
						resourcesManager
								.getString("ProcessIOFramePlugin.rightmenu.generateModels2DB")));
		menuGenerateModels2DB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);
						DlgModelBatchGenerator2DB batchGen = new DlgModelBatchGenerator2DB(
								null);
						batchGen.setVisible(true);
						ClientFrame.getInstance().refreshStatus();
						((ModelIOFramePlugin) module).freshTree();
						break;
					}
				}

			}

		});

		JMenuItem menuGenerateModels2Files = menuModelGeneration
				.add(new JMenuItem(
						resourcesManager
								.getString("ProcessIOFramePlugin.rightmenu.generateModels2Files")));
		menuGenerateModels2Files.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);

						DlgModelGenerating2File dlg = new DlgModelGenerating2File(
								null);
						dlg.setVisible(true);
						ClientFrame.getInstance().refreshStatus();
						((ModelIOFramePlugin) module).freshTree();

						break;
					}
				}

			}

		});

		JMenuItem menuGenerateQueryModels = menuModelGeneration
				.add(new JMenuItem(
						resourcesManager
								.getString("ProcessIOFramePlugin.rightmenu.generateQueryModels")));
		menuGenerateQueryModels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);

						DlgQueryModelGenerator dlg = new DlgQueryModelGenerator(
								null);
						dlg.setVisible(true);
						ClientFrame.getInstance().refreshStatus();
						((ModelIOFramePlugin) module).freshTree();

						break;
					}
				}

			}

		});

		JMenuItem menuItemCrawl = menuModelManagement.add(new JMenuItem(
				resourcesManager
						.getString("ProcessIOFramePlugin.rightmenu.crawl")));
		menuItemCrawl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelIOFramePlugin) {
						setModule(module);

						DlgCrawler crawler = new DlgCrawler(null);
						crawler.show();
						ClientFrame.getInstance().refreshStatus();
						((ModelIOFramePlugin) module).freshTree();

						break;
					}
				}
			}

		});

		// analyze menu
		JMenu menuModelAnalyze = (JMenu) menuBar.add(new JMenu(resourcesManager
				.getString("ModelAnalyzeMenu")));
		JMenuItem menuItemPetriNetMetrics = menuModelAnalyze
				.add(new JMenuItem(resourcesManager
						.getString("CommandMenu.command_modelMetrics")));
		menuItemPetriNetMetrics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof FeatureExtractionPlugin) {
						setModule(module);

						DlgModelMetrics dlg = new DlgModelMetrics(null);
						dlg.setVisible(true);

						break;
					}
				}
			}

		});

		JMenuItem menuItemLabelStatistics = menuModelAnalyze.add(new JMenuItem(
				resourcesManager
						.getString("CommandMenu.command_labelStatistics")));
		menuItemLabelStatistics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof FeatureExtractionPlugin) {
						setModule(module);

						DlgLabelStatistics dlg = new DlgLabelStatistics(null);
						dlg.setVisible(true);

						break;
					}
				}

			}

		});

		JMenuItem menuItemFrequentGraph = menuModelAnalyze.add(new JMenuItem(
				resourcesManager.getString("CommandMenu.command_frequent")));
		menuItemFrequentGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof FeatureExtractionPlugin) {
						setModule(module);

						DlgFrequentGraph dlg = new DlgFrequentGraph(null);
						dlg.setVisible(true);

						break;
					}
				}

			}

		});

		// model similarity measurement
		JMenu menuModelSimilarity = (JMenu) menuBar.add(new JMenu(
				resourcesManager.getString("ModelSimilarity")));
		JMenuItem menuItemSimilarityMeasure = menuModelSimilarity
				.add(new JMenuItem(resourcesManager
						.getString("SimilarityMeasure")));
		menuItemSimilarityMeasure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof SimilarityMetricPlugin) {
						setModule(module);

						break;
					}
				}
			}

		});

		// model query
		JMenu menuModelQuery = (JMenu) menuBar.add(new JMenu(resourcesManager
				.getString("ModelQuery")));
		JMenuItem menuItemQueryByString = menuModelQuery.add(new JMenuItem(
				resourcesManager.getString("ModelQuery.Plugin.qbk.tabTitle")));
		menuItemQueryByString.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelQueryPlugin) {
						setModule(module);
						((ModelQueryPlugin) module).tabpane.setSelectedIndex(0);
						break;
					}
				}
			}

		});
		JMenuItem menuItemQueryByExample = menuModelQuery.add(new JMenuItem(
				resourcesManager.getString("ModelQuery.Plugin.qbe.tabTitle")));
		menuItemQueryByExample.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelQueryPlugin) {
						setModule(module);
						((ModelQueryPlugin) module).tabpane.setSelectedIndex(1);
						break;
					}
				}
			}

		});
		JMenuItem menuItemQueryByTL = menuModelQuery.add(new JMenuItem(
				resourcesManager.getString("ModelQuery.Plugin.qbtl.tabTitle")));
		menuItemQueryByTL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelQueryPlugin) {
						setModule(module);
						((ModelQueryPlugin) module).tabpane.setSelectedIndex(3);
						break;
					}
				}
			}

		});

		// refactor menu
		JMenu menuModelRefactoring = (JMenu) menuBar.add(new JMenu(
				resourcesManager.getString("ModelRefactoring")));
		JMenuItem menuItemParallismRefactoring = menuModelRefactoring
				.add(new JMenuItem(resourcesManager
						.getString("ModelRefactoring.plugin")));
		menuItemParallismRefactoring.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof ModelRefactoringPlugin) {
						setModule(module);
						break;
					}
				}
			}

		});

		// mining menu
		JMenu menuMining = (JMenu) menuBar.add(new JMenu(resourcesManager
				.getString("MiningMenu")));
		JMenuItem menuItemLogGenerating = menuMining.add(new JMenuItem(
				resourcesManager.getString("CustomizableLog.plugin")));
		menuItemLogGenerating.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof CustomizableLogGeneratorPlugin) {
						setModule(module);
						break;
					}
				}
			}

		});
		JMenuItem menuItemMiningEvaluate = menuMining.add(new JMenuItem(
				resourcesManager.getString("MiningEvaluate.plugin")));
		menuItemMiningEvaluate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (FunctionFramePlugin module : moduleList) {
					if (module instanceof MiningEvaluatePlugin) {
						setModule(module);
						break;
					}
				}
			}

		});

		// option menu
		lafMenu = new JMenu(resourcesManager.getString("LafMenu.laf_label"));
		lafMenu.setMnemonic(resourcesManager
				.getMnemonic("LafMenu.laf_mnemonic"));
		lafMenu.getAccessibleContext().setAccessibleDescription(
				resourcesManager
						.getString("LafMenu.laf_accessible_description"));

		mi = createLafMenuItem(lafMenu, "LafMenu.java_label",
				"LafMenu.java_mnemonic", "LafMenu.java_accessible_description",
				metal);
		mi.setSelected(true);

		UIManager.LookAndFeelInfo[] lafInfo = UIManager
				.getInstalledLookAndFeels();

		for (int counter = 0; counter < lafInfo.length; counter++) {
			String className = lafInfo[counter].getClassName();
			if (className == motif) {
				createLafMenuItem(lafMenu, "LafMenu.motif_label",
						"LafMenu.motif_mnemonic",
						"LafMenu.motif_accessible_description", motif);
			} else if (className == windows) {
				createLafMenuItem(lafMenu, "LafMenu.windows_label",
						"LafMenu.windows_mnemonic",
						"LafMenu.windows_accessible_description", windows);
			} else if (className == gtk) {
				createLafMenuItem(lafMenu, "LafMenu.gtk_label",
						"LafMenu.gtk_mnemonic",
						"LafMenu.gtk_accessible_description", gtk);
			}
		}

		optionsMenu = (JMenu) menuBar.add(new JMenu(resourcesManager
				.getString("OptionsMenu.options_label")));
		optionsMenu.setMnemonic(resourcesManager
				.getMnemonic("OptionsMenu.options_mnemonic"));
		optionsMenu
				.getAccessibleContext()
				.setAccessibleDescription(
						resourcesManager
								.getString("OptionsMenu.options_accessible_description"));

		optionsMenu.add(lafMenu);

		JMenu menuLanguage = new JMenu(resourcesManager
				.getString("LanguageMenu"));
		optionsMenu.add(menuLanguage);
		JMenuItem menuChinese = menuLanguage.add(new JMenuItem("中文"));
		menuChinese.addActionListener(new LanguageAction(this, false));
		JMenuItem menuEnglish = menuLanguage.add(new JMenuItem("English"));
		menuEnglish.addActionListener(new LanguageAction(this, true));
		
		JMenuItem menuItemSystemConfig = optionsMenu.add(new JMenuItem(
				resourcesManager.getString("CommandMenu.command_configure")));
		menuItemSystemConfig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DlgSystemConfig dlg = new DlgSystemConfig(null);
				dlg.setVisible(true);
			}

		});

		optionsMenu.addSeparator();

		mi = createCheckBoxMenuItem(optionsMenu, "OptionsMenu.tooltip_label",
				"OptionsMenu.tooltip_mnemonic",
				"OptionsMenu.tooltip_accessible_description",
				new ToolTipAction());
		mi.setSelected(true);

		// // new menu for command
		// JMenu menuCommand = (JMenu) menuBar.add(new JMenu(resourcesManager
		// .getString("CommandMenu.command")));


		return menuBar;
	}

	private JMenuItem createCheckBoxMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, Action action) {
		JCheckBoxMenuItem mi = (JCheckBoxMenuItem) menu
				.add(new JCheckBoxMenuItem(resourcesManager.getString(label)));
		mi.setMnemonic(resourcesManager.getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				resourcesManager.getString(accessibleDescription));
		mi.addActionListener(action);
		return mi;
	}

	private JMenuItem createMenuItem(JMenu menu, String label, String mnemonic,
			String accessibleDescription, Action action) {
		JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(resourcesManager
				.getString(label)));
		mi.setMnemonic(resourcesManager.getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				resourcesManager.getString(accessibleDescription));
		mi.addActionListener(action);
		if (action == null) {
			mi.setEnabled(false);
		}
		return mi;
	}

	/**
	 * for the Look and Feel menu
	 */
	private JMenuItem createLafMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, String laf) {
		JMenuItem mi = (JRadioButtonMenuItem) menu
				.add(new JRadioButtonMenuItem(resourcesManager.getString(label)));
		lafMenuGroup.add(mi);
		mi.setMnemonic(resourcesManager.getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				resourcesManager.getString(accessibleDescription));
		mi.addActionListener(new ChangeLookAndFeelAction(this, laf));

		mi.setEnabled(isAvailableLookAndFeel(laf));

		return mi;
	}

	/**
	 * 
	 * @return
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu("JPopupMenu demo");

		createPopupMenuItem(popup, "LafMenu.java_label",
				"LafMenu.java_mnemonic", "LafMenu.java_accessible_description",
				metal);

		createPopupMenuItem(popup, "LafMenu.mac_label", "LafMenu.mac_mnemonic",
				"LafMenu.mac_accessible_description", mac);

		createPopupMenuItem(popup, "LafMenu.motif_label",
				"LafMenu.motif_mnemonic",
				"LafMenu.motif_accessible_description", motif);

		createPopupMenuItem(popup, "LafMenu.windows_label",
				"LafMenu.windows_mnemonic",
				"LafMenu.windows_accessible_description", windows);

		createPopupMenuItem(popup, "LafMenu.gtk_label", "LafMenu.gtk_mnemonic",
				"LafMenu.gtk_accessible_description", gtk);

		InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_MASK),
				"postMenuAction");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0),
				"postMenuAction");
		getActionMap().put("postMenuAction",
				new ActivatePopupMenuAction(this, popup));

		return popup;
	}

	private JMenuItem createPopupMenuItem(JPopupMenu menu, String label,
			String mnemonic, String accessibleDescription, String laf) {
		JMenuItem mi = menu
				.add(new JMenuItem(resourcesManager.getString(label)));
		popupMenuGroup.add(mi);
		mi.setMnemonic(resourcesManager.getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				resourcesManager.getString(accessibleDescription));
		mi.addActionListener(new ChangeLookAndFeelAction(this, laf));
		mi.setEnabled(isAvailableLookAndFeel(laf));

		return mi;
	}

	private FunctionFramePlugin addModule(FunctionFramePlugin module) {
		//		
		moduleList.add(module);

		SwingUtilities.invokeLater(new MainFrameRunnable(this, module) {
			public void run() {
				SwitchToModuleAction action = new SwitchToModuleAction(frame,
						(FunctionFramePlugin) obj);
				JToggleButton tb = frame.getToolBar().addToggleButton(action);
				frame.getToolBarGroup().add(tb);
				if (frame.getToolBarGroup().getSelection() == null) {
					tb.setSelected(true);
				}
				tb.setText(null);
				tb.setToolTipText(((FunctionFramePlugin) obj).getToolTip());
			}
		});
		return module;
	}

	private void setModule(FunctionFramePlugin module) {

		JComponent currentDemoPanel = module.getModulePanel();
		SwingUtilities.updateComponentTreeUI(currentDemoPanel);

		functionPanel.removeAll();
		functionPanel.add(currentDemoPanel, BorderLayout.CENTER);

		tabbedPane.setSelectedIndex(0);
		tabbedPane.setTitleAt(0, module.getName());
		tabbedPane.setToolTipTextAt(0, module.getToolTip());
		module.onLoad();
	}

	private void showApp() {
		if (getFrame() != null) {
			JFrame f = getFrame();
			f.setTitle(resourcesManager.getString("Frame.title"));
			f.getContentPane().add(this, BorderLayout.CENTER);
			f.pack();

			Rectangle screenRect = f.getGraphicsConfiguration().getBounds();
			Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
					f.getGraphicsConfiguration());

			int centerWidth = screenRect.width < f.getSize().width ? screenRect.x
					: screenRect.x + screenRect.width / 2 - f.getSize().width
							/ 2;
			int centerHeight = screenRect.height < f.getSize().height ? screenRect.y
					: screenRect.y + screenRect.height / 2 - f.getSize().height
							/ 2;

			centerHeight = centerHeight < screenInsets.top ? screenInsets.top
					: centerHeight;

			f.setLocation(centerWidth, centerHeight);
			f.setVisible(true);

		}
	}

	// *******************************************************
	// ****************** Utility Methods ********************
	// *******************************************************

	/**
	 * @param javaClassName
	 *            the java class name, including the path of package
	 */
	private FunctionFramePlugin loadModule(String javaClassName) {
		setStatus(resourcesManager.getString("Status.loading"));
		FunctionFramePlugin module = null;
		try {
			Class moduleClass = Class.forName(javaClassName);
			Constructor moduleConstructor = moduleClass
					.getConstructor(new Class[] { ClientFrame.class });
			module = (FunctionFramePlugin) moduleConstructor
					.newInstance(new Object[] { this });
			addModule(module);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return module;
	}

	private boolean loadModules() {
		try {
			FileInputStream fin = new FileInputStream(framePluginConfigureFile);
			String className;
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("=") == -1)
					continue;
				className = line.substring(line.indexOf("=") + 1);
				FunctionFramePlugin module = loadModule(className);
				if (count == 0) {
					this.setModule(module);
				}
				count++;
			}
			br.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * A utility function that layers on top of the LookAndFeel's
	 * isSupportedLookAndFeel() method. Returns true if the LookAndFeel is
	 * supported. Returns false if the LookAndFeel is not supported and/or if
	 * there is any kind of error checking if the LookAndFeel is supported.
	 * 
	 * The L&F menu will use this method to detemine whether the various L&F
	 * options should be active or inactive.
	 * 
	 */
	protected boolean isAvailableLookAndFeel(String laf) {
		try {
			Class<?> lnfClass = Class.forName(laf);
			LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
			return newLAF.isSupportedLookAndFeel();
		} catch (Exception e) { // If ANYTHING weird happens, return false
			return false;
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Returns the menubar
	 */
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * Returns the toolbar
	 */
	public ToggleButtonToolBar getToolBar() {
		return toolbar;
	}

	/**
	 * Returns the toolbar button group
	 */
	public ButtonGroup getToolBarGroup() {
		return toolbarGroup;
	}

	/**
	 * Returns the content pane wether we're in an applet or application
	 */
	public Container getContentPane() {
		if (contentPane == null) {
			if (getFrame() != null) {
				contentPane = getFrame().getContentPane();
			}
		}
		return contentPane;
	}

	/**
	 * Create a frame for MainFrame to reside in if brought up as an
	 * application.
	 */
	public static JFrame createFrame(GraphicsConfiguration gc) {
		JFrame frame = new JFrame(gc);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}

	/**
	 * Set the status
	 */
	public void setStatus(String s) {
		// do the following on the gui thread
		SwingUtilities.invokeLater(new MainFrameRunnable(this, s) {
			public void run() {
				frame.statusField.setText((String) obj);
			}
		});
	}

	public void refreshStatus() {
		long nModels = GlobalParameter.getNModels();
		setStatus(resourcesManager.getString("Status.numberOfModels") + nModels);
	}

	/**
	 * Creates an icon from an image contained in the "images" directory.
	 */
	private ImageIcon createImageIcon(String filename, String description) {
		String path = "/resources/images/" + filename;
		return new ImageIcon(getClass().getResource(path));
	}

	/**
	 * Stores the current L&F, and calls updateLookAndFeel, below
	 */
	public void setLookAndFeel(String laf) {
		if (currentLookAndFeel != laf) {
			currentLookAndFeel = laf;
			/*
			 * The recommended way of synchronizing state between multiple
			 * controls that represent the same command is to use Actions. The
			 * code below is a workaround and will be replaced in future version
			 * of MainFrame module.
			 */
			String lafName = null;
			if (laf == mac)
				lafName = resourcesManager.getString("LafMenu.mac_label");
			if (laf == metal)
				lafName = resourcesManager.getString("LafMenu.java_label");
			if (laf == motif)
				lafName = resourcesManager.getString("LafMenu.motif_label");
			if (laf == windows)
				lafName = resourcesManager.getString("LafMenu.windows_label");
			if (laf == gtk)
				lafName = resourcesManager.getString("LafMenu.gtk_label");

			updateLookAndFeel();
			for (int i = 0; i < lafMenu.getItemCount(); i++) {
				JMenuItem item = lafMenu.getItem(i);
				if (item.getText() == lafName) {
					item.setSelected(true);
				} else {
					item.setSelected(false);
				}
			}
		}
	}

	private void updateLookAndFeel() {

		try {
			UIManager.setLookAndFeel(currentLookAndFeel);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		JFrame frame = getFrame();
		if (frame == null) {
			SwingUtilities.updateComponentTreeUI(this);
		} else {
			SwingUtilities.updateComponentTreeUI(frame);
		}

		SwingUtilities.updateComponentTreeUI(popupMenu);
		if (aboutBox != null) {
			SwingUtilities.updateComponentTreeUI(aboutBox);

		}
	}

	// *******************************************************
	// ************** ToggleButtonToolbar *****************
	// *******************************************************
	static Insets zeroInsets = new Insets(1, 1, 1, 1);

	protected class ToggleButtonToolBar extends JToolBar {
		/**
		 * 
		 */

		private static final long serialVersionUID = 1L;
		private ArrayList<JToggleButton> tbList = new ArrayList<JToggleButton>();

		public ToggleButtonToolBar() {
			super();
		}

		JToggleButton addToggleButton(Action a) {
			JToggleButton tb = new JToggleButton((String) a
					.getValue(Action.NAME), (Icon) a
					.getValue(Action.SMALL_ICON));
			tbList.add(tb);
			tb.setMargin(zeroInsets);
			tb.setText(null);
			tb.setEnabled(a.isEnabled());
			tb.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
			tb.setAction(a);
			add(tb);
			return tb;
		}

		public void clear() {
			for (JToggleButton jtb : tbList) {
				this.remove(jtb);
			}
			tbList.clear();
		}
	}

	// *******************************************************
	// ********* ToolBar Panel / Docking Listener ***********
	// *******************************************************
	class ToolBarPanel extends JPanel implements ContainerListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean contains(int x, int y) {
			Component c = getParent();
			if (c != null) {
				Rectangle r = c.getBounds();
				return (x >= 0) && (x < r.width) && (y >= 0) && (y < r.height);
			} else {
				return super.contains(x, y);
			}
		}

		public void componentAdded(ContainerEvent e) {
			Container c = e.getContainer().getParent();
			if (c != null) {
				c.getParent().validate();
				c.getParent().repaint();
			}
		}

		public void componentRemoved(ContainerEvent e) {
			Container c = e.getContainer().getParent();
			if (c != null) {
				c.getParent().validate();
				c.getParent().repaint();
			}
		}
	}

	// *******************************************************
	// ****************** Runnables ***********************
	// *******************************************************

	/**
	 * Generic MainFrame runnable. This is intended to run on the AWT gui event
	 * thread so as not to muck things up by doing gui work off the gui thread.
	 * Accepts a MainFrame and an Object as arguments, which gives subtypes of
	 * this class the two "must haves" needed in most runnables for this module.
	 */
	class MainFrameRunnable implements Runnable {
		protected ClientFrame frame;
		protected Object obj;

		public MainFrameRunnable(ClientFrame frame, Object obj) {
			this.frame = frame;
			this.obj = obj;
		}

		public void run() {
		}
	}

	// *******************************************************
	// ******************** Actions ***********************
	// *******************************************************

	public class SwitchToModuleAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ClientFrame frame;
		FunctionFramePlugin module;

		public SwitchToModuleAction(ClientFrame frame,
				FunctionFramePlugin module) {
			super(module.getName(), module.getIcon());
			this.frame = frame;
			this.module = module;
		}

		public void actionPerformed(ActionEvent e) {
			frame.setModule(module);
			for (int i = 0; i < moduleList.size(); i++) {
				if (moduleList.get(i).equals(module)) {
					selectedModuleIndex = i;
					break;
				}
			}
		}
	}

	class OkAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JDialog aboutBox;

		protected OkAction(JDialog aboutBox) {
			super("OkAction");
			this.aboutBox = aboutBox;
		}

		public void actionPerformed(ActionEvent e) {
			aboutBox.setVisible(false);
		}
	}

	class ChangeLookAndFeelAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ClientFrame swingset;
		String laf;

		protected ChangeLookAndFeelAction(ClientFrame swingset, String laf) {
			super("ChangeTheme");
			this.swingset = swingset;
			this.laf = laf;
		}

		public void actionPerformed(ActionEvent e) {
			swingset.setLookAndFeel(laf);
		}
	}

	class ActivatePopupMenuAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ClientFrame swingset;
		JPopupMenu popup;

		protected ActivatePopupMenuAction(ClientFrame swingset, JPopupMenu popup) {
			super("ActivatePopupMenu");
			this.swingset = swingset;
			this.popup = popup;
		}

		public void actionPerformed(ActionEvent e) {
			Dimension invokerSize = getSize();
			Dimension popupSize = popup.getPreferredSize();
			popup.show(swingset, (invokerSize.width - popupSize.width) / 2,
					(invokerSize.height - popupSize.height) / 2);
		}
	}

	// Turns on or off the tool tips for the module.
	class ToolTipAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected ToolTipAction() {
			super("ToolTip Control");
		}

		public void actionPerformed(ActionEvent e) {
			boolean status = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			ToolTipManager.sharedInstance().setEnabled(status);
		}
	}

	class ExitAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ClientFrame mainframe;

		protected ExitAction(ClientFrame mainframe) {
			super("ExitAction");
			this.mainframe = mainframe;
		}

		public void actionPerformed(ActionEvent e) {
			DataManager dm = DataManager.getInstance();
			dm.close();
			System.out.println("Welcome to use BeehiveZ again");
			System.exit(0);
		}
	}

	class AboutAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ClientFrame mainframe;

		protected AboutAction(ClientFrame mainframe) {
			super("AboutAction");
			this.mainframe = mainframe;
		}

		public void actionPerformed(ActionEvent e) {
			if (aboutBox == null) {
				// JPanel panel = new JPanel(new BorderLayout());
				JPanel panel = new AboutPanel(mainframe);
				panel.setLayout(new BorderLayout());

				aboutBox = new JDialog(mainframe.getFrame(), resourcesManager
						.getString("AboutBox.title"), false);
				aboutBox.setResizable(false);
				aboutBox.getContentPane().add(panel, BorderLayout.CENTER);

				// JButton button = new
				// JButton(resourcesManager.getString("AboutBox.ok_button_text"));
				JPanel buttonpanel = new JPanel();
				buttonpanel.setBorder(new javax.swing.border.EmptyBorder(0, 0,
						3, 0));
				buttonpanel.setOpaque(false);
				JButton button = (JButton) buttonpanel.add(new JButton(
						resourcesManager.getString("AboutBox.ok_button_text")));
				panel.add(buttonpanel, BorderLayout.SOUTH);

				button.addActionListener(new OkAction(aboutBox));
			}
			aboutBox.pack();

			aboutBox.setLocationRelativeTo(getFrame());

			aboutBox.setVisible(true);
		}
	}

	// *******************************************************
	// ********************** Misc *************************
	// *******************************************************

	class AboutPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ImageIcon aboutimage = null;
		ClientFrame mainframe = null;

		public AboutPanel(ClientFrame mainframe) {
			this.mainframe = mainframe;
			aboutimage = mainframe.createImageIcon("About.jpg",
					"AboutBox.accessible_description");
			setOpaque(false);
		}

		public void paint(Graphics g) {
			aboutimage.paintIcon(this, g, 0, 0);
			super.paint(g);
		}

		public Dimension getPreferredSize() {
			return new Dimension(aboutimage.getIconWidth(), aboutimage
					.getIconHeight());
		}
	}

	// ***************************************************
	// ***************英语转换Action******************
	// ***************************************************

	class LanguageAction extends AbstractAction {
		ClientFrame mainFrame;
		boolean isEnglish;

		public LanguageAction(ClientFrame _mainFrame, boolean _isEnglish) {
			mainFrame = _mainFrame;
			isEnglish = _isEnglish;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (isEnglish) {
				System.out.println("英文");
				mainFrame.resourcesManager.setLocale(Locale.US);
				// 1.mainFrame的菜单
				mainFrame.menuBar = mainFrame.createMenus();
				mainFrame.frame.setJMenuBar(mainFrame.menuBar);
				// 2.mainFrame的标题
				mainFrame.frame.setTitle(resourcesManager
						.getString("Frame.title"));
				// 3.mainFrame的module
				mainFrame.moduleList.clear();
				mainFrame.toolbar.clear();
				mainFrame.loadModules();
				mainFrame.setModule(moduleList
						.get(mainFrame.selectedModuleIndex));
			} else {
				System.out.println("中文");
				mainFrame.resourcesManager.setLocale(Locale.CHINESE);

				mainFrame.menuBar = mainFrame.createMenus();
				mainFrame.frame.setJMenuBar(mainFrame.menuBar);
				// 1.mainFrame的菜单
				mainFrame.menuBar = mainFrame.createMenus();
				mainFrame.frame.setJMenuBar(mainFrame.menuBar);
				// 2.mainFrame的标题
				mainFrame.frame.setTitle(resourcesManager
						.getString("Frame.title"));
				// 3.mainFrame的module
				moduleList.clear();
				mainFrame.toolbar.clear();
				mainFrame.loadModules();
				mainFrame.setModule(moduleList
						.get(mainFrame.selectedModuleIndex));
			}

		}

	}

	class JValidateFileChooser extends JFileChooser {

		public JValidateFileChooser() {
			super();
		}

		public JValidateFileChooser(File currentDirectory) {
			super(currentDirectory);
		}

		public JValidateFileChooser(File currentDirectory, FileSystemView fsv) {
			super(currentDirectory, fsv);
		}

		public JValidateFileChooser(FileSystemView fsv) {
			super(fsv);
		}

		public JValidateFileChooser(String currentDirectoryPath) {
			super(currentDirectoryPath);
		}

		public JValidateFileChooser(String currentDirectoryPath,
				FileSystemView fsv) {
			super(currentDirectoryPath, fsv);
		}

		/**
		 * the override the approveSelection method,support the validate
		 */
		@Override
		public void approveSelection() {
			File file = getSelectedFile();

			if (!validateFileName(file.getName())) {
				JOptionPane.showMessageDialog(getParent(), resourcesManager
						.getString("ProcessIOFramePlugin.invalidanme"),
						"WARNING", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(getParent(), resourcesManager
						.getString("ProcessIOFramePlugin.fileexist"),
						"QUESTION", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					return;
				else {
					super.approveSelection();
				}
			}
			super.approveSelection();
		}

		/*
		 * validate the validity of the file name
		 */
		private boolean validateFileName(String name) {
			if (name.indexOf('/') != -1 || name.indexOf('/') != -1
					|| name.indexOf(':') != -1 || name.indexOf('*') != -1
					|| name.indexOf('?') != -1 || name.indexOf('"') != -1
					|| name.indexOf('<') != -1 || name.indexOf('>') != -1
					|| name.indexOf('|') != -1) {

				return false;
			} else {
				return true;
			}
		}
	}

	class ExtensionFilter extends FileFilter {
		String extension, description;

		public ExtensionFilter(String ext, String desp) {
			extension = ext;
			description = desp;
		}

		public boolean accept(File file) {
			return (file.isDirectory() || file.getName().toLowerCase()
					.endsWith(extension));
		}

		public String getDescription() {
			return description;
		}
	}
}
