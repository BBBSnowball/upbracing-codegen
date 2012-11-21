package de.upbracing.code_generation.tests;

public class OptionValidator implements Validator {
	private OptionShaper shaper;
	private String[] options;

	public OptionValidator(OptionShaper shaper, String[] options) {
		this.shaper = shaper;
		this.options = options;
	}

	@Override
	public Result validate(String text) {
		if (text.equals("\n"))
			// we don't want a second line
			return Result.INVALID;
		
		String shaped_option = (shaper != null ? shaper.process(text) : text);
		
		boolean incomplete = false;
		for (String valid_option : options) {
			if (valid_option.equals(shaped_option))
				return Result.VALID;
			else if (valid_option.startsWith(shaped_option))
				incomplete = true;
		}
		
		if (incomplete)
			return Result.INCOMPLETE;
		else
			return Result.INVALID;
	}
	
}
