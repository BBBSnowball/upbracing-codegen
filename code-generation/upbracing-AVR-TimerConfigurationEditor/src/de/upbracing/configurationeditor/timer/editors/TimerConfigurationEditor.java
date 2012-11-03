package de.upbracing.configurationeditor.timer.editors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.EditorPart;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.TimerGenerator;
import de.upbracing.configurationeditor.timer.viewmodel.ConfigurationViewModel;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.eculist.ECUDefinition;

/**
 * This is the graphical timer configuration editor, which is loaded by Eclipse.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class TimerConfigurationEditor extends EditorPart {

	private boolean finishedLoading;
	private boolean isDirty;
	private IFile file;
	private ConfigurationViewModel model;
	private Text freqText;
	private ExpandBar bar;
	
	/**
	 * Creates an editor instance.
	 */
	public TimerConfigurationEditor() {
		super();
		finishedLoading = false;
		isDirty = false;
	}

	/**
	 * Saves the current timer configuration.
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor arg0) {
		
		// Calls the Save("path") Method
		try {
			Save(file.getRawLocation().toOSString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save As functionality is not implemented. This method does nothing.
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// For now, no "Save As..." functionality is implemented
	}

	/**
	 * Initializes the editor and loads the configuration file.
	 * @param arg0 {@code IEditorSite} to display
	 * @param arg1 {@code IEditorInput} (contains path to timer configuration file)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
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

	/**
	 * Returns true, when the timer configuration has unsaved changes.
	 * @return true, when the timer configuration has unsaved changes.
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		// Returns whether the file was altered...
		return isDirty;
	}
	
	/**
	 * Sets the dirty flag of the editor window.
	 * @param value new dirty flag
	 */
	public void setDirty(boolean value) {
		if (finishedLoading) {
			this.isDirty = value;
			firePropertyChange(IEditorPart.PROP_DIRTY); 
		}
		else
			this.isDirty = false;
	}

	/**
	 * Since Save As is not supported, this method always returns false.
	 * @return false
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// For now, no "Save As..." functionality is implemented
		return false;
	}

	/**
	 * Sets focus for frequency text field in the editor window.
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// Changing a drop-down value will make the project dirty but will
		// not allow to save, while no text field is in focus.
		// -> Focus frequency text field to make sure, that a "Save" command will always work
		freqText.setFocus();
	}
	
	/**
	 * Creates the content of the editor window.
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
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
		d.grabExcessHorizontalSpace = true;
		d.horizontalAlignment = SWT.RIGHT;
		newConfigButton.setLayoutData(d);
		
		// Generate Code Button
		final Button generateCodeButton = new Button(arg0, SWT.BORDER);
		generateCodeButton.setText("Generate Code");
		generateCodeButton.setEnabled(true);
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
		
		newConfigButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// Add a new configuration to the model
				UseCaseViewModel newModel = model.addConfiguration();
				// Add the view for this configuration
				ExpandItem ei = new ExpandItem(bar, SWT.NONE);
				// The empty String is (for some strange reason)
				// necessary to make the title appear in linux.
				ei.setText(" ");
				new ConfigurationExpandItemComposite(bar, SWT.NONE, bar, ei, newModel, getEditor());
				// Set project status "dirty"
				setDirty(true);
			}
		});
		
		final Composite mainComposite = arg0;
		generateCodeButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				
				MessageBox m2 = new MessageBox(mainComposite.getShell(), SWT.ICON_ERROR | SWT.OK);
				try {
					MCUConfiguration mcu = new MCUConfiguration(true);
					mcu.setEcus(new ArrayList<ECUDefinition>());
					
					mcu.setTimerConfig(model.getModel());
					// Get the configuration file name:
					String projectFileName = file.getRawLocation().toFile().getName();
					projectFileName = projectFileName.substring(0, projectFileName.indexOf("."));
					
					// Generate code:
					TimerGenerator gen = new TimerGenerator(projectFileName);
					ITemplate cTemp = gen.getFiles().get(projectFileName + ".c");
					ITemplate hTemp = gen.getFiles().get(projectFileName + ".h");
					String cFileContents = cTemp.generate(mcu, projectFileName);
					String headerContents = hTemp.generate(mcu, projectFileName);
					
					String pathPrefix = ((IPath) file.getRawLocation().clone()).removeLastSegments(1).toOSString();
					if (pathPrefix != null) {
						// Write out both files:
						try {
							// Store the generated code on disk:
							PrintWriter w = new PrintWriter(pathPrefix + "/" + projectFileName + ".c");
							w.print(cFileContents);
							w.flush();
							w.close();
							w = new PrintWriter(pathPrefix + "/" + projectFileName + ".h");
							w.print(headerContents);
							w.flush();
							w.close();
							// Refresh files in workspace
							file.getProject().getFile(file.getParent().getProjectRelativePath().toOSString() + "/" + projectFileName + ".c").refreshLocal(0, null);
							file.getProject().getFile(file.getParent().getProjectRelativePath().toOSString() + "/" + projectFileName + ".h").refreshLocal(0, null);
						} catch (FileNotFoundException e) {
							// TODO Give useful error message here!
							MessageBox m = new MessageBox(mainComposite.getShell(), SWT.ICON_ERROR | SWT.OK);
							m.setText("Error");
							m.setMessage("Cannot open file \"" + pathPrefix + "/" + projectFileName + ".h" + "\" for writing.");
							m.open();
							e.printStackTrace();
						} catch (CoreException e) {
							// TODO Handle refresh error
							MessageBox m = new MessageBox(mainComposite.getShell(), SWT.ICON_ERROR | SWT.OK);
							m.setText("Error");
							m.setMessage("Something went wrong during code generation.");
							m.open();
							e.printStackTrace();
						}
					}
				}
				catch (Exception e) {
					m2 = new MessageBox(mainComposite.getShell(), SWT.ICON_ERROR | SWT.OK);
					m2.setText("Error");
					m2.setText("Something went wrong\r\n" + e.getMessage());
				}
				
				
//				CodeGenerator gen = new CodeGenerator(model.getModel());
//				gen.generateCode("/Users/peer/timertest", "/Volumes/Data/Peer/Documents/Uni/RacingCarIT/Program/code-generation/upbracing-AVR-TimerConfigurationEditor/templates/");
			}});

		this.finishedLoading = true;
	}

	/**
	 * Loads a {@code ConfigurationModel} from disk, creates a {@link ConfigurationViewModel} from it
	 * and finally returns it.
	 * @param path to model file
	 * @return loaded/created {@link ConfigurationViewModel}
	 * @throws FileNotFoundException
	 */
	private ConfigurationViewModel Load(String path) throws FileNotFoundException {
		
		return ConfigurationViewModel.Load(path);
	}
	
	/**
	 * Saves the current configuration to disk.
	 * @param path to model file
	 * @throws IOException if save process failed or 
	 * eclipse failed to synchronize the file changes.
	 */
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
	
	/**
	 * This method is a workaround to access this editor from
	 * the "Add new configuration" event handler. "this" does
	 * work in that scenario. 
	 * @return editor instance (this)
	 */
	private TimerConfigurationEditor getEditor() {
		return this;
	}
}
