package symmetryencoding.cegeneration;

import java.util.ArrayList;
import java.util.List;

public class WordGenerator {

	private int minLength;
	private int maxLength;
	private int numLetters;
	
	public WordGenerator(int numLetters, int minLength, int maxLength){
		this.numLetters = numLetters;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	public List<List<Integer>> generate(){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for(int i = minLength; i <= maxLength; i++){
			result.addAll(getWordsToAccept(i));
		}
		
		return result;
	}
	
	private List<List<Integer>> getWordsToAccept(int length){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		
		int[] indexSelection = new int[length];
		
		do{
			List<Integer> word = getWord(indexSelection);
			result.add(word);
		}while(increase(indexSelection));
		
		return result;
	}
	
	
	/**
	 * indexSelection[i] = a then the i.th label in the word is a
	 * @param indexSelection
	 * @return
	 */
	private boolean increase(int[] indexSelection){
		for(int i = indexSelection.length - 1; i >= 0; i--){
			if(indexSelection[i] < numLetters - 1){
				indexSelection[i]++;
				for(int j = i + 1; j < indexSelection.length; j++){
					indexSelection[j] = 0;
				}
				return true;
			}
		}
		
		return false;
	}
	
	private List<Integer> getWord(int[] indexSelection){
		List<Integer> word = new ArrayList<Integer>();
		for(int i = 0; i < indexSelection.length; i++){
			word.add(indexSelection[i]);
		}
		
		return word;
	}
	
	
}
