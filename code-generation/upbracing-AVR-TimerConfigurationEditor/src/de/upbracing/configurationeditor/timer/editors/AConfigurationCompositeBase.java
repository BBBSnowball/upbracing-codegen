package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;

public abstract class AConfigurationCompositeBase extends Composite {

	protected TimerConfigurationEditor editor;
	protected UseCaseViewModel model;
	private Group settingsGroup;
	private Group summaryGroup;
	private ConfigurationBaseExpandItem expandItem;
	
	public AConfigurationCompositeBase(Composite parent, ConfigurationBaseExpandItem expandItem, int style, final TimerConfigurationEditor editor, UseCaseViewModel model) {
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
			ComboViewer prescaleC = new ComboViewer(settingsGroup, SWT.BORDER);
			prescaleC.setContentProvider(ArrayContentProvider.getInstance());
			prescaleC.setInput(PrescaleFactors.values());
			DataBindingContext c = new DataBindingContext();
			c.bindValue(ViewersObservables.observeSingleSelection(prescaleC),
					BeansObservables.observeValue(model, "prescale"));
			prescaleC.addPostSelectionChangedListener(new ISelectionChangedListener() {
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
		d.widthHint = 350;
		summaryGroup.setLayoutData(d);
		Label descriptionL = new Label(summaryGroup, SWT.WRAP | SWT.BORDER);
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		descriptionL.setLayoutData(d);
		summaryGroup.layout();
		setFontStyle(summaryGroup, SWT.BOLD);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(descriptionL), 
				BeansObservables.observeValue(model, "description"));
	}
	
	public Group getSettingsGroup() {
		return settingsGroup;
	}
	public Group getSummaryGroup() {
		return summaryGroup;
	}
	
	protected void createTopRegisterSelection(Composite parent, Object[] choices, String choicesProperty) {
		
		Label topValueL = new Label(parent, SWT.NONE);
		topValueL.setText("Top value register:");
		ComboViewer topValueC = new ComboViewer(parent, SWT.BORDER);
		topValueC.setContentProvider(ArrayContentProvider.getInstance());
		topValueC.setInput(choices);
		DataBindingContext c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(topValueC),
				BeansObservables.observeValue(model, choicesProperty));
		
		topValueC.addPostSelectionChangedListener(new ISelectionChangedListener() {
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
}
