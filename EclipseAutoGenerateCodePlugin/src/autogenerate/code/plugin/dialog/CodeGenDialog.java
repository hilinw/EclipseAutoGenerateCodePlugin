package autogenerate.code.plugin.dialog;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import autogenerate.code.plugin.generator.CodeGenException;
import autogenerate.code.plugin.generator.CodeGenerator;
import autogenerate.code.plugin.generator.ProjectConfig;
import autogenerate.code.plugin.swt.SWTResourceManager;
import autogenerate.code.plugin.tools.SelectionUtil;

public class CodeGenDialog extends Dialog {

	private final IStructuredSelection selection;
	private Text textPath;

	private Button btnVo;
	private Button btnVoGetSet;
	private Button btnService;
	private Button btnController;
	private Button btnDao;
	private Button btnXml;
	private Button btnDDL;
	private Button btnSameDir;
	private Button btnTransactional;
	private Label errMsgLable;
	
	protected ProjectConfig config = null;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */

	public CodeGenDialog(Shell parentShell, IStructuredSelection selection) {
		super(parentShell);
		this.selection = selection;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);

		Composite composite_title = new Composite(container, SWT.BORDER);
		composite_title.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_title.setBounds(0, 0, 580, 64);

		Label lbltitle = new Label(composite_title, SWT.NONE);
		lbltitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lbltitle.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD | SWT.ITALIC));
		lbltitle.setBounds(10, 10, 219, 22);
		lbltitle.setText("\u4EE3\u7801\u81EA\u52A8\u751F\u6210");

		Label lbltitle1 = new Label(composite_title, SWT.NONE);
		lbltitle1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lbltitle1.setBounds(20, 38, 516, 17);
		lbltitle1.setText(
				"生成的代码包括数据库、Mybatis配置文件、VO、Dao、Service和Controller代码");

		Composite composite_center = new Composite(container, SWT.BORDER);
		composite_center.setBounds(0, 64, 580, 247);

		btnVo = new Button(composite_center, SWT.CHECK);
		btnVo.setSelection(true);
		btnVo.setText("Generate Vo.java");
		btnVo.setBounds(20, 16, 300, 17);

		btnVoGetSet = new Button(composite_center, SWT.CHECK);
		btnVoGetSet.setSelection(true);
		btnVoGetSet.setText("Add Getter and Setter");
		btnVoGetSet.setBounds(43, 49, 277, 17);

		btnService = new Button(composite_center, SWT.CHECK);
		btnService.setSelection(true);
		btnService.setText("Generate Service.java");
		btnService.setBounds(20, 82, 300, 17);

		btnController = new Button(composite_center, SWT.CHECK);
		btnController.setSelection(true);
		btnController.setText("Generate Controller.java");
		btnController.setBounds(20, 115, 300, 17);

		btnDao = new Button(composite_center, SWT.CHECK);
		btnDao.setSelection(true);
		btnDao.setText("Generate Dao.java");
		btnDao.setBounds(20, 148, 300, 17);

		btnXml = new Button(composite_center, SWT.CHECK);
		btnXml.setSelection(true);
		btnXml.setText("Generate Ibatis Configuration File");
		btnXml.setBounds(20, 181, 300, 17);

		btnDDL = new Button(composite_center, SWT.CHECK);
		btnDDL.setSelection(true);
		btnDDL.setText("Generate DDL Files");
		btnDDL.setBounds(20, 214, 300, 17);
		
		btnSameDir = new Button(composite_center, SWT.CHECK);
		btnSameDir.setBounds(326, 16, 223, 17);
		btnSameDir.setText("Generate files in the same directory");
		
		btnTransactional = new Button(composite_center, SWT.CHECK);
		btnTransactional.setSelection(true);
		btnTransactional.setBounds(326, 82, 225, 17);
		btnTransactional.setText("Add Transactional");

		Composite composite_project = new Composite(container, SWT.BORDER);
		composite_project.setBounds(0, 310, 580, 43);

		Label label = new Label(composite_project, SWT.NONE);
		label.setText("Project path\uFF1A");
		label.setBounds(10, 13, 91, 17);

		textPath = new Text(composite_project, SWT.BORDER | SWT.READ_ONLY);
		textPath.setText("");
		textPath.setBounds(96, 10, 470, 23);
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setBounds(0, 351, 580, 40);
		composite.setLayout(null);
		
		errMsgLable = new Label(composite, SWT.NONE);
		errMsgLable.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		errMsgLable.setBounds(10, 8, 541, 22);

		this.initListener();
		this.init();

		return container;
	}

	private void initListener() {

		btnVo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnVo.getSelection()) {
					btnVoGetSet.setEnabled(true);
					btnVoGetSet.setSelection(true);
				} else {
					btnVoGetSet.setEnabled(false);
					btnVoGetSet.setSelection(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

	}

	public void init() {
		try {
			errMsgLable.setText("");
			
			Object adapter = this.selection.getFirstElement();
			IResource file = (IFile) SelectionUtil.getType(adapter, IResource.class);
			IProject project = file.getProject();
			File projectFile = new File(project.getLocationURI());
			this.textPath.setText(projectFile.toString());
			String fileName = file.getName();
			if (fileName != null && fileName.indexOf(".") > 0) {
				int p = fileName.indexOf(".");

				fileName = fileName.substring(0, p);
			}

			String frist = "Generate ";

			this.btnVo.setText(frist + fileName + "VO.java");
			this.btnService.setText(frist + fileName + "Service.java");
			this.btnController.setText(frist + fileName + "Controller.java");
			this.btnDao.setText(frist + fileName + "Dao.java");
			this.btnXml.setText(frist + "sqlmapping-" + fileName + ".xml");
			this.btnDDL.setText(frist + "Create-" +fileName + ".sql");

		} catch (Exception e) {
			System.out.print("CodeGenDialog.init error:\n" + e.getMessage());
			Shell shell = this.getShell();
			MessageDialog.openInformation(shell, "出错", e.getMessage());
		}

	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(586, 476);
	}

	public ProjectConfig getConfig() {
		return config;
	}

	public void setConfig(ProjectConfig config) {
		this.config = config;
	}

	
	public Label getErrMsgLable() {
		return errMsgLable;
	}

	protected void okPressed() {
		boolean isOk = true;
		try {
			isOk = this.onOK();
		} catch (Exception e1) {
			errMsgLable.setText("Error:" + e1.getMessage());
			isOk = false;
			Shell shell = this.getShell();
			MessageDialog.openInformation(shell, "生成出错", e1.getMessage());
		}
		if(isOk) {
			this.close();
		}
	}

	private boolean onOK() {

		CodeGenerator generator = new CodeGenerator();

		config.setGenerateVo(btnVo.getSelection());
		config.setGenerateGetterAndSetter(btnVoGetSet.getSelection());
		config.setGenerateService(btnService.getSelection());
		config.setGenerateController(btnController.getSelection());
		config.setGenerateDao(btnDao.getSelection());
		config.setGenerateDDL(btnDDL.getSelection());
		config.setGenerateIbatisSql(btnXml.getSelection());
		config.setSameDir(btnSameDir.getSelection());
		config.setAddTransactional(btnTransactional.getSelection());
		//config.setGenerateResource(btnXml.getSelection());

		try {
			generator.setConfig(config);
			// generator.addEntity(entity);

			generator.generate();
			generator.copyFile();
			generator.reflash();
			// SelectionUtil.refreshProject(project);
			// MessageDialog.openInformation(this.getShell(), "提示", "代码生成成功");
		} catch (CodeGenException e) {
			errMsgLable.setText("Error:" + e.getMessage());
			return false;
		}
		return true;

	}
}
