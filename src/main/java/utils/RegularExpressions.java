package utils;

public class RegularExpressions {
	
	public static boolean isNumber(String text) {
		if(text.matches("\\d*"))
			return true;
		return false;
	}
	
	public static void main(String args[]) {
		
			System.out.println(isNumber("123345$%^^&&"));
		
	}
}
