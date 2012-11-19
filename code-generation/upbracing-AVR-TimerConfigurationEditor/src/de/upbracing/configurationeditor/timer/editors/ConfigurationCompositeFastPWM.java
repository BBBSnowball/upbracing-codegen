package de.upbracing.configurationeditor.timer.editors;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

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
		createPeriodComposites();
		initPWMComposites();
		
		layout();
	}

	private void initPWMComposites() {

		// PWM Groups
		createTopValueItem(ocrAComposite, "ocrAPeriod", "singleSlopePWMPinModeA");
		createTopValueItem(ocrBComposite, "ocrBPeriod", "singleSlopePWMPinModeB");
		createTopValueItem(ocrCComposite, "ocrCPeriod", "singleSlopePWMPinModeC");
	}
	
	private void createTopValueItem(Composite parent, 
			String nameProperty,
			String pinModeProperty) {
			
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
