package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.upbracing.configurationeditor.timer.Activator;
import de.upbracing.shared.timer.model.validation.ValidationResult;

/**
 * Converts a ValidationResult to an Image.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class StatusImageConverter implements IConverter {
	
	private Image errorImage;
	private Image warningImage;
	private Image okImage;
	
	/**
	 * Loads the images for error, warning and ok statuses.
	 */
	public StatusImageConverter() {
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("./images/icon_ok.gif");
		if (imageDescriptor != null)
			okImage = imageDescriptor.createImage();
		else
			// fallback in case we cannot load the image file
			// (I don't know how to solve the problem, so I add this workaround.)
			okImage = PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		
		errorImage = PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		warningImage = PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
	}
	
	/**
	 * Converts {@code arg0} from ValidationResult to Image.
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object arg0) {
		
		if (((ValidationResult) arg0).equals(ValidationResult.ERROR)) {
			return errorImage;
		}
		if (((ValidationResult) arg0).equals(ValidationResult.WARNING)) {
			return warningImage;
		}
		
		return okImage;
	}

	/**
	 * FromType is ValidationResult.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		// FromType is ValidationResult
		return ValidationResult.class;
	}

	/**
	 * ToType is Image.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		// ToType is Image
		return Image.class;
	}

}
