package de.upbracing.configurationeditor.timer.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;

/**
 * This is the base class for the bottom part of the {@link UseCaseViewModel}'s ExpandItem.<p>
 * It consists of:<br>
 * - Settings group (left)<br>
 * - Summary group (right)<p>
 * The derived classes specify the content of the settings group.<br>
 * Summary group is databound to the {@link UseCaseViewModel}'s 
 * "description" property.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public abstract class AConfigurationCompositeBase extends Composite {

	protected TimerConfigurationEditor editor;
	protected UseCaseViewModel model;
	protected String ls = System.getProperty("line.separator");
	private Group settingsGroup;
	private Group summaryGroup;
	private ConfigurationExpandItemComposite expandItem;
	private Canvas canvas;
	
	/**
	 * Prepares an {@link AConfigurationCompositeBase} object.
	 * @param parent parent {@code Composite} to add this instance to
	 * @param expandItem {@code ExpandItem} for which this object provides content
	 * @param style style passed through to {@code Composite} constructor
	 * @param editor {@link TimerConfigurationEditor} reference, to set dirty flag, if
	 * necessary.
	 * @param model {@link UseCaseViewModel} to databind visual elements to
	 */
	public AConfigurationCompositeBase(Composite parent, 
									   final ConfigurationExpandItemComposite expandItem, 
									   int style, 
									   final TimerConfigurationEditor editor, 
									   UseCaseViewModel model,
									   final int canvasHeight) {
		super(parent, style);
		
		this.editor = editor;
		this.model = model;
		this.expandItem = expandItem;
		
		// Composite Layout (necessary to display anything, it seems)
		GridLayout gl = new GridLayout(2, false);
		setLayout(gl);
		GridData d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		setLayoutData(d);		
		
		// Left Side: Settings Group
		settingsGroup = new Group(this, SWT.NONE);
		settingsGroup.setText("Settings:");
		gl = new GridLayout(2, false);
		settingsGroup.setLayout(gl);
		d = new GridData();
		d.verticalAlignment = SWT.TOP;
		settingsGroup.setLayoutData(d);
		setFontStyle(settingsGroup, SWT.BOLD);
			// Prescale Combo
			Label prescaleL = new Label(settingsGroup, SWT.NONE);
			prescaleL.setText("Prescale divisor:");
			ComboValidationComposite prescaleC = new ComboValidationComposite(settingsGroup, SWT.NONE, model, "prescale", null, PrescaleFactors.values());
			prescaleC.getCombo().addPostSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent arg0) {
					editor.setDirty(true);
				}
			});
		
		// Right Side: Summary Group
		summaryGroup = new Group(this, SWT.NONE);
		summaryGroup.setText("Configuration Summary:");
		gl = new GridLayout(1, false);
		summaryGroup.setLayout(gl);
		d = new GridData();
		d.horizontalAlignment = SWT.RIGHT;
		d.grabExcessHorizontalSpace = true;
		d.verticalAlignment = SWT.TOP;
		d.widthHint = 400;
		summaryGroup.setLayoutData(d);
		Label descriptionL = new Label(summaryGroup, SWT.WRAP);
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		descriptionL.setLayoutData(d);
		canvas = new Canvas(summaryGroup, SWT.BORDER);
		canvas.setSize(370, canvasHeight);
		d = new GridData();
		d.horizontalAlignment = SWT.CENTER;
		d.widthHint = 370;
		d.heightHint = canvasHeight;
		d.grabExcessHorizontalSpace = true;
		d.verticalIndent = 5;
		canvas.setLayoutData(d);
		summaryGroup.layout();
		setFontStyle(summaryGroup, SWT.BOLD);
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(descriptionL), 
				BeansObservables.observeValue(model, "description"));
		model.addPropertyChangeListener("description", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				
				// Only relayout, if the expandItem is expanded
				// -> otherwise no one would see the changes and we could save the CPU time ;)
				if (expandItem.getExpandItem().getExpanded()) {
					summaryGroup.layout();
					layout();
					expandItem.updateLayout();
				}
			}
			
		});
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				GC gc = new GC(canvas);
				gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				gc.fillRectangle(0, 0, 370, canvasHeight);
				gc.setAntialias(SWT.ON);
				drawDescriptionImage(gc);
			    gc.dispose();
			}
			
		});
		
		redrawDescriptionImageIfPropertyChanges("description");
	}
	
	protected void redrawDescriptionImage() {
		// Only redraw image, if the expandItem is expanded
		// -> otherwise no one would see the changes and we could save the CPU time ;)
		if (expandItem.getExpandItem().getExpanded()) {
			canvas.redraw();
		}
	}

	protected void redrawDescriptionImageIfPropertyChanges(String property) {
		model.addPropertyChangeListener(property, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				redrawDescriptionImage();
			}
		});
	}

	protected void redrawDescriptionImageIfPropertyChanges(String... properties) {
		for (String property : properties)
			redrawDescriptionImageIfPropertyChanges(property);
	}
	
	/**
	 * Returns the inner SWT settings Group.
	 * @return SWT settings Group
	 */
	public Group getSettingsGroup() {
		return settingsGroup;
	}
	
	protected void createTopRegisterSelection(Composite parent, Object[] choices, String choicesProperty) {
		
		Label topValueL = new Label(parent, SWT.NONE);
		topValueL.setText("Top value register:");
		
		ComboValidationComposite topComposite = new ComboValidationComposite(parent, SWT.NONE, model, choicesProperty, model.getValidator(), choices);
		
		topComposite.getCombo().addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
				expandItem.updateLayout();
			}
		});
	}
	
	protected void setFontStyle(Control c, int style) {
		FontData[] fD = c.getFont().getFontData();
		fD[0].setStyle(style);
		final Font newFont = new Font(c.getDisplay(),fD[0]);
		c.setFont(newFont);
	}
	
	public abstract void drawDescriptionImage(GC gc);
}
