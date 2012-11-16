package de.upbracing.code_generation.tests.context;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TestContext {
	private String name;
	private Result result;
	private PropertyChangeSupport propchange;
	
	public TestContext(String name) {
		this.name = name;
		this.result = Result.Running.instance;
		this.propchange = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propchange.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propchange.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propchange.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propchange.removePropertyChangeListener(propertyName, listener);
	}

	public String getName() {
		return name;
	}
	
	public Result getResult() {
		return result;
	}
	
	protected void setResult(Result result) {
		Result old_result = result;
		
		this.result = result;
		
		propchange.firePropertyChange("result", old_result, result);
	}
	
	public Result updateResult(Result child_result) {
		setResult(result.combineWith(child_result));
		
		return result;
	}
	
	public void finished() {
		if (result instanceof Result.Running)
			setResult(Result.Success.instance);
	}
}
