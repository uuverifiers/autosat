package symmetryencoding;

import java.util.HashSet;
import java.util.Set;

import symmetryencoding.parasym.CheckingConditionBuilder;
import symmetryencoding.parasym.CheckingConditionBuilder.Condition;

public class Configuration {
	public static int MAX_LENGTH_TO_ACCEPT = 3;
	public static Condition[] CHECKING_ORDER = new Condition[]{
													Condition.Automorphism,
													Condition.Permutative,
													Condition.OutputUnviersal,
													Condition.InputUniversal,
													Condition.InjectiveInput,
													Condition.InjectiveOutput,
													Condition.Copycat
												};
	
	
	public static Set<Condition> offConditions = new HashSet<CheckingConditionBuilder.Condition>();
	public static boolean offParikh = false;
	public static boolean exportTransducer = false;
	
	public static String inputFileName;
}
