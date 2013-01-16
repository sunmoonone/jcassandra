package smn.learn.jcassandra;

import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtil {
	protected static JSONParser parser=new JSONParser();
	
	public static Object json_decode(String json) throws ParseException {
		  return parser.parse(json);
//		  catch(ParseException pe){
//			    System.out.println("position: " + pe.getPosition());
//			    System.out.println(pe);
//			  }
	}
	public static String json_encode(Object obj){
		  return JSONValue.toJSONString(obj);
	}
}
