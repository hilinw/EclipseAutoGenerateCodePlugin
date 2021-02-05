package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Column;
import www.autogeneratecode.model.Comment;
import www.autogeneratecode.model.Table;

public class DDLGenerator {

	protected Class<?> javaClass;

	protected boolean hasPk = false;
	protected String pkColName = "";

	public DDLGenerator(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	public final void generate() {
		try {

			this.parsingClass();
		} catch (CodeGenException cge) {
			throw cge;
		} catch (Exception e) {
			throw new CodeGenException(e.getMessage(), e);
		}
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	/**
	 * 
	 */
	protected void parsingClass() {

	}

	public File write(File dir) throws IOException {

		dir.mkdirs();

		File file = new File(dir, "Create-"+javaClass.getSimpleName() + ".sql");
		file = IOUtils.write(file, getDDL());
		System.out.println("Generate DDL file path:" + file.getPath());
		return file;
	}

	public String getDDL() throws CodeGenException {

		StringBuilder sb = new StringBuilder();
		String tableName = getTableName();
		sb.append("CREATE TABLE ");
		sb.append(tableName);
		sb.append("\n");
		sb.append("(");

		sb.append("\n");
		sb.append(generateFields());

		// file last );
		sb.append("\n");
		sb.append(");");

		// 如果有ID字段，增加PK
		if (hasPk) {

			sb.append("\n");
			sb.append("ADD CONSTRAINT PK_");
			sb.append(tableName);
			sb.append(" PRIMARY KEY (");
			sb.append(pkColName);
			sb.append(");");
			sb.append("\n");

		}
		return sb.toString();
	}

	private String getTableName() {
//        PsiAnnotation psiAnnotation = psiClass.getAnnotation("www.autogeneratecode.model.Table");

		Table table = javaClass.getAnnotation(Table.class);
		if(table == null) {
			throw new RuntimeException("Generate sqlmapping error, The source file no annotation of 'Table' ");
		}
//        String tableName = getAnnotateText(psiAnnotation, "name");
		String tableName = table.name();

		return tableName;
	}

	private String generateFields() throws CodeGenException {

		StringBuilder sb = new StringBuilder();
//        PsiField[] psiAllFields = psiClass.getAllFields();
//        PsiAnnotation psiAnnotation = null;
		Field[] fields = javaClass.getFields();
		Column column = null;
		Comment comment = null;
		String fieldName = "";
		String colName = "";
		String dataType = "";
		int length = 0;
//        int precision = 0;
//        int scale = 0;
		boolean nullable = true;

		for (Field field : fields) {
			fieldName = field.getName();
			column = field.getAnnotation(Column.class);
			comment = field.getAnnotation(Comment.class);

			if (column == null) {
				continue;
			}


			colName = column.name();
			dataType = column.dataType();
			length = column.length();
//            precision = annotation.precision();
//            scale = annotation.scale();

			if (StringUtils.isBlank(colName)) {
				throw new CodeGenException("属性[" + fieldName + "]的数据库表字段名称(name)不能为空");
			}
			if (StringUtils.isBlank(dataType)) {
				throw new CodeGenException("属性[" + fieldName + "]的数据库表字段类型(dataType)不能为空");
			}
			nullable = column.nullable();

			if ("id".equalsIgnoreCase(fieldName)) {
				this.hasPk = true;
				this.pkColName = colName;
			}

			if (sb.length() > 0) {
				sb.append(",\n");
			}

			sb.append("\t");
			sb.append(colName).append(" ").append(dataType);
			if ("varchar".equalsIgnoreCase(dataType)) {
				if (length > 0) {
					sb.append("(").append(length).append(")");
				}
				if (!nullable) {
					sb.append(" NOT NULL");
				}
			} else if("int".equalsIgnoreCase(dataType) || "decimal".equalsIgnoreCase(dataType)){
					sb.append(" default 0");
			} else {
				if (!nullable) {
					sb.append(" NOT NULL");
				}
			}

			if (comment != null) {
				sb.append(" COMMENT '" + comment.content() +"'");

			}
		}

		return sb.toString();

	}

}
