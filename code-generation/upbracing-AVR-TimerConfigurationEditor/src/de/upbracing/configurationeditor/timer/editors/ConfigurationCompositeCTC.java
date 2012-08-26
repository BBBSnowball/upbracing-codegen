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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.CTCTopValues;

public class ConfigurationCompositeCTC extends AConfigurationCompositeBase {

	public ConfigurationCompositeCTC(Composite parent, ConfigurationExpandItemComposite expandItem, int style,
			TimerConfigurationEditor editor, UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model);
		

		createTopRegisterSelection(getSettingsGroup(), CTCTopValues.values(), "ctcTop");
		initCTCTopValueGroup(getSettingsGroup());
		
		layout();
	}

	private void initCTCTopValueGroup(Group g) {

		// OCR Groups
		createTopValueItem(g, "icrName", "icrPeriod", "icrVisibility", false, null, null);
		createTopValueItem(g, "ocrAName", "ocrAPeriod", null, true, "compareInterruptA", "comparePinModeA");
		createTopValueItem(g, "ocrBName", "ocrBPeriod", "ocrChannelsVisibility", true, "compareInterruptB", "comparePinModeB");
		createTopValueItem(g, "ocrCName", "ocrCPeriod", "ocrChannelsVisibility", true, "compareInterruptC", "comparePinModeC");
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
		freqLOA.setText("Period:");
		
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
		
//		// Label for Unit
//		Label lbUnit = new Label(scComp, SWT.NONE);
//		lbUnit.setText("s");
		
		if (compareInterrupt) {
			// Interrupt enable checkbox for Compare Match
			Label intL = new Label(scComp, SWT.NONE);
			intL.setText("Compare match interrupt:");
			Button intCb = new Button(scComp, SWT.CHECK);
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
		}
		
		if (pinModeProperty != null) {
			// Toggle Mode:
			Label toggleL = new Label(scComp, SWT.NONE);
			toggleL.setText("Output Pin Operation:");
			
			// Toggle Combo:
			ComboViewer toggleC = new ComboViewer(scComp, SWT.BORDER | SWT.READ_ONLY);
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
	}
}
