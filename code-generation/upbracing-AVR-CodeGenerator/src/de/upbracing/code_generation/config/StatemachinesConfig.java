package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import Statecharts.StateMachine;
import Statecharts.StatechartsFactory;
import Statecharts.StatechartsPackage;

/**
 * list of statemachines to generate code for
 * 
 * @author benny
 *
 */
public class StatemachinesConfig implements List<StateMachine> {
	private ArrayList<StateMachine> list = new ArrayList<StateMachine>();
	
	/** load a statemachine and add it to the list of statemachines to generate code for
	 * 
	 * @param filename relative path to statecharts file
	 * @return the state machine
	 */
	public StateMachine load(String filename) {
		// make absolute path
		filename = MCUConfiguration.makeAbsolute(filename);
		
	    // initialize the model
	    StatechartsPackage.eINSTANCE.eClass();
	    StatechartsFactory.eINSTANCE.eClass();
	    
	    // register XMI resource factory for .statecharts extension
	    Resource.Factory.Registry res_factory_registry = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = res_factory_registry.getExtensionToFactoryMap();
	    m.put("statecharts", new XMIResourceFactoryImpl());

	    // load resource
	    Resource resource = new ResourceSetImpl().getResource(
	    		URI.createURI(filename), true);
	    
	    // get first element and hope it has the right type
	    StateMachine sm = (StateMachine) resource.getContents().get(0);
	    
	    // add to this list
	    this.add(sm);
	    
	    return sm;
	}

	public void add(int index, StateMachine element) {
		list.add(index, element);
	}

	public boolean add(StateMachine e) {
		return list.add(e);
	}

	public boolean addAll(Collection<? extends StateMachine> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends StateMachine> c) {
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

	public StateMachine get(int index) {
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

	public Iterator<StateMachine> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<StateMachine> listIterator() {
		return list.listIterator();
	}

	public ListIterator<StateMachine> listIterator(int index) {
		return list.listIterator(index);
	}

	public StateMachine remove(int index) {
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

	public StateMachine set(int index, StateMachine element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	public List<StateMachine> subList(int fromIndex, int toIndex) {
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
	
}
