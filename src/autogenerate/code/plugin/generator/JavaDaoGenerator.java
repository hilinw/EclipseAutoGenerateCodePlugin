package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;

import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Comment;

public class JavaDaoGenerator extends JavaFileGenerator {


	public JavaDaoGenerator(Class<?> javaClass, String packageName) {
		super(javaClass, packageName);

	}

	@Override
	protected void generateJavaFile() {

	}

	public File write(File dir) throws IOException {

		dir.mkdirs();
	
		File file = new File(dir, javaClass.getSimpleName() +"Dao"+ ".java");
		file = IOUtils.write(file, getSource());
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	public String getSource() {

		StringBuilder sb = new StringBuilder();
		String sourceName = javaClass.getSimpleName();
		String varSourceName = sourceName.substring(0,1).toLowerCase()+sourceName.substring(1); 
		String voName = sourceName +"VO";
		String varVoName = varSourceName +"VO"; 
//		String daoName = sourceName +"Dao";
		String varDaoName = varSourceName +"Dao"; 
		// 包名
		sb.append("package ");
		sb.append(packageName);
		if(isSameDir()) {
			sb.append(";");
		}else {
			sb.append(".dao;");
		}
		
		sb.append("\n\n");

		// 导入import
		sb.append("import java.util.List;\n");
		sb.append("import java.util.Map;\n");
		//sb.append(this.generateImports());
		
		if(!isSameDir()) {
			sb.append("\n");
			sb.append("import ");
			sb.append(packageName);
			sb.append(".vo.");
			sb.append(voName).append(";\n");
		}

		Comment comment = javaClass.getAnnotation(Comment.class);
		sb.append(getCommentContent(comment, "", "Dao of "));

		sb.append("\n");
		sb.append("public interface ").append(javaClass.getSimpleName()).append("Dao").append(" {");
		sb.append("\n");
		sb.append(getAddMethod(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getUpdateMethod(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getDeleteById(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getDeleteByIds(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getQueryById(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getQueryListByIds(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getQueryListMethod(voName, varVoName,varDaoName, false));
		sb.append("\n");
		sb.append(getQueryListCountMethod(voName, varVoName,varDaoName, false));
		sb.append("\n");


		// file last '}'
		sb.append("\n");
		sb.append("}");

		return sb.toString();
	}


	private String getAddMethod(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("void add(").append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".add(");
			sb.append(varName);
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}	
	
	private String getUpdateMethod(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("void update(").append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".update(");
			sb.append(varName);
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	private String getDeleteById(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("void deleteById(");
//		sb.append(voName);
//		sb.append(" ");
//		sb.append(varName);
		sb.append("long id");
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".deleteById(");
//			sb.append(varName);
			sb.append("id");
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	/**
	 * 
	 * @param voName
	 * @param varName
	 * @param varDaoName
	 * @param isImpl
	 * @return
	 * 
	 * void deleteByIds(Map<String, Object> parameterObject);
	 */
	private String getDeleteByIds(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("void deleteByIds(Map<String, Object> parameterObject)");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".deleteByIds(idSet);");
			sb.append("\n\t}");
		}else {
			sb.append(";");
		}
		
		return sb.toString();
		
	}

	private String getQueryById(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("public ").append(voName);
		sb.append(" queryById(");
		sb.append("long id");
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".queryById(");
			sb.append("id");
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	private String getQueryListByIds(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> queryListByIds(");
		sb.append("List<String> idSet");
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".queryListByIds(");
			sb.append("idSet");
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	private String getQueryListMethod(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> queryList(");
//		sb.append(voName);
//		sb.append(" ");
//		sb.append(varName);
		sb.append("Map<String, Object> parameterObject");
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".queryList(");
//			sb.append(varName);
			sb.append("parameterObject");
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	private String getQueryListCountMethod(String voName,String varName,String varDaoName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@Override");
		}
		sb.append("\n\t");
		sb.append("public int queryListCount(");
//		sb.append(voName);
//		sb.append(" ");
//		sb.append(varName);
		sb.append("Map<String, Object> parameterObject");
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append(varDaoName);
			sb.append(".queryListCount(");
//			sb.append(varName);
			sb.append("parameterObject");
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}	
}
