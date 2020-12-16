package ES_API_Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class Create_Index {
	public static TransportClient  client = ES_Client.get_client();// 创建索引时需要指定索引名称，索引类型，索引ID
	public static void main(String[] args) {
		String Index_Name = "patent";
		String Index_Type = "document";
		//String Index_ID = "1";
		IndicesExistsResponse response = client.admin().indices()
				.exists(Requests.indicesExistsRequest(Index_Name)).actionGet();// 可以一次判断多个索引
																				// 例如："hch",
																				// "patent_data"
		if (response.isExists() == false) {
			System.out.println("#####正在创建新索引和映射！#####");
			createIndex(Index_Name,client);
			createMapping(Index_Name, Index_Type,client);
		} else {
			//System.out.println("#####索引已经存在，正在写入数据！#####");
			 System.out.println("#####索引已经存在，正在删除原有索引，并创建新索引和映射！#####");
			 deleteIndex(Index_Name,client);
			 System.out.println("#####正在创建新索引和映射！#####");
			 createIndex(Index_Name,client);
			 createMapping(Index_Name,Index_Type,client);
		}
//		json_Add_Record(Index_Name,Index_Type,Index_ID,client);
//		XContentBuilder_Add_Record(Index_Name, Index_Type, "2",client);
		json_Add_Record_Fromtxt(Index_Name,Index_Type,client);
	}

	// 创建索引并设置分片数和备份数的方法
	public static void createIndex(String Index_Name,TransportClient  client) {
		Settings settings = Settings.builder().put("index.number_of_shards", 5)// 分片数量
				.put("index.number_of_replicas", 0).build();// 备份数量
		client.admin().indices().prepareCreate(Index_Name).setSettings(settings).get();// 创建新的索引时需要指定索引名称(必须为小写,且不能已经存在于索引库中)，索引类型
		System.out.println("*********索引创建成功！*******");
	}

	// 删除索引的方法
	public static void deleteIndex(String Index_Name,TransportClient  client) {
		// 删除索引时需要指定索引名称
		client.admin().indices().prepareDelete(Index_Name).get();
		System.out.println("*********索引删除成功！*******");
	}

	// 为索引添加映射的方法
	public static void createMapping(String Index_Name, String Index_Type,TransportClient  client) {
		// 创建mapping约束字段
		PutMappingResponse response = null;
		// 创建索引映射时需要指定索引名称
		try {
			XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject("properties")
					.startObject("专利号") // 文档字段title
					.field("type", "text").field("index", "true").endObject().startObject("标题") 
					.field("type", "text").field("index", "true").field("analyzer", "ik_max_word")
					.field("search_analyzer", "ik_smart").endObject().startObject("分类号")
					.field("type", "text").field("index", "true").endObject().startObject("申请人")
					.field("type", "text").field("index", "true").field("analyzer", "ik_max_word")
					.field("search_analyzer", "ik_smart").endObject().startObject("地址")
					.field("type", "text").field("index", "true").field("analyzer", "ik_max_word")
					.field("search_analyzer", "ik_smart").endObject().startObject("发明人")
					.field("type", "text").field("index", "true").field("analyzer", "ik_max_word")
					.field("search_analyzer", "ik_smart").endObject().startObject("摘要")
					.field("type", "text").field("index", "true").field("analyzer", "ik_max_word")
					.field("search_analyzer", "ik_smart").endObject().startObject("说明书")
					.field("type", "text").field("index", "true").field("analyzer", "ik_max_word")
					.field("search_analyzer", "ik_smart").endObject().endObject().endObject();
			// 添加mapping 绑定到指定的 index
			PutMappingRequest putMappingRequest = Requests.putMappingRequest(Index_Name).type(Index_Type)
					.source(mapping);
			response = client.admin().indices().putMapping(putMappingRequest).actionGet();
			System.out.println("*********映射创建成功！*******" + response.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 利用json对象组装数据并增量更新和插入新数据的方法
	public static void json_Add_Record_Fromtxt(String Index_Name, String Index_Type,TransportClient  client) {
		int K=0;
		File[] list = new File("E:/CH/fmsq1专利数据集").listFiles();
        for(File file : list)
        {
           if(file.isFile())
           {
               if (file.getName().endsWith(".txt")) {
            	   K++;
                // 就输出该文件的绝对路径
                //System.out.println(file.getAbsolutePath());
           		Map<String, Map<String, String>> data = Get_Data.readtext(file.getAbsolutePath());
           		for (String key : data.keySet()) {
           			Map<String, Object> json = new HashMap<String, Object>();
           			json.put("专利号", data.get(key).get("专利号").toString());
           			json.put("标题", data.get(key).get("标题").toString());
           			json.put("分类号", data.get(key).get("分类号").toString());
           			json.put("申请人", data.get(key).get("申请人").toString());
           			json.put("地址", data.get(key).get("地址").toString());
           			json.put("发明人", data.get(key).get("发明人").toString());
           			json.put("摘要",data.get(key).get("摘要").toString());
           			json.put("说明书", data.get(key).get("说明书").toString());
               		// 创建索引时需要指定索引名称，索引类型，索引ID
           			IndexResponse response = client.prepareIndex(Index_Name, Index_Type, String.valueOf(K)).setSource(json).get();
           			System.out.println("<第"+K+"条索引数据的存储地址为：" + response.getLocation(String.valueOf(K)) + ">");  
           			json.clear();
           		}   
           		data.clear();
               }
           }
        }
	}
	// 利用json对象组装数据并增量更新和插入新数据的方法
	public static void json_Add_Record(String Index_Name, String Index_Type, String Index_ID,TransportClient  client) {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("专利号", "CN00100306.2;00100306.2");
		json.put("标题", "具有转子平衡装置的电动机");
		json.put("分类号", "H02K15/16(2006.01)");
		json.put("申请人", "台达电子工业股份有限公司");
		json.put("地址", "中国台湾");
		json.put("发明人", "林国正;黄文喜;周初宪;蔡明熹");
		json.put("摘要",
				"一种具有转子平衡装置的电动机，包含一扇叶外壳、一装设于扇叶外壳内侧之铁壳以及一装设于该铁壳内侧之磁带，其与一定子配合，其中定子包含一线圈以及一导磁矽钢片；定子上之线圈通电激磁后，是经导磁矽钢片吸引磁带，以带动转子平衡装置转动；一导磁片装设于扇叶外壳之顶部内侧下方，与铁壳之间无导磁关系，且与线圈间具有相互吸引之磁力，以改善转子转动时的平衡性。");
		json.put("说明书", "具有转子平衡装置的电动机");
		IndexResponse response = client.prepareIndex(Index_Name, Index_Type, Index_ID).setSource(json).get();
		System.out.println("<索引数据存储地址为：" + response.getLocation(Index_ID) + ">");
	}

	// 利用XContentBuilder工厂模式组装数据并增量更新和插入新数据的方法
	public static void XContentBuilder_Add_Record(String Index_Name, String Index_Type, String Index_ID,TransportClient  client) {
		XContentBuilder builder;
		try {
			builder = XContentFactory.jsonBuilder().startObject().field("专利号", "CN00100306.2;00100306.2")
					.field("标题", "具有转子平衡装置的电动机").field("分类号", "H02K15/16(2006.01)").field("申请人", "台达电子工业股份有限公司")
					.field("地址", "中国台湾").field("发明人", "林国正;黄文喜;周初宪;蔡明熹")
					.field("摘要",
							"一种具有转子平衡装置的电动机，包含一扇叶外壳、一装设于扇叶外壳内侧之铁壳以及一装设于该铁壳内侧之磁带，其与一定子配合，其中定子包含一线圈以及一导磁矽钢片；定子上之线圈通电激磁后，是经导磁矽钢片吸引磁带，以带动转子平衡装置转动；一导磁片装设于扇叶外壳之顶部内侧下方，与铁壳之间无导磁关系，且与线圈间具有相互吸引之磁力，以改善转子转动时的平衡性。")
					.field("说明书", "具有转子平衡装置的电动机").endObject();
			// 创建索引时需要指定索引名称，索引类型，索引ID
			IndexResponse response = client.prepareIndex(Index_Name, Index_Type, Index_ID).setSource(builder).get();
			System.out.println("<索引数据存储地址为：" + response.getLocation(Index_ID) + ">");
			builder.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
