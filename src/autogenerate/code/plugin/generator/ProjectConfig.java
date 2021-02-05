package autogenerate.code.plugin.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ProjectConfig {

    private File workSpace;
    private File projectDirectory;
    private String shortName;
    private boolean generateVo = true;
    private boolean generateGetterAndSetter = true;
    private boolean generateService = true;
    private boolean generateController = true;
    private boolean generateDao = true;
    private boolean generateIbatisSql = true;
    private boolean generateDDL = true;
    private boolean generateResource = true;

    private boolean generateServiceTestCase = true;
    private boolean generateIbatisConfig = true;
    private boolean generateSpringConfig = true;
    private boolean databaseDrop = false;
    private boolean databaseCreate = false;
    private boolean executeInitScript = true;
    private boolean projectCompile = true;
    private boolean projectTest = false;
    
    private boolean isSameDir = true;
    private boolean addTransactional = true;
    private boolean isNoInterface = true;

    private IStructuredSelection selection;
    private IJavaProject javaProject;
    protected List<Class<?>> entities = new ArrayList<Class<?>>();
    private String packageName;
    private File sourceFile;
    protected List<String> imports = new ArrayList<String>();
    private String extClass;


    public File getWorkSpace() {
        return workSpace;
    }

    public void setWorkSpace(File workSpace) {
        this.workSpace = workSpace;
    }

    public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public File getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(File projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isGenerateVo() {
		return generateVo;
	}

	public void setGenerateVo(boolean generateVo) {
		this.generateVo = generateVo;
	}

	public boolean isGenerateGetterAndSetter() {
        return generateGetterAndSetter;
    }

    public void setGenerateGetterAndSetter(boolean generateGetterAndSetter) {
        this.generateGetterAndSetter = generateGetterAndSetter;
    }

    public boolean isGenerateService() {
        return generateService;
    }

    public void setGenerateService(boolean generateService) {
        this.generateService = generateService;
    }

    public boolean isGenerateController() {
        return generateController;
    }

    public void setGenerateController(boolean generateController) {
        this.generateController = generateController;
    }

    public boolean isGenerateDao() {
		return generateDao;
	}

	public void setGenerateDao(boolean generateDao) {
		this.generateDao = generateDao;
	}

	public boolean isGenerateIbatisSql() {
        return generateIbatisSql;
    }

    public void setGenerateIbatisSql(boolean generateIbatisSql) {
        this.generateIbatisSql = generateIbatisSql;
    }

    public boolean isGenerateDDL() {
        return generateDDL;
    }

    public void setGenerateDDL(boolean generateDDL) {
        this.generateDDL = generateDDL;
    }

    public boolean isGenerateResource() {
        return generateResource;
    }

    public void setGenerateResource(boolean generateResource) {
        this.generateResource = generateResource;
    }

    public boolean isGenerateServiceTestCase() {
        return generateServiceTestCase;
    }

    public void setGenerateServiceTestCase(boolean generateServiceTestCase) {
        this.generateServiceTestCase = generateServiceTestCase;
    }

    public boolean isGenerateIbatisConfig() {
        return generateIbatisConfig;
    }

    public void setGenerateIbatisConfig(boolean generateIbatisConfig) {
        this.generateIbatisConfig = generateIbatisConfig;
    }

    public boolean isGenerateSpringConfig() {
        return generateSpringConfig;
    }

    public void setGenerateSpringConfig(boolean generateSpringConfig) {
        this.generateSpringConfig = generateSpringConfig;
    }

    public boolean isDatabaseDrop() {
        return databaseDrop;
    }

    public void setDatabaseDrop(boolean databaseDrop) {
        this.databaseDrop = databaseDrop;
    }

    public boolean isDatabaseCreate() {
        return databaseCreate;
    }

    public void setDatabaseCreate(boolean databaseCreate) {
        this.databaseCreate = databaseCreate;
    }

    public boolean isExecuteInitScript() {
        return executeInitScript;
    }

    public void setExecuteInitScript(boolean executeInitScript) {
        this.executeInitScript = executeInitScript;
    }

    public boolean isProjectCompile() {
        return projectCompile;
    }

    public void setProjectCompile(boolean projectCompile) {
        this.projectCompile = projectCompile;
    }

    public boolean isProjectTest() {
        return projectTest;
    }

    public void setProjectTest(boolean projectTest) {
        this.projectTest = projectTest;
    }

	public IStructuredSelection getSelection() {
		return selection;
	}

	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public List<Class<?>> getEntities() {
		return entities;
	}

	public void setEntities(List<Class<?>> entities) {
		this.entities = entities;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	public String getExtClass() {
		return extClass;
	}

	public void setExtClass(String extClass) {
		this.extClass = extClass;
	}

	public boolean isSameDir() {
		return isSameDir;
	}

	public void setSameDir(boolean isSameDir) {
		this.isSameDir = isSameDir;
	}

	public boolean isAddTransactional() {
		return addTransactional;
	}

	public void setAddTransactional(boolean addTransactional) {
		this.addTransactional = addTransactional;
	}

	public boolean isNoInterface() {
		return isNoInterface;
	}

	public void setNoInterface(boolean isNoInterface) {
		this.isNoInterface = isNoInterface;
	}
	
}

