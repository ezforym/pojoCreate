package pojoCreate;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JdbcUtil {
	public static void main(String[] args) throws Exception {
		// jar:file:/home/ez/555/PojoInsert.jar!/pojoCreate/
		// System.out.println("==============>" +
		// PojoInsert.class.getResource("").toString());
		String x = PojoInsert.class.getResource("").toString();
		System.out.println(x);
		String[] sxxx = x.split("PojoCreate.jar");
		String[] sx = sxxx[0].split("jar:file:");
		// System.out.println(sx[1]);
		File f = new File(sx[1] + "jdbc.txt");
		String str = "";
		try {
			str = PojoInsert.readTxtFile(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] sxx = str.split("null");
		JSONObject jsStr = JSONObject.fromObject(sxx[1]);
		String url2 = jsStr.getString("url");
		String user = jsStr.getString("user");
		String path = jsStr.getString("path");
		String pac = jsStr.getString("package");
		if (pac.equals("") || pac == null) {
			pac = "com.wyz.pojo";
		}
		String password = jsStr.getString("password");
		String url = url2 + "?" + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8";
		JSONArray ja = jsStr.getJSONArray("pojo");
		String func = jsStr.getString("func");
		for (int i = 0; i < ja.size(); i++) {
			getdata(url, ja.get(i).toString(), func, path, pac);
		}
	}

	private static void getdata(String url, String tname, String func, String path, String pac) throws SQLException {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM " + tname;
			ResultSet rs = stmt.executeQuery(sql);// 得到查询结果,一个数据集
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount(); // 得到数据集的列数
			Pojo pojo = new Pojo();
			pojo.setTname(tname);
			List<Clu> list = new ArrayList<Clu>();
			for (int j = 1; j <= numberOfColumns; j++) {
				Clu clu = new Clu();
				clu.setTname(rsmd.getColumnName(j));
				clu.setType(rsmd.getColumnTypeName(j));
				list.add(clu);
			}
			pojo.setClu(list);
			PojoInsert p = new PojoInsert();
			p.sb(pojo, func, path, pac);
			// rsmd.getColumnTypeName(1);
			// rsmd.getColumnName(1);
			// PreparedStatement mstmt = conn.prepareStatement(sql);

			// int type = -1;
			// for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			// String name = rsmd.getColumnName(i);
			// type = rsmd.getColumnType(i);//返回列类型对应的整形数表示。package
			// java.sql.Type类有各类型对应的整数表示。
			//
			// switch (type) {
			// case Types.BIGINT:
			// mstmt.setLong(i, rs.getLong(name));
			// break;
			// case Types.BOOLEAN:
			// mstmt.setBoolean(i, rs.getBoolean(name));
			// break;
			// case Types.DATE:
			// mstmt.setDate(i, rs.getDate(name));
			// break;
			// case Types.DOUBLE:
			// mstmt.setDouble(i, rs.getDouble(name));
			// break;
			// case Types.FLOAT:
			// mstmt.setFloat(i, rs.getFloat(name));
			// break;
			// case Types.INTEGER:
			// mstmt.setInt(i, rs.getInt(name));
			// break;
			// case Types.SMALLINT:
			// mstmt.setInt(i, rs.getInt(name));
			// break;
			// case Types.TIME:
			// mstmt.setTime(i, rs.getTime(name));
			// break;
			// case Types.TIMESTAMP:
			// mstmt.setTimestamp(i, rs.getTimestamp(name));
			// break;
			// case Types.TINYINT:
			// mstmt.setShort(i, rs.getShort(name));
			// break;
			// case Types.VARCHAR:
			// mstmt.setString(i, rs.getString(name));
			// break;
			// case Types.NCHAR:
			// mstmt.setString(i, rs.getNString(name));
			// break;
			// case Types.NVARCHAR:
			// mstmt.setString(i, rs.getNString(name));
			// break;
			// case Types.BIT:
			// mstmt.setByte(i, rs.getByte(name));
			// break;
			// }
			//

			// System.out.println(numberOfColumns);
		} catch (SQLException e) {
			System.out.println("MySQL操作错误");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
