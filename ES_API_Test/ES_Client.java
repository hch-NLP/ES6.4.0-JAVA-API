package ES_API_Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ES_Client {
	public static TransportClient get_client() {
		TransportClient client;
		Settings settings = Settings.builder()
                .put("cluster.name", "HCH-ES")
                .put("client.transport.sniff", "true")//增加自动嗅探配置
                .build();
        try {
        	client = new PreBuiltTransportClient(settings);
			client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
			System.out.println(client.toString());
			return client;
        } catch (Exception e) {
        	System.out.println("ES连接错误！");
			e.printStackTrace();
			return new PreBuiltTransportClient(settings);
		}
	}

}
