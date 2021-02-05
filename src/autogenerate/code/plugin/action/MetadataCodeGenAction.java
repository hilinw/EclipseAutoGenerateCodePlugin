package autogenerate.code.plugin.action;

import java.io.File;
import java.lang.annotation.Annotation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;

import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import autogenerate.code.plugin.dialog.CodeGenDialog;
import autogenerate.code.plugin.dialog.NotMetaDataDialog;
import autogenerate.code.plugin.generator.ProjectConfig;
import autogenerate.code.plugin.tools.IOUtils;
import autogenerate.code.plugin.tools.ProjectClassLoader;
import autogenerate.code.plugin.tools.SelectionUtil;

public class MetadataCodeGenAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {
	public static final String ID = "autogenerate.code.plugin.action.MetadataCodeGenAction";
	private IStructuredSelection selection;
	private IWorkbenchWindow window;

	public MetadataCodeGenAction() {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		try {
			if (!this.isMetadata()) {
//            Shell shell = this.window != null ? this.window.getShell() : null;
//            MessageDialog.openInformation(shell, "出错", "");
				IStructuredSelection structuredSelection = (IStructuredSelection) this.selection;

				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				NotMetaDataDialog dialog = new NotMetaDataDialog(shell, structuredSelection);
				dialog.create();
				dialog.open();

			} else {
				IStructuredSelection structuredSelection = (IStructuredSelection) this.selection;

				ProjectConfig projectConfig = getProjectConfig();
				
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				CodeGenDialog dialog = new CodeGenDialog(shell, structuredSelection);
				
				dialog.setConfig(projectConfig);
				
				//dialog.init();
				
				dialog.create();
				dialog.open();

//            CodeGenWizard wizard = new CodeGenWizard(structuredSelection);
//            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//            WizardDialog dialog = new WizardDialog(shell, wizard);
//            dialog.create();
//            dialog.open();
			}
		} catch (Exception e) {

			e.printStackTrace();

			Shell shell = this.window != null ? this.window.getShell() : null;
			MessageDialog.openInformation(shell, "错误", e.getMessage());
		}
	}


    private ProjectConfig getProjectConfig() {

        ProjectConfig projectConfig = new ProjectConfig();
        
        Object adapter = this.selection.getFirstElement();
        IResource resource = (IResource)SelectionUtil.getType(adapter, IResource.class);
        IProject project = resource.getProject();
        File sourceFile = new File(resource.getRawLocationURI());
        
        projectConfig.setSourceFile(sourceFile);
        
        File workspaceFile = new File(project.getWorkspace().getRoot().getLocationURI());
        File tempFile = new File(workspaceFile, ".metadata\\.plugins\\autogeneratecode\\buildcode_temp");
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        projectConfig.setWorkSpace(tempFile);
        projectConfig.setProjectDirectory(new File(project.getLocationURI()));
        //projectConfig.setShortName(shortName);

        IJavaProject javaProject = JavaCore.create(project);
        projectConfig.setJavaProject(javaProject);
        projectConfig.setSelection(this.selection);

        
        String fileInfo = IOUtils.readFile(sourceFile);

		
		String packageName = ProjectClassLoader.getPackageName(fileInfo); 
		projectConfig.setPackageName(packageName);
		
        projectConfig.getEntities().add(getClassFile(javaProject,sourceFile,fileInfo));
        
        projectConfig.setImports(SelectionUtil.getImports(fileInfo));
        projectConfig.setExtClass(SelectionUtil.getExtendsFile(fileInfo));
        
        return projectConfig;
    }
	

	public Class<?> getClassFile(IJavaProject javaProject,File sourceFile,String fileInfo) {
		ProjectClassLoader clazzLoader = null;
		try {
			clazzLoader = new ProjectClassLoader(javaProject);
			String projectPath = javaProject.getPath().toString();
			System.out.println("projectPath: " + projectPath);
			
			Class<?> entity = clazzLoader.loadClassByFile(sourceFile,fileInfo);
			
			return entity;
			
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + sourceFile.getName());
		} catch (Exception e) {
			System.out.println("getClassFile error: " + e.getMessage());
		}finally {
			if(clazzLoader != null) {
				try {
					clazzLoader.close();
				} catch (Exception e) {
					System.out.println("clazzLoader close error: " + e.getMessage());
				} 
			}
		}
		return null;
	}
	
    
	public boolean isMetadata() throws Exception {

		if (this.selection == null || this.selection.isEmpty()) {
			return false;
		}
		Object adapter = this.selection.getFirstElement();
		IResource resource = (IResource) SelectionUtil.getType(adapter, IResource.class);
		
		IProject project = resource.getProject();
		File sourceFile = new File(resource.getRawLocationURI());

		// 
		if (adapter instanceof JavaElement) {
			JavaElement javaElement = (JavaElement) adapter;
			IJavaProject javaProject = javaElement.getJavaProject();
			if (adapter instanceof CompilationUnit) {
				CompilationUnit compilationUnit = (CompilationUnit) adapter;
				String packageRoot = new String(compilationUnit.getPackageName()[0]);
				if (!packageRoot.equals("metadata")) {
					return false;
				}

				return checkAnnotation(javaProject,sourceFile);

			}

		} else if (adapter instanceof IEditorInput) { // 

			IEditorInput iEditorInput = (IEditorInput) adapter;
			String editName =  iEditorInput.getName();
			System.out.println("IEditorPart name: " + editName);
			if(!editName.endsWith(".java")) {
				return false;
			}
			
			IJavaProject javaProject = JavaCore.create(project);
			return checkAnnotation(javaProject,sourceFile);
		}

		return false;

	}
	
	
	public boolean checkAnnotation(IJavaProject javaProject,File sourceFile) throws Exception {
		ProjectClassLoader clazzLoader = null;
		try {
			clazzLoader = new ProjectClassLoader(javaProject);
			String projectPath = javaProject.getPath().toString();
			System.out.println("projectPath: " + projectPath);
			String fileInfo = IOUtils.readFile(sourceFile);
			Class<?> entity = clazzLoader.loadClassByFile(sourceFile,fileInfo);
			
			Annotation[] snnotations = entity.getAnnotations();

			for (Annotation annotation : snnotations) {
				if(annotation.annotationType().getName().equalsIgnoreCase("www.autogeneratecode.model.Entity")) {
					return true;
				}
			}
			

		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + sourceFile.getName());
		}finally {
			if(clazzLoader != null) {
				clazzLoader.close();
			}
		}
		
		return false;
	}
	
	

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}

	}

	public ISelection getSelection() {
		return this.selection;
	}

	public IWorkbenchWindow getWindow() {
		return this.window;
	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {

	}

}
