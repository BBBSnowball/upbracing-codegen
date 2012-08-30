package de.upbracing.codegenerator.timer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ui.commands.NotHandledException;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

public class CodeGenerator {
	
	private ConfigurationModel model;
	
	public CodeGenerator(ConfigurationModel model) {
		this.model = model;
	}
	
	public void generateCode(String path, String templatePath) {
		
		// 1) Open output files (.c/.h)
		try {
			File fileHeader = new File(path + ".h");
			File fileCode = new File(path + ".c");
			DataOutputStream osHeader = new DataOutputStream(new FileOutputStream(fileHeader));
			DataOutputStream osCode = new DataOutputStream(new FileOutputStream(fileCode));
			
			// 2) Iterate over all UseCaseConfigurations
			for (UseCaseModel uc: model.getConfigurations()) {
				// a) Decide, which template to use
				if (uc.getTimer().equals(TimerEnum.TIMER0) || uc.getTimer().equals(TimerEnum.TIMER2))
					templatePath += "8Bit/";
				else
					templatePath += "16Bit/";
				if (uc.getMode().equals(TimerOperationModes.OVERFLOW)) 
					templatePath += "NormalMode.ctemplate";
				else
					throw new Exception("This mode is not yet supported!");
				
				// b) Fill out template
				File templateFile = new File(templatePath);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile)));
				String template = "";
				while (br.ready()) {
					template += br.readLine();
				}
				
				// c) Get placeholder names
				String regex = "<%(\\w+)%>";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(template);
				List<String> placeHolders = new ArrayList<String>();
				while (matcher.find()) {
					if (!placeHolders.contains(matcher.group()))
						placeHolders.add(matcher.group());
				}
				
				// d) Replace them
				int i = placeHolders.size();
				
				
				// e) Append code to output files
			}
			
			// 3) Flush and close output files
			osHeader.close();
			osCode.close();
			
		}
		catch (Exception ex) { }
	}
}
