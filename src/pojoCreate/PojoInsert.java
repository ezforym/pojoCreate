package pojoCreate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PojoInsert {
	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			file.delete();
		}
		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
			return false;
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			System.out.println("目标文件所在目录不存在，准备创建它！");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目标文件所在目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
			return false;
		}
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "失败！");
			return false;
		}
	}

	public static String createTempFile(String prefix, String suffix, String dirName) {
		File tempFile = null;
		if (dirName == null) {
			try {
				// 在默认文件夹下创建临时文件
				tempFile = File.createTempFile(prefix, suffix);
				// 返回临时文件的路径
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("创建临时文件失败！" + e.getMessage());
				return null;
			}
		} else {
			File dir = new File(dirName);
			// 如果临时文件所在目录不存在，首先创建
			if (!dir.exists()) {
				if (!PojoInsert.createDir(dirName)) {
					System.out.println("创建临时文件失败，不能创建临时文件所在的目录！");
					return null;
				}
			}
			try {
				// 在指定目录下创建临时文件
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("创建临时文件失败！" + e.getMessage());
				return null;
			}
		}
	}

	public static String readTxtFile(File fileName) throws Exception {
		String result = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			try {
				String read = null;
				while ((read = bufferedReader.readLine()) != null) {
					result = result + read + "\r\n";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (fileReader != null) {
				fileReader.close();
			}
		}
		System.out.println("读取出来的文件内容是：" + "\r\n" + result);
		return result;
	}

	public static void contentToTxt(String filePath, String content) {
		String str = new String(); // 原有txt内容
		String s1 = new String();// 内容更新
		try {
			File f = new File(filePath);
			if (f.exists()) {
				System.out.print("文件存在");
			} else {
				System.out.print("文件不存在");
				f.createNewFile();// 不存在则创建
			}
			BufferedReader input = new BufferedReader(new FileReader(f));

			while ((str = input.readLine()) != null) {
				s1 += str + "\n";
			}
			System.out.println(s1);
			input.close();
			s1 += content;
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(s1);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void sb(Pojo pojo, String func, String path, String pac) {
		String dirName = path;
		PojoInsert.createDir(dirName);
		// 创建文件
		String fin = pojo.getTname();
		if (func.equals("java")) {
			fin = fin.replaceFirst(fin.substring(0, 1), fin.substring(0, 1).toUpperCase());
		}
		String fileName = dirName + "/" + fin + "." + func;
		System.out.println(PojoInsert.createFile(change(fileName)));
		String xxxxx = "";
		if (func.equals("php")) {
			xxxxx = huoquString(pojo);
		} else if (func.equals("java")) {
			xxxxx = javahuoquString(pojo, pac);
		}
		contentToTxt(fileName, xxxxx);
	}

	private static String javahuoquString(Pojo pojo, String pac) {
		String str = "package " + pac + ";\n";
		int date1 = 0;
		int math1 = 0;
		for (int i = 0; i < pojo.getClu().size(); i++) {
			String x3 = pojo.getClu().get(i).getType();
			if ((x3.equals("TIME") || x3.equals("DATE") || x3.equals("DATETIME") || x3.equals("TIMESTAMP")
					|| x3.equals("YEAR")) && date1 == 0) {
				str = str + "import java.sql.*;\n";
				date1 = 1;
			}
			if ((x3.equals("DECIMAL") || x3.equals("BIGINT")) && math1 == 0) {
				str = str + "import java.math.*;\n";
				math1 = 1;
			}
		}
		str = str + "public class " + change(pojo.getTname().replaceFirst(pojo.getTname().substring(0, 1),
				pojo.getTname().substring(0, 1).toUpperCase())) + "{\n";
		for (int i = 0; i < pojo.getClu().size(); i++) {
			str = str + "private " + getType(pojo.getClu().get(i).getType()) + " "
					+ change(pojo.getClu().get(i).getTname()) + ";\n";
		}
		for (int i = 0; i < pojo.getClu().size(); i++) {
			String tn = change(pojo.getClu().get(i).getTname());
			str = str + "public void set" + tn.replaceFirst(tn.substring(0, 1), tn.substring(0, 1).toUpperCase()) + "("
					+ getType(pojo.getClu().get(i).getType()) + " " + tn + "){\n" + "this." + tn + "=" + tn + ";\n"
					+ "}\n";
		}
		for (int i = 0; i < pojo.getClu().size(); i++) {
			String tn = change(pojo.getClu().get(i).getTname());
			str = str + "public " + getType(pojo.getClu().get(i).getType()) + " get"
					+ tn.replaceFirst(tn.substring(0, 1), tn.substring(0, 1).toUpperCase()) + "(){\n" + "return " + tn
					+ ";" + "}\n";
		}
		str = str + "}\n";
		return str;
	}

	private static String huoquString(Pojo pojo) {
		String str = "<?php\n class " + pojo.getTname().replaceFirst(pojo.getTname().substring(0, 1),
				pojo.getTname().substring(0, 1).toUpperCase()) + "{\n";
		for (int i = 0; i < pojo.getClu().size(); i++) {
			str = str + "var $" + pojo.getClu().get(i).getTname() + ";\n";
		}
		for (int i = 0; i < pojo.getClu().size(); i++) {
			String tn = pojo.getClu().get(i).getTname();
			str = str + "function set" + tn.replaceFirst(tn.substring(0, 1), tn.substring(0, 1).toUpperCase()) + "($"
					+ tn + "){\n" + "$this->" + tn + "=$" + tn + ";\n" + "}\n";
		}
		for (int i = 0; i < pojo.getClu().size(); i++) {
			String tn = pojo.getClu().get(i).getTname();
			str = str + "function get" + tn.replaceFirst(tn.substring(0, 1), tn.substring(0, 1).toUpperCase()) + "(){\n"
					+ "return $this->" + tn + ";" + "}\n";
		}
		str = str + "}\n?>";
		return str;
	}

	private static String getType(String type) {
		String x = "";
		switch (type) {
		case "YEAR":
			x = "Date";
			break;
		case "TIMESTAMP":
			x = "Timestamp";
			break;
		case "DATETIME":
			x = "Timestamp";
			break;
		case "DATE":
			x = "Date";
			break;
		case "TIME":
			x = "Time";
			break;
		case "DECIMAL":
			x = "BigDecimal";
			break;
		case "ID":
			x = "Long";
			break;
		case "BOOLEAN":
			x = "boolean";
			break;
		case "DOUBLE":
			x = "double";
			break;
		case "FLOAT":
			x = "float";
			break;
		case "BIT":
			x = "boolean";
			break;
		case "MEDIUMINT":
			x = "Integer";
			break;
		case "INT":
			x = "Integer";
			break;
		case "SMALLINT":
			x = "Integer";
			break;
		case "TINYINT":
			x = "boolean";
			break;
		case "INTEGER":
			x = "Long";
			break;
		case "BIGINT":
			x = "BigInteger";
			break;
		case "VARCHAR":
			x = "String";
			break;
		case "CHAR":
			x = "String";
			break;
		case "TEXT":
			x = "String";
			break;
		default:
			x = "String";
			break;
		}
		return x;
	}

	private static String change(String str) {
		String re = "";
		char[] xxxx = str.toCharArray();
		for (int i = 0; i < xxxx.length; i++) {
			if (95 == xxxx[i]) {
				if (i < xxxx.length && xxxx[i + 1] > 96 && xxxx[i + 1] < 123) {
					re = re + (char) (xxxx[i + 1] - 32);
				}
			} else {
				re = re + xxxx[i];
			}
		}
		return re;
	}
}