package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class CollapsibleComposite extends Composite {

	private Composite parent;
	
	public CollapsibleComposite(Composite parent, int style) {
		super(parent, style);
		
		this.parent = parent;
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);

		GridData d = (GridData) this.getLayoutData();
		if (b)
			d.heightHint = -1;
		else
			d.heightHint = 0;
		d.exclude = !b;
		setVisible(b);
		
		parent.layout();
	}
}
