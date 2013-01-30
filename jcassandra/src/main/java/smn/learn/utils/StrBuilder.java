package smn.learn.utils;


public class StrBuilder {

	private StringBuilder builder;
	
	public StrBuilder(){
		builder=new StringBuilder();
	}
	
	public StrBuilder(String s){
		builder=new StringBuilder(s);
	}
	public StrBuilder append(String... str){
		for(String s:str){
			builder.append(s);
		}
		return this;
	}
	public StrBuilder append(Object... str){
		for(Object s:str){
			builder.append(s);
		}
		return this;
	}
	
	public String toString(){
		return builder.toString();
	}
}
