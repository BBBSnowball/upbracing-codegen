package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;

import de.upbracing.shared.timer.model.validation.ValidationResult;

public class StatusImageConverter implements IConverter {
	
	@Override
	public Object convert(Object arg0) {
		
		if (((ValidationResult) arg0).equals(ValidationResult.ERROR)) {
			FieldDecoration fieldDecoration = FieldDecorationRegistry
			    .getDefault().getFieldDecoration(
			         FieldDecorationRegistry.DEC_ERROR);
			return fieldDecoration.getImage();
		}
		if (((ValidationResult) arg0).equals(ValidationResult.WARNING)) {
			FieldDecoration fieldDecoration = FieldDecorationRegistry
			    .getDefault().getFieldDecoration(
			         FieldDecorationRegistry.DEC_WARNING);
			return fieldDecoration.getImage();
		}
		
		FieldDecoration fieldDecoration = FieldDecorationRegistry
			    .getDefault().getFieldDecoration(
			         FieldDecorationRegistry.DEC_INFORMATION);
			return fieldDecoration.getImage();
	}

	@Override
	public Object getFromType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getToType() {
		// TODO Auto-generated method stub
		return Image.class;
	}

}
