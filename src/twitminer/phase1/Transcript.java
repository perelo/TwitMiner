package twitminer.phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitminer.TwitMiner;
import twitminer.phase4.model.CExc;

public class Transcript {
	
	private static Map<String, Integer> trendsMap =
									new HashMap<String, Integer>();
	
	public  static final String FILE_TRENDS     = TwitMiner.FILE_TRENDS;
	private static final String FILE_TRANSCRIPT = TwitMiner.FILE_TRANSCRIPT;
	private static final String TRANSCRIPT_SEPARATOR = " = ";
	private static final String TRANS_SEPARATOR = " ";
	private static final String OUT_SEPARATOR = TRANS_SEPARATOR;
	private static final String CSV_SEPARATOR = ";";
	
	private static final String CORRUPTED_FILE =
						"Corrupted transcription file, delete it !";
	
	// Un fichier .conv est un fichier qui associe un trend unique à un numéro
	// La première ligne correspont au nom du fichier trends.csv
	// auquel il fait référence, les lignes sont du genre
	// Afrique du Sud = 2
	// #ThingsPeopleHaveToStopDoing = 4
	// ...
	
	static {
		// On ne peux pas lancer d'exceptions dans un bloc static,
		// Donc si on a une exception ici, elle est catchée,
		// et le programme continue :/
		try {
			initializeTrendsMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (CExc e) {
			e.show();
		}
	}
	
	// Initialisation de la map de correspondance trend -> entier
	// grace à un fichier de transcription
	private static void initializeTrendsMap() throws IOException, CExc {
		
		System.out.println("creating transcription file/map...");
		
		// Un fichier de transcription est "associé" à un fichier de trends
		// il est donc nécessaire de vérifier si le fichier
		// de transcription existe bien est correspond au FILE_TREND
		if (! matchFiles()) {
			createTranscriptionFile();
			return; // la map est initialisée à la création du fichier
		}
		
		FileReader fr = new FileReader(FILE_TRANSCRIPT);
		BufferedReader br = new BufferedReader(fr);
		
		br.readLine(); // nom du fichier associé
		for (String str; null != (str = br.readLine()); ) {
			
			String[] splitStr = str.split(TRANSCRIPT_SEPARATOR);
			Integer code = null;
			
			if (2 != splitStr.length) {
				throw new CExc("Trend transcription", CORRUPTED_FILE);
			}
			try {
				code = Integer.parseInt(splitStr[1]);
			} catch (IllegalArgumentException e) {
				throw new CExc("Trend transcription", CORRUPTED_FILE);
			}
			
			trendsMap.put(splitStr[0], code);
		}
		
	}
	
	public static void createTranscriptionFile() throws IOException, CExc {
		
		System.out.println("Creating transcription file...");
		
		FileReader fr;
		FileWriter fw;
		try {
			fr = new FileReader(FILE_TRENDS);
			fw = new FileWriter(FILE_TRANSCRIPT);
		} catch (FileNotFoundException e)  {
			throw new CExc("Trend transcription", e.getLocalizedMessage());
		}
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(FILE_TRENDS); bw.newLine();
		int i = 0;
		for (String itemset; null != (itemset = br.readLine());) {
			for (String trend : itemset.split(CSV_SEPARATOR)) {
				
				String synoTrend = twitminer.phase3.Clean.getSyno(trend);
				if (trendsMap.containsKey(synoTrend)) continue;
				
				++i;
				trendsMap.put(synoTrend, i);
				bw.write(synoTrend + TRANSCRIPT_SEPARATOR + i); bw.newLine();
			}
		}
		bw.flush();
		
		// On aurais aussi pu ajouter tous les trends et utiliser
		// la cmd unix "sort -u"
		
		br.close();
		fr.close();
		bw.close();
		fw.close();
		
	}
	
	private static boolean matchFiles() {
		
		boolean qOk = true;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(FILE_TRANSCRIPT);
			br = new BufferedReader(fr);
			
			qOk = br.readLine().equals(FILE_TRENDS);
			
		} catch (IOException e) {
			return false;
		}
		
		try {
			fr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return qOk;
	}

	public static void CsvToTrans (String fileCsv, String fileTrans)
														throws IOException, CExc {
		
		FileReader fr;
		FileWriter fw;
		try {
			fr = new FileReader(fileCsv);
			fw = new FileWriter(fileTrans);
		} catch (FileNotFoundException e)  {
			throw new CExc("Csv to trans", e.getLocalizedMessage());
		}
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (String transacCsv, transacInt;
			 (transacCsv = br.readLine()) != null; ) {
			transacInt = "";
			for (String libelle : transacCsv.split(CSV_SEPARATOR)) {

				libelle = twitminer.phase3.Clean.getSyno(libelle);
				
				Integer code = trendsMap.get(libelle);
				if (null == code) {
					throw new CExc("Csv to trans", CORRUPTED_FILE);
				}
				transacInt += code + TRANS_SEPARATOR;

			}
			transacInt =
					twitminer.phase3.Clean.removeDuplicates(
												transacInt, TRANS_SEPARATOR);
			bw.write(transacInt); bw.newLine(); bw.flush();
		}
		
		br.close();
		fr.close();
		bw.close();
		fw.close();
		
	}
	
	public static void OutToCsv (String fileOut, String fileCsv)
													throws IOException, CExc {
		
		FileReader fr;
		FileWriter fw;
		try {
			fr = new FileReader(fileOut);
			fw = new FileWriter(fileCsv);
		} catch (FileNotFoundException e)  {
			throw new CExc("Out to Csv", e.getLocalizedMessage());
		}
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);

		
		Set<String> keys = trendsMap.keySet();
		
		bw.write(br.readLine()); bw.newLine(); // nb de transactions
		for (String transac; (transac = br.readLine()) != null; ) {
			List<String> freqs = Arrays.asList(transac.split(OUT_SEPARATOR));
			String csvStr = "";
			
			for (int i = 0; i < freqs.size()-1; ++i) {
				
				Integer intTrend;
				try {
					intTrend = Integer.parseInt(freqs.get(i));
				} catch (IllegalArgumentException e) {
					throw new CExc("Csv to trans", "Invalid file .out");
				}
				
				if (!trendsMap.containsValue(intTrend)) {
					throw new CExc("Csv to trans", CORRUPTED_FILE);
				}
				
				for (String key : keys) {
					if (trendsMap.get(key).equals(intTrend)) {
						csvStr += key + CSV_SEPARATOR;
						break;
					}
				}
				
			}
			csvStr.substring(0, csvStr.lastIndexOf(CSV_SEPARATOR));
			csvStr += " " + freqs.get(freqs.size()-1); // fréquence
			bw.write(csvStr); bw.newLine();
		}
		bw.flush();
		
		br.close();
		fr.close();
		bw.close();
		fw.close();

	}

}
