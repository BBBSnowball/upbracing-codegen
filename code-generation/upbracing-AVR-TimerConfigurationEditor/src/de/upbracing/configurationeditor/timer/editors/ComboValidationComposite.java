package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.converters.StatusImageConverter;

public class ComboValidationComposite extends Composite {

	private ComboViewer combo;
	
	public ComboValidationComposite(Composite parent, int style, Object model, String textProperty, Object validator, Object[] choices) {
		super(parent, style);
		
		int columns = 1;
		if (validator != null)
			columns = 2;
		
		GridLayout gl = new GridLayout(columns, false);
		setLayout(gl);
		
		combo = new ComboViewer(this, SWT.BORDER | SWT.READ_ONLY);
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setInput(choices);
		GridData d = new GridData();
		d.horizontalSpan = 1;
		this.setLayoutData(d);
		
		DataBindingContext c;
		if (model != null) {
			c = new DataBindingContext();
			c.bindValue(ViewersObservables.observeSingleSelection(combo),
					BeansObservables.observeValue(model, textProperty));		
		}
		
		if (validator != null) {
			Label imageLabel = new Label(this, (SWT.IMAGE_PNG | SWT.BORDER));
			c = new DataBindingContext();
			c.bindValue(SWTObservables.observeImage(imageLabel), 
					BeansObservables.observeValue(validator, textProperty + "Error"), 
					null, new UpdateValueStrategy().setConverter(new StatusImageConverter()));
			c = new DataBindingContext();
			c.bindValue(SWTObservables.observeTooltipText(imageLabel), BeansObservables.observeValue(validator, textProperty + "ErrorText"));
		}
	}
	
	public ComboViewer getCombo() {
		return combo;
	}
	
}
