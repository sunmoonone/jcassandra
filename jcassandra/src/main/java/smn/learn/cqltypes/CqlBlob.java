package smn.learn.cqltypes;

import java.io.UnsupportedEncodingException;

import smn.learn.utils.StrHelper;

/**
 * Arbitrary bytes (no validation), expressed as hexadecimal
 * @author sunmoonone
 *
 */
public class CqlBlob extends CqlType{
	private byte[] value;
	public CqlBlob(){
		
	}
	
	public CqlBlob(String str,String charsetName) throws UnsupportedEncodingException{
		if(str==null)throw new NullPointerException("str");
		if(charsetName==null)throw new NullPointerException("charsetName");
		value=str.getBytes(charsetName);
	}
	
	public CqlBlob(byte[] bytes){
		value=bytes;
	}
	
	public String toString(){
		return StrHelper.toHexString(value);
	}
}
