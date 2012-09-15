package twitminer.phase3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitminer.TwitMiner;

import twitminer.phase2.Rule;
import twitminer.phase4.model.CExc;

public class Clean {
	
	private static final String FILE_SYNO = TwitMiner.FILE_SYNO;
	private static final String SYNO_CORRUPTED =
							"Synonym file corrupted, do it again...";
	private static final String SYNO_SEPARATOR = " = ";
	private static Map<String, String> mapSyno;

	/*
	 * Synonymes
	 */
	public static String getSyno(String libelle) throws CExc {
		
		if (null == mapSyno)
			try {
				initializeMapSyno();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		String syno = mapSyno.get(libelle);

		return (null == syno ? libelle : syno);
		
	}
	
	private static void initializeMapSyno() throws IOException, CExc {
		
		System.out.println("parsing synonym file...");
		
		FileReader fr;
		try {
			fr = new FileReader(FILE_SYNO);
		} catch (FileNotFoundException e) {
			throw new CExc("Synonym", e.getLocalizedMessage());
		}
		BufferedReader br = new BufferedReader(fr);
		
		mapSyno = new HashMap<String, String>();
		
		for (String syno; null != (syno = br.readLine()); ) {
			String[] splitSyno = syno.split(SYNO_SEPARATOR);
			
			if (2 != splitSyno.length) {
				throw new CExc("Synonym", SYNO_CORRUPTED);
			}
			mapSyno.put(splitSyno[0], splitSyno[1]);
		}
		
		fr.close();
		br.close();
		
	}
	
	public static String removeDuplicates(String str, String separator) {
		
		List<String> list = new ArrayList<String>(
								Arrays.asList(str.split(separator)));
		List<String> cleanList = new ArrayList<String>(list);

		for (int i = 0; i < list.size(); ++i) {
			for (int j = 0; i > j; ++j) {
				if (list.get(i).equals(list.get(j)))
					cleanList.remove(j);
			}
		}
		
		return twitminer.phase2.ExtractRules.ContacElements(
													cleanList, separator);
	}

	/*
	 * Retrait des règles non-min et non-max
	 */
	public static void cleanRules(List<Rule> rules) throws CExc {

		if (null == rules) {
			throw new CExc("Rules", "List of rules is null");
		}

		System.out.println("cleaning rules...");

		List<Rule> cleanRules = new ArrayList<Rule>(rules);
		
		//for (Rule rule : rules) {
		for (int i = 0; i < rules.size(); ++i) {
			Rule rule = rules.get(i);
			
			List<String> x = rule.getLeft();
			List<String> y = new ArrayList<String>(x);
			y.addAll(rule.getRight());
			
			//for (Rule rPrime : rules) {
			for (int j = 0; j < rules.size(); ++j) {
				if (i == j) continue;
				
				Rule rPrime = rules.get(j);
				
				List<String> xPrime = rPrime.getLeft();
				List<String> yPrime = new ArrayList<String>(xPrime);
				yPrime.addAll(rPrime.getRight());
				
				if (y.containsAll(yPrime) &&
					yPrime.containsAll(x) &&
					rPrime.getFreq().equals(rule.getFreq())) { // non-max

					List<String> rightPrime = new ArrayList<String>(yPrime);
					rightPrime.removeAll(x);
					cleanRules.remove(new Rule(x, rightPrime, 0, 0));
				}
				else if (x.containsAll(xPrime) &&
						 ((Double) (rPrime.getFreq() / rPrime.getConf())).equals(
						  (Double) (rule.getFreq() / rule.getConf()))) { // non-min
					cleanRules.remove(rule);
				}
				
			}
			
		}
		int totRules = rules.size();
		rules.retainAll(cleanRules);
		System.out.println(cleanRules.size() + " rules left over " + totRules);
		
	}

}
