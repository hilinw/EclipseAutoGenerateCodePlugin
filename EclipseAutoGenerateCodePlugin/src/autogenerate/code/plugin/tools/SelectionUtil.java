package autogenerate.code.plugin.tools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.Openable;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SelectionUtil {
//    private static Log LOG = LogFactory.getLog(SelectionUtil.class);
	public static final int UNSUPPORTED = 0;
	public static final int PROJECT_WITH_NATURE = 1;
	public static final int PROJECT_WITHOUT_NATURE = 2;
	public static final int POM_FILE = 4;
	public static final int JAR_FILE = 8;
	public static final int JAVA_FILE = 16;
	public static final int METADATA_FILE = 32;
	public static final int PROPERTS_FILE = 990;
	public static final int JSPROPERTS_FILE = 991;
	public static final int METADATA_FIELD = 64;
	public static final int METADATA_METHOD = 128;
	public static final int METADATA_TYPE = 256;

	public SelectionUtil() {
	}

	public static int getSelectionType(IStructuredSelection selection) {
		int type = 0;

		int elementType;
		for (Iterator it = selection.iterator(); it.hasNext(); type |= elementType) {
			elementType = getElementType(it.next());
			if (elementType == 0) {
				return 0;
			}
		}

		return type;
	}

	public static int getElementType(Object element) {
		if (element instanceof IProject || element instanceof IJavaProject) {
			IProject project = (IProject) getType(element, IProject.class);
			if (project != null) {
				try {
					if (project.hasNature(".leviathanNature")) {
						return 1;
					}

					return 2;
				} catch (CoreException var5) {
				}
			}
		}

		Object adapter = getType(element, IJavaElement.class);
		CompilationUnit compilationUnit;
		String packageRoot;
		if (adapter instanceof SourceField) {
			SourceField sourceField = (SourceField) adapter;
			compilationUnit = (CompilationUnit) sourceField.getCompilationUnit();
			if (compilationUnit != null) {
				packageRoot = new String(compilationUnit.getPackageName()[0]);
				if (packageRoot.equals("metadata")) {
					return 64;
				}
			}
		}

		if (adapter instanceof SourceMethod) {
			SourceMethod sourceMethod = (SourceMethod) adapter;
			compilationUnit = (CompilationUnit) sourceMethod.getCompilationUnit();
			if (compilationUnit != null) {
				packageRoot = new String(compilationUnit.getPackageName()[0]);
				if (packageRoot.equals("metadata")) {
					return 128;
				}
			}
		}

		if (adapter instanceof SourceType) {
			SourceType sourceType = (SourceType) adapter;
			compilationUnit = (CompilationUnit) sourceType.getCompilationUnit();
			if (compilationUnit != null) {
				packageRoot = new String(compilationUnit.getPackageName()[0]);
				if (packageRoot.equals("metadata")) {
					return 256;
				}
			}
		}

		if (adapter instanceof CompilationUnit) {
			compilationUnit = (CompilationUnit) adapter;
			if (compilationUnit != null) {
				packageRoot = new String(compilationUnit.getPackageName()[0]);
				if (packageRoot.equals("metadata")) {
					return 32;
				}
			}
		}

		IFile file = (IFile) getType(element, IFile.class);
		if (file != null) {
			String filename = file.getFullPath().lastSegment();
			if ("pom.xml".equals(filename)) {
				return 4;
			}

			if (filename.endsWith("zh_CN.js")) {
				return 991;
			}

			if (filename.endsWith("_zh_CN.properties")) {
				return 990;
			}
		}

		if (file.getFullPath().getFileExtension().equals("java")) {
			return 16;
		} else {
			IPackageFragmentRoot fragment = (IPackageFragmentRoot) getType(element, IPackageFragmentRoot.class);
			return fragment != null && fragment.isArchive() ? 8 : 0;
		}
	}

	public static Object getType(Object element, Class<?> type) {
		if (type.isInstance(element)) {
			return element;
		} else {
			Object target = Platform.getAdapterManager().getAdapter(element, type);
			if (target == null) {
				if (type == IFile.class) {
					if (element instanceof Openable) {
						Openable openable = (Openable) element;
						IResource resource = openable.getResource();
						if (resource instanceof IFile) {
							return (IFile) resource;
						}
					}

					if (element instanceof SourceField) {
						return ((SourceField) element).getResource();
					}
				}

				JavaElement javaElement;
				if (type == IJavaProject.class) {
					if (element instanceof JavaElement) {
						javaElement = (JavaElement) element;
						return javaElement.getJavaProject();
					}

					if (element instanceof IProject) {
						return JavaCore.create((IProject) element);
					}
				}

				if (type == IProject.class) {
					if (element instanceof JavaElement) {
						javaElement = (JavaElement) element;
						return javaElement.getResource().getProject();
					}

					if (element instanceof IResource) {
						return ((IResource) element).getProject();
					}
				}
			}

			return target;
		}
	}

	public static void refreshProject2(IProject project) {
		IOUtils.refreshProject(project);

	}

	public static void refreshSelection(IStructuredSelection selection) {
		IAdaptable element = (IAdaptable) selection.getFirstElement();
		IResource resource = (IResource) element.getAdapter(IResource.class);
		IOUtils.refreshProject(resource.getProject());

	}


	public static IFolder getFolder(IResource res) {
		if (res == null) {
			return null;
		} else {
			IProject pro = res.getProject();
			if (pro != null && pro.getLocation() != null) {
				String proStr = pro.getLocation().toPortableString();
				String iniFolderStr = res.getLocation().toPortableString();
				String folderStr = iniFolderStr.replaceFirst(proStr, "");
				if (folderStr.startsWith("/")) {
					folderStr = folderStr.substring(1);
				}

				int index = folderStr.indexOf(47);
				if (index > -1) {
					folderStr = folderStr.substring(0, index);
				}

				if (folderStr == null || folderStr.length() == 0) {
					File file = new File(proStr);
					if (file.listFiles().length > 0) {
						String path = "";
						File[] var11;
						int var10 = (var11 = file.listFiles()).length;

						for (int var9 = 0; var9 < var10; ++var9) {
							File tempFile = var11[var9];
							if (tempFile.isDirectory()) {
								path = tempFile.getAbsolutePath();
								if (path.lastIndexOf(".metadata") <= -1 && path.lastIndexOf("bin") <= -1
										&& path.lastIndexOf(".cvs") <= -1 && path.lastIndexOf(".svn") <= -1) {
									folderStr = path.substring(path.lastIndexOf("\\") + 1);
									break;
								}
							}
						}
					}
				}

				if (folderStr != null && folderStr.length() != 0) {
					IFolder rfolder = pro.getFolder(folderStr);
					return rfolder;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	/**
	 *  get imports from sourece file
	 * @param sourceFile
	 * @return
	 */
	public static List<String> getImports(String fileInfo) {

//		String fileInfo = IOUtils.readFile(sourceFile);

		List<String> imports = new ArrayList<String>();

		int i = fileInfo.indexOf("public class ");
		if (i > 0) {
			fileInfo =  fileInfo.substring(0,i);
		}
		imports = getImport(fileInfo, imports);
		
		return imports;
	}
	

	public static String getExtendsFile(String fileInfo) {

		String extendsFile = "";


		int i = fileInfo.indexOf("extends ");
		if (i > 0) {
			fileInfo =  fileInfo.substring(i+8);
		}else {
			return "";
		}
		i = fileInfo.indexOf("{");
		if (i > 0) {
			extendsFile =  fileInfo.substring(0,i);
		}
		
		return extendsFile.trim();
	}
	
	
	public static List<String> getImport(String fileInfo,List<String> imports){
		int p  = fileInfo.indexOf("import");
		if(p >0) {
			fileInfo = fileInfo.substring(p+7);
			boolean isMeta = false;
			if(fileInfo.startsWith("metadata.")) {
				fileInfo = fileInfo.substring(9);
				isMeta = true;
			}
			int i = fileInfo.indexOf(";");
			if(i >0) {
				if(isMeta) {
					imports.add(fileInfo.substring(0,i)+"VO");
				}else {
					imports.add(fileInfo.substring(0,i));
				}
				fileInfo = fileInfo.substring(i+1);
				return getImport(fileInfo, imports);
			}
			
		}
		
		
		return imports;
	}

}