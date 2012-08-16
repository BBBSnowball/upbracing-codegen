package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.Activator;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;

public class ConfigurationCompositeOverflow extends AConfigurationCompositeBase {

	public ConfigurationCompositeOverflow(Composite parent, ConfigurationExpandItemComposite expandItem, int style,
			TimerConfigurationEditor editor, UseCaseViewModel model) {
		super(parent, expandItem, style, editor, model);
		
		// Interrupt enable checkbox for overflow
		Label intL = new Label(getSettingsGroup(), SWT.NONE);
		intL.setText("Overflow interrupt:");
		Button intCb = new Button(getSettingsGroup(), SWT.CHECK);
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeSelection(intCb), 
				BeansObservables.observeValue(model, "overflowInterrupt"));
		
		// Image
		ImageDescriptor img = null;
		try {
			img = Activator.getImageDescriptor("./images/Overflow.png");
			Image i = img.createImage();
			Label comp = new Label(getSettingsGroup(), SWT.IMAGE_PNG);
			GridData d = new GridData();
			d.horizontalSpan = 2;
			d.grabExcessHorizontalSpace = true;
			comp.setLayoutData(d);
			comp.setImage(i);
		}
		catch (Exception e) {
		}
		
		layout();		
	}

}
