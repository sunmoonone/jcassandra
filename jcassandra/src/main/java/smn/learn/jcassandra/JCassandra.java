package smn.learn.jcassandra;
import java.util.Arrays;

import org.json.simple.JSONObject;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.*;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;

public class JCassandra {
	private Cluster cluster;
	private Keyspace ksp;
	private ColumnFamilyTemplate<String, String> template;
	
	private final String KEYSPACE="Tsubscribe";
	
	private final String COLUMN_FAMILY="StatusData";
	private final int REPLICA_FACTOR=2;
	
	public JCassandra(String clusterName,String host,String port){
		this.cluster=HFactory.getOrCreateCluster(clusterName,host+port);
		
		
		if( cluster.describeKeyspace(KEYSPACE)==null){
			this.createSchema();
		}
		
		this.ksp=HFactory.createKeyspace(KEYSPACE, cluster);
		this.template=new ThriftColumnFamilyTemplate<String, String>(ksp,
                                                               this.COLUMN_FAMILY,
                                                               StringSerializer.get(),
                                                               StringSerializer.get());
	}
	
	public boolean createSchema(){
        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(KEYSPACE,
        		COLUMN_FAMILY,
                ComparatorType.BYTESTYPE);

        KeyspaceDefinition keyspaceDef = HFactory.createKeyspaceDefinition(KEYSPACE,
		              ThriftKsDef.DEF_STRATEGY_CLASS,
		              REPLICA_FACTOR,
		              Arrays.asList(cfDef));
		//Add the schema to the cluster.
		//"true" as the second param means that Hector will block until all nodes see the change.
		this.cluster.addKeyspace(keyspaceDef, true);
		return true;
	}
	
	public String update(String rowKey,JSONObject data){
		// <String, String> correspond to key and Column name.
		ColumnFamilyUpdater<String, String> updater = template.createUpdater(rowKey);
		
		updater.setString("domain", "www.datastax.com");
		updater.setLong("time", System.currentTimeMillis());

		try {
		    template.update(updater);
		    return "OK";
		} catch (HectorException e) {
		    // do something ...
			return e.getMessage();
		}
	}
}