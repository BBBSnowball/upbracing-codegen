package de.upbracing.configurationeditor.timer.editors;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.EditorPart;

import de.upbracing.configurationeditor.timer.viewmodel.ConfigurationViewModel;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;

public class TimerConfigurationEditor extends EditorPart implements Listener {

	private boolean finishedLoading = false;
	private boolean isDirty = false;
	private IFile file;
	private ConfigurationViewModel model;
	private Text freqText;
	private ExpandBar bar;
	
	public TimerConfigurationEditor() {
		super();
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		
		// Calls the Save("path") Method
		try {
			Save(file.getRawLocation().toOSString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		// For now, no "Save As..." functionality is implemented
	}

	@Override
	public void init(IEditorSite arg0, IEditorInput arg1)
			throws PartInitException {
		
		// Loads the file
		setSite(arg0);
		setInput(arg1);
		file = ResourceUtil.getFile(arg1);
		try {
			model = Load(file.getRawLocation().toOSString());
			setPartName(file.getName());
		} catch (FileNotFoundException e) {
			// Print stack and quit, if file was not found
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDirty() {
		// Returns whether the file was altered...
		return isDirty;
	}
	
	public void setDirty(boolean value) {
		if (finishedLoading) {
			this.isDirty = value;
			firePropertyChange(IEditorPart.PROP_DIRTY); 
		}
		else
			this.isDirty = false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// For now, no "Save As..." functionality is implemented
		return false;
	}

	@Override
	public void setFocus() {
		// Changing a drop-down value will make the project dirty but will
		// not allow to save, while no text field is in focus.
		// -> Focus frequency text field to make sure, that a "Save" command will always work
		freqText.setFocus();
	}
	
	@Override
	public void createPartControl(Composite arg0) {
		
		finishedLoading = false;
		
		// Initializes the view from the loaded file (model)
		GridLayout fillLayout = new GridLayout(3, false);
		fillLayout.marginHeight = fillLayout.marginWidth = 10;
		arg0.setLayout(fillLayout);
		
		// General Settings Group
		Group gs = new Group(arg0, SWT.BORDER);
		GridLayout layout = new GridLayout(3, false);
		layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginBottom = 5;
		layout.verticalSpacing = 5;
		gs.setLayout(layout);
		gs.setText("General Settings:");
		Label label = new Label(gs, SWT.NONE);
		label.setText("CPU Clock:");
		TextValidationComposite text = new TextValidationComposite(gs, SWT.NONE, model, "frequency", model.getValidator(), "Hz", Integer.class);
		freqText = text.getTextBox();
		freqText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				setDirty(true);
			}
		});
		FontData[] fD = gs.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		gs.setFont(new Font(gs.getDisplay(),fD[0]));
		gs.layout();
		
		// Add Configuration Button
		Button newConfigButton = new Button(arg0, SWT.BORDER);
		newConfigButton.setText("Add new configuration");
		GridData d = new GridData();
		d.horizontalAlignment = SWT.RIGHT;
		newConfigButton.setLayoutData(d);
		
		// Generate Code Button
		Button generateCodeButton = new Button(arg0, SWT.BORDER);
		generateCodeButton.setText("Generate Code");
		generateCodeButton.setEnabled(false);
		d = new GridData();
		d.horizontalAlignment = SWT.RIGHT;
		generateCodeButton.setLayoutData(d);
		
		// ExpandBar with individual configurations
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalSpan = 3;
		
		bar = new ExpandBar(arg0, SWT.V_SCROLL);
		bar.setLayoutData(data);
		
		for (UseCaseViewModel m: model.getConfigurations()) {
			ExpandItem ei = new ExpandItem(bar, SWT.NONE);
			// The empty String is (for some strange reason)
			// necessary to make the title appear in linux.
			ei.setText(" ");
			new ConfigurationExpandItemComposite(bar, SWT.NONE, bar, ei, m, this);
		}
		
		newConfigButton.addListener(SWT.Selection, this);
		generateCodeButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
//				CodeGenerator gen = new CodeGenerator(model.getModel());
//				gen.generateCode("/Users/peer/timertest", "/Volumes/Data/Peer/Documents/Uni/RacingCarIT/Program/code-generation/upbracing-AVR-TimerConfigurationEditor/templates/");
			}});

		this.finishedLoading = true;
	}
	
	private ConfigurationViewModel Load(String path) throws FileNotFoundException {
		
		return ConfigurationViewModel.Load(path);
	}
	
	private void Save(String path) throws IOException {
		
		// Resynchronize the file after it has been altered
		try {
			model.Save(path);
			file.refreshLocal(0, null);
			
			// File is not dirty anymore
			setDirty(false);
			
		} catch (CoreException e) {
			// Print stack and quit, if there was an error while saving or refreshing
			e.printStackTrace();
		}
	}

	public void handleEvent(Event event) {
		// Add a new configuration to the model
		UseCaseViewModel newModel = model.addConfiguration();
		// Add the view for this configuration
		ExpandItem ei = new ExpandItem(bar, SWT.NONE);
		// The empty String is (for some strange reason)
		// necessary to make the title appear in linux.
		ei.setText(" ");
		new ConfigurationExpandItemComposite(bar, SWT.NONE, bar, ei, newModel, this);
		// Set project status "dirty"
		setDirty(true);
	}
}
