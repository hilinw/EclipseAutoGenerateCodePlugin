package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Comment;

public class JavaEntityGenerator extends JavaFileGenerator {

	private boolean generateGetterAndSetter = true;

	public JavaEntityGenerator(Class<?> javaClass, String packageName) {
		super(javaClass, packageName);

	}

	@Override
	protected void generateJavaFile() {

	}

	public File write(File dir) throws IOException {

		dir.mkdirs();

		File file = new File(dir, javaClass.getSimpleName() + "VO" + ".java");
		file = IOUtils.write(file, getSource());
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	public String getSource() {

		StringBuilder sb = new StringBuilder();
		// 包名
		sb.append("package ");
		sb.append(packageName);
		if (isSameDir()) {
			sb.append(";");
		} else {
			sb.append(".vo;");
		}

		sb.append("\n\n");

		// 导入import
		sb.append("import java.io.Serializable;");
		sb.append("\n");
		sb.append(this.generateImports());

		Comment comment = javaClass.getAnnotation(Comment.class);
		sb.append(getCommentContent(comment, "", ""));

		sb.append("\n");
		sb.append("public class ").append(javaClass.getSimpleName()).append("VO");
		if (this.getExtClass() != null && this.getExtClass().trim().length() > 0) {
			sb.append(" extends ").append(this.getExtClass().trim()).append("VO");
		}
		sb.append(" implements Serializable {");
		sb.append("\n");
		sb.append("\n");
		sb.append("\t");
		sb.append("private static final long serialVersionUID = 1L;");

		sb.append("\n");
		sb.append(getFields());

		sb.append("\n\t");
		sb.append("public ").append(javaClass.getSimpleName()).append("VO").append("() {");
		sb.append("\n\t");
		sb.append("}");

		if (isGenerateGetterAndSetter()) {
			sb.append(generateGetterAndSetter());
		}

		// file last '}'
		sb.append("\n");
		sb.append("\n}");

		return sb.toString();
	}

	private String generateGetterAndSetter() {

		StringBuilder sb = new StringBuilder();
		Field[] fields = javaClass.getFields();
		Class<?> superClass = javaClass.getSuperclass();
		Field[] superfields = superClass.getFields();

		Comment comment = null;
		String fieldType = "";
		String soruceFieldName = "";
		String fieldName = "";
		for (Field field : fields) {

			if (isSupperField(field, superfields)) {
				continue;
			}

			fieldType = getPsiFieldTypeName(field);
			soruceFieldName = field.getName();
			fieldName = soruceFieldName;
			if (fieldType.equalsIgnoreCase("boolean") && fieldName.startsWith("is")) {
				fieldName = fieldName.substring(2);
			}
			fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

			comment = field.getAnnotation(Comment.class);

			// get
			sb.append("\n");
			sb.append(getCommentContent(comment, "\t", "取"));
			sb.append("\n\t");
			sb.append("public ");
			sb.append(fieldType);
			if (fieldType.equalsIgnoreCase("boolean")) {
				sb.append(" is");
			} else {
				sb.append(" get");
			}
			sb.append(fieldName).append("() {");
			sb.append("\n\t\t");
			sb.append("return ").append(soruceFieldName).append(";");
			sb.append("\n\t}");

			// set
			sb.append("\n");
			sb.append(getCommentContent(comment, "\t", "设置"));
			sb.append("\n\t");
			sb.append("public void set");

			sb.append(fieldName).append("(");

			sb.append(fieldType).append(" ").append(soruceFieldName).append(") {");

			sb.append("\n\t\t");
			sb.append("this.").append(soruceFieldName).append(" = ").append(soruceFieldName).append(";");
			sb.append("\n\t}");

		}

		return sb.toString();

	}

	public boolean isGenerateGetterAndSetter() {
		return generateGetterAndSetter;
	}

	public void setGenerateGetterAndSetter(boolean generateGetterAndSetter) {
		this.generateGetterAndSetter = generateGetterAndSetter;
	}
}
