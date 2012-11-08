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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;

/**
 * Content for the settings group in Fast PWM mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationCompositeFastPWM extends AConfigurationCompositeBase {

	/**
	 * Creates a new {@link ConfigurationCompositeFastPWM} instance.
	 * @param parent parent {@code Composite} to add this instance to
	 * @param expandItem @code ExpandItem} for which this object provides content
	 * @param style style passed through to {@code Composite} constructor
	 * @param editor {@link TimerConfigurationEditor} reference, to set dirty flag, if
	 * necessary.
	 * @param model {@link UseCaseViewModel} to databind visual elements to
	 */
	public ConfigurationCompositeFastPWM(Composite parent,
										 ConfigurationExpandItemComposite expandItem, 
										 int style,
										 TimerConfigurationEditor editor, 
										 UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model, 220);
		
		createTopRegisterSelection(getSettingsGroup(), PWMTopValues.values(), "fastPWMTop");
		initPWMTopValueGroup(getSettingsGroup());
		
		layout();
	}

	private void initPWMTopValueGroup(Group g) {

		// PWM Groups
		createTopValueItem(g, "icrName", "icrPeriod", "icrVisibility", false, null, null);
		createTopValueItem(g, "ocrAName", "ocrAPeriod", null, false, null, "singleSlopePWMPinModeA");
		createTopValueItem(g, "ocrBName", "ocrBPeriod", "ocrChannelsVisibility", false, null, "singleSlopePWMPinModeB");
		createTopValueItem(g, "ocrCName", "ocrCPeriod", "ocrChannelsVisibility", false, null, "singleSlopePWMPinModeC");
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
		Label lbPrefix = new Label(scComp, SWT.NONE);
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
	
//		if (compareInterrupt) {
//			// Interrupt enable checkbox for Compare Match
//			Label intL = new Label(scComp, SWT.NONE);
//			intL.setText("Compare match interrupt:");
//			Button intCb = new Button(scComp, SWT.CHECK);
//			c = new DataBindingContext();
//			c.bindValue(SWTObservables.observeSelection(intCb), 
//					BeansObservables.observeValue(model, compareInterruptProperty));
//			d = new GridData();
//			d.horizontalSpan = 2;
//			intCb.setLayoutData(d);
//			intCb.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					editor.setDirty(true);
//				}
//			});
//		}
		
		if (pinModeProperty != null) {
			// Toggle Mode:
			Label toggleL = new Label(scComp, SWT.NONE);
			toggleL.setText("Output Pin Operation:");
			
			// Toggle Combo Box:
			ArrayList<Object> o = new ArrayList<Object>();
			for (PWMSingleSlopeOutputPinMode m: PWMSingleSlopeOutputPinMode.values())
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
	
	/* (non-Javadoc)
	 * @see de.upbracing.configurationeditor.timer.editors.AConfigurationCompositeBase#drawDescriptionImage(org.eclipse.swt.graphics.GC)
	 */
	@Override
	public void drawDescriptionImage(GC gc) {
		
		// Period text:
		String periodString = UseCaseModelValidator.formatPeriod(model.getValidator().getTopPeriod());
		WaveformDrawHelper.drawPeriodText(gc, periodString, false);
		
		// Waveform:
		WaveformDrawHelper.drawWaveform(gc, false);
		WaveformDrawHelper.drawHorizontalLine(gc, 0, 0, "MIN");
		
		// Channels:
		WaveformDrawHelper.drawWaveformChannels(gc, model);
		
		// Output pins:
		WaveformDrawHelper.drawSingleSlopePWMOutputPin(gc, model, "Channel A", model.getOcrAPeriod(), model.getSingleSlopePWMPinModeA());	
		WaveformDrawHelper.drawSingleSlopePWMOutputPin(gc, model, "Channel B", model.getOcrBPeriod(), model.getSingleSlopePWMPinModeB());
		WaveformDrawHelper.drawSingleSlopePWMOutputPin(gc, model, "Channel C", model.getOcrCPeriod(), model.getSingleSlopePWMPinModeC());	
	}
}
