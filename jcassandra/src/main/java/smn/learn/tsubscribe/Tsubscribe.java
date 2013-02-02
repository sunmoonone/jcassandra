package smn.learn.tsubscribe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import joptsimple.OptionParser;
import joptsimple.OptionSet;


import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.*;

import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import smn.learn.model.Status;

public class Tsubscribe {
	private JSONParser jparser = null;
	private int subid;
	private String what;
	
	public static void initTsubscribeSchema(Session session)
			throws NoHostAvailableException {
		try {
			session.execute("CREATE KEYSPACE tsubscribe WITH replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 2 }");
		} catch (AlreadyExistsException e) { /* It's ok, ignore */
		}

		session.execute("USE tsubscribe");
		try {

			session.execute(new StringBuilder()
					.append("CREATE TABLE status (")
					.append("subid bigint,")
					.append("created_day int,")
					.append("id bigint,")
					// status ,retweeted status,comment,reply_comment
					.append("status_type int,")
					.append("status_text text,")
					.append("uid bigint,")
					.append("screen_name varchar,")
					.append("followers_count int,)")
					.append("status_source text,")
					.append("mid bigint,")
					.append("retweeted_status text,")
					.append("reply_comment text,")
					// store json str
					.append("user text,").append("verified int,")
					.append("ucreated_at int,").append("created_at int,")
					.append("comment_status text,").append("geo text,")
					.append("ctime int,")
					.append("PRIMARY KEY ((subid,created_day),id)").append(")")
					.append("WITH CLUSTERING ORDER BY (id DESC)").toString());
			//userful for query used by solr
			session.execute("CREATE INDEX created_at ON status(created_at)");
			
		} catch (AlreadyExistsException e) { /* It's ok, ignore */
		}
	}// end init schema
	
	private static void printHelp(OptionParser parser,String sub, String err) throws Exception {
		if(err!=null && err.length()>0){
			echo(err);
		}
		
		if(sub.equals("init")){
			echo("Usage: tsubscribe init [<option>]*\n");
		}else if(sub.equals("receive")){
			echo("Usage: tsubscribe receive [<option>]*\n");
		}else{
			echo("Usage: tsubscribe subcommand [<option>]*\n");
			echo("subcommand can be:");
			echo("receive,init");
			echo("type init -h to see options for init");
			echo("type receive -h to see options for receive");
			echo();
		}
        parser.printHelpOn(System.out);
    }

    public static void run(String[] args) throws Exception {
    	OptionParser parser = new OptionParser();
    	parser.accepts("-h", "Show this help message");
    	
        
    	if (args.length < 1) {
    		
            printHelp(parser, "","Missing argument, you must at least provide the subcommand to do");
            System.exit(1);
        }
    	if(args[0].equals("-h") || args[0].equals("--help")){
    		printHelp(parser, "","");
            System.exit(0);
    	}
    	
    	String cmd=args[0];
    	if(!cmd.equals("init") && !cmd.equals("receive")){
            printHelp(parser, "","subcommand is not supported");
            System.exit(1);
    	}
    	
    	
        if(cmd.equals("init")){
            parser.accepts("cip", "The cluster host ip to connect to").withRequiredArg().ofType(String.class).defaultsTo("127.0.0.1");
            parser.accepts("cport", "The cluster port to connect to").withRequiredArg().ofType(Integer.class).defaultsTo(9042);
            
        }else if(cmd.equals("receive")){
        	parser.accepts("what", "What to receive. Can be: status, comment").withRequiredArg().ofType(String.class).defaultsTo("comment");
            parser.accepts("subid", "The subscribe id").withRequiredArg().ofType(Integer.class);
            parser.accepts("ip", "The subscribe server ip to receive from. This is optional but is useful if the servers comiled inside is dead.");
            parser.accepts("cip", "The cluster host ip to connect to").withRequiredArg().ofType(String.class).defaultsTo("127.0.0.1");
            parser.accepts("cport", "The cluster port to connect to").withRequiredArg().ofType(Integer.class).defaultsTo(9042);
        }

         
         String[] opts = new String[args.length - 1];
         System.arraycopy(args, 1, opts, 0, opts.length);

         OptionSet options = null;
         try {
             options = parser.parse(opts);
         } catch (Exception e) {
             printHelp(parser, cmd,"Error parsing options: " + e.getMessage());
             System.exit(1);
         }


        try {
            // Create session to hosts
            Cluster cluster = new Cluster.Builder().addContactPoints(String.valueOf(options.valueOf("cip"))).build();

            //PoolingOptions pools = cluster.getConfiguration().getConnectionsConfiguration().getPoolingOptions();
            //pools.setCoreConnectionsPerHost(HostDistance.LOCAL, 2);
            //pools.setMaxConnectionsPerHost(HostDistance.LOCAL, 2);

            
            Metadata metadata = cluster.getMetadata();
            echo(String.format("Connected to cluster '%s' on %s.", metadata.getClusterName(), metadata.getAllHosts()));
            
            if(cmd.equals("init")){
            	echo("Creating schema...");
                initTsubscribeSchema(cluster.connect());
                System.exit(0);
            }
            
            if(cmd.equals("receive")){
            	Integer subid=(Integer)options.valueOf("subid");
            	String what=(String)options.valueOf("what");
            	
            	String ip="";
            	if(options.valueOf("ip")!=null){
            		ip=String.valueOf(options.valueOf("ip"));
            	}
            	new Tsubscribe().receive(cluster,subid, what,ip);
            }

            System.exit(0);

        } catch (NoHostAvailableException e) {
            System.err.println("No alive hosts to use: " + e.getMessage());
            System.exit(1);
        } catch (QueryExecutionException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (QueryValidationException e) {
            System.err.println("Invalid query: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
	
	
	private static SimpleDateFormat dtf;
	
	private static void echo(String... msgs){
		if(dtf==null){
			dtf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		}
		for(String msg:msgs){
			System.out.printf("%s - %s\n",dtf.format(new Date()),msg);
		}
		if(msgs.length<=0){
			System.out.println();
		}
	}
	private static void echo(Object... msgs){
		if(dtf==null){
			dtf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		}
		for(Object msg:msgs){
			System.out.print(dtf.format(new Date()));
			System.out.println(" - ");
			System.out.println(msg);
		}
		if(msgs.length<=0){
			System.out.println();
		}
	}
	
	private Long since_id;
	private Session sess;
	public void receive(Cluster cluster,Integer subid,String what,String ip){
		if(!what.equals("status") && !what.equals("comment")){
			System.out.println("receiving for "+what+" is not supported");
			System.exit(1);
		}
		sess=cluster.connect();
		HttpClient httpclient = new DefaultHttpClient();
		try{
			StringBuilder uri=new StringBuilder("http://");
			String response;
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			
			echo("receiving "+what+" "+subid);
			if(ip.isEmpty()){
				if(what.equals("status")){
					uri.append("180.149.153.38");
					echo("from 180.149.153.38");
					//"180.149.153.38";
					//180.149.153.39
				}else if (what.equals("comment")){
					uri.append("180.149.153.40");
					echo("from 180.149.153.40");
//					$host='180.149.153.40';
					//180.149.153.41
				}
			}else{
				uri.append(ip);
				echo("from "+ip);
			}
			
			uri.append("/datapush/").append(what).append("?subid=").append(subid);
			if(since_id >0){
				uri.append("&since_id=").append(since_id);
			}
			echo("with:",uri.toString());
			HttpGet httpget = new HttpGet(uri.toString());
			
			while(true){
				// System.out.println("executing request " + httpget.getURI());

				// Create a response handler
				response = httpclient.execute(httpget, responseHandler);
				this.saveStatus(response);
				Thread.sleep(500);
			}
		} catch (ClientProtocolException e) {
			 e.printStackTrace();
		} catch (IOException e) {
			 e.printStackTrace();
		} catch(InterruptedException ine){
			System.err.println(ine.getMessage());
			System.exit(2);
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}
	private Set<Long> sample;
	private int sample_limit=10000;
	protected boolean test_dup(Long id){
		if(sample==null){
			sample=new TreeSet<Long>();
		}
		boolean is_dup=false;
		if(sample.contains(id)){
			is_dup=true;
		}
		if(sample.size() > sample_limit){
			//remove 1000
			//in ascending order
			int count=0;
			Iterator<Long> it = sample.iterator();
			while(it.hasNext()){
				sample.remove(it.next());
				count++;
				if(count >= 1000){
					break;
				}
			}
		}
		//add to sample
		sample.add(id);
		return is_dup;
	}
	
	
	public void saveStatus(String jsonstr) {
		if (this.jparser == null) {
			this.jparser = new JSONParser();
		}
		if(jsonstr==null || jsonstr.isEmpty()){
			echo("content is empty");
		}
//		{"id":1301080032461886,"text":{
//			"type":"status","event":"add",
//			"status":{"created_at":"Tue Jan 08 14:39:20 +0800 2013","id":3532206679477825,"text":"大部分女孩子一年四季告别裤子了，都说穿裙子的女人才是漂亮的女人，本款让您在寒冷的冬天里展现魅力，配上本款时尚休闲卫衣，毛绒靴子。在这个冬天里，你才是最迷人的！留住青春，让我们开始吧！ 本店女装，女鞋，新品上架淘宝店铺大促销，全场七折出售. http://t.cn/zjuCnqw","source":"<a href=\"http://app.weibo.com/t/feed/GohEh\" rel=\"nofollow\">晒宝堂3D商城</a>","favorited":false,"truncated":false,"in_reply_to_status_id":"","in_reply_to_user_id":"","in_reply_to_screen_name":"","thumbnail_pic":"http://ww4.sinaimg.cn/thumbnail/95dc0740jw1e0m5g0mbv4j.jpg","bmiddle_pic":"http://ww4.sinaimg.cn/bmiddle/95dc0740jw1e0m5g0mbv4j.jpg","original_pic":"http://ww4.sinaimg.cn/large/95dc0740jw1e0m5g0mbv4j.jpg","geo":null,"mid":"3532206679477825","user":{"id":2514224960,"screen_name":"绚丽花街","name":"绚丽花街","province":"100","city":"1000","location":"","description":"","url":"","profile_image_url":"http://tp1.sinaimg.cn/2514224960/50/0/1","domain":"","gender":"m","followers_count":0,"friends_count":0,"statuses_count":0,"favourites_count":0,"created_at":"Thu Jan 01 08:00:00 +0800 1970","following":false,"allow_all_act_msg":false,"geo_enabled":true,"verified":false
//			}
//		}
//		}
//		}
		try {
			JSONObject obj = (JSONObject)jparser.parse(jsonstr);
//			JSONObject is a java.util.Map and JSONArray is a java.util.List
			System.out.println(obj);

			since_id=(Long) obj.get("id");
				
			StringBuilder sb=new StringBuilder("insert tinto");
			ResultSet rs= sess.execute(sb.toString());
			echo(rs);
		} catch (ParseException e) {
			echo(e.getMessage());
		} catch(NoHostAvailableException e){
			echo(e.getMessage());
		}
	}
}
