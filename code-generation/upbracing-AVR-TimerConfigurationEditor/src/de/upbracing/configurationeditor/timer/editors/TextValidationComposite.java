package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TextValidationComposite extends Composite {

	private Text text;
	private Label imageLabel;
	
	public TextValidationComposite(Composite parent, int style, Object model, String textProperty, Object validator) {
		super(parent, style);
		
		GridLayout gl = new GridLayout(2, false);
		setLayout(gl);
		
		DataBindingContext c = new DataBindingContext();
		text = new Text(this, SWT.BORDER);
		GridData d = new GridData();
		d.widthHint = 100;
		d.minimumWidth = 100;
		d.horizontalAlignment = SWT.RIGHT;
		d.grabExcessHorizontalSpace = true;
		text.setLayoutData(d);
		c.bindValue(SWTObservables.observeText(text, SWT.Modify), BeansObservables.observeValue(model, textProperty));
		
		imageLabel = new Label(this, SWT.IMAGE_PNG);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeImage(imageLabel), 
				BeansObservables.observeValue(validator, textProperty + "Error"), 
				null, new UpdateValueStrategy().setConverter(new StatusImageConverter()));
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeTooltipText(imageLabel), BeansObservables.observeValue(validator, textProperty + "ErrorText"));

	}
	
	public Text getTextBox() {
		return text;
	}

}
