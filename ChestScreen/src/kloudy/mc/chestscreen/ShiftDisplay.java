package kloudy.mc.chestscreen;

public class ShiftDisplay {
	
	public ShiftDisplay(){
		
	}
	
	/**
	 * Takes input  and converts it into boolean array that represents the character
	 * 
	 * Example:
	 * 
	 * A
	 * 
	 * boolean[] array = {   | { 0, 1, 0
	 * false, true, false,   |   1, 0, 1
	 * true, false, true,    |   1, 1, 1
	 * true, false, true,    |   1, 0, 1
	 * true, true, true,     |   1, 0, 1 }
	 * true, false, true,    |
	 * }
	 * 
	 * @return boolean array of representing a character
	 */
	private boolean[] parseCharacter(char c){		
		String value = CharacterEnums.valueOf("" + c).tosString();
		boolean[] bitMatrix = new boolean[value.length()];
		
		for(int i = 1; i <= value.length(); i++){
			
			//pixel off
			if(value.charAt(i) == ' '){
				bitMatrix[i] = false;
			}
			
			//pixel on
			else{
				bitMatrix[i] = true;
			}
		}	
		return bitMatrix;
	}
	
	/**
	 * Converts String of text into boolean array
	 * @return boolean array
	 */
	public boolean[] parseString(String str){
		
		for(int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			
			switch(c){
			
			case 'a':
				parseCharacter(c);
				break;
			}
		}
		return null;
	}
}