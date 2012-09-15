package twitminer.phase2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitminer.phase4.model.CExc;

public class ExtractRules {
	
	private static final String CSV_SEPARATOR = ";";
	private static List<Rule> rules = new ArrayList<Rule>();
	
	public static void extract (String fileFreq,
								String fileRules,
								double minConf) throws IOException, CExc {

		FileReader fr;
		FileWriter fw;
		try {
			fr = new FileReader(fileFreq);
			fw = new FileWriter(fileRules);
		} catch (FileNotFoundException e) {
			throw new CExc("Rule extraction", e.getLocalizedMessage());
		}
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		
		// On stock tous les motifs fréquents
		// car on a besoin de parcourir deux fois le fichier en parallèle
		List<String> freqTrends = new ArrayList<String>();
		br.readLine(); // (vide -> (nb de motifs))
		for (String str; null != (str = br.readLine());) {
			freqTrends.add(str);
		}

		// On cherche la position du premier motif fréquent
		// qui a plus d'un attribut, c'est la position à laquelle
		// on va commancer à chercher les sous-ensembles,
		// car un motif à un seul attribut n'a pas de sous-ensemble non-vide
		int posBeginMultiAtt = 0;
		for (int i = 0; i < freqTrends.size(); ++i) {
			if (freqTrends.get(i).split(CSV_SEPARATOR).length > 1) {
				posBeginMultiAtt = i;
				break;
			}
		}

		for (int i = posBeginMultiAtt; i < freqTrends.size(); ++i) {

			// On split les motifs fréquents en une liste
			// pour pouvoir utiliser les méthode containsAll() et removeAll()
			String strMotif = freqTrends.get(i);
			double freqItemset = (double) getFreq(strMotif);
			List<String> itemset = splitItemset(strMotif);

			// on pourrait ici faire une optimisation dans le même genre
			// que pour posBeginMultiAtt en ne cherchant les sous-ensembles
			// uniquement dans les motifs qui ont strictement moins
			// d'attributs que le motif en question,
			// mais est-ce qu'on y gagne vraiment ?
			
			// Au moins, ici, on ne cherche pas les sous-ensembles situés
			// "en dessous" du motif dans le fichier de motifs fréquents
			// (apriori classe les motifs par nombre d'attributs croissant)
			for (int j = 0;  i > j; ++j) {

				String strSubEns = freqTrends.get(j);
				double freqSubItemset = (double) getFreq(strSubEns);
				List<String> subItemset = splitItemset(strSubEns);

				double conf = freqItemset /
						      freqSubItemset;
				if (itemset.containsAll(subItemset) &&
					conf >= minConf) {
					// Génération de la rêgle d'association
					// en sauvegardant le motif XUY (itemset) avant de
					// lui enlever X, car sinon on perd de l'information
					// si, pour un même motif XUY, on a plusieurs rêgles
					List<String> ruleDest = new ArrayList<String>(itemset);
					ruleDest.removeAll(subItemset);
					rules.add(
							new Rule(subItemset, ruleDest, conf, freqItemset));
				}

			}
		}
		
		twitminer.phase3.Clean.cleanRules(rules);
		
		for (Rule r : rules) {
			bw.write(r.toString()); bw.newLine();
		}
		bw.flush();
		
		br.close();
		bw.close();
		fw.close();
		
	}

	public static int getFreq(String str) {
		return Integer.parseInt(
				str.substring(str.lastIndexOf("(")+1, str.length()-1));
	}

	public static List<String> splitItemset(String str) {
		// enlever la fréquence
		str = str.substring(0, str.lastIndexOf(" ("));
		
		List<String> list = 
				new ArrayList<String>(Arrays.asList(str.split(";")));
		
		return list;
	}
	
	public static String ContacElements(List<String> list, String separator) {
		String str = new String("");

		if (0 == list.size()) return str;

		for (String s : list) {
			str += s + separator;
		}

		return str.substring(0, str.lastIndexOf(separator));
	}

	// Pour la phase 4
	public static List<Rule> getRules() {
		return rules;
	}

}