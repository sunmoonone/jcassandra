package smn.learn.utils;

import java.io.UnsupportedEncodingException;

public class StrHelper {
	final static  char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f' };
	
	// Returns hex String representation of byte b
	static public String byteToHex(byte b) {
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	// Returns hex String representation of char c
	static public String charToHex(char c) {
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
	}

	public static String toHexString(byte[] bytes) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
			str.append(byteToHex(bytes[i]));
		return str.toString();
	}

	public static String fromHexString(String hex) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < hex.length(); i += 2) {
			str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
		}
		return str.toString();
	}
	
	public static String utf8ToHex(String str){
		if(str==null)return str;
		try {
			return toHexString(str.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static String strToHex(String str,String encode) throws UnsupportedEncodingException{
		if(str==null)return str;
		return toHexString(str.getBytes(encode));
	}
}
