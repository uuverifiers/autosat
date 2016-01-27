package symmetryencoding;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import symmetryencoding.cegeneration.WordGenerator;

public class WordGeneratorTest {

	@Test
	public void test1(){
		int numLetters = 4;
		int minLength = 1;
		int maxLength = 3;
		WordGenerator inputOutputWord = new WordGenerator(numLetters, minLength, maxLength);
		List<List<Integer>> allWords = inputOutputWord.generate();
		
		Assert.assertEquals(84, allWords.size());
		for(List<Integer> word: allWords){
			System.out.println(word);
		}
		
	}
}
