package pojoCreate;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		String xml = jsStr.getString("xml");
		for (int i = 0; i < ja.size(); i++) {
			getdata(url, ja.get(i).toString(), func, path, pac, xml);
		}
	}

	private static void getdata(String url, String tname, String func, String path, String pac, String xml)
			throws SQLException {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rsc = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			Statement stmt1 = conn.createStatement();
			String sql = "SELECT * FROM " + tname;
			rs = stmt.executeQuery(sql);// 得到查询结果,一个数据集
			Map<String, String> m = new HashMap<String, String>();
			rsc = stmt1.executeQuery("show full columns from " + tname);
			try {
				while (rsc.next()) {
					m.put(rsc.getString("Field"), rsc.getString("Comment"));
				}
			} catch (Exception e) {
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount(); // 得到数据集的列数
			Pojo pojo = new Pojo();
			pojo.setTname(tname);
			List<Clu> list = new ArrayList<Clu>();
			for (int j = 1; j <= numberOfColumns; j++) {
				Clu clu = new Clu();
				clu.setTname(rsmd.getColumnName(j));
				clu.setType(rsmd.getColumnTypeName(j));
				try {
					clu.setComment(m.get(rsmd.getColumnName(j)));
				} catch (Exception e) {
				}
				list.add(clu);
			}
			pojo.setClu(list);
			PojoInsert p = new PojoInsert();
			p.sb(pojo, func, path, pac, xml);
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
