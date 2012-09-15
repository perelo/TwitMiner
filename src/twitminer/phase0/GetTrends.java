package twitminer.phase0;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.Location;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class GetTrends {
	
	public static String parseDateTime (Calendar cal) {
		return cal.get(Calendar.DAY_OF_MONTH) + "/" +
			   (cal.get(Calendar.MONTH)+1) + "/" +
			   cal.get(Calendar.YEAR) + ":" +
			   cal.get(Calendar.HOUR_OF_DAY) + "h" + 
			   cal.get(Calendar.MINUTE);
	}
	
	public static void main(String[] args) {

		try {
			Twitter twitter = new TwitterFactory().getInstance();

			// libelles des locations pour lesquels on veux les trends
			List<String> libelles = new ArrayList<String>();
			libelles.add("Afrique du Sud");
			libelles.add("Australie");
			libelles.add("Brésil");
			libelles.add("Canada");
			libelles.add("Colombie");
			libelles.add("Espagne");
			libelles.add("États-Unis");
			libelles.add("France");
			libelles.add("Inde");
			libelles.add("Italie");
			libelles.add("Monde");
			libelles.add("Nigéria");
			libelles.add("Pays-Bas");
			libelles.add("Pérou");
			libelles.add("Philippines");
			libelles.add("République Dominicaine");
			libelles.add("Royaume Uni");
			libelles.add("Singapour");
			libelles.add("Suède");
			libelles.add("Turquie");
			
			// mapLocations contient toutes les locations
			// où des trends sont disponibles
			List<Location> locations = twitter.getAvailableTrends();
			Map<String, Integer> mapLocations = new HashMap<String, Integer>();
			for (Location loc : locations) {
				mapLocations.put(loc.getName(), loc.getWoeid());
			}
			
			// récupération des trends pour les locations définies
			// et écriture dans le fichier .csv
			FileWriter fwTrends = new FileWriter(
						"/home/eloi/Dropbox/ProjBDA/trends.csv", true);
			BufferedWriter outputTrends = new BufferedWriter(fwTrends);
			
			FileWriter fwError = new FileWriter(
					new File("/home/eloi/Dropbox/ProjBDA/trends_error.txt"));
			BufferedWriter outputError = new BufferedWriter(fwError);
			
			Trends trends = null;
			String tuple;
			GregorianCalendar cal;

			while (true) {
				for (String loc : libelles) {
					cal = new GregorianCalendar();
					try {
						trends = twitter.getLocationTrends(
												mapLocations.get(loc));
					} catch (NullPointerException e) {
						// si les trends pour un pays ne sont plus dispos
						outputError.write(loc + " non dispo à " +
						                  parseDateTime(cal));
						outputError.newLine();
						outputError.flush();
						continue;
					} catch (TwitterException e) {
						long sleepyTime = 30000;
						outputError.write("TwitterException at " +
										  parseDateTime(cal) + " => " +
										  e.getStatusCode() + 
										  " => sleeping " + sleepyTime);
						outputError.newLine();
						outputError.flush();
						Thread.sleep(sleepyTime);
					}
					cal = new GregorianCalendar();
					tuple = loc + ";" + parseDateTime(cal);
					for (Trend trend : trends.getTrends()) {
						tuple += ";" + trend.getName();
					}
					outputTrends.write(tuple);
					outputTrends.newLine();
				}
				outputTrends.flush();
				
				Thread.sleep(5 * 60000 - 4000); // 4min 56sec
				// (il faut environ 5sec pour récupérer tous les trends)
				
			}
			
			/* * /
			List<Location> locations = twitter.getAvailableTrends();
			for (Location loc : locations) {
				if (loc.getPlaceCode() == 12) // uniquement les pays
					System.out.println(loc.getName() + " " + loc.getWoeid());
			}
			/* */

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
