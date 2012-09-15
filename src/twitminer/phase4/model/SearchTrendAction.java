package twitminer.phase4.model;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import twitminer.TwitMiner;
import twitminer.phase2.Rule;
import twitminer.phase4.view.Window;
import twitminer.phase4.view.Window.OrderBy;
import twitminer.phase4.view.Window.SearchIn;

public class SearchTrendAction implements ActionListener {

	private Window window;
	
	private String trend;
	private Float minConf;
	private Float minFreq;
	private SearchIn side;
	private OrderBy order;

	private Rule currentRule;
	private List<Rule> rules;
	private List<Rule> rulesFound = new ArrayList<Rule>();
	
	private Pattern p;
	
	public SearchTrendAction(Window window) {
		this.window = window;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		try {
			getSearchComponents();
			getRules();
			showRules();
			rulesFound.clear();
		} catch (CExc e) {
			e.show();;
		}

	}

	private void getRules() throws CExc {
		
		rules = TwitMiner.getRules();
		extractRules();
		orderRules();
		
	}

	private void extractRules() throws CExc {
		
		Callable<List<String>> side = getSideFunc();

		try {
			for (Rule r : rules) {
				currentRule = r;
				for (String str : side.call()) {
					if (p.matcher(str.toLowerCase()).matches() &&
						currentRule.getConf() >= minConf       &&
						currentRule.getFreq() >= minFreq) {
						rulesFound.add(r);
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new CExc("Extraction", "Extraction failed");
		}
		
	}

	private void orderRules() throws CExc {
		
		if (order.equals(OrderBy.CONF))
			rulesFound =
					sort(rulesFound, on(Rule.class).getConf());
		else if (order.equals(OrderBy.FREQ))
			rulesFound =
					sort(rulesFound, on(Rule.class).getFreq());
		else throw new CExc("Order", "Oops, no order found");

	}
	
	private Callable<List<String>> getSideFunc() throws CExc {
		
		if (side.equals(SearchIn.LEFT)) {
			return new Callable<List<String>>() {
				@Override
				public List<String> call() throws Exception {
					return getCurrentRule().getLeft();
				}
			};
			
		} else if (side.equals(SearchIn.RIGHT)) {
			return new Callable<List<String>>() {
				@Override
				public List<String> call() throws Exception {
					return getCurrentRule().getRight();
				}
			};
		} else if (side.equals(SearchIn.BOTH)) {
			return new Callable<List<String>>() {
				@Override
				public List<String> call() throws Exception {
					return getCurrentRule().getBothSides();
				}
			};
		} else throw new CExc("Side", "Oops, no side found");
	}

	private void showRules() throws CExc {
		
		if (0 == rulesFound.size())
			throw new CExc("Rules", "No rules found");
			
		window.showRules(rulesFound);
		
	}

	private void getSearchComponents() throws CExc {
		
		trend = window.getTxtTrend().getText();
		if (trend.equals(""))
			throw new CExc("Rules", "No trend");
		
		try {
			this.p = Pattern.compile(".*" + trend.toLowerCase() + ".*");
		} catch (PatternSyntaxException e) {
			throw new CExc("Pattern", "Invalid patern");
		}

		try {
			minConf = Float.parseFloat(window.getTxtMinConf().getText());
			minFreq = Float.parseFloat(window.getTxtMinFreq().getText());
		} catch (NumberFormatException e) {
			throw new CExc("MinConf or MinFreq",
						   "MinConf et MinFreq doivent être de la forme x.xx");
		}
		
		if (minConf > 1 || 1 < minFreq) {
			throw new CExc("MinConf or MinFreq",
						   "MinConf et MinFreq doivent être <= 1");
		}
		
		order = window.getOrder();
		side = window.getSearchSide();
		
	}

	public Rule getCurrentRule() {
		return currentRule;
	}

	public SearchIn getSide() {
		return side;
	}
	
	public OrderBy getOrder() {
		return order;
	}

}
