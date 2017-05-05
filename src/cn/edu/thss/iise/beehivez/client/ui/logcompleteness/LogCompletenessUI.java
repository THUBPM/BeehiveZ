/**
 * 
 */
package cn.edu.thss.iise.beehivez.client.ui.logcompleteness;

import howmuch.LogCompleteness;
import howmuch.ParseEstimationResult;

import java.awt.LayoutManager;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.apache.commons.lang.RandomStringUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Button;
import java.awt.TextField;
import java.io.File;
import java.awt.TextArea;

/**
 * @author hedong
 *
 */
public class LogCompletenessUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField text_logs,text_config;
	private TextArea textArea;
	private JFileChooser fc=null;

	/**
	 * 
	 */
	public LogCompletenessUI() {
		fc = new JFileChooser();
		JButton btnStartEvaluation = new JButton("Start Evaluation");
		btnStartEvaluation.setBounds(30, 37, 148, 25);
		btnStartEvaluation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setEnabled(true);
				textArea.setVisible(true);
				textArea.setText("Starting...");
				String ext = "res";
				File dir = new File(".");
				String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), ext);
				File file = new File(dir, name);
				String[] args4est={
						text_logs.getText(), "-o",file.getAbsolutePath(),"-c",text_config.getText(),"-u","1000","-t","100" };
				textArea.setText("Estimating the log completeness of the specified logs now. Please wait.");
				LogCompleteness.evaluateWithArgs(args4est);
				String[] args4prs={file.getAbsolutePath(),"-c",text_config.getText(),"-u","1000","-t","100" };
				textArea.setText("Parsing the estimated values of log completeness of the specified logs now. Please wait.");
				ParseEstimationResult.parseWithArgs(args4prs);
				textArea.setText("The output result files are\n Coverage Estimation: Coverage"+file.getName()+".csv"+"\n"
				+"Classes Number Estimation: Classes"+file.getName()+".csv"+"\n"
				+"Log Length Estimation: LogLength"+file.getName()+".csv"+"\n");
			}
		});
		setLayout(null);
		add(btnStartEvaluation);
		
		JLabel lblLogFiles = new JLabel("Log Files:");
		lblLogFiles.setBounds(34, 10, 68, 15);
		add(lblLogFiles);
		
		text_logs = new JTextField();
		text_logs.setBounds(120, 8, 301, 19);
		add(text_logs);
		text_logs.setColumns(10);
		
		text_config = new JTextField();
		text_config.setEditable(false);
		text_config.setBounds(184, 74, 237, 19);
		text_config.setText("");
		add(text_config);
		
		Button btnConfig = new Button("Configuration File");
		btnConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			        int returnVal = fc.showOpenDialog(LogCompletenessUI.this);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            //This is where a real application would open the file.
			            text_config.setText( file.getAbsolutePath());
			        } else {
			        }
			}
		});
		btnConfig.setBounds(30, 70, 148, 23);
		add(btnConfig);
		
		textArea = new TextArea();
		textArea.setEnabled(false);
		textArea.setEditable(false);
		textArea.setVisible(false);
		textArea.setBounds(31, 129, 419, 161);
		add(textArea);
		
		
	}

	/**
	 * @param layout
	 */
	public LogCompletenessUI(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isDoubleBuffered
	 */
	public LogCompletenessUI(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public LogCompletenessUI(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}
}
