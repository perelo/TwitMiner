package twitminer.phase2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitminer.phase4.model.CExc;

public class Rule/* implements Entry<List<String>, List<String>> */{

	private static String RULE_SEPARATOR = " -> ";
	private static String TREND_SEPARATOR  = ";";
	private static String INFO_SEPARATOR = " : ";
	private static String CONF_FREQ_SEPARATOR = ", ";
	
	private List<String> left;
	private List<String> right;
	private Double conf;
	private Double freq;
	
	public Rule(List<String> left, List<String> right,
			    double conf, double freq) {
		super();
		this.left = left;
		this.right = right;
		this.conf = (double) Math.round(conf * 100) / 100;
		this.freq = (double) Math.round(conf * 100) / 100;
	}
	
	public String toString() {
		return ExtractRules.ContacElements(left, TREND_SEPARATOR) +
			   RULE_SEPARATOR +
			   ExtractRules.ContacElements(right, TREND_SEPARATOR) +
			   INFO_SEPARATOR + conf + CONF_FREQ_SEPARATOR + freq;
	}
	
	public static Rule toRule(String strRule) throws CExc {
		String error = "Invalid string to rule";
		
		String[] splitInfo = strRule.split(INFO_SEPARATOR);
		if (2 != splitInfo.length) {
			throw new CExc("Rule", error);
		}
		
		String[] splitRule = splitInfo[0].split(RULE_SEPARATOR);
		if (2 != splitRule.length) {
			throw new CExc("Rule", error);
		}
		
		String[] splitConfFreq = splitInfo[1].split(CONF_FREQ_SEPARATOR);
		if (2 != splitConfFreq.length) {
			throw new CExc("Rule", error);
		}
		
		List<String> left  = Arrays.asList(splitRule[0]);
		List<String> right = Arrays.asList(splitRule[1]);
		double conf;
		double freq;
		
		try {
			conf = Double.parseDouble(splitConfFreq[0]);
			freq = Double.parseDouble(splitConfFreq[1]);
		} catch (IllegalArgumentException e) {
			throw new CExc("Rule", error);
		}
		
		return new Rule(left, right, conf, freq);
		
	}
	
	public List<String> getLeft() {
		return left;
	}
	
	public List<String> getRight() {
		return right;
	}
	
	public List<String> getBothSides() {
		List<String> both = new ArrayList<String>(left);
		both.addAll(right);
		return both;
	}
	
	public Double getConf() {
		return this.conf;
	}
	
	public Double getFreq() {
		return this.freq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rule other = (Rule) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

}
