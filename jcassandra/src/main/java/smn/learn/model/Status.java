package smn.learn.model;

import java.util.Map;
import java.util.TreeMap;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;


class ClusterNotSetException extends Exception {
	public ClusterNotSetException(String message) {
		super(message);
	}
	private static final long serialVersionUID = -1000L;

}
class NotModelClassException extends Exception{
	private static final long serialVersionUID = -10001L;
	public NotModelClassException(Class<?> cls) {
		super("class:"+cls.getName()+" is not a subclass of BaseModel");
	}
}


class SessionManager {
	static Cluster cluster;
	static Map<String, Session> pool;
	/**
	 * 
	 * @param keyspace
	 * @return a new session sets to keyspace
	 * @throws ClusterNotSetException
	 * @throws NoHostAvailableException
	 */
	public static Session getSession(String keyspace) throws ClusterNotSetException, NoHostAvailableException {
		if (cluster == null) {
			throw new ClusterNotSetException(
					"Cluster is not set for SessionManager");
		}
		if(pool==null){
			pool=new TreeMap<String,Session>();
		}
		Session s=pool.get(keyspace);
		if(s==null){
			s=cluster.connect(keyspace);
			pool.put(keyspace, s);
		}
		return s;
	}

	public static void setCluster(Cluster c) {
		cluster = c;
	}
}

abstract class BaseModel {
	public abstract String getKeyspace();
	public abstract String getSaveQuery();

	final public Session getSession() throws NoHostAvailableException, ClusterNotSetException {
		return SessionManager.getSession(getKeyspace());
	}
	
	public static ModelManager Objects(Class<?> cls) throws NotModelClassException{
		return new ModelManager(cls);
	}
	
	public ResultSet save() throws NoHostAvailableException, ClusterNotSetException, NotModelClassException {
		return Objects(this.getClass()).save(this);
	}
	
	public static String cqls(String s){
		if(s==null)return "";
		return s.replaceAll("'", "''");
	}
	
	public static String cqls(Object value){
		if(value==null)return "";
		if(String.class.isAssignableFrom(value.getClass())){
			String v=(String)value;
			return v.replaceAll("'", "''");
		}else{
			String v=value.toString();
			return v.replaceAll("'", "''");
		}
	}
	public static Long cqln(Long l){
		if(l==null)return 0L;
		return l;
	}
	public static Integer cqln(Integer l){
		if(l==null)return 0;
		return l;
	}
	public static Double cqln(Double l){
		if(l==null)return 0D;
		return l;
	}
}

class ModelManager {
	private Class<?> clas;
	public ModelManager(Class<?> cls) throws NotModelClassException {
		if(cls.getSuperclass()!=BaseModel.class){
			throw new NotModelClassException(cls);
		}
		clas=cls;
	}

	public ResultSet save(BaseModel... baseModels) throws NoHostAvailableException, ClusterNotSetException {
		StringBuilder qs=new StringBuilder("BEGIN BATCH");
		Session sess=null;
		for(BaseModel b:baseModels){
			if(b.getClass()!=clas){
				continue;
			}
			if(sess==null){
				sess=b.getSession();
			}
			qs.append(b.getSaveQuery());
		}
		qs.append("APPLY BATCH");
		if(sess != null){
			return sess.execute(qs.toString());
		}
		return null;
	}
}
