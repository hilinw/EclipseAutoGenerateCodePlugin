package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Comment;

public class PropertiesGenerator extends JavaFileGenerator {


	public PropertiesGenerator(Class<?> javaClass, String packageName) {
		super(javaClass, packageName);

	}

	@Override
	protected void generateJavaFile() {

	}
	
	@Override
	public File write(File dir) throws IOException {
		dir.mkdirs();

		File file = new File(dir, javaClass.getSimpleName() + "VO.properties");
		file = IOUtils.write(file, getSource_zh_cn());
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	
	public File write_en(File dir) throws IOException {

		dir.mkdirs();

		File file = new File(dir, javaClass.getSimpleName() + "VO_en.properties");
		file = IOUtils.write(file, getSource_en());
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}
	
	public File write_zh_cn(File dir) throws IOException {

		dir.mkdirs();

		File file = new File(dir, javaClass.getSimpleName() + "VO_zh_CN.properties");
		file = IOUtils.write(file, getSource_zh_cn());
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	
	private String getSource_en() {
		return generateSource(true);
	}
	private String getSource_zh_cn() {
		return generateSource(false);
	}

	private String getNewTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}
	private String generateSource(boolean isEnglish) {

		StringBuilder sb = new StringBuilder();
		sb.append("### CreatTime:");
		sb.append(getNewTime());
		
		//类名，用Vo名
		String fileName = javaClass.getSimpleName()+"VO";
		Field[] fields = javaClass.getFields();
		//字段名
		String fieldName = "";
		//备注
		Comment comment = null;
		String commentName = "";
		for (Field field : fields) {
			
			fieldName = field.getName();
			sb.append("\n");
			sb.append(fileName).append(".");
			sb.append(fieldName).append("=");
			if(isEnglish) {
				sb.append(fieldName);
			}else {
				comment = field.getAnnotation(Comment.class);
				commentName = comment.content();
				sb.append(commentName);
			}
		}
		
		return sb.toString();
	}


}
