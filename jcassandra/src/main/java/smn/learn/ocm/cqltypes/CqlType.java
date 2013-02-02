package smn.learn.ocm.cqltypes;

public interface CqlType{
	public String toString();
	public CqlType valueOf(String s);
}
