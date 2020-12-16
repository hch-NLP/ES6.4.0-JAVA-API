package ES_API_Test;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;

public class Query_One_Doc {

	public static void main(String[] args) {
		String Index_Name="patent";
		String Index_Type="document";
		String ID = "2";//指定文档ID
		get_doc(Index_Name,Index_Type,ID);
	}

	public static void get_doc(String Index_Name,String Index_Type,String ID) {//根据索引ID查询指定数据
		TransportClient client = ES_Client.get_client();// 查询时需要指定索引名称，索引类型，索引ID
		GetResponse response = client.prepareGet(Index_Name, Index_Type, ID).get();
		System.out.println(response.getSourceAsString());
	}
}
