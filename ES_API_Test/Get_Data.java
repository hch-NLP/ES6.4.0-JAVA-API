package ES_API_Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Get_Data {

	public static void main(String[] args) {
		 File[] list = new File("E:/CH/fmsq1专利数据集").listFiles();
	        for(File file : list)
	        {
	           if(file.isFile())
	           {
	               if (file.getName().endsWith(".txt")) {
	                   // 就输出该文件的绝对路径
	                   System.out.println(file.getAbsolutePath());
	           		Map<String, Map<String, String>> data = readtext(file.getAbsolutePath());
	           		for (String key : data.keySet()) {
	           			System.out.println(data.get(key).get("标题").toString());
	           		}
	           		break;
	               }
	           }
	        }
	}

	public static Map<String, Map<String, String>> readtext(String path) {
		Map<String, Map<String, String>> json = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> doc = new HashMap<String, String>();
		try {
			FileInputStream fis = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(fis, "GB2312");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if (line.contains("<公开（公告）号>=")) {
						doc.put("专利号", line.split("<公开（公告）号>=")[1]);
					}
					if (line.contains("<名称>=")) {
						doc.put("标题", line.split("<名称>=")[1]);
					}
					if (line.contains("<分类号>=")) {
						doc.put("分类号", line.split("<分类号>=")[1]);
					}
					if (line.contains("<申请（专利权）人>=")) {
						doc.put("申请人", line.split("<申请（专利权）人>=")[1]);
					}
					if (line.contains("<地址>=")) {
						doc.put("地址", line.split("<地址>=")[1]);
					}
					if (line.contains("<发明（设计）人>=")) {
						doc.put("发明人", line.split("<发明（设计）人>=")[1]);
					}
					if (line.contains("<摘要>=")) {
						doc.put("摘要", line.split("<摘要>=")[1]);
					}
					if (line.contains("<说明书>=")) {
						doc.put("说明书", line.split("<说明书>=")[1]);
						break;
					}
				}
			}
			json.put(String.valueOf(json.size() + 1), doc);
			isr.close();
			fis.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
