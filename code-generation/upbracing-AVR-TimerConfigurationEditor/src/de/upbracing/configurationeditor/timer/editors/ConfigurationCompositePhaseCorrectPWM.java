package de.upbracing.configurationeditor.timer.editors;

import java.util.ArrayList;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;

public class ConfigurationCompositePhaseCorrectPWM extends
		AConfigurationCompositeBase {

	public ConfigurationCompositePhaseCorrectPWM(Composite parent,
			ConfigurationExpandItemComposite expandItem, int style,
			TimerConfigurationEditor editor, UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model);
		
		createTopRegisterSelection(getSettingsGroup(), PWMTopValues.values(), "phaseCorrectPWMTop");
		initPWMTopValueGroup(getSettingsGroup());
		
		layout();
	}

	private void initPWMTopValueGroup(Group g) {

		// PWM Groups
		createTopValueItem(g, "icrName", "icrPeriod", "icrVisibility", false, null, null);
		createTopValueItem(g, "ocrAName", "ocrAPeriod", null, false, null, "dualSlopePWMPinModeA");
		createTopValueItem(g, "ocrBName", "ocrBPeriod", "ocrChannelsVisibility", false, null, "dualSlopePWMPinModeB");
		createTopValueItem(g, "ocrCName", "ocrCPeriod", "ocrChannelsVisibility", false, null, "dualSlopePWMPinModeC");
	}
	
	private void createTopValueItem(Composite parent, 
			String nameProperty, 
			String periodProperty, 
			String enabledProperty, 
			boolean compareInterrupt, 
			String compareInterruptProperty,
			String pinModeProperty) {
		
		CollapsibleComposite scComp = new CollapsibleComposite(parent, SWT.BORDER);
		GridData d = new GridData();
		d.horizontalSpan = 2;
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		scComp.setLayoutData(d);
		GridLayout l = new GridLayout(2, false);
		scComp.setLayout(l);
		
		DataBindingContext c;
		c = new DataBindingContext();
		if (enabledProperty != null) {
			c.bindValue(SWTObservables.observeEnabled(scComp), 
					BeansObservables.observeValue(model, enabledProperty));
		}
		
		// Label for Register Name:
		Label lbPrefix = new Label(scComp, SWT.BORDER);
		lbPrefix.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT); 
		d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;
		d.horizontalSpan = 2;
		lbPrefix.setLayoutData(d);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(lbPrefix), 
				BeansObservables.observeValue(model, nameProperty));
		setFontStyle(lbPrefix, SWT.BOLD);
		
		// Label for Period
		Label freqLOA = new Label(scComp, SWT.NONE);
		freqLOA.setText("Duty-Cycle:");
		
		// Validated Text Box:
		TextValidationComposite tFreq = new TextValidationComposite(scComp, 
				SWT.NONE, 
				model, periodProperty, 
				model.getValidator(),
				"s",
				Double.class);
		tFreq.getTextBox().addModifyListener(new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			editor.setDirty(true);
		}});
		
		if (pinModeProperty != null) {
			// Toggle Mode:
			Label toggleL = new Label(scComp, SWT.NONE);
			toggleL.setText("Output Pin Operation:");
			
			// Toggle Combo Box:
			ArrayList<Object> o = new ArrayList<Object>();
			for (PWMDualSlopeOutputPinMode m: PWMDualSlopeOutputPinMode.values())
			{
				o.add(m);
			}
			if (!nameProperty.startsWith("ocrA"))
				o.remove(1);
			
			Object validator = model.getValidator();
			if (!pinModeProperty.endsWith("A"))
				validator = null;
			ComboValidationComposite toggleC = new ComboValidationComposite(scComp, SWT.NONE, model, pinModeProperty, validator, o.toArray());
			
			toggleC.getCombo().addPostSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent arg0) {
					editor.setDirty(true);
				}
			});
		}
	}
}
