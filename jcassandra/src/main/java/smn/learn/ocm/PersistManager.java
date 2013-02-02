package smn.learn.ocm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Query;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

public class PersistManager {
	private Cluster cluster;
	private String keySpace;
	
	public PersistManager(String host,String keySpace){
		if(host==null || keySpace==null){
			throw new IllegalArgumentException("host and keySpace are both required");
		}
        try {
			cluster = new Cluster.Builder().addContactPoints(host).build();
		} catch (NoHostAvailableException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
        
        this.keySpace=keySpace;
        
        //PoolingOptions pools = cluster.getConfiguration().getConnectionsConfiguration().getPoolingOptions();
        //pools.setCoreConnectionsPerHost(HostDistance.LOCAL, 2);
        //pools.setMaxConnectionsPerHost(HostDistance.LOCAL, 2);
        
//        metadata = cluster.getMetadata();
//        echo(String.format("Connected to cluster '%s' on %s.", metadata.getClusterName(), metadata.getAllHosts()));
	}
	
	@SuppressWarnings("rawtypes")
	public void createKeySpace(Map options){
		
	}
	
	public void createTable(Class<?> entity){
		
	}
	/**
	 * @param withKeySpace is set to true will create a session with inner keySpace of this cluster
	 * @return Session a session connected to this cluster
	 * @throws NoHostAvailableException
	 */
	public Session getSession(boolean withKeySpace) throws NoHostAvailableException{
		if(withKeySpace){
			return cluster.connect(keySpace);
		}else{
			return cluster.connect();
		}
	}
	
	public void useKeySpace(Session session,String keyspace) throws NoHostAvailableException{
		session.execute("USE "+keyspace);
	}
	
	public ResultSet insert(Object... entities){
		return null;
	}
	
	public ResultSet delete(Object... entities){
		return null;
	}
	public ResultSet update(Object... entities){
		return null;
	}
	
	public ResultSet execute(String query){
		return null;
	}
	
	public List<Object> execute(Query q){
		return null;
	}
}
