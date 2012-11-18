package de.upbracing.code_generation.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator implements Validator {
	Pattern pattern;

	public RegexValidator(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public RegexValidator(String regex) {
		this(Pattern.compile(regex));
	}

	@Override
	public Result validate(String text) {
		Matcher m = pattern.matcher(text);
		if (m.matches())
			return Result.VALID;
		else if (m.hitEnd())
			return Result.INCOMPLETE;
		else
			return Result.INVALID;
	}
}
