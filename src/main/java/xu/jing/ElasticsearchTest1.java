package xu.jing;

import java.net.InetAddress;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


public class ElasticsearchTest1 {

	private static String host="192.168.44.101"; // 服务器地址
	private static int port=9300; // 端口
	
	public static void main(String[] args) throws Exception{
		 Settings esSettings = Settings.builder()
	                .put("cluster.name", "my-application") //设置ES实例的名称
	                .put("client.transport.sniff", true) //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
	                .build();

		 TransportClient  client = new PreBuiltTransportClient(esSettings);//初始化client较老版本发生了变化，此方法有几个重载方法，初始化插件等。
	        //此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
	      client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host),port));

		
		XContentBuilder mapping = XContentFactory.jsonBuilder()
		    .startObject()
		        .startObject("settings")
		            .field("number_of_shards",1)
		            .field("number_of_replicas",0)
		        .endObject()
	        .endObject()  
	        .startObject()
                .startObject("student_info") //type
                    .startObject("properties")  
                        .startObject("name").field("type", "string").field("store", "yes").endObject()
                        .startObject("sex")  .field("type", "string").field("store", "yes").endObject()  
                        .startObject("college").field("type", "string").field("store", "yes").endObject()
                        .startObject("age").field("type", "long").field("store", "yes").endObject()  
                        .startObject("school").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
                    .endObject()
                .endObject()
            .endObject();  
       
        CreateIndexRequestBuilder cirb = client.admin().indices()
        		.prepareCreate("people")
        		.setSource(mapping);
        CreateIndexResponse response = cirb.execute().actionGet();
        
		client.close();
	}
}