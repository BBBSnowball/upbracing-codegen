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
