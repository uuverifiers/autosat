package symmetryencoding.inttype;

import java.util.ArrayList;
import java.util.List;

public class Range {
	public int lowerBound;
	public int upperBound;
	
	public int startBitIndex;
	public int numBits;
	
	
	public Range(int lower, int upper, int startBitIndex){
		this.lowerBound = lower;
		this.upperBound = upper;
		
		this.numBits = (int) Math.ceil(Math.log(upperBound - lowerBound + 1) / Math.log(2));
		this.startBitIndex = startBitIndex;
	}
	
	
	/**
	 * Return the binary representation of this value
	 * note that the representation is in DNF, not CNF
	 * @param value
	 * @param numBits
	 * @return
	 */
	private int[] intToBinary(int value){
		//margin the value, compared with lowerBound
		value -= lowerBound;
		
		int[] result = new int[numBits];
		
		int run = numBits-1;
		while(value > 0)
		{
			result[run] = value % 2;
			
			//
			value = value / 2;
			run--;
		}
		
		
		return result;
	}
	
	/**
	 * Return the CNF for the formula variable != value. For NOT_EQUAL
	 * Here, we do the negation of the binary representation
	 * @param varName
	 * @param value
	 * @return
	 */
	public int[] getNotEqualClause(int value){ 
		
		int[] binaryRepresentation = intToBinary(value);
		
		for(int i = 0; i < numBits; i++){
			//for NOT_EQUAL, negation of the value
			binaryRepresentation[i] = (binaryRepresentation[i] == 0)? (i + startBitIndex): (-(i + startBitIndex));
			
		}
		
		return binaryRepresentation;
		
	}
	
	public List<int[]> getEqualClause(int value){
		
		List<int[]> result = new ArrayList<int[]>();
		
		int[] binaryRepresentation = intToBinary(value);
		
		for(int i = 0; i < numBits; i++){
			int[] clause = new int[]{(binaryRepresentation[i] == 0)? -(i + startBitIndex): (i + startBitIndex)};
			
			result.add(clause);
			
		}
		
		return result;
	}
	
	public List<int[]> inRange(){
		int upperBoundByBinaryRepresentation = (int) (lowerBound + Math.pow(2, numBits) - 1);
		
		List<int[]> result = new ArrayList<int[]>();
		for(int i = upperBound + 1; i <= upperBoundByBinaryRepresentation; i++){
			result.add(getNotEqualClause(i));
		}
		
		return result;
	}

}
