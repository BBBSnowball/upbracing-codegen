package de.upbracing.configurationeditor.timer.popup.actions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.TimerGenerator;
import de.upbracing.eculist.ECUDefinition;
import de.upbracing.shared.timer.model.ConfigurationModel;

public class GenerateCodeAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public GenerateCodeAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		run(getSelectedFile());		
	}

	public void run(IFile file) {
		// Get file/model to generate code for:
        if (file != null) {
        	
            // Generate the code
    		try {
				MCUConfiguration mcu = new MCUConfiguration(true);
				mcu.setEcus(new ArrayList<ECUDefinition>());
				
				// Get the configuration file name:
				String projectFileName = file.getRawLocation().toFile().getName();
				projectFileName = projectFileName.substring(0, projectFileName.indexOf("."));
				// Get the model:
				String fullPath = file.getRawLocation().toOSString();
				ConfigurationModel model = ConfigurationModel.Load(fullPath);
				if (model == null) {
					// TODO: Handle loading errors
					MessageBox m = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					m.setText("Error");
					m.setMessage("Cannot load the model from path \"" + fullPath + "\".");
					m.open();
					return;
				}
				mcu.setTimerConfig(model);
				
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
						MessageBox m = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
						m.setText("Error");
						m.setMessage("Cannot open file \"" + pathPrefix + "/" + projectFileName + ".h" + "\" for writing.");
						m.open();
						e.printStackTrace();
						return;
					} catch (CoreException e) {
						// TODO Handle refresh error
						MessageBox m = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
						m.setText("Error");
						m.setMessage("Something went wrong during code generation.");
						m.open();
						e.printStackTrace();
						return;
					}
				}
			}
			catch (Exception e) {
				MessageBox m = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				m.setText("Error");
				m.setText("Something went wrong\r\n" + e.getMessage());
				return;
			}
        }
		
        // Success message
		MessageDialog.openInformation(
			shell,
			"AT90CAN Timer Code Generator",
			"Generate Code was executed successfully.");
	}
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	private IFile getSelectedFile() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = 
		        workbench == null ? null : workbench.getActiveWorkbenchWindow();
		ISelectionService service = window.getSelectionService();
		ISelection selection = service.getSelection();
		TreeSelection ts = (TreeSelection) selection;
		Object obj = ts.getFirstElement();
	    IFile file = (IFile) Platform.getAdapterManager().getAdapter(obj,
                IFile.class);
        if (file == null) {
            if (obj instanceof IAdaptable) {
                file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
            }
        }
        return file;
	}

}
