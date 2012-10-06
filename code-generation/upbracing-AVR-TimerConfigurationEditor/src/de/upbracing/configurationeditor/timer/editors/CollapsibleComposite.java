package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite, which hides itself when disabled.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class CollapsibleComposite extends Composite {

	private Composite parent;
	
	/**
	 * Creates a new {@link CollapsibleComposite} instance.
	 * @param parent {@code Composite} to add this instance to
	 * @param style passed through to {@code Composite} constructor
	 */
	public CollapsibleComposite(Composite parent, int style) {
		super(parent, style);
		
		this.parent = parent;
	}

	/**
	 * Hides the composite when disabled,
	 * shows the composite when enabled.
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
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
