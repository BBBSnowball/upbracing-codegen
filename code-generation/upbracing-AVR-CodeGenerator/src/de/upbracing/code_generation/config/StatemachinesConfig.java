package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import statemachine.StateMachine;
import statemachine.StatemachineFactory;
import statemachine.StatemachinePackage;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Helpers;


/**
 * list of statemachines to generate code for
 * 
 * @author benny
 *
 */
public class StatemachinesConfig implements List<StateMachineForGeneration> {
	private ArrayList<StateMachineForGeneration> list = new ArrayList<StateMachineForGeneration>();

	// interrupt hooks
	// key: interrupt name
	// value: additional user code for the interrupt handler (default: empty String)
	private Map<String, String> interrupt_user_code = new TreeMap<String, String>();
	
	/** load a statemachine and add it to the list of statemachines to generate code for
	 * 
	 * @param filename relative path to statemachine file
	 * @return the state machine
	 */
	public StateMachineForGeneration load(String name, String filename) {
		// make absolute path
		filename = CWDProvider.makeAbsolute(filename);
		
		return load(name, URI.createURI(filename));
	}
	
	/** load a statemachine and add it to the list of statemachines to generate code for
	 * 
	 * @param filename relative path to statemachine file
	 * @return the state machine
	 */
	public StateMachineForGeneration load(String name, URI statemachine_file) {
	    // initialize the model
	    StatemachinePackage.eINSTANCE.eClass();
	    StatemachineFactory.eINSTANCE.eClass();
	    
	    // register XMI resource factory for .statemachine extension
	    Resource.Factory.Registry res_factory_registry = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = res_factory_registry.getExtensionToFactoryMap();
	    m.put("statemachine", new XMIResourceFactoryImpl());

	    // load resource
	    Resource resource = new ResourceSetImpl().getResource(
	    		statemachine_file, true);
	    
	    // get first element and hope it has the right type
	    StateMachine sm = (StateMachine) resource.getContents().get(0);
	    
	    // wrap it
	    StateMachineForGeneration smg = new StateMachineForGeneration(name, sm);
	    
	    // add to this list
	    this.add(smg);
	    
	    return smg;
	}

	public void add(int index, StateMachineForGeneration element) {
		list.add(index, element);
	}

	public boolean add(StateMachineForGeneration e) {
		return list.add(e);
	}

	public boolean addAll(Collection<? extends StateMachineForGeneration> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends StateMachineForGeneration> c) {
		return list.addAll(index, c);
	}

	public void clear() {
		list.clear();
	}

	public Object clone() {
		return list.clone();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public void ensureCapacity(int minCapacity) {
		list.ensureCapacity(minCapacity);
	}

	public boolean equals(Object arg0) {
		return list.equals(arg0);
	}

	public StateMachineForGeneration get(int index) {
		return list.get(index);
	}

	public int hashCode() {
		return list.hashCode();
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<StateMachineForGeneration> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<StateMachineForGeneration> listIterator() {
		return list.listIterator();
	}

	public ListIterator<StateMachineForGeneration> listIterator(int index) {
		return list.listIterator(index);
	}

	public StateMachineForGeneration remove(int index) {
		return list.remove(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public StateMachineForGeneration set(int index, StateMachineForGeneration element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	public List<StateMachineForGeneration> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public String toString() {
		return list.toString();
	}

	public void trimToSize() {
		list.trimToSize();
	}

	void addFormatters(Messages messages) {
		Helpers.addStatemachineFormatters(messages, this);
	}
	
	public Map<String, String> getInterruptUserCode() {
		return Collections.unmodifiableMap(interrupt_user_code);
	}
	
	public String getInterruptUserCode(String interrupt_name) {
		return interrupt_user_code.get(interrupt_name);
	}

	private String combineCode(String a, String b) {
		if (a != null)
			return a + "\n" + b;
		else
			return b;
	}
	
	public void addInterruptUserCode(String interrupt_name, String code) {
		String code2 = combineCode(interrupt_user_code.get(interrupt_name), code);
		interrupt_user_code.put(interrupt_name, code2);
	}
}
