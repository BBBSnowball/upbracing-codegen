/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts.impl;

import Statecharts.OrthogonalState;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StatechartsPackage;
import Statecharts.TextArea;
import Statecharts.Transition;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>State Machine</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link Statecharts.impl.StateMachineImpl#getTransitions <em>Transitions</em>}</li>
 *   <li>{@link Statecharts.impl.StateMachineImpl#getStates <em>States</em>}</li>
 *   <li>{@link Statecharts.impl.StateMachineImpl#getTextboxes <em>Textboxes</em>}</li>
 *   <li>{@link Statecharts.impl.StateMachineImpl#getContains <em>Contains</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class StateMachineImpl extends EObjectImpl implements StateMachine
{
  /**
   * The cached value of the '{@link #getTransitions() <em>Transitions</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransitions()
   * @generated
   * @ordered
   */
  protected EList<Transition> transitions;

  /**
   * The cached value of the '{@link #getStates() <em>States</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStates()
   * @generated
   * @ordered
   */
  protected EList<State> states;

  /**
   * The cached value of the '{@link #getTextboxes() <em>Textboxes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTextboxes()
   * @generated
   * @ordered
   */
  protected EList<TextArea> textboxes;

  /**
   * The cached value of the '{@link #getContains() <em>Contains</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContains()
   * @generated
   * @ordered
   */
  protected OrthogonalState contains;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StateMachineImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return StatechartsPackage.Literals.STATE_MACHINE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Transition> getTransitions()
  {
    if (transitions == null)
    {
      transitions = new EObjectContainmentEList<Transition>(Transition.class, this, StatechartsPackage.STATE_MACHINE__TRANSITIONS);
    }
    return transitions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<State> getStates()
  {
    if (states == null)
    {
      states = new EObjectContainmentEList<State>(State.class, this, StatechartsPackage.STATE_MACHINE__STATES);
    }
    return states;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<TextArea> getTextboxes()
  {
    if (textboxes == null)
    {
      textboxes = new EObjectContainmentEList<TextArea>(TextArea.class, this, StatechartsPackage.STATE_MACHINE__TEXTBOXES);
    }
    return textboxes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrthogonalState getContains()
  {
    if (contains != null && contains.eIsProxy())
    {
      InternalEObject oldContains = (InternalEObject)contains;
      contains = (OrthogonalState)eResolveProxy(oldContains);
      if (contains != oldContains)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, StatechartsPackage.STATE_MACHINE__CONTAINS, oldContains, contains));
      }
    }
    return contains;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrthogonalState basicGetContains()
  {
    return contains;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContains(OrthogonalState newContains)
  {
    OrthogonalState oldContains = contains;
    contains = newContains;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StatechartsPackage.STATE_MACHINE__CONTAINS, oldContains, contains));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case StatechartsPackage.STATE_MACHINE__TRANSITIONS:
        return ((InternalEList<?>)getTransitions()).basicRemove(otherEnd, msgs);
      case StatechartsPackage.STATE_MACHINE__STATES:
        return ((InternalEList<?>)getStates()).basicRemove(otherEnd, msgs);
      case StatechartsPackage.STATE_MACHINE__TEXTBOXES:
        return ((InternalEList<?>)getTextboxes()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case StatechartsPackage.STATE_MACHINE__TRANSITIONS:
        return getTransitions();
      case StatechartsPackage.STATE_MACHINE__STATES:
        return getStates();
      case StatechartsPackage.STATE_MACHINE__TEXTBOXES:
        return getTextboxes();
      case StatechartsPackage.STATE_MACHINE__CONTAINS:
        if (resolve) return getContains();
        return basicGetContains();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case StatechartsPackage.STATE_MACHINE__TRANSITIONS:
        getTransitions().clear();
        getTransitions().addAll((Collection<? extends Transition>)newValue);
        return;
      case StatechartsPackage.STATE_MACHINE__STATES:
        getStates().clear();
        getStates().addAll((Collection<? extends State>)newValue);
        return;
      case StatechartsPackage.STATE_MACHINE__TEXTBOXES:
        getTextboxes().clear();
        getTextboxes().addAll((Collection<? extends TextArea>)newValue);
        return;
      case StatechartsPackage.STATE_MACHINE__CONTAINS:
        setContains((OrthogonalState)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case StatechartsPackage.STATE_MACHINE__TRANSITIONS:
        getTransitions().clear();
        return;
      case StatechartsPackage.STATE_MACHINE__STATES:
        getStates().clear();
        return;
      case StatechartsPackage.STATE_MACHINE__TEXTBOXES:
        getTextboxes().clear();
        return;
      case StatechartsPackage.STATE_MACHINE__CONTAINS:
        setContains((OrthogonalState)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case StatechartsPackage.STATE_MACHINE__TRANSITIONS:
        return transitions != null && !transitions.isEmpty();
      case StatechartsPackage.STATE_MACHINE__STATES:
        return states != null && !states.isEmpty();
      case StatechartsPackage.STATE_MACHINE__TEXTBOXES:
        return textboxes != null && !textboxes.isEmpty();
      case StatechartsPackage.STATE_MACHINE__CONTAINS:
        return contains != null;
    }
    return super.eIsSet(featureID);
  }

} //StateMachineImpl
