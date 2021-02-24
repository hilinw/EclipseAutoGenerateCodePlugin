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
//		String servoceName = sourceName +"Service";
		//service接口类名称
		String varServiceClassName = "";
		//service接口实列名称
		String varServiceName = "";
		
		if(this.isNoInterface()) {
			varServiceClassName = javaClass.getSimpleName() +"Service";
			varServiceName = varSourceName +"Service";	
		}else {
			varServiceClassName = "I"+javaClass.getSimpleName() +"Service";
			varServiceName = "i"+javaClass.getSimpleName() +"Service";
		}
		
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
//		sb.append("import java.util.HashMap;\n");
		sb.append("import java.util.List;\n");
		sb.append("import java.util.Map;\n");
		
		sb.append("\n");
		sb.append("import javax.annotation.Resource;\n");
//		sb.append("import javax.servlet.ServletException;\n");
//		sb.append("import javax.servlet.http.HttpServletRequest;\n");
//		sb.append("import javax.servlet.http.HttpServletResponse;\n");
		sb.append("\n");
		sb.append("import org.springframework.stereotype.Controller;\n");
		sb.append("import org.springframework.web.bind.annotation.RequestBody;\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
		sb.append("import org.springframework.web.bind.annotation.ResponseBody;\n");
		
		//sb.append(this.generateImports());
		if(!isSameDir()) {
			sb.append("\n");
			sb.append("import ");
			sb.append(packageName);
			sb.append(".vo.");
			sb.append(voName).append(";\n");
			sb.append("import ");
			sb.append(packageName);
			sb.append(".service.");
			sb.append(varServiceClassName).append(";\n");
		}
		
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
		sb.append(" {");
		sb.append("\n");
		
		sb.append("\n");
		sb.append("\t@Resource\n");
		sb.append("\tprivate ");
		sb.append(varServiceClassName).append(" ").append(varServiceName).append(";");
		
		sb.append("\n");
		
		sb.append(getAddMethod(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getUpdateMethod(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getDeleteById(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getDeleteByIds(voName, varVoName,varServiceName));
		sb.append("\n");
		sb.append(getQueryById(voName, varVoName,varServiceName, true));
		sb.append("\n");
		sb.append(getQueryListByIds(voName, varVoName,varServiceName, true));
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
			//sb.append("@RequestMapping(value = \"/add\",produces=\"text/html;charset=UTF-8\")");
			sb.append("@RequestMapping(value = \"/add\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");
		}
		sb.append("\n\t");
		sb.append("public void add(@RequestBody ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(") throws Exception ");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
//			sb.append("try {");
//			sb.append("\n\t\t\t");
			sb.append(varServiceName);
			sb.append(".add(");
			sb.append(varName);
			sb.append(");");
//			sb.append("\n\t\t");
//			sb.append("} catch (Exception e) {");
//			sb.append("\n\t\t\t");
//			sb.append("e.printStackTrace();");
//			sb.append("\n\t\t}");
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
			//sb.append("@RequestMapping(value = \"/update\",produces=\"text/html;charset=UTF-8\")");
			sb.append("@RequestMapping(value = \"/update\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");			
		}
		sb.append("\n\t");
		//sb.append("public void update(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("public void update(@RequestBody ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
		sb.append(") throws Exception ");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
//			sb.append("try {");
//			sb.append("\n\t\t\t");
			sb.append(varServiceName);
			sb.append(".update(");
			sb.append(varName);
			sb.append(");");
//			sb.append("\n\t\t");
//			sb.append("} catch (Exception e) {");
//			sb.append("\n\t\t\t");
//			sb.append("e.printStackTrace();");
//			sb.append("\n\t\t}");
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
			//sb.append("@RequestMapping(value = \"/deleteById\",produces=\"text/html;charset=UTF-8\")");
			sb.append("@RequestMapping(value = \"/deleteById\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");			
		}
		sb.append("\n\t");
		//sb.append("public void deleteById(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("public void deleteById(@RequestBody ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
//		sb.append("String id");
		sb.append(") throws Exception ");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
//			sb.append("try {");
//			sb.append("\n\t\t\t");
			sb.append(varServiceName);
			sb.append(".deleteById(");
			sb.append(varName).append(".getId()");
//			sb.append("id");
			sb.append(");");
//			sb.append("\n\t\t");
//			sb.append("} catch (Exception e) {");
//			sb.append("\n\t\t\t");
//			sb.append("e.printStackTrace();");
//			sb.append("\n\t\t}");
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
	 * @param varServiceName
	 * @return
	 * 
	 * 	@RequestMapping(value = "/deleteByIds")
		@ResponseBody
		public void deleteByIds(@RequestBody List<String> idSet) throws Exception {
			XxxxService.deleteByIds(idSet);
		}
	 */

	private String getDeleteByIds(String voName,String varName,String varServiceName) {

		StringBuilder sb = new StringBuilder();

		sb.append("\n\t");
		//sb.append("@RequestMapping(value = \"/deleteById\",produces=\"text/html;charset=UTF-8\")");
		sb.append("@RequestMapping(value = \"/deleteByIds\")");
		sb.append("\n\t");
		sb.append("@ResponseBody");			

		sb.append("\n\t");
		//sb.append("public void deleteById(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("public void deleteByIds(@RequestBody List<String> idSet) throws Exception {");
		sb.append("\n\t\t");
		sb.append(varServiceName);
		sb.append(".deleteByIds(idSet);");
		sb.append("\n\t}");
		
		return sb.toString();
		
	}
	
	private String getQueryById(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			//sb.append("@RequestMapping(value = \"/queryById\",produces=\"text/html;charset=UTF-8\")");
			sb.append("@RequestMapping(value = \"/queryById\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");
		}
		sb.append("\n\t");
		sb.append("public ").append(voName);
		sb.append(" ");
		//sb.append("queryById(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("queryById(@RequestBody ");
		sb.append(voName);
		sb.append(" ");
		sb.append(varName);
//		sb.append("String id");
		sb.append(") throws Exception ");
		
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("return ");
			sb.append(varServiceName);
			sb.append(".queryById(");
			sb.append(varName).append(".getId()");
			//sb.append("id");
			sb.append(");");
			sb.append("\n\t}");
		}else {
			
			sb.append(";");
			return sb.toString();
		}
		
		return sb.toString();
	}	
	
	/**
	 * 	@RequestMapping(value = "/queryListByIds")
		@ResponseBody
		public List<NewUserVO> queryListByIds(@RequestBody List<String> idSet) throws Exception {
			return XxxxService.queryListByIds(idSet);
		}
	 * @param voName
	 * @param varName
	 * @param varServiceName
	 * @param isImpl
	 * @return
	 */
	private String getQueryListByIds(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			sb.append("@RequestMapping(value = \"/queryListByIds\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> ");
		sb.append("queryListByIds(@RequestBody List<String> idSet) throws Exception ");
		
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("return ");
			sb.append(varServiceName);
			sb.append(".queryListByIds(idSet);");
			sb.append("\n\t}");
		}else {
			sb.append(";");
		}
		
		return sb.toString();
	}	
	
	
	private String getQueryListMethod(String voName,String varName,String varServiceName,boolean isImpl) {

		StringBuilder sb = new StringBuilder();
		if(isImpl) {
			sb.append("\n\t");
			//sb.append("@RequestMapping(value = \"/queryList\",produces=\"text/html;charset=UTF-8\")");
			sb.append("@RequestMapping(value = \"/queryList\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");
		}
		sb.append("\n\t");
		sb.append("public List<").append(voName);
		sb.append("> ");
		//sb.append("queryList(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("queryList(@RequestBody Map<String, Object> params");
		sb.append(") throws Exception ");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("List<").append(voName).append("> ");
			sb.append(varName).append("s = null;");
			sb.append("\n\t\t");
			sb.append("int offset = 0;");
			sb.append("\n\t\t");
			sb.append("int limit = Integer.MAX_VALUE;");
			sb.append("\n\t\t");
			sb.append("Object soffset = params.get(\"offset\");");
			sb.append("\n\t\t");
			sb.append("Object slimit = params.get(\"limit\");");
			sb.append("\n\n\t\t");
			sb.append("if (soffset != null && soffset.toString().length() > 0) {");
			sb.append("\n\t\t\t");
			sb.append("offset = Integer.parseInt(soffset.toString());");
			sb.append("\n\t\t");
			sb.append("}");
			sb.append("\n\t\t");
			sb.append("if (slimit != null && slimit.toString().length() > 0) {");
			sb.append("\n\t\t\t");
			sb.append("limit = Integer.parseInt(slimit.toString());");
			sb.append("\n\t\t");
			sb.append("}");
			sb.append("\n\t\t");
			sb.append("params.put(\"offset\", offset);");
			sb.append("\n\t\t");
			sb.append("params.put(\"limit\", limit);");

			
			sb.append("\n\n\t\t");
//			sb.append("try {");
//			sb.append("\n\t\t\t");
			sb.append(varName).append("s = ");
			sb.append(varServiceName);
			sb.append(".queryList(");
//			sb.append(varName);
			sb.append("params");
			sb.append(");");
//			sb.append("\n\t\t");
//			sb.append("} catch (Exception e) {");
//			sb.append("\n\t\t\t");
//			sb.append("e.printStackTrace();");
//			sb.append("\n\t\t}");
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
			//sb.append("@RequestMapping(value = \"/queryListCount\",produces=\"text/html;charset=UTF-8\")");
			sb.append("@RequestMapping(value = \"/queryListCount\")");
			sb.append("\n\t");
			sb.append("@ResponseBody");
		}
		sb.append("\n\t");
		//sb.append("public int queryListCount(HttpServletRequest request, HttpServletResponse response, ");
		sb.append("public int queryListCount(@RequestBody Map<String, Object> params");
		sb.append(") throws Exception ");
		if(isImpl) {
			sb.append("{");
			sb.append("\n\t\t");
			sb.append("int count = ");
			sb.append(varServiceName);
			sb.append(".queryListCount(");
			sb.append("params");
			sb.append(");");
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
		sb.append("\n\t *");
		sb.append("\n\t * @param parameterObject 参数表");
		sb.append("\n\t * @param ");
		sb.append(varName);
		sb.append("\n\t */");
		
		sb.append("\n\t");
		sb.append("public void setParameterMap(Map<String, Object> parameterObject, ");
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
			sb.append("\", ");
			sb.append(varName);
			if (fieldType.equalsIgnoreCase("boolean")) {
				sb.append(".is");
			} else {
				sb.append(".get");
			}
			sb.append(fieldName);
			sb.append("()); // ");
			sb.append(comment.content());
		}
		sb.append("\n\t");
		sb.append("}");
		return sb.toString();
	}
}
