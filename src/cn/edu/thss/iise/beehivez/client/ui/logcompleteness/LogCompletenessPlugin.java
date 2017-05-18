/**
 * 
 */
package cn.edu.thss.iise.beehivez.client.ui.logcompleteness;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cn.edu.thss.iise.beehivez.client.ui.ClientFrame;
import cn.edu.thss.iise.beehivez.client.ui.FunctionFramePlugin;
import cn.edu.thss.iise.beehivez.client.ui.customizableloggenerator.CustomizableLogGeneratorUI;
import cn.edu.thss.iise.beehivez.util.ResourcesManager;
/**
 * @author hedong
 *
 */
public class LogCompletenessPlugin extends FunctionFramePlugin {

	private static final long serialVersionUID = 1L;
	public LogCompletenessUI ui = null;
	ResourcesManager resourcesManager = new ResourcesManager();
	public LogCompletenessPlugin(ClientFrame mainframe) {
		super(mainframe);
		ui = new LogCompletenessUI();
		getModulePanel().add(ui, BorderLayout.CENTER);
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return resourcesManager.getString("LogCompleteness.plugin");
	}

	@Override
	public Icon getIcon() {
		String path = "/resources/images/Icon_LogCompleteness.gif";
		String description = "The evaluation of log completeness of event logs.";
		return new ImageIcon(getClass().getResource(path), description);
	}

	@Override
	public String getToolTip() {
		return resourcesManager.getString("LogCompleteness.plugin");
	}

}
