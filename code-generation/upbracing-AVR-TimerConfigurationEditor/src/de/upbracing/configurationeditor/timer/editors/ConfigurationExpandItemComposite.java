package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;

import de.upbracing.configurationeditor.timer.Activator;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

/**
 * Content for the {@code UseCaseModel} ExpandItems.<br>
 * Consists of "Name", "Timer", "Mode", "Delete Button",
 * and a "SettingsComposite".<p>
 * "SettingsComposite" is filled with {@link AConfigurationCompositeBase} 
 * objects for each possible use case. Depending on "Mode" setting, the correct
 * {@link AConfigurationCompositeBase} is displayed, while all others are hidden.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationExpandItemComposite extends Composite {

	private ExpandBar bar;
	private ExpandItem expandItem;
	private Composite settingsComposite;
	private ConfigurationCompositeOverflow overflowC;
	private ConfigurationCompositeCTC ctcC;
	private ConfigurationCompositeFastPWM fastPWMC;
	private ConfigurationCompositePhaseCorrectPWM pcPWMC;
	private ConfigurationCompositePhaseAndFrequencyCorrectPWM pfcPWMC;
	private AConfigurationCompositeBase activeC;
	
	/**
	 * Creates a new {@link ConfigurationExpandItemComposite} instance.
	 * @param parent 
	 * @param style style passed through to {@code Composite} constructor
	 * @param bar {@code ExpandBar} reference (needs for triggering its redraw() method)
	 * @param expandItem {@code ExpandItem} for which this instance provides content
	 * @param model {@link UseCaseViewModel} to databind visual elements to
	 * @param editor {@link TimerConfigurationEditor} reference, to set dirty flag, if
	 * necessary.
	 */
	public ConfigurationExpandItemComposite(Composite parent, 
											int style, 
											final ExpandBar bar, 
											final ExpandItem expandItem, 
											final UseCaseViewModel model, 
											final TimerConfigurationEditor editor) {
		super(parent, style);

		this.bar = bar;
		this.expandItem = expandItem;
		
		GridLayout layout = new GridLayout(4, false);
		layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginBottom = 5;
		setLayout(layout);
		
		// Image for Header:
		ImageDescriptor img = null;
		try {
			img = Activator.getImageDescriptor("./icons/clock.png");
			expandItem.setImage(img.createImage());
		}
		catch (Exception e) {
			
		}
		
//		// Test Composite with RowLayout:
//		Composite cmp = new Composite(this, SWT.BORDER);
//		GridData d = new GridData();
//		d.horizontalSpan = 6;
//		cmp.setLayoutData(d);
//		RowLayout rl = new RowLayout();
//		cmp.setLayout(rl);
		
		// Name Setting:
		final TextValidationComposite t = new TextValidationComposite(this, SWT.NONE, "Name:", null, "name", model.getValidator(), null, String.class);
		t.getTextBox().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				expandItem.setText("Timer Configuration: " + t.getTextBox().getText());
				bar.redraw();
				editor.setDirty(true);
			}
		});
		
		// Timer Combo Box:
		ComboValidationComposite timerC = new ComboValidationComposite(this, SWT.NONE, "Timer:", null, "timer", model.getValidator(), TimerEnum.values());
		
		// Mode Combo Box:
		ComboValidationComposite modeC = new ComboValidationComposite(this, SWT.NONE, "Mode:", null, "mode", model.getValidator(), TimerOperationModes.values());
		
		// Delete Button:
		final Button delB = new Button(this, SWT.NONE);
		delB.setText("Delete configuration");
		GridData d = new GridData();
		d.horizontalAlignment = SWT.RIGHT;
		delB.setLayoutData(d);
		
		delB.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				// Ask for confirmation
				MessageDialog d = new MessageDialog(delB.getShell(), "Delete confirmation", null, "The configuration " + model.getName() + " is about to be deleted. Do you want to continue?", 0, new String[] {"No", "Yes"}, 0);
				int result = d.open();
				// If ok, delete and set dirty
				if (result == 1) {
					// Remove model
					model.getParent().removeConfiguration(model);
					// Remove this from parent
					removeThis();
					// Set project dirty
					editor.setDirty(true);
				}
			}
			
		});
		
		// Settings Composite
		settingsComposite = new Composite(this, SWT.NONE);
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		d.horizontalSpan = 4;
		settingsComposite.setLayoutData(d);
		final StackLayout sl = new StackLayout();
		settingsComposite.setLayout(sl);
		
		overflowC = new ConfigurationCompositeOverflow(settingsComposite, this, SWT.NONE, editor, model);
		ctcC = new ConfigurationCompositeCTC(settingsComposite, this, SWT.NONE, editor, model);
		fastPWMC = new ConfigurationCompositeFastPWM(settingsComposite, this, SWT.NONE, editor, model);
		pcPWMC = new ConfigurationCompositePhaseCorrectPWM(settingsComposite, this, SWT.NONE, editor, model);
		pfcPWMC = new ConfigurationCompositePhaseAndFrequencyCorrectPWM(settingsComposite, this, SWT.NONE, editor, model);
		initUseCaseGroups();
		setVisibility(overflowC, false);
		setVisibility(ctcC, false);
		setVisibility(fastPWMC, false);
		setVisibility(pcPWMC, false);
		setVisibility(pfcPWMC, false);
		
		// Selection Listeners:
		timerC.getCombo().addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);		
				
				updateLayout();
			}
		});
		modeC.getCombo().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				TimerOperationModes mode = (TimerOperationModes) ((StructuredSelection)arg0.getSelection()).getFirstElement();

				selectGroup(mode);
				
				sl.topControl = activeC;
				updateLayout();
				editor.setDirty(true);
				
			}
		});
		modeC.getCombo().addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				TimerOperationModes mode = (TimerOperationModes) ((StructuredSelection)arg0.getSelection()).getFirstElement();

				selectGroup(mode);
				
				sl.topControl = activeC;
				updateLayout();
				editor.setDirty(true);
				
			}
		});
		
		// DataBindings:
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(t.getTextBox(), SWT.Modify), 
				BeansObservables.observeValue(model, "name"));
		c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(modeC.getCombo()),
				BeansObservables.observeValue(model, "mode"));
		c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(timerC.getCombo()),
				BeansObservables.observeValue(model, "timer"));
		
		layout();
		expandItem.setControl(this);
		expandItem.setHeight(activeC.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 50);
	}

	/**
	 * Forces this composite to recalculate size and repaint.
	 */
	public void updateLayout() {
		activeC.layout();
		settingsComposite.layout();
		layout();
		
		expandItem.setHeight(activeC.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 50);
	}
	
	public ExpandItem getExpandItem() {
		return this.expandItem;
	}

	private void initUseCaseGroups() {
		
		activeC = overflowC;
		settingsComposite.layout();
	}
	
	private void selectGroup(TimerOperationModes mode) {
		
		if (mode.equals(TimerOperationModes.OVERFLOW)) {
			activeC = overflowC;
		}
		
		if (mode.equals(TimerOperationModes.CTC)) {;
			activeC = ctcC;
		}
		
		if (mode.equals(TimerOperationModes.PWM_FAST)) {
			activeC = fastPWMC;
		}
		
		if (mode.equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
			activeC = pcPWMC;
		}
		
		if (mode.equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
			activeC = pfcPWMC;
		}
	}

	private void removeThis() {
		
		int index = -1;
		for (ExpandItem i: bar.getItems()) {
			index++;
			if (i == expandItem) {
				break;
			}
		}
		
		// Delete at index and shift every following page one back
		bar.getItem(index).getControl().dispose();
		bar.getItem(index).dispose();
		bar.layout();
		dispose();
	}

	private void setVisibility(Composite c, boolean b) {

		GridData d = (GridData) c.getLayoutData();
		if (b)
			d.heightHint = -1;
		else
			d.heightHint = 0;
		d.exclude = !b;
		c.setVisible(b);
	}

}
