package com.nmbs.util;

import android.content.Context;

/**
 * This class Deal with credit card.
 */
public class CardUtils {

	/**
	 * Must be 17 number
	 * @param s1
	 * @return
	 */
	public static boolean isValidFTPCard(String s1) {
		if (s1 != null && s1.trim().length() == 17 ) {			
			try {
				Double.parseDouble(s1);				
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * The Luhn test is used by some credit card companies to distinguish valid
	 * credit card numbers from what could be a random selection of digits.
	 * 
	 * @param number
	 * @return
	 */
	public static boolean luhnTest(String number) {
		int s1 = 0, s2 = 0;
		String reverse = new StringBuffer(number).reverse().toString();
		for (int i = 0; i < reverse.length(); i++) {
			int digit = Character.digit(reverse.charAt(i), 10);
			if (i % 2 == 0) {// this is for odd digits, they are 1-indexed in
								// the algorithm
				s1 += digit;
			} else {// add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
				s2 += 2 * digit;
				if (digit >= 5) {
					s2 -= 9;
				}
			}
		}
		return (s1 + s2) % 10 == 0;
	}
	
	public static Card validateCard(Context context, String card1, String card2){
		
		if (card2 == null) {
			if (card1.equals("")) {
				return Card.EMPTY_FTPCARD;
			} 
			if(!card1.equals("")) {
				if(card1.length() == 6){
					return Card.EMPTY_FTPCARD;
				}
				if (!isValidFTPCard(card1) ) {
					return Card.EMPTY_INCORRECT;
				}
				if (!luhnTest(card1) ) {
					return Card.EMPTY_INCORRECT;
				}				
			}
		} else {
			if (card1.equals("")) {
				return Card.EMPTY_FTPCARD;
			} else if(card2.equals("")) {
				return Card.EMPTY_FTPCARD;
			}else if(!"".equals(card1) && !"".equals(card2)) {
				if(card2.length() == 6){
					return Card.EMPTY_FTPCARD;
				}
				if (!isValidFTPCard(card1) || !isValidFTPCard(card2) || !luhnTest(card1) || !luhnTest(card2)) {
					return Card.EMPTY_INCORRECT;
				}
				if (card1.equals( card2)) {
					return Card.REPEAT_CARD;
				}
			}
		}
		return Card.CORRECT;
	}
	
	public enum Card {
		CORRECT, EMPTY_FTPCARD, EMPTY_INCORRECT, REPEAT_CARD
	}

}
