package ES_API_Test;

import java.util.Iterator;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class TermQuery {

	public static void main(String[] args) {
		// 普通查询方法可参考 https://www.cnblogs.com/shine_cn/p/6122576.html
		// 高级查询方法好高亮显示可参考https://www.cnblogs.com/w-bb/articles/9743978.html
		TransportClient client = ES_Client.get_client();// 创建客户端
		QueryBuilder queryBuilder =QueryBuilders.termQuery("标题", "计算机");
		SearchResponse searchResponse = client.prepareSearch("patent")
		        .setTypes("document")
		        .setQuery(queryBuilder)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setFrom(0)
		        .setSize(10)//设置返回的最大相关文档数
		        .setExplain(true)//排序
		        .execute()
		        .actionGet();
		SearchHits hits = searchResponse.getHits(); // 获取命中次数，查询结果有多少对象
        System.out.println("查询结果有：" + hits.getTotalHits() + "条！");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
        	System.out.println("----------------------------------------");
            SearchHit searchHit = iterator.next(); // 每个查询对象
            System.out.println("专利号:" + searchHit.getSourceAsMap().get("专利号"));
            System.out.println("标题:" + searchHit.getSourceAsMap().get("标题"));
            System.out.println("发明人:" + searchHit.getSourceAsMap().get("发明人"));
            System.out.println("申请人:" + searchHit.getSourceAsMap().get("申请人"));
        }

	}

}
