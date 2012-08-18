//package de.upbracing.configurationeditor.timer.editors;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//import org.eclipse.core.databinding.validation.IValidator;
//import org.eclipse.core.databinding.validation.ValidationStatus;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Status;
//import org.eclipse.jface.fieldassist.ControlDecoration;
//
//import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
//import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;
//
//public class ValidationRouter implements IValidator {
//
//	private String propertyStatus;
//	private UseCaseViewModel model;
//	private ControlDecoration decoration;
//	
//	public ValidationRouter(UseCaseViewModel model, String propertyStatus, ControlDecoration decoration) {
//		super();
//		this.propertyStatus = propertyStatus;
//		this.model = model;
//		this.decoration = decoration;
//	}
//	
//	@Override
//	public IStatus validate(Object value) {
//		
//		// Get validation status:
//		UseCaseModelValidator validator = model.getValidator();
//		boolean status = false;
//		try {
//			Method m = validator.getClass().getDeclaredMethod(propertyStatus);
//			status = (Boolean) m.invoke(validator);
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if (status) {
//			decoration.show();
//			return ValidationStatus.error("There was an error with this value!");
//		}
////		if (value instanceof Double) {
////            String text = (String) value;
////            if (text.trim().length() == 0) {
////                decoration.show();
////                return ValidationStatus
////                        .error(errorText);
////            }
////        }
//        decoration.hide();
//        return Status.OK_STATUS;
//	}
//
//}
