package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.converters.StatusImageConverter;

/**
 * Databound SWT ComboViewer and validation Image with error hover text.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ComboValidationComposite extends Composite {

	private ComboViewer combo;
	
	/**
	 * Creates a new {@link ComboValidationComposite} instance.
	 * @param parent {@code Composite} to add this instance to
	 * @param style passed through to {@code Composite} constructor
	 * @param model data source to bind to
	 * @param textProperty property name within data source
	 * @param validator model validator object
	 * @param choices collection of choices for the Combo
	 */
	public ComboValidationComposite(Composite parent, 
									int style,
									String labelText,
									Object model, 
									String textProperty, 
									Object validator, 
									Object[] choices) {
		super(parent, style);
		
		int columns = 2;
		if (validator != null)
			columns = 3;
		
		GridLayout gl = new GridLayout(columns, false);
		gl.marginWidth = 0;
		setLayout(gl);
		
		Label label = new Label(this, SWT.NONE);
		label.setText(labelText);
		
		combo = new ComboViewer(this, SWT.BORDER | SWT.READ_ONLY);
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setInput(choices);
		
		DataBindingContext c;
		if (model != null) {
			c = new DataBindingContext();
			c.bindValue(ViewersObservables.observeSingleSelection(combo),
					BeansObservables.observeValue(model, textProperty));		
		}
		
		if (validator != null) {
			Label imageLabel = new Label(this, (SWT.IMAGE_PNG));
			c = new DataBindingContext();
			c.bindValue(SWTObservables.observeImage(imageLabel), 
					BeansObservables.observeValue(validator, textProperty + "Error"), 
					null, new UpdateValueStrategy().setConverter(new StatusImageConverter()));
			c = new DataBindingContext();
			c.bindValue(SWTObservables.observeTooltipText(imageLabel), BeansObservables.observeValue(validator, textProperty + "ErrorText"));
		}
	}
	
	/**
	 * Returns the inner SWT ComboViewer.
	 * @return SWT ComboViewer
	 */
	public ComboViewer getCombo() {
		return combo;
	}
	
}
