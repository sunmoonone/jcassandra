package smn.learn.tsubscribe;

import java.util.Map;
import org.json.simple.JSONValue;

import smn.learn.ocm.annotations.Column;
import smn.learn.ocm.annotations.Table;
import smn.learn.utils.StrBuilder;

@Table(keySpace="tsubscribe")
public class StatusModel {
	@Column(primaryKey=true,primaryOrder=1,primaryNested=true)
	private long subid;
	
	@Column(primaryKey=true,primaryOrder=2,primaryNested=true)
	private int created_day;
	
	@Column(primaryKey=true,primaryOrder=3)
	private long id;
	
	@Column
	private int status_type;

	@Column
	private String status_text;
	
	@Column
	private long uid;
	
	@Column
	private String screen_name;
	
	@Column
	private int followers_count;
	
	@Column
	private String status_source;
	
	@Column
	private long  mid;
	
	@Column(embedded=true)
	private RetweetedStatus retweeted_status;
	
	@Column(embedded=true)
	private ReplyComment reply_comment;
	
	@Column(embedded=true)
	private User user;
	
	@Column(embedded=true)
	private CommentStatus comment_status;
	
	@Column
	private String geo;
	
	@Column
	private int ucreated_at;
	
	@Column
	private boolean verified_type;

	@Column(indexed=true)
	private int created_at;
	
	private Map<String,?> _values;
	private Map<String,?> _st;
	private Map<String,?> _status;
	
	private boolean _loaded=false;
	final public int type_status=1;
	final public int type_comment=2;
	final public int type_retweeted_status=4;
	final public int type_reply_comment=8;
	
	public StatusModel(Map<String,?> data){
		setValues(data);
	}
	/**
	 * used by Model manager to load values from cluster
	 * @param data
	 */
	public void _load(Map<String,?> data){
		setValues(data);
		_loaded=true;
	}
	public boolean isStatus(){
		return (getStatus_type() & type_status)!=0;
	}
	public boolean isComment(){
		return (getStatus_type() & type_comment)!=0;
	}
	
	protected void setValues(Map<String,?> data){
		_values=data;
		_st=(Map<String, ?>) _values.get("text");
		if(isStatus()){
			_status=(Map<String,?>)_st.get("status");
		}else if(isComment()){
			_status=(Map<String,?>)_st.get("comment");
		}
	}
	
	
	/**
	 * parse created_at to a timestamp in seconds
	 * @return
	 * @throws ParseException
	 */
	private long getCreated_at() throws ParseException{
//		String stime="Tue Jan 08 14:39:20 +0800 2013";
		long t= new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").
				parse((String)_status.get("created_at")).getTime();
		return t/1000;
	}
	/**
	 * get the current timestamp
	 * @return
	 */
	private static long time(){
		return new Date().getTime()/1000;
	}
	/**
	 * return the created_at day in seconds
	 * @return
	 * @throws ParseException 
	 */
	public long getCreated_day() throws ParseException{
		Date d = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").
				parse((String)_status.get("created_at"));
		Calendar cal= Calendar.getInstance();
		cal.setTime(d);
		long t=d.getTime()/1000;
		return t-(cal.get(Calendar.HOUR_OF_DAY)*3600+cal.get(Calendar.MINUTE)*60+cal.get(Calendar.SECOND));
	}
	
	public long getSubid(){
		return (Long)_values.get("id");
	}
	
	public boolean hasRetweeted_status(){
		Object obj=_st.get("status");
		if(obj==null)return false;
		Map m=(Map)obj;
		Object r=m.get("retweeted_status");
		if(r==null)return false;
		Map rm=(Map)r;
		return rm.size()>0;
	}
	
	public boolean hasReply_comment(){
		Object obj=_st.get("comment");
		if(obj==null)return false;
		Map m=(Map)obj;
		Object r=m.get("reply_comment");
		if(r==null)return false;
		Map rm=(Map)r;
		return rm.size()>0;
	}
	
	public String getRetweeted_status(){
		Object obj=_st.get("status");
		if(obj==null)return null;
		Map m=(Map)obj;
		Object r=m.get("retweeted_status");
		if(r==null)return null;
		Map rm=(Map)r;
		return JSONValue.toJSONString(rm);
	}
	
	public String getReply_comment(){
		Object obj=_st.get("comment");
		if(obj==null)return null;
		Map m=(Map)obj;
		Object r=m.get("reply_comment");
		if(r==null)return null;
		Map rm=(Map)r;
		return JSONValue.toJSONString(rm);
	}
	
	public int getStatus_type(){
		String type=(String)_st.get("type");
		int _type=0;
		if(type.equals("status")){
			_type=_type|type_status;
			Map obj=(Map)_st.get("status");
			Object r=obj.get("retweeted_status");
			if(r!=null){
				Map rm=(Map)r;
				if(rm.size()>0){
					_type=_type|type_retweeted_status;
				}
			}
		}else if (type.equals("comment")){
			
			_type=_type|type_comment;
			Map obj=(Map)_st.get("comment");
			Object r=obj.get("reply_comment");
			if(r!=null){
				Map rm=(Map)r;
				if(rm.size()>0){
					_type=_type|type_reply_comment;
				}
			}
		}
		return _type;
	}
	public long getCtime(){
		return time();
	}
	public long getId(){
		return (Long)_status.get("id");
	}
	public String getStatus_text(){
		return (String)_status.get("text");
	}
	public long getUid(){
		Map user=(Map)_status.get("user");
		return (Long)user.get("id");
	}
	public String getScreen_name(){
		Map user=(Map)_status.get("user");
		return (String)user.get("screen_name");
	}
	public int getFollowers_count(){
		Map user=(Map)_status.get("user");
		return (Integer)user.get("followers_count");
	}
	public String getUser(){
		Object user=_status.get("user");
		return JSONValue.toJSONString(user);
	}
	public String getStatus_source(){
		Object obj=_status.get("source");
		if(obj==null)return null;
		return (String)obj;
	}
	public Long getMid(){
		Object obj=_status.get("mid");
		if(obj==null)return null;
		return (Long)obj;
	}
	
	public Integer getVerified(){
		Map user=(Map)_status.get("user");
		Boolean b=(Boolean)user.get("verified");
		if(b){
			return 1;
		}else{
			return 0;
		}
	}
	public Long getUcreated_at() throws ParseException{
		Map user=(Map)_status.get("user");
		String ct=(String)user.get("created_at");
		long t= new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").
				parse(ct).getTime();
		return t/1000;
	}
	
	public String getComment_status(){
		Object stat=_status.get("status");
		if(stat==null)return null;
		return JSONValue.toJSONString(stat);
	}
	public String getGeo(){
		Object geo=_status.get("geo");
		if(geo==null)return null;
		return JSONValue.toJSONString(geo);
	}
	public Long getIn_reply_to_status_id(){
		Object in_=_status.get("in_reply_to_status_id");
		if(in_==null)return null;
		return (Long)in_;
	}
	public Long getIn_reply_to_user_id(){
		Object in_=_status.get("in_reply_to_user_id");
		if(in_==null)return null;
		return (Long)in_;
	}
	public String getIn_reply_to_screen_name(){
		Object in_=_status.get("in_reply_to_screen_name");
		if(in_==null)return null;
		return (String)in_;
	}
	
	public String getSaveQuery(){
		if(_values==null)return null;
		
		StrBuilder sb=new StrBuilder();
		if(_loaded){
			return null;
//			UPDATE users SET password = 'ps22dhds' WHERE userID = 'user2'
		}else{
//			//cols
			try {
				sb.append("INSERT INTO status (")
				.append("subid ,")
				.append("created_day ,")
				.append("id ,")
				// status ,retweeted status,comment,reply_comment
				.append("status_type ,")
				.append("status_text ,")
				.append("uid ,")
				.append("screen_name ,")
				.append("followers_count ,")
				.append("status_source ,")
				.append("mid ,")
				.append("retweeted_status ,")
				.append("reply_comment ,")
				// store json str
				.append("user ,")
				.append("verified ,")
				.append("ucreated_at ,")
				.append("created_at ,")
				.append("comment_status ,")
				.append("geo ,")
				.append("in_reply_to_status_id ,")
				.append("in_reply_to_user_id ,")
				.append("in_reply_to_screen_name ,")
				.append("ctime")
//			.append("PRIMARY KEY ((subid,created_day),id)").append(")")

				.append(")").append(" values (")
				//values
//			.append("subid bigint,")
//					.append("created_day varchar,")
//					.append("id bigint,")
//					// status ,retweeted status,comment,reply_comment
//					.append("status_type int,")
//					.append("status_text text,")
//					.append("uid bigint,")
//					.append("screen_name varchar,")
//					.append("followers_count int,)")
//					.append("status_source text,")
//					.append("thumbnail_pic varchar,")
//					.append("bmiddle_pic varchar,")
//					.append("original_pic varchar,")
//					.append("mid varchar,")
//					.append("retweeted_status text,")
//					.append("reply_comment text,")
//					// store json str
//					.append("user text,").append("verified int,")
//					.append("ucreated_at int,").append("created_at int,")
//					.append("status text,").append("geo text,")
//					.append("in_reply_to_status_id bigint,")
//					.append("in_reply_to_user_id bigint,")
//					.append("in_reply_to_screen_name varchar,")
//					.append("ctime int,")
				.append(getSubid(),",")
				.append(getCreated_day(),",")
				.append(getId(),",")
				// status ,retweeted status,comment,reply_comment
				.append(getStatus_type(),",")
				
				.append("'",cqls(getStatus_text()),"',")
				.append(getUid(),",")
				.append("'",getScreen_name(),"',")
				.append(getFollowers_count(),",")
				.append("'",cqls(getStatus_source()),"',")
				.append(getMid(),",")
				.append("'",cqls(getRetweeted_status()),"',")
				.append(values.get("reply_comment"))
				// store json str
//				.append(values.get("user"))
//				.append(values.get("verified"))
//				.append(values.get("ucreated_at"))
//				.append(getCreated_at())
//				.append(values.get("comment_status"))
//				.append(values.get("geo"))
//				.append(values.get("in_reply_to_status_id"))
//				.append(values.get("in_reply_to_user_id"))
//				.append(values.get("in_reply_to_screen_name"))
				.append(time());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sb.append(")");
		}
		return null;
	}
}


class CommentStatus{
	
}
class ReplyComment{
	
}
class RetweetedStatus{
	
}
class User{
	
}