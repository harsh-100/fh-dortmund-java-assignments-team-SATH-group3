package com.thirdProblem.main;

public class thirdProblem {
	
	
	public static boolean isVowel(char c) {
		
		String vowels = "aeiouAEIOU";
		
		return vowels.indexOf(c) != -1;
	}

	public static void main(String[] args) {
		
		String text = "To be or not to be, that is the question;"
				+ "Whether `tis nobler in the mind to suffer"
				+ " the slings and arrows of outrageous fortune,"
				+ " or to take arms against a sea of troubles,"
				+ " and by opposing end them?";
		
		int spaces = 0, vowels = 0, letters = 0;
		
		for (int i = 0; i < text.length(); i++) {
			
			char current = text.charAt(i);
			
			if (current == ' ')
				spaces++;
			else if (Character.isLetter(current))
				letters++;
			if (isVowel(current))
				vowels++;
		}
		
		System.out.println("The text contained vowels: " + vowels + "\n" + "consonants: " + (letters - vowels) + "\n"+ "spaces: " + spaces);
	
	}	

}
