package autogenerate.code.plugin.tools;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;


public class ProjectClassLoader extends URLClassLoader {
    private final IJavaProject javaProject;

    public ProjectClassLoader(IJavaProject project) throws JavaModelException {
        super(getURLSFromProject(project, (URL[])null), Thread.currentThread().getContextClassLoader());
        this.javaProject = project;
    }

    public ProjectClassLoader(IJavaProject project, URL[] extraUrls) throws JavaModelException {
        super(getURLSFromProject(project, extraUrls), Thread.currentThread().getContextClassLoader());
        this.javaProject = project;
    }

    
    public Class<?> loadClassByFile(File sourceFile,String fileInfo) throws ClassNotFoundException {
    	
    	String projectPath = javaProject.getPath().toString();
		System.out.println("projectPath: " + projectPath);
    	
		String soruceFileName = sourceFile.getName();
		System.out.println("soruceFileName: " + soruceFileName);
		String fileName = soruceFileName; 
		
		int p = soruceFileName.indexOf(".");
		if(p> 0) {
			fileName = soruceFileName.substring(0,p);
		}
		
		String packageName = ProjectClassLoader.getPackageName(fileInfo); 
		
		String className = packageName + "." + fileName;
		
		System.out.println("className: " + className);
		
        Class<?> clazz = super.loadClass(className);
        return clazz;
    }

    /**
     * get packageName
     * @param fileInfo
     * @return
     */
    public static String getPackageName(String fileInfo) {
    	
    	if(fileInfo == null || fileInfo.trim().length() ==0) {
    		return "";
    	}
    	int p  = fileInfo.indexOf("package");
    	
    	if(p > 0) {
    		String newstr = fileInfo.substring(p+8);
    		int i = newstr.indexOf(";");
    		if(i > 0) {
    			String packageName = newstr.substring(0,i);
    			return packageName;
    		}
    	}
    	
    	return "";
    	
    }
    
    
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = super.loadClass(name);
        return clazz;
    }
    
    public URL getResource(String name) {
        URL url = super.getResource(name);
        if (url == null && name.endsWith(".java")) {
            try {
                IClasspathEntry[] var6;
                int var5 = (var6 = this.javaProject.getResolvedClasspath(false)).length;

                int var4;
                File file;
                for(var4 = 0; var4 < var5; ++var4) {
                    IClasspathEntry entry = var6[var4];
                    IPath path = entry.getPath();
                    if (path != null) {
                        File javaFile;
                        if (path.toString().replace('\\', '/').startsWith("/")) {
                            javaFile = new File(this.javaProject.getProject().getWorkspace().getRoot().getLocationURI());
                            file = new File(javaFile, path.toString());
                        } else {
                            file = path.toFile();
                        }

                        if (!file.getName().endsWith(".jar") && !file.getName().endsWith(".zip")) {
                            javaFile = new File(file, name);
                            if (javaFile.exists()) {
                                try {
                                    return javaFile.toURL();
                                } catch (MalformedURLException var18) {
                                    var18.printStackTrace();
                                }
                            }

                            file = null;
                        } else {
                            file = null;
                        }
                    }
                }

                String[] var21;
                var5 = (var21 = this.javaProject.getRequiredProjectNames()).length;

                for(var4 = 0; var4 < var5; ++var4) {
                    String requiredProjectName = var21[var4];
                    IProject requriedProject = this.javaProject.getProject().getWorkspace().getRoot().getProject(requiredProjectName);
                    IJavaProject requriedJavaProject = JavaCore.create(requriedProject);
                    if (requriedJavaProject != null) {
                        IClasspathEntry[] var12;
                        int var11 = (var12 = requriedJavaProject.getResolvedClasspath(false)).length;

                        for(int var10 = 0; var10 < var11; ++var10) {
                            IClasspathEntry entry = var12[var10];
                            IPath path = entry.getPath();
                            if (path != null) {
                                File javaFile;
                                if (path.toString().replace('\\', '/').startsWith("/")) {
                                    javaFile = new File(this.javaProject.getProject().getWorkspace().getRoot().getLocationURI());
                                    file = new File(javaFile, path.toString());
                                } else {
                                    file = path.toFile();
                                }

                                if (!file.getName().endsWith(".jar") && !file.getName().endsWith(".zip")) {
                                    javaFile = new File(file, name);
                                    if (javaFile.exists()) {
                                        try {
                                            return javaFile.toURL();
                                        } catch (MalformedURLException var17) {
                                            var17.printStackTrace();
                                        }
                                    }

                                    file = null;
                                } else {
                                    file = null;
                                }
                            }
                        }
                    }

                    file = null;
                }

                return url;
            } catch (JavaModelException var19) {
                var19.printStackTrace();
            }
        }

        return url;
    }

    private static URL[] getURLSFromProject(IJavaProject project, URL[] extraUrls) throws JavaModelException {
        if (project == null) {
            throw new IllegalArgumentException("project is null");
        } else {
            List<URL> list = new ArrayList();
            if (extraUrls != null) {
                for(int i = 0; i < extraUrls.length; ++i) {
                    list.add(extraUrls[i]);
                }
            }

            IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
            IPath installPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();

            for(int i = 0; i < roots.length; ++i) {
                try {
                    if (roots[i].isArchive()) {
                        File f = new File(FileLocator.resolve(installPath.append(roots[i].getPath()).toFile().toURL()).getFile());
                        if (!f.exists()) {
                            f = new File(FileLocator.resolve(roots[i].getPath().makeAbsolute().toFile().toURL()).getFile());
                        }

                        list.add(f.toURL());
                    } else {
                        IPath path = roots[i].getJavaProject().getOutputLocation();
                        if (path.segmentCount() > 1) {
                            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                            path = root.getFolder(path).getLocation();
                            list.add(path.toFile().toURL());
                        } else {
                            path = roots[i].getJavaProject().getProject().getLocation();
                            list.add(path.toFile().toURL());
                        }
                    }
                } catch (Exception var8) {
                }
            }

            URL[] urls = new URL[list.size()];
            int index = 0;

            for(Iterator it = list.iterator(); it.hasNext(); ++index) {
                urls[index] = (URL)it.next();
            }

            return urls;
        }
    }
}
