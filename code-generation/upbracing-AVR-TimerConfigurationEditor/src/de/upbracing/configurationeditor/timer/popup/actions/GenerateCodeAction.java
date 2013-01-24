package de.upbracing.configurationeditor.timer.popup.actions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.Messages.Severity;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.TimerConfigProvider;
import de.upbracing.code_generation.generators.TimerGenerator;
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
        	
        	// Get the model:
			String fullPath = file.getRawLocation().toOSString();
			try {
				ConfigurationModel model = ConfigurationModel.Load(fullPath);
				run(file, model);
			} catch (FileNotFoundException e) {
				showMessageDialog(shell, Severity.ERROR, "AT90CAN Timer Code Generator", "Cannot load Timer Configuration file from path \"" + fullPath + "\".");
			}
    
        }
	}
	
	public void run(IFile file, ConfigurationModel model) {
        // Generate the code
		try {
			@SuppressWarnings("unchecked")
			CodeGeneratorConfigurations config = new CodeGeneratorConfigurations(TimerConfigProvider.class);
			
			// Get the configuration file name:
			String projectFileName = file.getRawLocation().toFile().getName();
			projectFileName = projectFileName.substring(0, projectFileName.indexOf("."));
			config.setState(TimerConfigProvider.STATE, model);
			
			// Create Generator:
			TimerGenerator gen = new TimerGenerator(projectFileName);
			ITemplate cTemp = gen.getFiles().get(projectFileName + ".c");
			ITemplate hTemp = gen.getFiles().get(projectFileName + ".h");
			
			// Validate the model and get the messages
			gen.validate(config, true, null);
			Messages messages = config.getMessages();
			
			// Generate code:
			String cFileContents = cTemp.generate(config, projectFileName);
			String headerContents = hTemp.generate(config, projectFileName);
			
			// If there are messages, show error/warning dialog
			String dialogTxt = "";
			Severity s = Severity.INFO;
			for (Message message : messages.getMessages()) {
				// Store highest severity
				if (message.getSeverity().ordinal() > s.ordinal())
					s = message.getSeverity();
				// Append message
				dialogTxt += message.getMessage() + System.getProperty("line.separator");
			}
			if (s.ordinal() > Severity.INFO.ordinal()) {
				showMessageDialog(shell, s, "AT90CAN Timer Code Generator", dialogTxt);
			}
			
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
					showMessageDialog(shell, Severity.ERROR, "AT90CAN Timer Code Generator", "Cannot open file \"" + pathPrefix + "/" + projectFileName + ".h" + "\" for writing.");
					e.printStackTrace();
					return;
				} catch (CoreException e) {
					// TODO Handle refresh error
					showMessageDialog(shell, Severity.ERROR, "AT90CAN Timer Code Generator", "Something went wrong during code generation.");
					e.printStackTrace();
					return;
				}
			}
			if (s.ordinal() > Severity.INFO.ordinal()) {
				// Finished with errors message
				showMessageDialog(shell, s, "AT90CAN Timer Code Generator", "Generate Code exited with errors or warnings.");
			} else {
				// Success message
				showMessageDialog(shell, s, "AT90CAN Timer Code Generator", "Generate code was executed successfully.");
			}
		}
		catch (Exception e) {
			showMessageDialog(shell, Severity.ERROR, "AT90CAN Timer Code Generator", "Something went wrong\r\n" + e.getMessage());
		}
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

	private void showMessageDialog(Shell shell, Severity severity, String title, String message) {
		if (severity.equals(Severity.ERROR)) {
			MessageDialog.openError(shell, title, message);
		} else if (severity.equals(Severity.WARNING)) {
			MessageDialog.openWarning(shell, title, message);
		} else {
			MessageDialog.openInformation(shell, title, message);
		}
	}
	
}
