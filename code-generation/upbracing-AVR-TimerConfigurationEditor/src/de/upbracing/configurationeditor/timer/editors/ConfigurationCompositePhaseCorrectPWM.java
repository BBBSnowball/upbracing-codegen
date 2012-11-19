package de.upbracing.configurationeditor.timer.editors;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;

/**
 * Content for the settings group in Phase correct PWM mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationCompositePhaseCorrectPWM extends
		AConfigurationCompositeBase {

	/**
	 * Creates a new {@link ConfigurationCompositePhaseCorrectPWM} instance.
	 * @param parent parent {@code Composite} to add this instance to
	 * @param expandItem @code ExpandItem} for which this object provides content
	 * @param style style passed through to {@code Composite} constructor
	 * @param editor {@link TimerConfigurationEditor} reference, to set dirty flag, if
	 * necessary.
	 * @param model {@link UseCaseViewModel} to databind visual elements to
	 */
	public ConfigurationCompositePhaseCorrectPWM(Composite parent,
												 ConfigurationExpandItemComposite expandItem, 
												 int style,
												 TimerConfigurationEditor editor, 
												 UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model, 220);
		
		createTopRegisterSelection(getSettingsGroup(), PWMTopValues.values(), "phaseCorrectPWMTop");
		createPeriodComposites();
		initPWMComposites();
		
		layout();
	}

	private void initPWMComposites() {

		// PWM Groups
		createTopValueItem(ocrAComposite, "ocrAName", "dualSlopePWMPinModeA");
		createTopValueItem(ocrBComposite, "ocrBName", "dualSlopePWMPinModeB");
		createTopValueItem(ocrCComposite, "ocrCName", "dualSlopePWMPinModeC");
	}
	
	private void createTopValueItem(Composite parent, 
			String nameProperty,
			String pinModeProperty) {
		
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
		ComboValidationComposite toggleC = new ComboValidationComposite(parent, SWT.NONE, "Output Pin operation:", model, pinModeProperty, validator, o.toArray());
		
		toggleC.getCombo().addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see de.upbracing.configurationeditor.timer.editors.AConfigurationCompositeBase#drawDescriptionImage(org.eclipse.swt.graphics.GC)
	 */
	@Override
	public void drawDescriptionImage(GC gc) {
		
		// Period text:
		String periodString = UseCaseModelValidator.formatPeriod(model.getValidator().getTopPeriod());
		WaveformDrawHelper.drawPeriodText(gc, periodString, true);
		
		// Waveform:
		WaveformDrawHelper.drawWaveform(gc, true);
		WaveformDrawHelper.drawHorizontalLine(gc, 0, 0, "MIN");
		
		// Channels:
		WaveformDrawHelper.drawWaveformChannels(gc, model);
		
		// Output pins:
		WaveformDrawHelper.drawDualSlopePWMOutputPin(gc, model, "Channel A", model.getOcrAPeriod(), model.getDualSlopePWMPinModeA());	
		WaveformDrawHelper.drawDualSlopePWMOutputPin(gc, model, "Channel B", model.getOcrBPeriod(), model.getDualSlopePWMPinModeB());
		WaveformDrawHelper.drawDualSlopePWMOutputPin(gc, model, "Channel C", model.getOcrCPeriod(), model.getDualSlopePWMPinModeC());	
	}
}
