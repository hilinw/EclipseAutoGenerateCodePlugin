package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import autogenerate.code.plugin.tools.CopyTask;
import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Entity;

public class CodeGenerator {
	private static final Log LOG = LogFactory.getLog(CodeGenerator.class);
	protected ProjectConfig config;
	protected List<CopyTask> fileTasks = new ArrayList<CopyTask>();

	public CodeGenerator() {
	}

	public CodeGenerator(ProjectConfig config) {
		this.config = config;
	}

	private File getProjectDir() {
		return this.config != null ? this.config.getProjectDirectory() : null;
	}

	public void generate() throws CodeGenException {

		if (this.config.getWorkSpace() == null) {
			throw new CodeGenException("代码生成的工作目录未设置");
		} else {
			String shortName = this.config.getShortName();
			if (StringUtils.isEmpty(shortName)) {
				shortName = this.config.getProjectDirectory().getName();
			}

			int p = shortName.indexOf("-");
			if (p > 0) {
				shortName = shortName.substring(p + 1);
			}

			this.config.setShortName(shortName);

			try {
				if (LOG.isInfoEnabled()) {
					LOG.info("workspace : " + this.config.getWorkSpace());
				}

				Iterator<Class<?>> iterator = this.config.getEntities().iterator();

				while (iterator.hasNext()) {
					Class<?> javaClass = (Class<?>) iterator.next();

					Annotation annotation = javaClass.getAnnotation(Entity.class);
					if (annotation != null) {
						if (this.config.isGenerateVo()) {
							this.generateVoFile(javaClass);
						}

					}
					if (this.config.isGenerateController()) {
						this.generateControllerFile(javaClass);
					}
					if (this.config.isGenerateService()) {
						this.generateServiceFile(javaClass);
					}
					if (this.config.isGenerateDao()) {
						this.generateDaoFile(javaClass);
					}
					if (this.config.isGenerateIbatisSql()) {
						this.generateSqlMappingFile(javaClass);
					}
					if (this.config.isGenerateDDL()) {
						this.generateDDLFile(javaClass);
					}

				}

				if (this.getProjectDir() != null) {

				}

			} catch (IOException e1) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e1.getMessage(), e1);
				}
				throw new CodeGenException(e1);
			}
			this.reflash();
		}
	}

	public void reflash() {
		IOUtils.refreshProject(config.getJavaProject().getProject());
	}

	private File generateVoFile(Class<?> javaClass) throws IOException {

		String packageName = config.getPackageName();

		JavaEntityGenerator generator = new JavaEntityGenerator(javaClass, packageName);
		generator.setGenerateGetterAndSetter(config.isGenerateGetterAndSetter());
		generator.setSourceFileImports(config.getImports());
		generator.setExtClass(config.getExtClass());
		generator.setSameDir(config.isSameDir());
		generator.generate();
		File file = generator.write(this.config.getWorkSpace());

		if (this.getProjectDir() != null) {
			this.fileTasks.add(new CopyTask(file, this.getJavaFileDir("vo")));
		}

		LOG.info("generate: '" + file.getName() + "'  ok.");
		System.out.println("generate: '" + file.getName() + "'  ok.");

		return file;

	}

	private File generateControllerFile(Class<?> javaClass) throws IOException {

		String packageName = config.getPackageName();
		JavaControllerGenerator generator = new JavaControllerGenerator(javaClass, packageName);
		generator.setSourceFileImports(config.getImports());
		generator.setExtClass(config.getExtClass());
        generator.setSameDir(config.isSameDir());
        generator.setAddTransactional(config.isAddTransactional());//是否增加 事务标注
        generator.setNoInterface(config.isNoInterface());
		generator.generate();
		File file = generator.write(this.config.getWorkSpace());

		if (this.getProjectDir() != null) {
			this.fileTasks.add(new CopyTask(file, this.getJavaFileDir("controller")));
		}

		LOG.info("generate: '" + file.getName() + "'  ok.");
		System.out.println("generate: '" + file.getName() + "'  ok.");

		return file;

	}

	private File generateDaoFile(Class<?> javaClass) throws IOException {

		String packageName = config.getPackageName();
		JavaDaoGenerator generator = new JavaDaoGenerator(javaClass, packageName);
		generator.setSourceFileImports(config.getImports());
		generator.setExtClass(config.getExtClass());
		generator.setSameDir(config.isSameDir());
		generator.generate();
		File file = generator.write(this.config.getWorkSpace());

		if (this.getProjectDir() != null) {
			this.fileTasks.add(new CopyTask(file, this.getJavaFileDir("dao")));
		}

		LOG.info("generate: '" + file.getName() + "'  ok.");
		System.out.println("generate: '" + file.getName() + "'  ok.");

		return file;

	}

	private File generateServiceFile(Class<?> javaClass) throws IOException {

		String packageName = config.getPackageName();

		JavaServiceGenerator generator = new JavaServiceGenerator(javaClass, packageName);
		generator.setSourceFileImports(config.getImports());
		generator.setSameDir(config.isSameDir());
		generator.setAddTransactional(config.isAddTransactional());//是否增加 事务标注
		generator.setNoInterface(config.isNoInterface());
		
		generator.generate();
		
		if(!this.config.isNoInterface()) {
			//接口类
			File file = generator.write(this.config.getWorkSpace());
	
			if (this.getProjectDir() != null) {
				this.fileTasks.add(new CopyTask(file, this.getJavaFileDir("service")));
			}
	
	//		LOG.info("generate: '" + file.getName() + "'  ok.");
			System.out.println("generate: '" + file.getName() + "'  ok.");
		}
		// 写实现类
		File file = generator.writeImpl(this.config.getWorkSpace());

		if (this.getProjectDir() != null) {
			String filepath = "";
			if(this.config.isNoInterface()) {
				filepath = "service";
			}else {
				filepath = "service" + File.separator + "impl";
			}
			this.fileTasks.add(new CopyTask(file, this.getJavaFileDir(filepath)));
		}

//		LOG.info("generate: '" + file.getName() + "'  ok.");
		System.out.println("generate: '" + file.getName() + "'  ok.");

		return file;

	}

	private File generateSqlMappingFile(Class<?> javaClass) throws IOException {

		String packageName = config.getPackageName();
		SqlMappingGenerator generator = new SqlMappingGenerator(javaClass, packageName);
		generator.setSameDir(config.isSameDir());
		File file = generator.write(this.config.getWorkSpace());

		if (this.getProjectDir() != null) {
			this.fileTasks.add(new CopyTask(file, this.getSqlFileDir("sql")));
		}

		LOG.info("generate: '" + file.getName() + "'  ok.");
		System.out.println("generate: '" + file.getName() + "'  ok.");

		return file;

	}

	private File generateDDLFile(Class<?> javaClass) throws IOException {

		DDLGenerator generator = new DDLGenerator(javaClass);

		generator.generate();
		File file = generator.write(this.config.getWorkSpace());

		if (this.getProjectDir() != null) {
			this.fileTasks.add(new CopyTask(file, this.getSqlFileDir("sql")));
		}

		LOG.info("generate: '" + javaClass.getName() + "'  ok.");

		return file;

	}

	/**
	 * 生成VO文件的路径。 在原来的路径里面去掉metadata
	 */
	private File getJavaFileDir(String childDir) {

//		String filePath = psiJavaFileImpl.getVirtualFile().getParent().getPath();
		String filePath = config.getSourceFile().getParentFile().getPath();
		int p = filePath.indexOf("metadata");
		String newPath = "";
		if (p > 0) {
			newPath = filePath.substring(0, p);
			newPath = newPath + filePath.substring(p + 9);
		}

		if (!config.isSameDir()) {
			if (childDir != null && childDir.trim().length() > 0) {

				newPath = newPath + File.separator + childDir;
			}
		}

		File file = new File(newPath);
		System.out.println("newJavaFilePath:" + file.getPath());
		return file;

	}

	/**
	 * 生成sql文件的路径。 
	 * 如果是maven结构，生成在 src/main/resources目录下
	 */
	private File getSqlFileDir(String childDir) {

//		String filePath = psiJavaFileImpl.getVirtualFile().getParent().getPath();
		String filePath = config.getSourceFile().getParentFile().getPath();
		//生成在 src/main/resources目录下
		int p = filePath.indexOf("src/main");
		String newPath = "";
		if (p > 0) {
			newPath = filePath.substring(0, p+8);
			newPath = newPath + File.separator+"resources";
		}else {
			p = filePath.indexOf("src");
			if (p > 0) {
				newPath = filePath.substring(0, p+3);
				newPath = newPath +File.separator+"main"+ File.separator+"resources";
			}else {
				//在原来的路径里面去掉metadata
				p = filePath.indexOf("metadata");
				if (p > 0) {
					newPath = filePath.substring(0, p);
					newPath = newPath + filePath.substring(p + 9);
				}
				
			}
		}
		
		if (childDir != null && childDir.trim().length() > 0) {
			newPath = newPath + File.separator + childDir;
		}

		File file = new File(newPath);
		System.out.println("newJavaFilePath:" + file.getPath());
		return file;

	}

	public void copyFile() {
		try {
			for (CopyTask copyTask : fileTasks) {

				copyTask.execute();
			}
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			new CodeGenException("copy code error:" + e.getMessage());

		}

	}

	public ProjectConfig getConfig() {
		return config;
	}

	public void setConfig(ProjectConfig config) {
		this.config = config;
	}
}
