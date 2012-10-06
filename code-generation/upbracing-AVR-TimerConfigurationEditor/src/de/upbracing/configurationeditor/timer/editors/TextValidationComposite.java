package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.upbracing.configurationeditor.timer.converters.DoubleDisplayToModelConverter;
import de.upbracing.configurationeditor.timer.converters.DoubleModelToDisplayConverter;
import de.upbracing.configurationeditor.timer.converters.IntegerDisplayToModelConverter;
import de.upbracing.configurationeditor.timer.converters.IntegerModelToDisplayConverter;
import de.upbracing.configurationeditor.timer.converters.StatusImageConverter;

/**
 * Databound SWT Text, unit Label and validation Image with error hover text.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class TextValidationComposite extends Composite {

	private Text text;
	
	/**
	 * Creates a new {@link TextValidationComposite} instance.
	 * @param parent {@code Composite} to add this instance to
	 * @param style passed through to {@code Composite} constructor
	 * @param model data source to bind to
	 * @param textProperty property name within data source
	 * @param validator model validator object
	 * @param unit String for unit label
	 * @param type filters for integer or double, if not null
	 */
	public TextValidationComposite(Composite parent, 
								   int style, 
								   Object model, 
								   String textProperty, 
								   Object validator, 
								   String unit, 
								   Class<?> type) {
		super(parent, style);
		
		GridLayout gl = new GridLayout(2, false);
		setLayout(gl);
		
		if (unit != null)
			setLayout(new GridLayout(3, false));
		
		
		text = new Text(this, SWT.BORDER);
		GridData d = new GridData();
		d.widthHint = 100;
		d.minimumWidth = 100;
		d.horizontalAlignment = SWT.RIGHT;
		d.grabExcessHorizontalSpace = true;
		text.setLayoutData(d);
		
		DataBindingContext c = new DataBindingContext();
		
		// Integer only character filtering
		if (type != null && type == Integer.class) {
			
			c.bindValue(SWTObservables.observeText(text, SWT.Modify), 
			BeansObservables.observeValue(model, textProperty), 
			new UpdateValueStrategy().setConverter(new IntegerDisplayToModelConverter()), 
			new UpdateValueStrategy().setConverter(new IntegerModelToDisplayConverter()));
			
			text.addVerifyListener(new VerifyListener() {

				@Override
				public void verifyText(VerifyEvent arg0) {
					switch(arg0.keyCode) {
						case SWT.BS: // Backspace
						case SWT.DEL: // Delete
						case SWT.HOME: // Home
						case SWT.END: // End
						case SWT.ARROW: // Arrow Keys
							return;
					}
				
				if (!Character.isDigit(arg0.character))
					arg0.doit = false;
				
				}
			});
		}
		// Double character filtering
		else if (type != null && type == Double.class) {
			
			c.bindValue(SWTObservables.observeText(text, SWT.Modify), 
			BeansObservables.observeValue(model, textProperty), 
			new UpdateValueStrategy().setConverter(new DoubleDisplayToModelConverter()), 
			new UpdateValueStrategy().setConverter(new DoubleModelToDisplayConverter()));
			
			text.addVerifyListener(new VerifyListener() {

				@Override
				public void verifyText(VerifyEvent arg0) {
					switch(arg0.keyCode) {
						case SWT.BS: // Backspace
						case SWT.DEL: // Delete
						case SWT.HOME: // Home
						case SWT.END: // End
						case SWT.ARROW: // Arrow Keys
							return;
					}
				
				if (!Character.isDigit(arg0.character) && arg0.keyCode != '.')
					arg0.doit = false;
				
				}
			});
		}
		
		// Display Unit for this text field:
		if (unit != null) {
			Label l = new Label(this, SWT.NONE);
			l.setText(unit);
		}
		
		Label imageLabel = new Label(this, SWT.IMAGE_PNG);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeImage(imageLabel), 
				BeansObservables.observeValue(validator, textProperty + "Error"), 
				null, new UpdateValueStrategy().setConverter(new StatusImageConverter()));
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeTooltipText(imageLabel), BeansObservables.observeValue(validator, textProperty + "ErrorText"));

	}
	
	/**
	 * Returns the inner SWT Text.
	 * @return SWT text
	 */
	public Text getTextBox() {
		return text;
	}

}
 