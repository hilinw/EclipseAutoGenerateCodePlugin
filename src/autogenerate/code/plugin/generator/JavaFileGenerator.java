package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import www.autogeneratecode.model.Comment;

public abstract class JavaFileGenerator {

	protected String packageName = "";
	private boolean isSameDir = false;
	private boolean addTransactional = true;
	private boolean isNoInterface = true;
	
	// 源文件的导入部分
	protected List<String> sourceFileImports = new ArrayList<String>();
	// 生成文件的导入部分
	protected List<String> imports = new ArrayList<String>();
	protected Class<?> javaClass;
	private String extClass;

	protected List<String> ignoreImports = new ArrayList<String>();

	public JavaFileGenerator(Class<?> javaClass, String packagename) {

		this.javaClass = javaClass;
		if (packagename != null && packagename.startsWith("metadata.")) {
			packageName = packagename.substring(9);
		} else {
			packageName = packagename;
		}

		ignoreImports.add("www.autogeneratecode.model.Column");
		ignoreImports.add("www.autogeneratecode.model.Comment");
		ignoreImports.add("www.autogeneratecode.model.Entity");
		ignoreImports.add("www.autogeneratecode.model.Table");
	}

	public final void generate() {
		try {
			this.beforeGenerate();
			this.generateJavaFile();
			this.afterGenerate();
		} catch (CodeGenException cge) {
			throw cge;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CodeGenException(e.getMessage(), e);
		}
	}

	public abstract File write(File file) throws IOException;

	protected abstract void generateJavaFile();

	protected void beforeGenerate() throws Exception {
		parsingImports();

	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	public List<String> getSourceFileImports() {
		return sourceFileImports;
	}

	public void setSourceFileImports(List<String> sourceFileImports) {
		this.sourceFileImports = sourceFileImports;
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

	/**
	 * get all imports
	 */
	protected void parsingImports() {
		if (javaClass != null) {
			for (String importString : sourceFileImports) {
				if (!ignoreImports.contains(importString)) {
					imports.add(importString);
				}
			}
		}
	}

	protected void afterGenerate() throws Exception {
	}

	protected String generateImports() {
		StringBuilder sb = new StringBuilder();

		for (String importString : imports) {
			sb.append("import ").append(importString).append(";");
			sb.append("\n");
		}

		return sb.toString();
	}

	protected String getFields() {

		Field[] fields = javaClass.getFields();

		Class<?> superClass = javaClass.getSuperclass();
		Field[] superfields = superClass.getFields();

		StringBuilder sb = new StringBuilder();

		for (Field field : fields) {
			if (isSupperField(field, superfields)) {
				continue;
			}

			Comment comment = field.getAnnotation(Comment.class);

			sb.append("\n\t");
			sb.append("private ");
			sb.append(getPsiFieldTypeName(field));
			sb.append(" ").append(field.getName());
			sb.append("; // ");

			sb.append(comment.content());

			sb.append("\n");

		}

		return sb.toString();

	}

	protected boolean isSupperField(Field field, Field[] fields) {
		boolean isSupperField = false;
		for (Field field2 : fields) {
			if (field2.getName().equals(field.getName())) {
				return true;
			}
		}
		return isSupperField;
	}

	protected String getPsiFieldTypeName(Field field) {
		// String s = field.getAnnotation(annotationClass);

		String s = field.getType().getCanonicalName();
//		if ("java.lang.String".equals(s)) {
//			s = "String";
//		}
		int p = s.lastIndexOf(".");
		if (p > 0) {
			s = s.substring(p + 1);
		}

		return s;
	}

	protected String getCommentContent(Comment annotation, String tab, String addBeforeStr) {

		StringBuilder sb = new StringBuilder();
		sb.append("\n").append(tab);
		sb.append("/**");
		sb.append("\n").append(tab);
		sb.append(" * ");
//        sb.append(getAnnotateText(psiAnnotation, "content", addBeforeStr));
		sb.append(addBeforeStr);
		sb.append(annotation.content());
		sb.append("\n").append(tab);
		sb.append(" */");

		return sb.toString();

	}

	protected String getAnnotateText(Annotation annotation, String text, String addBeforeStr) {

		if (annotation == null) {
			return "";
		}
//        if (annotation.findAttributeValue(text) != null) {
//            String s = psiAnnotation.findAttributeValue(text).getContext().getLastChild().getText();
//            s = s.replaceAll("\"", "");
//            return addBeforeStr + s;
//        }
//    	annotation.

		return "";
	}

}
