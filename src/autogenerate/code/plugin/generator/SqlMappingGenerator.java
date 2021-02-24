package autogenerate.code.plugin.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import autogenerate.code.plugin.tools.IOUtils;
import www.autogeneratecode.model.Column;
import www.autogeneratecode.model.Table;

public class SqlMappingGenerator {
	protected String packageName = "";
	protected Class<?> javaClass;
	private boolean isSameDir = false;
	
	protected boolean hasPk = false;
	protected String pkColName = "";
	
	public SqlMappingGenerator(Class<?> javaClass, String packagename) {
		this.javaClass = javaClass;
		if (packagename != null && packagename.startsWith("metadata.")) {
			packageName = packagename.substring(9);
		} else {
			packageName = packagename;
		}

	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	public boolean isSameDir() {
		return isSameDir;
	}

	public void setSameDir(boolean isSameDir) {
		this.isSameDir = isSameDir;
	}

	public File write(File dir) throws IOException {

		dir.mkdirs();
		this.genPkColName();
		File file = new File(dir, "sqlmapping-" + javaClass.getSimpleName() + ".xml");
		file = IOUtils.write(file, getSource());
		System.out.println("Generate sqlmapping source path:" + file.getPath());
		return file;
	}

	public String getSource() {

		StringBuilder sb = new StringBuilder();
		String sourceName = javaClass.getSimpleName();
		String voName = "";
		String daoName = "";
		if(isSameDir()) {
			voName = packageName + "." + sourceName + "VO";
			daoName = packageName + "." + sourceName + "Dao";
		}else {
			voName = packageName + ".vo." + sourceName + "VO";
			daoName = packageName + ".dao." + sourceName + "Dao";
		}

		Table table = javaClass.getAnnotation(Table.class);
		if(table == null) {
			throw new RuntimeException("Generate sqlmapping error, The source file no annotation of 'Table' ");
		}
		String tableName = table.name();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		sb.append("<!DOCTYPE mapper\n");
		sb.append("PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n");
		sb.append("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		sb.append("<mapper namespace=\"");
		sb.append(daoName);
		sb.append("\">\n");
		
		sb.append("\n");
		sb.append(getResultMap(tableName, voName, daoName));

		sb.append("\n\n");
		sb.append(getAddMethod(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getUpdateMethod(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getDeleteById(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getDeleteByIds(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getQueryById(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getQueryListByIds(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getQueryListMethod(tableName, voName, daoName));
		sb.append("\n\n");
		sb.append(getQueryListCountMethod(tableName, voName, daoName));
		sb.append("\n");

		// file last 'mapper'
		sb.append("\n");
		sb.append("</mapper>");

		return sb.toString();
	}

	/**
	 * 
 <resultMap id="resultMap" type="com.uniprint.template.service.UserVO">
    <id column="FcoId" property="coId" />
	<result column="FUSERID" property="id" />
	<result column="FNAME" property="name" />
	<result column="fnumber" property="number" />
	<result column="fmobile" property="mobile" />
	<result column="faddress" property="address" />
	<result column="femployeeId" property="employeeId" />
	<result column="fisAdmin" property="isAdmin" />
	<result column="fisSuperAdmin" property="isSuperAdmin" />
	<result column="fsex" property="sex" />
	<result column="fcreateTime" property="createTime" />
	<result column="fmodifytime" property="modifytime" />
	<result column="fisDelete" property="isDelete" />
	<result column="fremark" property="remark" />
  </resultMap>
	 * 
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getResultMap(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("<resultMap id=\"resultMap\" type=\"");
		sb.append(voName);
		sb.append("\">");
		sb.append("\n\t");
		sb.append("<id column=\"");
		if(hasPk) {
			//主键名称不一定是fid
			sb.append(this.pkColName).append("\" property=\"id\" />");
		}else {
			sb.append("<id column=\"id\" property=\"id\" />");
		}
		
		Field[] fields = javaClass.getFields();
		Column column = null;
		for (Field field : fields) {
			if (field.getName().equalsIgnoreCase("id")) {
				continue;
			}

			column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			sb.append("\n\t");
//			<result column="FUSERID" property="id" />
			sb.append("<result column=\"").append(column.name());
			sb.append("\"");
			sb.append(" property=\"").append(field.getName());
			sb.append("\" />");
		}
		
		sb.append("\n");
		sb.append("</resultMap>");
		return sb.toString();
	}
	
	/**
	 * <insert id="add" parameterType="xxx.vo.XxxxxVo"> INSERT INTO tableName
	 * (column1,column2,...)VALUES(#{field1}#,#{field2},...) </insert>
	 * 
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getAddMethod(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t<insert id=\"add\" ");
		sb.append("parameterType=\"");
		sb.append(voName);
		sb.append("\">");
		sb.append("\n\t\t");
		sb.append("INSERT INTO ");
		sb.append(tableName);
		sb.append(" (");
		sb.append(getAddFields());
		sb.append(")");
		sb.append("\n\t\t");
		sb.append("VALUES (");
		sb.append(getValues());
		sb.append(")");

		sb.append("\n\t</insert>");
		return sb.toString();
	}

	/**
	 * <update id="update" parameterType="xxx.vo.XxxxxVo"> UPDATE tableName set
	 * column1 = #{field1}, column2 = #{field2},... WHERE fid = #{id}, </update>
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getUpdateMethod(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t<update id=\"update\" ");
		sb.append("parameterType=\"");
		sb.append(voName);
		sb.append("\">");
		sb.append("\n\t\t");
		sb.append("UPDATE ");
		sb.append(tableName);
		sb.append("\n\t\t");
		sb.append("SET ");
		sb.append(getUpdateFields());
		
		sb.append("\n\t\t");
//		sb.append("WHERE Fid = #{id}");
		if(hasPk) {
			//主键名称不一定是fid
			sb.append("WHERE ").append(this.pkColName).append(" = #{id} ");
		}else {
			sb.append("WHERE Fid = #{id}");
		}
		
		sb.append("");

		sb.append("\n\t</update>");
		return sb.toString();

	}

	/**
	 * <delete id="deleteById" parameterType="xxx.vo.XxxxxVo"> DELETE FROM tableName
	 * WHERE fid = #{id}, </delete>
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getDeleteById(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t<delete id=\"deleteById\" ");
		sb.append("parameterType=\"");
		sb.append(voName);
		sb.append("\">");
		sb.append("\n\t\t");
		sb.append("DELETE FROM ");
		sb.append(tableName);
		sb.append("\n\t\t");
//		sb.append("WHERE Fid = #{id}");
		if(hasPk) {
			//主键名称不一定是fid
			sb.append("WHERE ").append(this.pkColName).append(" = #{id} ");
		}else {
			sb.append("WHERE Fid = #{id}");
		}

		sb.append("\n\t</delete>");
		return sb.toString();

	}
	
	/**
	 * 	
	 <delete id="deleteByIds" parameterType="java.util.List">
		DELETE FROM t_bs_user
		WHERE FUSERID in
			<foreach collection="idSet" item="id" index="index" open="(" close=")" separator=",">
				#{id}
			</foreach>
		</delete>
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getDeleteByIds(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append("<delete id=\"deleteByIds\" parameterType=\"java.util.List\">");
		sb.append("\n\t\t");
		sb.append("DELETE FROM ");
		sb.append(tableName);
		sb.append("\n\t\t");
		if(hasPk) {
			//主键名称不一定是fid
			sb.append("WHERE ").append(this.pkColName).append(" in ");
		}else {
			sb.append("WHERE Fid in");
		}

		sb.append("\n\t\t\t");
		sb.append("<foreach collection=\"idSet\" item=\"id\" index=\"index\" open=\"(\" close=\")\" separator=\",\">");
		sb.append("\n\t\t\t\t");
		sb.append("#{id}");
		sb.append("\n\t\t\t");
		
		sb.append("</foreach>");

		sb.append("\n\t\t");
		
		sb.append("\n\t</delete>");
		return sb.toString();

	}
	
	/**
	 * <select id="queryListByIds" parameterType="java.lang.String"
	 * resultType="xxx.vo.XxxxxVo" SELECT column1,column2.... 
	 * FROM tableName LIMIT
	 * Where Fid = #{id}
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getQueryById(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t<select id=\"queryById\" ");
//		sb.append("parameterType=\"java.lang.String\" ");
		// sb.append(voName);
		// sp.append("\" ");
//		sb.append("resultType=\"");
		//sb.append(voName);
		//sb.append("\">");
		sb.append("resultMap=\"resultMap\">");
		sb.append("\n\t\t");
		sb.append("SELECT ");
		sb.append(getAddFields());

		sb.append("\n\t\t");
		sb.append("FROM ");
		sb.append(tableName);
		sb.append("\n\t\t");
		//sb.append("WHERE Fid = #{id}");
		if(hasPk) {
			//主键名称不一定是fid
			sb.append("WHERE ").append(this.pkColName).append(" = #{id} ");
		}else {
			sb.append("WHERE Fid = #{id}");
		}

		//sb.append(getTestFields());

		sb.append("\n\t\t");
		//sb.append("LIMIT #{offset}, #{limit}");

		sb.append("\n\t</select>");
		return sb.toString();
	}
		

	/**
	 * <select id="queryListByIds" parameterType="java.util.List" resultMap="resultMap">
	 * SELECT column1,column2.... 
	 * FROM tableName LIMIT
	 * Where id in idSet
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getQueryListByIds(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append("<select id=\"queryListByIds\" parameterType=\"java.util.List\" resultMap=\"resultMap\">");	
		sb.append("\n\t\t");
		sb.append("SELECT ");
		sb.append(getAddFields());

		sb.append("\n\t\t");
		sb.append("FROM ");
		sb.append(tableName);
		sb.append("\n\t\t");
		if(hasPk) {
			//主键名称不一定是fid
			sb.append("WHERE ").append(this.pkColName).append(" in");
		}else {
			sb.append("WHERE Fid in");
		}
		
		sb.append("\n\t\t\t");
		sb.append("<foreach collection=\"idSet\" item=\"id\" index=\"index\" open=\"(\" close=\")\" separator=\",\">");
		sb.append("\n\t\t\t\t");
		sb.append("#{id}");
		sb.append("\n\t\t\t");
		
		sb.append("</foreach>");

		sb.append("\n\t\t");

		sb.append("\n\t</select>");
		return sb.toString();
	}
	
	

	/**
	 * <select id="queryList" parameterType="java.util.Map"
	 * resultType="xxx.vo.XxxxxVo" SELECT column1,column2.... FROM tableName LIMIT
	 * #{offset}, #{limit}
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getQueryListMethod(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t<select id=\"queryList\" ");
		sb.append("parameterType=\"java.util.Map\" ");
		// sb.append(voName);
		// sp.append("\" ");
//		sb.append("resultType=\"");
//		sb.append(voName);
//		sb.append("\">");
		sb.append("resultMap=\"resultMap\">");
		
		sb.append("\n\t\t");
		sb.append("SELECT ");
		sb.append(getAddFields());

		sb.append("\n\t\t");
		sb.append("FROM ");
		sb.append(tableName);
		sb.append("\n\t\t");
		sb.append("WHERE 1 = 1");

		sb.append(getTestFields());
		//增加排序字段
		sb.append(getTestOrderbyFields());
		
		sb.append("\n\t\t");
		sb.append("LIMIT #{offset}, #{limit}");

		sb.append("\n\t</select>");
		return sb.toString();
	}

	/**
	 * <select id="queryListCount" parameterType="xxx.vo.XxxxxVo" resultType="int" SELECT
	 * count(1) FROM tableName LIMIT #{offset}, #{limit}
	 * 
	 * @param tableName
	 * @param voName
	 * @param daoName
	 * @return
	 */
	private String getQueryListCountMethod(String tableName, String voName, String daoName) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t<select id=\"queryListCount\" ");
		sb.append("parameterType=\"java.util.Map\" ");
		// sb.append(voName);
		// sp.append("\" ");
		sb.append("resultType=\"int\">");
//		sb.append(voName);
//		sb.append("\">");
		sb.append("\n\t\t");
		sb.append("SELECT count(1) ");

		sb.append("\n\t\t");
		sb.append("FROM ");
		sb.append(tableName);
		sb.append("\n\t\t");
		sb.append("WHERE 1 = 1");

		sb.append(getTestFields());

		sb.append("\n\t\t");

		sb.append("\n\t</select>");
		return sb.toString();
	}

	private String getTestFields() {

		Field[] fields = javaClass.getFields();

		StringBuilder sb = new StringBuilder();
		Column column = null;
		for (Field field : fields) {

			column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			
			sb.append("\n\t\t\t");
			
			sb.append("<if test=\"");
			sb.append(field.getName());
			sb.append(" != null and ");
			sb.append(field.getName());
			sb.append(" != '' \">");
			sb.append("\n\t\t\t\t");			
			sb.append("and ");
			sb.append(column.name());
			
			if(isLikeField(field.getName()) ) {
				sb.append(" like '%${");
				sb.append(field.getName());
				sb.append("}%'");
			}else {
				sb.append(" = #{");
				sb.append(field.getName());
				sb.append("}");
			}
			sb.append("\n\t\t\t");			
			sb.append("</if>");
			
			//使用范围查询的字段 字段后面增加 "From"和"To"
			if(isFromToField(field.getName()) ) {
				sb.append(getTestFromToFields(field.getName()+"From",column.name(),true));
				sb.append(getTestFromToFields(field.getName()+"To",column.name(),false));
			}
			
		}
		return sb.toString();

	}
	
	
	/**
	* 增加通用排序字段
	* <if test="orderby != null and orderby != '' ">
	* 	order by '${orderby}'
	* </if>
	* （{"orderby":"FparentId ASC"}）：
	* SELECT * FROM T_pt_templatetype WHERE 1 = 1  order by 'FparentId ASC'
	* SELECT * FROM T_pt_templatetype WHERE 1 = 1  order by 'FparentId ASC'
	 */
	private String getTestOrderbyFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t\t");
		
		sb.append("<if test=\"orderby != null and orderby != '' \">");
		sb.append("\n\t\t\t\t");			
		sb.append("order by '${orderby}'");
		sb.append("\n\t\t\t");			
		sb.append("</if>");
		
		return sb.toString();
	}
	
	/**
	* 增加通用过滤，非标准字段
	* <if test="filter != null and filter != '' ">
	* and '${filter}'
	* </if>
	* 暂时不用吧，因为加了也起不了实际效果，如下sql实际导致数据库有数据却查不出数据（{"filter":"FCOID = 1"}）：
	* SELECT * FROM T_pt_templatetype WHERE 1 = 1 and 'FCOID = 1' order by 'FparentId ASC'
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String getTestfilterFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t\t");
		
		sb.append("<if test=\"filter != null and filter != '' \">");
		sb.append("\n\t\t\t\t");			
		sb.append("and '${filter}'");
		sb.append("\n\t\t\t");			
		sb.append("</if>");
		
		return sb.toString();
	}
	
	
	/**
	 * 使用like查询的字段
	 */
	private boolean isLikeField(String fileName) {
		if("name".equalsIgnoreCase(fileName) || "number".equalsIgnoreCase(fileName)
				|| "remark".equalsIgnoreCase(fileName)) {
			return true;
		}
		return false;
	}
	/**
	 * 使用范围查询的字段
	 * 如日期，时间字段
	 */
	private boolean isFromToField(String fileName) {
		if("createTime".equalsIgnoreCase(fileName) || "modifyTime".equalsIgnoreCase(fileName)
				|| "billDate".equalsIgnoreCase(fileName)) {
			return true;
		}
		return false;
	}
	private String getTestFromToFields(String fieldName, String columnName, boolean isFrom) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t\t");
		sb.append("<if test=\"");
		sb.append(fieldName);
		sb.append(" != null and ");
		sb.append(fieldName);
		sb.append(" != '' \">");
		sb.append("\n\t\t\t\t");			
		sb.append("and ");
		sb.append(columnName);
		if(isFrom) {
			sb.append(" &gt;= #{");
		}else {
			sb.append(" &lt;= #{");
		}
		sb.append(fieldName);
		sb.append("}");

		sb.append("\n\t\t\t");			
		sb.append("</if>");		
		return sb.toString();
	}
	
	
	private String getAddFields() {

		Field[] fields = javaClass.getFields();

		StringBuilder sb = new StringBuilder();
		Column column = null;
		int i = 0;
		for (Field field : fields) {

			column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			if (i == 5) {
				sb.append("\n\t\t\t");
				i = 0;
			} else {
				i++;
			}
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(column.name());

		}
		return sb.toString();

	}

	private String getValues() {

		Field[] fields = javaClass.getFields();

		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Field field : fields) {

			if (i == 5) {
				sb.append("\n\t\t\t");
				i = 0;
			} else {
				i++;
			}
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("#{");
			sb.append(field.getName());
			sb.append("}");

		}
		return sb.toString();

	}

	private String getUpdateFields() {

		Field[] fields = javaClass.getFields();

		StringBuilder sb = new StringBuilder();
		int i = 0;
		Column column = null;
		for (Field field : fields) {
			if (field.getName().equalsIgnoreCase("id")) {
				continue;
			}

			column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			
			if (i == 5) {
				sb.append("\n\t\t\t");
				i = 0;
			} else {
				i++;
			}
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(column.name());
			sb.append(" = ");
			sb.append("#{");
			sb.append(field.getName());
			sb.append("}");

		}
		return sb.toString();
	}

	private void genPkColName()  {

		Field[] fields = javaClass.getFields();
		Column column = null;
		String fieldName = "";
		String colName = "";

		for (Field field : fields) {
			fieldName = field.getName();
			column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			colName = column.name();
			if ("id".equalsIgnoreCase(fieldName)) {
				this.hasPk = true;
				this.pkColName = colName;
				break;
			}
		}
	}
	
}
