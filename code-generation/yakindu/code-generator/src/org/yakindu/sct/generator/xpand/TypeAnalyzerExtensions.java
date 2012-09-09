package org.yakindu.sct.generator.xpand;

import org.eclipse.xtend.expression.ExecutionContext;
import org.eclipse.xtend.expression.IExecutionContextAware;
import org.yakindu.base.types.ITypeSystemAccess;
import org.yakindu.base.types.Type;
import org.yakindu.sct.model.sexec.If;
import org.yakindu.sct.model.sexec.Reaction;
import org.yakindu.sct.model.sexec.Step;
import org.yakindu.sct.model.sgraph.Statement;
import org.yakindu.sct.model.stext.validation.ITypeInferrer;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class TypeAnalyzerExtensions implements IExecutionContextAware {

	private static final Object CONTEXT_INJECTOR_PROPERTY_NAME = "AbstractXpandBasedCodeGenerator.Injector";
	@Inject
	private ITypeInferrer typeInferrer;
	@Inject
	private ITypeSystemAccess access;
	Step step;
	

	public void setExecutionContext(ExecutionContext ctx) {
		Injector injector = null;
		if (ctx.getGlobalVariables().get(CONTEXT_INJECTOR_PROPERTY_NAME) != null) {
			injector = (Injector) ctx.getGlobalVariables()
					.get(CONTEXT_INJECTOR_PROPERTY_NAME).getValue();
		} else if (ctx.getGlobalVariables().get(Injector.class.getName()) != null) {
			injector = (Injector) ctx.getGlobalVariables()
					.get(Injector.class.getName()).getValue();
		}
		if (injector != null) {
			injector.injectMembers(this);
		}
	}

	public boolean isBoolean(Type type) {
		return access.isBoolean(type);
	}

	public boolean isInteger(Type type) {
		return access.isInteger(type);
	}

	public boolean isReal(Type type) {
		return access.isReal(type);
	}

	public boolean isString(Type type) {
		return access.isString(type);
	}

	public Type getType(Statement stmt) {
		return typeInferrer.getType(stmt);
	}
}
