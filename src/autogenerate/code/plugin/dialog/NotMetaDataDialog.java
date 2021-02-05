package autogenerate.code.plugin.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import autogenerate.code.plugin.swt.SWTResourceManager;
import autogenerate.code.plugin.tools.IOUtils;
import autogenerate.code.plugin.tools.SelectionUtil;

import org.eclipse.swt.custom.StyledText;

public class NotMetaDataDialog extends Dialog {

	private final IStructuredSelection selection;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public NotMetaDataDialog(Shell parentShell, IStructuredSelection selection) {
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

		Composite composite = new Composite(container, SWT.BORDER);
		composite.setBounds(0, 0, 544, 65);
		composite.setLayout(null);

		Label lblTitle = new Label(composite, SWT.NONE);
		lblTitle.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.BOLD));
		lblTitle.setBounds(10, 10, 317, 22);
		lblTitle.setText("Can't generate code info");

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(20, 34, 399, 17);
		lblNewLabel.setText("The selected file can not generate code automatically !");

		Composite composite_1 = new Composite(container, SWT.BORDER);
		composite_1.setBounds(0, 65, 544, 213);
		composite_1.setLayout(null);

		StyledText styledText = new StyledText(composite_1, SWT.BORDER);
		styledText.setText(
				"The automatically generated file must be:\r\n1\u3001It's a java file and package name is start with 'metadata.';\r\n2\u3001It's Annotation by 'www.autogeneratecode.model.Entity'.\r\n\r\n\u80FD\u81EA\u52A8\u751F\u6210\u4EE3\u7801\u7684\u6587\u4EF6\u5FC5\u987B\u662F:\r\n1\u3001java\u6587\u4EF6\u4E14\u5305\u540D\u79F0\u4EE5\"metadata.\"\u5F00\u5934;\r\n2\u3001\u6709\u6CE8\u89E3 'www.autogeneratecode.model.Entity'\r\n\u70B9\u51FBOK\u6309\u94AE\u53EF\u4EE5\u5E2E\u5FD9\u751F\u6210\u4E00\u4E2A\u6A21\u677F\u6587\u4EF6\u3002\r\n\r\nFor more information, please refer to github:\r\nhttps://github.com/hilinw/IdeaAutoGenerateCodePlugin.git");
		styledText.setBounds(0, 0, 540, 209);

		Composite composite_2 = new Composite(container, SWT.BORDER);
		composite_2.setBounds(0, 278, 544, 37);

		Label lblOkTohelp = new Label(composite_2, SWT.NONE);
		lblOkTohelp.setBounds(10, 10, 396, 17);
		lblOkTohelp.setText("Check 'OK' to Help create a template file.");

		return container;
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
		return new Point(550, 400);
	}

	protected void okPressed() {

		try {
			this.onOK();
		} catch (Exception e1) {
			Shell shell = this.getShell();
			MessageDialog.openInformation(shell, "生成出错", "生成ExampleFile.java文件出错");
		}
		this.close();
	}

	/**
	 * 点ok，生成metadata 模板文件
	 */
	private void onOK() {

		Object adapter = this.selection.getFirstElement();
		IResource resource = (IResource) SelectionUtil.getType(adapter, IResource.class);
//        IProject project = resource.getProject();
		File sourceFile = new File(resource.getRawLocationURI());
//        IJavaProject javaProject = (IJavaProject)SelectionUtil.getType(adapter, IJavaProject.class);		

		// String filePath = sourceFile.getPath();

		String packageName = null;

		IFolder root = SelectionUtil.getFolder(resource);
		if (root != null) {
			String rootPath = root.getLocation().toString();
//			System.out.println("rootPath:"+rootPath);
			String resPath = resource.getLocation().toPortableString();

			if (resource instanceof IFile && resPath.lastIndexOf("/") != -1) {
				resPath = resPath.substring(0, resPath.lastIndexOf("/"));
			}

			if (resPath.startsWith(rootPath) && resPath.length() > rootPath.length()) {
				packageName = resPath.substring(rootPath.length() + 1);
				packageName = packageName.replaceAll("/", ".");
			}
		}
//		System.out.println("packageName0000000000ln:"+packageName);
		if (packageName == null) {
			return;
		}
		//maven的 代码结构
		if(packageName.startsWith("main.java.")) {
			packageName = packageName.substring(10);
		}
		if(packageName.startsWith("java.")) {
			packageName = packageName.substring(5);
		}
		
//		System.out.println("packageName1111111ln:"+packageName);
		String newPackageName = packageName;
		boolean isMetaData = true;
		if (!packageName.startsWith("metadata.")) {
			newPackageName = "metadata." + packageName;
			isMetaData = false;
		}

		String filePath = sourceFile.getPath();
		String packageName2 = packageName.replace(".", "\\");
		int p = filePath.indexOf(packageName2);
		String newPath = "";
		if (p > 0) {
			newPath = filePath.substring(0, p);
			if(isMetaData) {
				newPath = newPath  + packageName2;
			}else {
				newPath = newPath + "metadata" + "\\" + packageName2;
			}
				
		}
		if (newPath.length() > 0) {
			File file = new File(newPath);
			file.mkdirs();
			try {
				String source = getExampleFile(newPackageName);
				File javafile = new File(newPath, "ExampleFile.java");
				System.out.println("javafile:" + javafile.getPath());
				if (javafile.exists()) {
					javafile.delete();
				}
				OutputStreamWriter writer = null;
				try {
					writer = new OutputStreamWriter(new FileOutputStream(javafile), "UTF-8");
					writer.write(source);
				} finally {
					IOUtils.close(writer);
				}
				IOUtils.refreshProject(resource.getProject());

			} catch (Exception e1) {
				Shell shell = this.getShell();
				MessageDialog.openInformation(shell, "生成出错", "生成ExampleFile.java文件出错");
			}
		}

	}

	private String getExampleFile(String newPackageName) {

		StringBuilder sb = new StringBuilder();

		sb.append("package ");
		sb.append(newPackageName);

		sb.append(";\n");
		sb.append("import java.util.Date;\n");
		sb.append("\n");
		sb.append("import www.autogeneratecode.model.Column;\n");
		sb.append("import www.autogeneratecode.model.Comment;\n");
		sb.append("import www.autogeneratecode.model.Entity;\n");
		sb.append("import www.autogeneratecode.model.Table;\n");

		sb.append("\n");

		sb.append("@Entity\n");
		sb.append("@Comment(content = \"ExampleFile\")\n");
		sb.append("@Table(name=\"T_ExampleFile\")\n");

		sb.append("\n");
		sb.append("\n");

		sb.append("/**\n");
		sb.append("* note:\n");
		sb.append(
				"* import class:(Entity,Column,Comment,Table) is in jarfile: auto_generate_code_mode.jar, you can download from :\n");
		sb.append("* https://github.com/hilinw/IdeaAutoGenerateCodePlugin.git  dir: lib/ \n");
		sb.append("* \n");
		sb.append("*/\n");

		sb.append("\n");
		sb.append("\n");

		sb.append("public class ExampleFile {\n");

		sb.append("\n");
		sb.append("    @Comment(content=\"ID\")\n");
		sb.append("    @Column(name=\"FID\", dataType=\"decimal\", precision=32)\n");
		sb.append("    public long id;\n");
		sb.append(" \n");
		
		sb.append("\n");
		sb.append("    @Comment(content=\"用户ID\")\n");
		sb.append("    @Column(name=\"FUSERID\", dataType=\"decimal\", precision=32)\n");
		sb.append("    public long userId;\n");
		sb.append(" \n");

		sb.append("    @Comment(content=\"用户名称\")\n");
		sb.append("    @Column(name=\"FNAME\", dataType=\"VARCHAR\" ,nullable=false ,length=50)\n");
		sb.append("    public String name;\n");
		sb.append("\n");

		sb.append("    @Comment(content=\"用户编码\")\n");
		sb.append("    @Column(name=\"FNUMBER\", dataType=\"VARCHAR\" ,nullable=false ,length=20)\n");
		sb.append("    public String number;\n");
		sb.append("\n");

		sb.append("    @Comment(content=\"状态: 0:在职,1:离职\")\n");
		sb.append("    @Column(name=\"Fstatus\", dataType=\"int\" , precision=1)\n");
		sb.append("    public int status;\n");
		sb.append("\n");

		sb.append("    @Comment(content=\"是否禁用\")\n");
		sb.append("    @Column(name=\"FISDELETE\", dataType=\"int\" , nullable=false)\n");
		sb.append("    public boolean isDelete;\n");
		sb.append("\n");
		
		sb.append("    @Comment(content=\"创建日期\")\n");
		sb.append("    @Column(name=\"FCreateDate\", dataType=\"Date\" ,nullable=false ,length=20)\n");
		sb.append("    public Date createDate;\n");
		sb.append("\n");
		
		sb.append("    @Comment(content=\"更新日期\")\n");
		sb.append("    @Column(name=\"FUpdataDate\", dataType=\"Date\" ,nullable=false ,length=20)\n");
		sb.append("    public Date updataDate;\n");
		sb.append("\n");

		sb.append("}\n");
		sb.append("\n");

		return sb.toString();

	}

}
