package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.upbracing.configurationeditor.timer.Activator;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

public class ConfigurationBaseExpandItem extends ExpandItem {
	
	private UseCaseViewModel model;
	private TimerConfigurationEditor editor;
	private Composite composite;
	private Composite settingsComposite;
	private ExpandBar bar;
	private Group overflowG;
	private Group fastPWMG;
	private Group pcPWMG;
	private Group pfcPWMG;
	private Group ctcG;
	private Group activeG;
	
	public ConfigurationBaseExpandItem(final ExpandBar parent, int style, final UseCaseViewModel model, final TimerConfigurationEditor editor) {
		super(parent, style);
		
		this.model = model;
		this.editor = editor;
		this.bar = parent;
		composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(7, false);
		layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginBottom = 5;
		layout.verticalSpacing = 5;
		composite.setLayout(layout);
		
		// Image for Header:
		ImageDescriptor img = null;
		try {
			img = Activator.getImageDescriptor("./icons/clock.png");
			setImage(img.createImage());
		}
		catch (Exception e) {
			
		}
		
		// Name Setting:
		Label label = new Label(composite, SWT.NONE);
		label.setText("Name:");
		GridData d = new GridData();
		d.horizontalIndent = 1;
		d.widthHint = 150;
		final Text t = new Text(composite, SWT.SINGLE | SWT.BORDER);
		t.setLayoutData(d);
		t.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				setText("Timer Configuration: " + t.getText());
				editor.setDirty(true);
			}
		});
		
		// Timer Combo Box:
		Label timerL = new Label(composite, SWT.NONE);
		timerL.setText("Timer:");
		d = new GridData();
		d.horizontalIndent = 2;
		timerL.setLayoutData(d);
		ComboViewer timerC = new ComboViewer(composite, SWT.BORDER);
		d = new GridData();
		d.horizontalIndent = 3;
		timerC.getControl().setLayoutData(d);
		timerC.setContentProvider(ArrayContentProvider.getInstance());
		timerC.setInput(TimerEnum.values());
		
		// Mode Combo Box:
		Label modeL = new Label(composite, SWT.NONE);
		modeL.setText("Mode:");
		d = new GridData();
		d.horizontalIndent = 4;
		modeL.setLayoutData(d);
		ComboViewer modeC = new ComboViewer(composite, SWT.BORDER);
		d = new GridData();
		d.horizontalIndent = 5;
		modeC.getControl().setLayoutData(d);
		modeC.setContentProvider(ArrayContentProvider.getInstance());
		modeC.setInput(TimerOperationModes.values());
		
		// Delete Button:
		final Button delB = new Button(composite, SWT.NONE);
		delB.setText("Delete configuration");
		d = new GridData();
		d.horizontalIndent = 6;
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
		settingsComposite = new Composite(composite, SWT.NONE);
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		d.horizontalSpan = 7;
		settingsComposite.setLayoutData(d);
		final StackLayout sl = new StackLayout();
		settingsComposite.setLayout(sl);
		initUseCaseGroups();
		
		// Selection Listeners:
		timerC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);		
				
				updateLayout();
			}
		});
		modeC.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				TimerOperationModes mode = (TimerOperationModes) ((StructuredSelection)arg0.getSelection()).getFirstElement();

				selectGroup(mode);
				
				sl.topControl = activeG;
				updateLayout();
				editor.setDirty(true);
				
			}
		});
		modeC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				TimerOperationModes mode = (TimerOperationModes) ((StructuredSelection)arg0.getSelection()).getFirstElement();

				selectGroup(mode);
				
				sl.topControl = activeG;
				updateLayout();
				editor.setDirty(true);
				
			}
		});
		
		// DataBindings:
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(t, SWT.Modify), 
				BeansObservables.observeValue(model, "name"));
		c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(modeC),
				BeansObservables.observeValue(model, "mode"));
		c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(timerC),
				BeansObservables.observeValue(model, "timer"));
		c = new DataBindingContext();
		
		composite.layout();
		setControl(composite);
		setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}
	
	private void initUseCaseGroups() {
		
		initOverflowGroup();
		initFastPWMGroup();
		initPhaseCorrectPWMGroup();
		initPhaseAndFrequencyCorrectPWMGroup();
		initCTCGroup();
		
		activeG = overflowG;
		settingsComposite.layout();
		setHeight(settingsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}
	
	private Group initGroupWidget() {
		Group useCaseG = new Group(settingsComposite, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		useCaseG.setLayout(gl);
		GridData g = new GridData();
		g.horizontalAlignment = SWT.FILL;
		g.grabExcessHorizontalSpace = true;
		useCaseG.setLayoutData(g);
		return useCaseG;
	}
	
	private Group initTopValueGroup(Group g, String title) {

		// OCR Groups
		initTopValueItem(g, "icrName", "icrPeriod", "icrVisibility", false);
		initTopValueItem(g, "ocrAName", "ocrAPeriod", null, true);
		initTopValueItem(g, "ocrBName", "ocrBPeriod", "ocrChannelsVisibility", true);
		initTopValueItem(g, "ocrCName", "ocrCPeriod", "ocrChannelsVisibility", true);
		
		return g;
	}

	private void initTopValueItem(Group g, String nameProperty, String periodProperty, String enabledProperty, boolean compareInterrupt) {
		
		CollapsibleComposite scComp = new CollapsibleComposite(g, SWT.BORDER);
		GridData d = new GridData();
		d.horizontalSpan = 4;
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		scComp.setLayoutData(d);
		GridLayout l = new GridLayout(3, false);
		scComp.setLayout(l);
		
		DataBindingContext c;
		c = new DataBindingContext();
		if (enabledProperty != null) {
			c.bindValue(SWTObservables.observeEnabled(scComp), 
					BeansObservables.observeValue(model, enabledProperty));
		}
		
		// Label for Register Name:
		Label lbPrefix = new Label(scComp, SWT.BORDER);
		lbPrefix.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT); 
		d = new GridData();
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.FILL;
		d.horizontalSpan = 3;
		lbPrefix.setLayoutData(d);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(lbPrefix), 
				BeansObservables.observeValue(model, nameProperty));
		setFontStyle(lbPrefix, SWT.BOLD);
		
		// Label for Period
		Label freqLOA = new Label(scComp, SWT.NONE);
		freqLOA.setText("Period:");
		// Textbox
		Text tFreq = new Text(scComp, SWT.BORDER);
		d = new GridData();
		d.widthHint = 100;
		d.minimumWidth = 100;
		d.horizontalAlignment = SWT.RIGHT;
		d.grabExcessHorizontalSpace = true;
		tFreq.setLayoutData(d);
		c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(tFreq, SWT.Modify), 
				BeansObservables.observeValue(model, periodProperty));
		if (enabledProperty != null) {
			c.bindValue(SWTObservables.observeEnabled(tFreq), 
					BeansObservables.observeValue(model, enabledProperty));
		}
		tFreq.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				editor.setDirty(true);
			}});
		// Label for Unit
		Label lbUnit = new Label(scComp, SWT.NONE);
		lbUnit.setText("s");
		
		if (compareInterrupt) {
			// Interrupt enable checkbox for Compare Match
			Label intL = new Label(scComp, SWT.NONE);
			intL.setText("Compare match interrupt:");
			Button intCb = new Button(scComp, SWT.CHECK);
			c = new DataBindingContext();
			c.bindValue(SWTObservables.observeSelection(intCb), 
					BeansObservables.observeValue(model, "overflowInterrupt"));
			d = new GridData();
			d.horizontalSpan = 2;
			intCb.setLayoutData(d);
		}
	}
	
	private void initPrescaleCombo(Group g) {
		
		Label prescaleL = new Label(g, SWT.NONE);
		GridData d = new GridData();
		d.horizontalSpan = 2;
		prescaleL.setLayoutData(d);
		prescaleL.setText("Prescale divisor:");
		ComboViewer timerC = new ComboViewer(g, SWT.BORDER);
		timerC.setContentProvider(ArrayContentProvider.getInstance());
		timerC.setInput(PrescaleFactors.values());
		DataBindingContext c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(timerC),
				BeansObservables.observeValue(model, "prescale"));
		
		timerC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
			}
		});
		
		// Empty placeholder at end
		Label place = new Label(g, SWT.NONE);
		d = new GridData();
		d.horizontalIndent = 4;
		place.setLayoutData(d);
	}
	
	private void initSummaryGroup(Group g) {
		
		// Summary Group
		Group descriptionG = new Group(g, SWT.NONE);
		descriptionG.setText("Configuration Summary:");
		GridLayout gl = new GridLayout(1, false);
		descriptionG.setLayout(gl);
		GridData d = new GridData();
		d.horizontalAlignment = SWT.RIGHT;
		d.grabExcessHorizontalSpace = true;
		d.verticalAlignment = SWT.FILL;
		d.widthHint = 350;
		descriptionG.setLayoutData(d);
		Label descriptionL = new Label(descriptionG, SWT.WRAP | SWT.BORDER);
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		d.verticalAlignment = SWT.FILL;
		d.grabExcessVerticalSpace = true;
		descriptionL.setLayoutData(d);
		descriptionG.layout();
		
		setFontStyle(descriptionG, SWT.BOLD);
		
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeText(descriptionL), 
				BeansObservables.observeValue(model, "description"));
	}
	
	private Group initSettingsGroup(Group g) {
		// Settings Group
		Group settingsG = new Group(g, SWT.NONE);
		settingsG.setText("Settings:");
		GridLayout gl = new GridLayout(4, false);
		settingsG.setLayout(gl);
		GridData d = new GridData();
		d.verticalAlignment = SWT.TOP;
//		d.horizontalAlignment = SWT.FILL;
//		d.grabExcessHorizontalSpace = true;
		settingsG.setLayoutData(d);
		
		setFontStyle(settingsG, SWT.BOLD);
		
		return settingsG;
	}
	
	private void initOverflowGroup() {
		
		overflowG = initGroupWidget();
		
		
		Group settingsG = initSettingsGroup(overflowG);
		initPrescaleCombo(settingsG);
		initSummaryGroup(overflowG);
		
		// Interrupt enable checkbox for overflow
		Label intL = new Label(settingsG, SWT.NONE);
		intL.setText("Overflow interrupt:");
		GridData d = new GridData();
		d.horizontalSpan = 2;
		intL.setLayoutData(d);
		Button intCb = new Button(settingsG, SWT.CHECK);
		DataBindingContext c = new DataBindingContext();
		c.bindValue(SWTObservables.observeSelection(intCb), 
				BeansObservables.observeValue(model, "overflowInterrupt"));
		d = new GridData();
		d.horizontalSpan = 2;
		intCb.setLayoutData(d);
		
		// Image
		ImageDescriptor img = null;
		try {
			img = Activator.getImageDescriptor("./images/Overflow.png");
			Image i = img.createImage();
			Label comp = new Label(settingsG, SWT.IMAGE_PNG);
			d = new GridData();
			d.horizontalSpan = 4;
			d.grabExcessHorizontalSpace = true;
			comp.setLayoutData(d);
			comp.setImage(i);
		}
		catch (Exception e) {
		}
		
		overflowG.layout();
	}
	
	private void initFastPWMGroup() {
		
		fastPWMG = initGroupWidget();
		
		Group settingsG = initSettingsGroup(fastPWMG);
		initPrescaleCombo(settingsG);
		initSummaryGroup(fastPWMG);
		
		GridData d = new GridData();
		d.horizontalSpan = 2;
		Label topValueL = new Label(settingsG, SWT.NONE);
		topValueL.setText("Top value register:");
		topValueL.setLayoutData(d);
		ComboViewer topValueC = new ComboViewer(settingsG, SWT.BORDER);
		topValueC.setContentProvider(ArrayContentProvider.getInstance());
		topValueC.setInput(PWMTopValues.values());
		DataBindingContext c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(topValueC),
				BeansObservables.observeValue(model, "fastPWMTop"));
		d = new GridData();
		d.horizontalSpan = 2;
		topValueC.getControl().setLayoutData(d);
		
		topValueC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
			}
		});
		
		fastPWMG.layout();
	}
	
	private void initPhaseCorrectPWMGroup() {
		
		pcPWMG = initGroupWidget();
		
		Group settingsG = initSettingsGroup(pcPWMG);
		initPrescaleCombo(settingsG);
		initSummaryGroup(pcPWMG);
		
		GridData d = new GridData();
		d.horizontalSpan = 2;
		Label topValueL = new Label(settingsG, SWT.NONE);
		topValueL.setText("Top value register:");
		topValueL.setLayoutData(d);
		ComboViewer topValueC = new ComboViewer(settingsG, SWT.BORDER);
		topValueC.setContentProvider(ArrayContentProvider.getInstance());
		topValueC.setInput(PWMTopValues.values());
		DataBindingContext c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(topValueC),
				BeansObservables.observeValue(model, "phaseCorrectPWMTop"));
		d = new GridData();
		d.horizontalSpan = 2;
		topValueC.getControl().setLayoutData(d);
		
		topValueC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
			}
		});
		
		pcPWMG.layout();
	}
	
	private void initPhaseAndFrequencyCorrectPWMGroup() {
		
		pfcPWMG = initGroupWidget();
		
		Group settingsG = initSettingsGroup(pfcPWMG);
		initPrescaleCombo(settingsG);
		initSummaryGroup(pfcPWMG);
		
		GridData d = new GridData();
		d.horizontalSpan = 2;
		Label topValueL = new Label(settingsG, SWT.NONE);
		topValueL.setText("Top value register:");
		topValueL.setLayoutData(d);
		ComboViewer topValueC = new ComboViewer(settingsG, SWT.BORDER);
		topValueC.setContentProvider(ArrayContentProvider.getInstance());
		topValueC.setInput(PhaseAndFrequencyCorrectPWMTopValues.values());
		DataBindingContext c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(topValueC),
				BeansObservables.observeValue(model, "phaseAndFrequencyCorrectPWMTop"));
		d = new GridData();
		d.horizontalSpan = 2;
		topValueC.getControl().setLayoutData(d);
		
		topValueC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
			}
		});
		
		pfcPWMG.layout();
	}
	
	private void initCTCGroup() {
		
		ctcG = initGroupWidget();
		
		final Group settingsG = initSettingsGroup(ctcG);
		initPrescaleCombo(settingsG);
		initSummaryGroup(ctcG);
		
		GridData d = new GridData();
		d.horizontalSpan = 2;
		Label topValueL = new Label(settingsG, SWT.NONE);
		topValueL.setLayoutData(d);
		topValueL.setText("Top value register:");
		ComboViewer topValueC = new ComboViewer(settingsG, SWT.BORDER);
		topValueC.setContentProvider(ArrayContentProvider.getInstance());
		topValueC.setInput(CTCTopValues.values());
		DataBindingContext c = new DataBindingContext();
		c.bindValue(ViewersObservables.observeSingleSelection(topValueC),
				BeansObservables.observeValue(model, "ctcTop"));
		d = new GridData();
		d.horizontalSpan = 2;
		topValueC.getControl().setLayoutData(d);
		
		initTopValueGroup(settingsG, "");
		
		topValueC.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				editor.setDirty(true);
				updateLayout();
			}
		});
	
		ctcG.layout();
	}
	
	private void selectGroup(TimerOperationModes mode) {
		
		if (mode.equals(TimerOperationModes.OVERFLOW)) {
			activeG = overflowG;
		}
		
		if (mode.equals(TimerOperationModes.CTC)) {;
			activeG = ctcG;
		}
		
		if (mode.equals(TimerOperationModes.PWM_FAST)) {
			activeG = fastPWMG;
		}
		
		if (mode.equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
			activeG = pcPWMG;
		}
		
		if (mode.equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
			activeG = pfcPWMG;
		}
	}

	private void removeThis() {
		
		int index = -1;
		for (ExpandItem i: bar.getItems()) {
			index++;
			if (i == this) {
				break;
			}
		}
		
		// Delete at index and shift every following page one back
		bar.getItem(index).getControl().dispose();
		bar.getItem(index).dispose();
		bar.layout();
		
	}

	private void updateLayout() {
		activeG.layout();
		settingsComposite.layout();
		composite.layout();
		
		setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}
	
	private void setFontStyle(Control c, int style) {
		FontData[] fD = c.getFont().getFontData();
		fD[0].setStyle(style);
		final Font newFont = new Font(c.getDisplay(),fD[0]);
		c.setFont(newFont);
		
//		c.addDisposeListener(new DisposeListener() {
//
//			@Override
//			public void widgetDisposed(DisposeEvent arg0) {
//				// TODO Auto-generated method stub
//				newFont.dispose();
//			}
//			
//		});
	}
}
