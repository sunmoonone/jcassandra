package smn.learn.ocm.cqltypes;

public class CqlBoolean implements CqlType{
	private boolean value=false;
	
	public boolean getValue(){
		return value;
	}
	
	public CqlBoolean(){
		Boolean b;
	}
	public CqlBoolean(boolean val){
		value=val;
	}
	public String toString(){
		return String.valueOf(value);
	}
	public boolean or(boolean val){
		return value || val;
	}
	public boolean or(CqlBoolean val){
		return value || val.getValue();
	}
	
	public boolean and(boolean val){
		return value && val;
	}
	
	public boolean and(CqlBoolean val){
		return value && val.getValue();
	}
	
	public boolean not(){
		return !value;
	}

	
	public CqlType valueOf(String s) {
		return new CqlBoolean(Boolean.valueOf(s));
	}
}
