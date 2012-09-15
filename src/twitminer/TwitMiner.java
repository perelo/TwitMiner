package twitminer;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;

import twitminer.phase1.Transcript;
import twitminer.phase2.ExtractRules;
import twitminer.phase2.Rule;
import twitminer.phase4.model.CExc;
import twitminer.phase4.view.Window;

public class TwitMiner {

	private static final String FILE_APRIORI    =
							"/home/perelo/workspace/apriori/apriori";
	private static final String DIR = "/home/perelo/TwitMinerFiles/";
	public  static final String FILE_TRENDS     = DIR + "trends_notime.csv";
	public  static final String FILE_SYNO       = DIR + "nedseb_trends.syno";
	public  static final String FILE_TRANSCRIPT = DIR + "trends.conv";
	private static final String FILE_CSV_FREQ   = DIR + "trendsFreq.csv";
	private static final String FILE_TRANS      = DIR + "trends.trans";
	private static final String FILE_OUT_FREQ   = DIR + "trends.out";
	public static final String FILE_RULE       = DIR + "trendsRules.csv";
	private static final int SUPPORT = 300;
	private static final double MIN_CONF = 0.7;
	
	private static List<Rule> rules;
	
	public static void main(String[] args) {

		System.out.println("<!> TwitMiner <!>");

		try {
			/* */
			createFiles();
			/* */
			initRules();
		} catch (CExc e) {
			e.show();
			return;
		} catch (Exception e) {
			new CExc("Unknown", e.getLocalizedMessage()).show();
			return;
		}
		
		Window window = new Window();
		window.setVisible(true);
		
	}
	
	private static void initRules() throws CExc {
		
		rules = twitminer.phase2.ExtractRules.getRules();
		if (0 == rules.size())
			getRulesFromFile(twitminer.TwitMiner.FILE_RULE);
		
		if (0 == rules.size()) {
			throw new CExc("Getting rules",
						   "No rules found,\nTry to create files before");
		}
		
	}
	
	private static void getRulesFromFile(String fileRule) throws CExc {
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(fileRule));
			
			for (String line; null != (line = br.readLine()); ) {
				rules.add(Rule.toRule(line));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void createFiles() throws Exception {

		JDialog dialog = new JDialog();
		dialog.setSize(new Dimension(400, 50));
		JLabel lblCreatingFiles = new JLabel("Création des fichiers : " +
				"MinConf = " + MIN_CONF + " et " +
				"Support = " + SUPPORT);
		dialog.add(lblCreatingFiles);
		dialog.setVisible(true);
		
		try {
	
			/*
			 * Parsing des trends en entiers pour apriori
			 */
			System.out.println("parsing .csv to .trans...");
			Transcript.CsvToTrans(FILE_TRENDS, FILE_TRANS);
			/* */
			
			/*
			 * Exécution de l'algo apriori
			 */
			System.out.println("apriori algorithm in progress...");
			try {
				Runtime.getRuntime().exec(FILE_APRIORI + " " +
						FILE_TRANS   + " " +
						SUPPORT      + " " +
						FILE_OUT_FREQ)
						.waitFor();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
				throw new CExc("Apriori", e.getLocalizedMessage());
			}
			/* */
			
			/*
			 * Parsing des entiers sorti d'apriori en trends fréquents
			 */
			System.out.println("parsing .out to .csv (freqs)...");
			Transcript.OutToCsv(FILE_OUT_FREQ, FILE_CSV_FREQ);
			/* */
			
			/*
			 * Extraction des rêgles d'associations
			 * à partir des trends fréquents
			 */
			System.out.println("extracting association rules...");
			ExtractRules.extract(FILE_CSV_FREQ, FILE_RULE, MIN_CONF);
			/* */
			
			System.out.println("Done");
			
			dialog.dispose();
		
		} catch (Exception e) {
			dialog.dispose();
			throw e;
		}
		
	}

	public static List<Rule> getRules() {
		return rules;
	}

}
