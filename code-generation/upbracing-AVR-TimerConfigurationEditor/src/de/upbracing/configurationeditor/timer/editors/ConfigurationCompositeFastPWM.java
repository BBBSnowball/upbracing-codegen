package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
import org.eclipse.swt.widgets.Text;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.PWMTopValues;

public class ConfigurationCompositeFastPWM extends AConfigurationCompositeBase {

	public ConfigurationCompositeFastPWM(Composite parent,
			ConfigurationExpandItemComposite expandItem, int style,
			TimerConfigurationEditor editor, UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model);
		
		createTopRegisterSelection(getSettingsGroup(), PWMTopValues.values(), "fastPWMTop");
		initPWMTopValueGroup(getSettingsGroup());
		
		layout();
	}

	private void initPWMTopValueGroup(Group g) {

		// PWM Groups
		createTopValueItem(g, "icrName", "icrPeriod", "icrVisibility", false, null);
		createTopValueItem(g, "ocrAName", "ocrAPeriod", null, false, null);
		createTopValueItem(g, "ocrBName", "ocrBPeriod", "ocrChannelsVisibility", false, null);
		createTopValueItem(g, "ocrCName", "ocrCPeriod", "ocrChannelsVisibility", false, null);
	}
	
	private void createTopValueItem(Composite parent, 
			String nameProperty, 
			String periodProperty, 
			String enabledProperty, 
			boolean compareInterrupt, 
			String compareInterruptProperty) {
		
		CollapsibleComposite scComp = new CollapsibleComposite(parent, SWT.BORDER);
		GridData d = new GridData();
		d.horizontalSpan = 2;
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		scComp.setLayoutData(d);
		GridLayout l = new GridLayout(3, false);
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
		d.horizontalSpan = 3;
		lbPrefix.setLayoutData(d);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(lbPrefix), 
				BeansObservables.observeValue(model, nameProperty));
		setFontStyle(lbPrefix, SWT.BOLD);
		
		// Label for Period
		Label freqLOA = new Label(scComp, SWT.NONE);
		freqLOA.setText("Duty-Cycle:");
		
		// Textbox
		Text tFreq = new Text(scComp, SWT.BORDER);
		d = new GridData();
		d.widthHint = 100;
		d.minimumWidth = 100;
		d.horizontalAlignment = SWT.RIGHT;
		d.grabExcessHorizontalSpace = true;
		tFreq.setLayoutData(d);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(tFreq, SWT.Modify), 
				BeansObservables.observeValue(model, periodProperty));
		if (enabledProperty != null) {
			c.bindValue(SWTObservables.observeEnabled(tFreq), 
					BeansObservables.observeValue(model, enabledProperty));
		}
		tFreq.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				editor.setDirty(true);
			}});
		// Label for Unit
		Label lbUnit = new Label(scComp, SWT.NONE);
		lbUnit.setText("s");
		
		if (compareInterrupt) {
			// Interrupt enable checkbox for Compare Match
			Label intL = new Label(scComp, SWT.NONE);
			intL.setText("Compare match interrupt:");
			Button intCb = new Button(scComp, SWT.CHECK);
			c = new DataBindingContext();
			c.bindValue(SWTObservables.observeSelection(intCb), 
					BeansObservables.observeValue(model, compareInterruptProperty));
			d = new GridData();
			d.horizontalSpan = 2;
			intCb.setLayoutData(d);
			intCb.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					editor.setDirty(true);
				}
			});
		}
	}
	
}
