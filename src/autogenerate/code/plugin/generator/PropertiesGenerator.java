package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

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
		file = IOUtils.write(file, getSource());
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	public File write_en(File dir) throws IOException {

		dir.mkdirs();

		File file = new File(dir, javaClass.getSimpleName() + "VO_en.properties");
		FileOutputStream out = null;
		Properties resource = getSource_en();
		try {
			out = new FileOutputStream(file);
			resource.store(out, (String) null);
		} finally {
			IOUtils.close(out);
		}
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	public File write_zh_cn(File dir) throws IOException {
		dir.mkdirs();

		File file = new File(dir, javaClass.getSimpleName() + "VO_zh_CN.properties");
		FileOutputStream out = null;
		Properties resource = getSource_zh_cn();
		try {
			out = new FileOutputStream(file);
			resource.store(out, (String) null);
		} finally {
			IOUtils.close(out);
		}
		System.out.println("Generate JavaFile source path:" + file.getPath());
		return file;
	}

	private Properties getSource_en() {
		return generateSource(true);
	}

	private Properties getSource_zh_cn() {
		return generateSource(false);
	}

	private String getSource() {
		return generateSource();
	}

	private String getNewTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}

	private Properties generateSource(boolean isEnglish) {
		Properties resource = new Properties();
		// 类名，用Vo名
		String fileName = javaClass.getSimpleName() + "VO";
		Field[] fields = javaClass.getFields();
		// 字段名
		String fieldName = "";
		// 备注
		Comment comment = null;
		String commentName = "";
		for (Field field : fields) {
			fieldName = field.getName();
			if (isEnglish) {
				commentName = fieldName;
			} else {
				comment = field.getAnnotation(Comment.class);
				commentName = comment.content();
				if (StringUtils.isBlank(commentName)) {
					commentName = fieldName;
				}
//				if (isUtf8) {
//					commentName = IOUtils.stringToUnicode(commentName);
//				}
			}
			resource.put(fileName + "." + fieldName, commentName);
		}

		return resource;
	}

	private String generateSource() {

		StringBuilder sb = new StringBuilder();
		sb.append("### CreatTime:");
		sb.append(getNewTime());

		// 类名，用Vo名
		String fileName = javaClass.getSimpleName() + "VO";
		Field[] fields = javaClass.getFields();
		// 字段名
		String fieldName = "";
		// 备注
		Comment comment = null;
		String commentName = "";
		for (Field field : fields) {

			fieldName = field.getName();
			sb.append("\n");
			sb.append(fileName).append(".");
			sb.append(fieldName).append("=");

			comment = field.getAnnotation(Comment.class);
			commentName = comment.content();
			if (StringUtils.isBlank(commentName)) {
				commentName = fieldName;
			}
			sb.append(commentName);
		}

		return sb.toString();
	}

}
