package smn.learn.cqltypes;
//ascii US-ASCII character string
//bigint 64-bit signed long
//blob Arbitrary bytes (no validation), expressed as hexadecimal
//boolean true or false
//counter Distributed counter value (64-bit long)
//decimal Variable-precision decimal
//double 64-bit IEEE-754 floating point
//float 32-bit IEEE-754 floating point
//inet IP address string in xxx.xxx.xxx.xxx form. See 1).
//int 32-bit signed integer
//list A collection of one or more ordered elements
//map A collection of one or more timestamp, value pairs
//set A collection of one or more elements
//CQL 3 Reference
//74
//text UTF-8 encoded string
//timestamp Date plus time, encoded as 8 bytes since epoch
//uuid Type 1 or type 4 UUID in standard UUID format
//timeuuid Type 1 UUID only (CQL 3)
//varchar UTF-8 encoded string
//varint Arbitrary-precision integer
public class CqlBigInt extends CqlType{
	private Long val;
	public CqlBigInt(Long value){
		val=value;
	}
	public String toString(){
		if(val==null)return "0";
		return val.toString();
	}
}
