package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Comment;

public class JavaControllerGenerator extends JavaFileGenerator {


	public JavaControllerGenerator(Class<?> javaClass, String packageName) {
		super(javaClass, packageName);

	}

	@Override
	protected void generateJavaFile() {

	}

	public File write(File dir) throws IOException {

		dir.mkdirs();
	
		File file = new File(dir, javaClass.getSimpleName() +"Controller"+ ".java");
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
		String servoceName = sourceName +"Service";
		String varServiceName = varSourceName +"Service"; 
		
		// 包名
		sb.append("package ");
		sb.append(packageName);
		if(isSameDir()) {
			sb.append(";");
		}else {
			sb.append(".controller;");
		}
		sb.append("\n\n");

		// 导入import
		sb.append("import java.util.HashMap;\n");
		sb.append("import java.util.List;\n");
		sb.append("import java.util.Map;\n");
		
		sb.append("\n");
		sb.append("import javax.annotation.Resource;\n");
//		sb.append("import javax.servlet.ServletException;\n");
		sb.append("import javax.servlet.http.HttpServletRequest;\n");
		sb.append("import javax.servlet.http.HttpServletResponse;\n");
		sb.append("\n");
		sb.append("import org.springframework.stereotype.Controller;\n");
//		sb.append("import org.springframework.web.bind.annotation.RequestBody;\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
//		sb.append("import org.springframework.web.bind.annotation.ResponseBody;\n");
		
		//sb.append(this.generateImports());
		sb.append("\n");
		sb.append("import ");
		sb.append(packageName);
		if(isSameDir()) {
			sb.append(".");
		}else {
			sb.append(".vo.");
		}
		
		sb.append(voName).append(";\n");
		sb.append("import ");
		sb.append(packageName);
		if(isSameDir()) {
			sb.append(".");
		}else {
			sb.append(".service.");
		}
		sb.append(sourceName).append("Service").append(";\n");
				
		
		Comment comment = javaClass.getAnnotation(Comment.class);
		sb.append(getCommentContent(comment, "", "Controller of "));
		
		
		sb.append("\n");
		sb.append("@Controller");
		sb.append("\n");
		sb.append("@RequestMapping(\"");
		sb.append(varSourceName);
		sb.append("\")");
		sb.append("\n");

		sb.append("public class ").append(sourceName).append("Controller");
//		sb.append("implements ").append(sourceName).append("Service");
		sb.append("{");
		sb.append("\n");
		
		sb.append("\n");
		sb.append("\t@Resource\n");
		sb.append("\tprivate ");
		sb.append(servoceName).append(" ").append(varServiceName).append(";");
		
		sb.append("\n");
		
		sb.append(getAddMethod(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getUpdateMethod(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getDeleteById(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getQueryById(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getQueryListById(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getQueryListMethod(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getQueryListCountMethod(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getSetParameterMap(voName, varVoName));
		sb.append("\n");


		// file last '}'
		sb.append("\n");
		sb.append("}");

		return sb.toString();
	}


	private String getAddMethod(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/add\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public void add(HttpServletRequest request, HttpServletResponse response, ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append(varServiceName);
			sb.append(".add(");
			sb.append(varName);
			sb.append(");");
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}	
	
	private String getUpdateMethod(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/update\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public void update(HttpServletRequest request, HttpServletResponse response, ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append(varServiceName);
			sb.append(".update(");
			sb.append(varName);
			sb.append(");");
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	private String getDeleteById(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/deleteById\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public void deleteById(HttpServletRequest request, HttpServletResponse response, ");
//		sb.append(voName);
//		sb.append(" ");
//		sb.append(varName);
		sb.append("String id");
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append(varServiceName);
			sb.append(".deleteById(");
//			sb.append(varName);
			sb.append("id");
			sb.append(");");
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
		
	}
	
	
	private String getQueryById(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/queryById\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> ");
		sb.append("queryById(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("String id");
		sb.append(")");
		
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("List<").append(voName).append("> ");
			sb.append(varName).append("s = null;");
//			sb.append("\n\t\t");
//			sb.append("Map<String, Object> parameterObject = new HashMap<String, Object>();");
//			sb.append("\n\t\t");
//			sb.append("setParameterMap(parameterObject, ");
//			sb.append(varName);
//			sb.append(");");
			
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append(varName).append("s = ");
			sb.append(varServiceName);
			
			sb.append(".queryById(");
			sb.append("id");
			sb.append(");");
			
			
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t\t");
			sb.append("return ");
			sb.append(varName).append("s;");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}	
	
	private String getQueryListById(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/queryListById\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> ");
		sb.append("queryListById(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("List<String> idSet");
		sb.append(")");
		
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("List<").append(voName).append("> ");
			sb.append(varName).append("s = null;");
//			sb.append("\n\t\t");
//			sb.append("Map<String, Object> parameterObject = new HashMap<String, Object>();");
//			sb.append("\n\t\t");
//			sb.append("setParameterMap(parameterObject, ");
//			sb.append(varName);
//			sb.append(");");
			
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append(varName).append("s = ");
			sb.append(varServiceName);
			
			sb.append(".queryListById(");
			sb.append("idSet");
			sb.append(");");
			
			
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t\t");
			sb.append("return ");
			sb.append(varName).append("s;");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}	
	
	private String getQueryListMethod(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/queryList\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> ");
		sb.append("queryList(HttpServletRequest request, HttpServletResponse response, ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("List<").append(voName).append("> ");
			sb.append(varName).append("s = null;");
			sb.append("\n\t\t");
			sb.append("Map<String, Object> parameterObject = new HashMap<String, Object>();");
			sb.append("\n\t\t");
			sb.append("setParameterMap(parameterObject, ");
			sb.append(varName);
			sb.append(");");
			
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append(varName).append("s = ");
			sb.append(varServiceName);
			sb.append(".queryList(");
//			sb.append(varName);
			sb.append("parameterObject");
			sb.append(");");
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t\t");
			sb.append("return ");
			sb.append(varName).append("s;");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}
	
	private String getQueryListCountMethod(String voName,String varName,String varServiceName,boolean isImpl) {
		
		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value=\"/queryListCount\",produces=\"text/html;charset=UTF-8\")");
		}
		sb.append("\n\t");
		sb.append("public int queryListCount(HttpServletRequest request, HttpServletResponse response, ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(")");
		if(isImpl) {
			sb.append("{");
			
			sb.append("\n\t\t");
			sb.append("Map<String, Object> parameterObject = new HashMap<String, Object>();");
			sb.append("\n\t\t");
			sb.append("setParameterMap(parameterObject, ");
			sb.append(varName);
			sb.append(");");
			
			sb.append("\n\t\t");
			sb.append("int count = 0;");
			
			sb.append("\n\t\t");
			sb.append("try {");
			sb.append("\n\t\t\t");
			sb.append("count = ");
			sb.append(varServiceName);
			sb.append(".queryListCount(");
//			sb.append(varName);
			sb.append("parameterObject");
			sb.append(");");
			sb.append("\n\t\t");
			sb.append("} catch (Exception e) {");
			sb.append("\n\t\t\t");
			sb.append("e.printStackTrace();");
			sb.append("\n\t\t}");
			sb.append("\n\t\t");
			sb.append("return count;");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}
	

	private String getSetParameterMap(String voName,String varName) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t");
		sb.append("/**");
		sb.append("\n\t*");
		sb.append("\n\t*@param parameterObject 参数表");
		sb.append("\n\t*@param ");
		sb.append(varName);
		sb.append("\n\t*/");
		
		sb.append("\n\t");
		sb.append("public void setParameterMap(Map<String, Object> parameterObject,");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(") {");
		
		Field[] fields = javaClass.getFields();
		String fieldName = "";
		String soruceFieldName = "";
		String fieldType = "";
		Comment comment = null;
		int i = 0;
		for (Field field : fields) {
			
			if(i ==5) {
				sb.append("\n");
				i = 0;
			}{
				i++;
			}
			comment = field.getAnnotation(Comment.class);
			fieldType = getPsiFieldTypeName(field);
			soruceFieldName = field.getName();
			fieldName = field.getName();
			//getName = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
			
			if (fieldType.equalsIgnoreCase("boolean") && fieldName.startsWith("is")) {
				fieldName = fieldName.substring(2);
			}
			fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			
			sb.append("\n\t\t");
			sb.append("parameterObject.put(\"");
			sb.append(soruceFieldName);
			sb.append("\",");
			sb.append(varName);
			if (fieldType.equalsIgnoreCase("boolean")) {
				sb.append(".is");
			} else {
				sb.append(".get");
			}
			sb.append(fieldName);
			sb.append("()); //");
			sb.append(comment.content());
		}
		sb.append("\n\t");
		sb.append("}");
		return sb.toString();
	}
}
