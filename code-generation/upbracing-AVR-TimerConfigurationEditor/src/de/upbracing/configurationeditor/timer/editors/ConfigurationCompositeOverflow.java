package de.upbracing.configurationeditor.timer.editors;

import java.awt.Color;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.Activator;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;

/**
 * Content for the settings group in Overflow mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationCompositeOverflow extends AConfigurationCompositeBase {

	/**
	 * Creates a new {@link ConfigurationCompositeOverflow} instance.
	 * @param parent parent {@code Composite} to add this instance to
	 * @param expandItem @code ExpandItem} for which this object provides content
	 * @param style style passed through to {@code Composite} constructor
	 * @param editor {@link TimerConfigurationEditor} reference, to set dirty flag, if
	 * necessary.
	 * @param model {@link UseCaseViewModel} to databind visual elements to
	 */
	public ConfigurationCompositeOverflow(Composite parent, 
										  ConfigurationExpandItemComposite expandItem, 
										  int style,
										  TimerConfigurationEditor editor, 
										  UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model, 160);
		
		// Interrupt enable checkbox for overflow
		Label intL = new Label(getSettingsGroup(), SWT.NONE);
		intL.setText("Overflow interrupt:");
		Button intCb = new Button(getSettingsGroup(), SWT.CHECK);
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeSelection(intCb), 
				BeansObservables.observeValue(model, "overflowInterrupt"));
		
//		// Image
//		ImageDescriptor img = null;
//		try {
//			img = Activator.getImageDescriptor("./images/Overflow.png");
//			Image i = img.createImage();
//			Label comp = new Label(getSettingsGroup(), SWT.IMAGE_PNG);
//			GridData d = new GridData();
//			d.horizontalSpan = 2;
//			d.grabExcessHorizontalSpace = true;
//			comp.setLayoutData(d);
//			comp.setImage(i);
//		}
//		catch (Exception e) {
//		}
		
		layout();		
	}

	/* (non-Javadoc)
	 * @see de.upbracing.configurationeditor.timer.editors.AConfigurationCompositeBase#drawDescriptionImage(org.eclipse.swt.graphics.GC)
	 */
	@Override
	public void drawDescriptionImage(GC gc) {

		// Period text:
		String periodString = UseCaseModelValidator.formatPeriod(model.getValidator().calculatePeriodForRegisterValue(model.getValidator().getMaximumValue()));
		WaveformDrawHelper.drawPeriodText(gc, periodString, false);
		
		// Waveform:
		WaveformDrawHelper.drawWaveform(gc, false);
		WaveformDrawHelper.drawHorizontalLine(gc, 0, "MAX " + "(" + model.getValidator().getMaximumValue() + ")");
		WaveformDrawHelper.drawHorizontalLine(gc, 80, "MIN " + "(0)");
	    
	    // Interrupts:
	    WaveformDrawHelper.drawResetInterrupts(gc, model.getOverflowInterrupt());
    	
    	// Interrupt enabled/disabled text:
    	WaveformDrawHelper.drawOverflowInterruptText(gc, model.getOverflowInterrupt());
	}
}
