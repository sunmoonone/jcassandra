package smn.learn.cqltypes;

public class CqlBoolean implements CqlType,Comparable<Boolean> {
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

	public int compareTo(Boolean o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
