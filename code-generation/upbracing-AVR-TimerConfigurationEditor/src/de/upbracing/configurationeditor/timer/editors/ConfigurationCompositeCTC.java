package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;

/**
 * Content for the settings group in CTC mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationCompositeCTC extends AConfigurationCompositeBase {

	/**
	 * Creates a new {@link ConfigurationCompositeCTC} instance.
	 * @param parent parent {@code Composite} to add this instance to
	 * @param expandItem @code ExpandItem} for which this object provides content
	 * @param style style passed through to {@code Composite} constructor
	 * @param editor {@link TimerConfigurationEditor} reference, to set dirty flag, if
	 * necessary.
	 * @param model {@link UseCaseViewModel} to databind visual elements to
	 */
	public ConfigurationCompositeCTC(Composite parent, 
									 ConfigurationExpandItemComposite expandItem, 
									 int style,
									 TimerConfigurationEditor editor, 
									 UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model, 220);
		

		createTopRegisterSelection(getSettingsGroup(), CTCTopValues.values(), "ctcTop");
		createPeriodComposites();
		initCTCComposites();
		
		layout();
	}

	private void initCTCComposites() {

		// OCR Groups
		createTopValueItem(ocrAComposite, "compareInterruptA", "comparePinModeA");
		createTopValueItem(ocrBComposite, "compareInterruptB", "comparePinModeB");
		createTopValueItem(ocrCComposite, "compareInterruptC", "comparePinModeC");
	}
	
	private void createTopValueItem(Composite parent, 
			String compareInterruptProperty,
			String pinModeProperty) {
		
		GridData d;
		DataBindingContext c;
		
		// Interrupt enable checkbox for Compare Match
		Label intL = new Label(parent, SWT.NONE);
		intL.setText("Compare match interrupt:");
		Button intCb = new Button(parent, SWT.CHECK);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeSelection(intCb), 
				BeansObservables.observeValue(model, compareInterruptProperty));
		d = new GridData();
		d.horizontalSpan = 1;
		intCb.setLayoutData(d);
		intCb.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editor.setDirty(true);
			}
		});
		
		// Toggle Mode:
		Label toggleL = new Label(parent, SWT.NONE);
		toggleL.setText("Output Pin Operation:");
		
		// Toggle Combo:
		ComboViewer toggleC = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		toggleC.setContentProvider(ArrayContentProvider.getInstance());
		toggleC.setInput(CTCOutputPinMode.values());
		c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(toggleC),
				BeansObservables.observeValue(model, pinModeProperty));
		toggleC.addPostSelectionChangedListener(new ISelectionChangedListener() {
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
		
		// Interrupts:
		if (model.getCtcTop().equals(CTCTopValues.OCRnA)
			|| (model.getValidator().calculateQuantizedPeriod(model.getOcrAPeriod()) == model.getValidator().calculateQuantizedPeriod(model.getIcrPeriod())))
			WaveformDrawHelper.drawResetInterrupts(gc, model.getCompareInterruptA());
		
		// Output pins:
		WaveformDrawHelper.drawCTCOutputPin(gc, model, "Channel A", model.getOcrAPeriod(), model.getComparePinModeA());	
		WaveformDrawHelper.drawCTCOutputPin(gc, model, "Channel B", model.getOcrBPeriod(), model.getComparePinModeB());
		WaveformDrawHelper.drawCTCOutputPin(gc, model, "Channel C", model.getOcrCPeriod(), model.getComparePinModeC());	
	}
}
