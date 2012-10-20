package de.upbracing.code_generation.common;

import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.str;
import info.reflectionsofmind.parser.exception.GrammarParsingException;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.matcher.NamedMatcher;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.transform.AbstractTransformer;
import info.reflectionsofmind.parser.transform.ChildTransformer2;
import info.reflectionsofmind.parser.transform.ITransform;
import info.reflectionsofmind.parser.transform.Transform;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.upbracing.code_generation.common.TransformHelpers.*;

/** can parse and format times */
public final class Times {
	private Times() { }

	/** parse a time like "1.7ms", "1:30:02.7" or even "1/3 day"
	 * 
	 * @param time text to parse
	 * @return parsed time in seconds
	 */
	public static double parseTime(String time) {
		return (Double)getParser().parse("time", time.trim());
	}

	/** format a time value
	 * 
	 * The value is meaningful for a user, e.g. it could be "100ms". The value can also
	 * be parsed with {@link #parseTime(String)} which yields the almost same value as
	 * before. There can be a round-off error and the textual representation is less
	 * exact than the double value.
	 * 
	 * @param time time to format for the user
	 * @return the time as a string
	 */
	public static String formatTime(double time) {
		final Map<String, Double> factors = new HashMap<String, Double>();
		factors.put("ms", 1e-3);
		factors.put("us", 1e-6);
		factors.put("ns", 1e-9);
		factors.put("ps", 1e-12);
		factors.put("fs", 1e-15);
		
		String sign = "";
		if (time < 0) {
			sign = "-";
			time = -time;
		}
		
		// big numbers are handled specially because for times the factors are quite weird...
		if (time > 60) {
			if (time < 60*60*24 * 5) {
				int mins = ((int)time) / 60 % 60;
				int hours = ((int)time) / 60 / 60;
				double secs = time - mins*60 - hours*60*60;
				return String.format("%s%02d:%02d:%06.3f", sign, hours, mins, secs);
			} else {
				return String.format("%4.2f days", time / (60*60*24));
			}
		}
		
		// shift it in the range 1 to 10
		int e = 0;
		double t = time;
		while (t >= 10) {
			t /= 10;
			e += 1;
		}
		while (t < 1) {
			t *= 10;
			e -= 1;
		}
		
		// now: time == t*10^e
		
		// find out how many digits we need, so we won't ignore a non-zero one
		int significant_digits = 1;
		int factor = 10;
		for (int i=2;i<6;i++) {
			if (Math.round(t * factor) % 10 != 0)
				significant_digits = i;
			factor *= 10;
		}
		
		// for more than 5s we always write it in seconds, but with adjusted precision
		if (time > 5 || e > 0) {
			// significant digits without the ones before the point (e+1)
			int precision = significant_digits - (e+1);
			if (precision < 0)
				precision = 0;
			
			return String.format("%1." + precision + "f sec", time);
		}
		
		// we have two possibilities: use the suffix that makes the number short or the one below
		e = -e;	// make it positive
		int unit = (e+2) / 3;
		int digitsBeforePoint = e % 3 + 1;
		int digitsAfterPoint = significant_digits - digitsBeforePoint;
		
		if (digitsAfterPoint > 2 && unit < 5) {
			unit++;
			digitsBeforePoint += 3;
			digitsAfterPoint -= 3;
		}
		
		if (digitsAfterPoint < 0)
			digitsAfterPoint = 0;
		
		if (unit > 5)
			// I won't implement anything smaller than femto-seconds. No ato-seconds... sorry ;-)
			// Don't adjust digitsAfterPoint because it is quite already too much.
			unit = 5;
		
		for (int i=0;i<unit;i++)
			time *= 1e3;
		
		String number = String.format("%s%1." + digitsAfterPoint + "f", sign, time);
		
		switch (unit) {
		case 0:
			return number + " sec";
		case 1:
			return number + " ms";
		case 2:
			return number + " us";
		case 3:
			return number + " ns";
		case 4:
			return number + " ps";
		case 5:
			return number + " fs";
		default:
			throw new RuntimeException("Should never get here!");
		}
		
	}
	
	private static Map<String, Double> getTimeFactors() {
		final Map<String, Double> factors = new HashMap<String, Double>();
		factors.put("days", 24*60*60.0);
		factors.put("day",  24*60*60.0);
		factors.put("hours",   60*60.0);
		factors.put("hour",    60*60.0);
		factors.put("h",       60*60.0);
		factors.put("min",        60.0);
		factors.put("m",          60.0);
		factors.put("sec",         1.0);
		factors.put("s",           1.0);
		factors.put("ms", 1e-3);
		factors.put("us", 1e-6);
		factors.put("ns", 1e-9);
		factors.put("ps", 1e-12);
		factors.put("fs", 1e-15);
		return factors;
	}

	private static Matcher getTimeSuffixMatcher() {
		Set<String> suffixes = getTimeFactors().keySet();
		
		Matcher options[] = new Matcher[suffixes.size()];
		int i = 0;
		for (String suffix : suffixes)
			options[i++] = str(suffix);
		
		return new NamedMatcher("time-suffix").define(cho(options));
	}
	
	private static Parser time_parser = null;
	public static Parser getParser() {
		if (time_parser == null) {
			Map<String, Matcher> previous_defs = new HashMap<String, Matcher>();
			previous_defs.put("time-suffix", getTimeSuffixMatcher());
			
			try {
				time_parser = new Parser(Times.class, "time.g", createTransform(),
						Parser.getCommonParser(), new Parser(previous_defs, null));
			} catch (GrammarParsingException e) {
				throw new RuntimeException(e);
			}
		}
		
		return time_parser;
	}

	private static Transform createTransform() {
		Transform transform = new Transform();
		
		transform.add(new AbstractTransformer("clock-time") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				transform.transform(node.children);
				
				List<AbstractNode> xs = node.findNamedChildren();
				Collections.reverse(xs);

				double time = 0;
				double factor = 1;
				for (AbstractNode node2 : xs) {
					double x;
					if (node2.value instanceof Integer)
						x = (Integer)node2.value;
					else
						x = (Double)node2.value;
					
					time += factor * x;
					factor *= 60;
				}
				
				node.value = time;
			}
		});
		
		transform.add(new ChildTransformer2<Double, String>(Double.class, String.class, "time-with-suffix", "~ws", "*", "#text") {
			@Override
			protected Object transform(Double time, String factor_name) {
				return time * getTimeFactors().get(factor_name);
			}
		});
		
		transformChoice(transform, "time");
		
		return transform;
	}
}
