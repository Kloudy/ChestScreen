package kloudy.mc.enums;
/**
 * Set of Character Enumerated with pixel data.
 */

public enum CharacterEnums{
	
	A (	" a " +
		"a a" +
		"aaa" +
		"a a" +
		"a a", 3),
		
	B (	"bb " +
		"b b" +
		"bb " +
		"b b" +
		"bb ", 3),
	
	C ( " cc" +
		"c  " +
		"c  " +		
		"c  " +
		" cc", 3),
	
	D ( "dd " +
		"d d" +
		"d d" +
		"d d" +
		"dd ", 3),
	
	E ( "eee" +
		"e  " +
		"eee" +
		"e  " +
		"eee", 3),
	
	F ( "fff" +
		"f  " +
		"fff" +
		"f  " +
		"f  ", 3),
	
	G ( " gg " +
		"g   " +
		"g gg" +
		"g  g" +
		" ggg", 4),
	
	H ( "h h" +
		"h h" +
		"hhh" +
		"h h" +
		"h h", 3),
	
	I ( "iii" +
		" i " +
		" i " +
		" i " +
		"iii", 3),
	
	J ( "jjj" +
		"  j" +
		"j j" +
		" j ", 3),
	
	K ( "k   k" +
		"k  k " +
		"kk   " +
		"k  k " +
		"k   k", 4),
	
	L ( "l  " +
		"l  " +
		"l  " +
		"l  " +
		"lll", 3),
	
	M ( "m   m" +
		"mm mm" +
		"m m m" +
		"m   m" +
		"m   m", 5),
	
	N ( "n   n" +
		"nn  n" +
		"n n n" +
		"n  nn" +
		"n   n", 5),
	
	O ( " oo " +
		"o  o" +
		"o  o" +
		"o  o" +
		" oo ", 4),
	
	P ( "ppp" +
		"p p" +
		"ppp" +
		"p  " +
		"p  ", 3),
	
	Q ( " qqq " +
		"q   q" +
		"q q q" +
		"q  q " +
		" qq q", 5),
		
	R ( "rr " +
		"r r" +
		"rr " +
		"r r" +
		"r r", 3),
	
	S ( " ss" +
		"s  " +
		" s " +
		"  s" +
		"ss ", 3),
	
	T ( "ttt" +
		" t " +
		" t " +
		" t " +
		" t ", 3),
	
	U ( "u u" +
		"u u" +
		"u u" +
		"u u" +
		"uuu", 3),

	V ( "v v" +
		"v v" +
		"v v" +
		"v v" +
		" v ", 3),
	
	W ( "w   w" +
		"w   w" +
		"w w w" +
		"w w w" +
		" w w ", 5),
	
	X ( "x   x" +
		" x x " +
		"  x  " +
		" x x " +
		"x   x", 4),
	
	Y ( "y  y" +
		"y  y" +
		" yyy" +
		"   y" +
		" yy ", 4),
	
	Z ( "zzz " +
		"   z" +
		" zz " +
		"z   " +
		" zzz", 4),
	
	ZERO (  "000" +
			"0 0" +
			"0 0" +
			"0 0" +
			"000", 3),


	ONE (   " 1 " +
			"11 " +
			" 1 " +
			" 1 " +
			"111", 3),
	
	TWO (   "222" +
			"  2" +
			"222" +
			"2  " +
			"222", 3),
	
	THREE ( "333" +
			"  3" +
			"333" +
			"  3" +
			"333", 3),
	
	FOUR (  "4 4" +
			"4 4" +
			"444" +
			"  4" +
			"  4", 3),
	
	FIVE (  "555" +
			"5  " +
			"555" +
			"  5" +
			"555", 3),
	
	SIX  (  "666" +
			"6  " +
			"666" +
			"6 6" +
			"666", 3),
	
	SEVEN ( "777" +
			"  7" +
			"  7" +
			"  7" +
			"  7", 3),
	
	EIGHT ( "888" +
			"8 8" +
			"888" +
			"8 8" +
			"888", 3),
	
    NINE  ( "999" +
			"9 9" +
			"999" +
			"  9" +
			"999", 3);

	 private String charString;
	 private int charWidth;
	 
	 CharacterEnums(String charString, int charWidth){
		 this.charString = charString;
		 this.charWidth = charWidth;
	 }
	 
	 public String tosString(){
		 return charString;
	 }
	 
	 public int getCharWidth(){
		 return charWidth;
	 }
}