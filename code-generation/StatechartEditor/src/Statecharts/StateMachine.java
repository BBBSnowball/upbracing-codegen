/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>State Machine</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.StateMachine#getTransitions <em>Transitions</em>}</li>
 *   <li>{@link Statecharts.StateMachine#getStates <em>States</em>}</li>
 *   <li>{@link Statecharts.StateMachine#getTextboxes <em>Textboxes</em>}</li>
 *   <li>{@link Statecharts.StateMachine#getContains <em>Contains</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getStateMachine()
 * @model annotation="gmf.diagram foo='bar'"
 * @generated
 */
public interface StateMachine extends EObject
{
  /**
   * Returns the value of the '<em><b>Transitions</b></em>' containment reference list.
   * The list contents are of type {@link Statecharts.Transition}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transitions</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transitions</em>' containment reference list.
   * @see Statecharts.StatechartsPackage#getStateMachine_Transitions()
   * @model containment="true"
   * @generated
   */
  EList<Transition> getTransitions();

  /**
   * Returns the value of the '<em><b>States</b></em>' containment reference list.
   * The list contents are of type {@link Statecharts.State}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>States</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>States</em>' containment reference list.
   * @see Statecharts.StatechartsPackage#getStateMachine_States()
   * @model containment="true"
   * @generated
   */
  EList<State> getStates();

  /**
   * Returns the value of the '<em><b>Textboxes</b></em>' containment reference list.
   * The list contents are of type {@link Statecharts.TextArea}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Textboxes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Textboxes</em>' containment reference list.
   * @see Statecharts.StatechartsPackage#getStateMachine_Textboxes()
   * @model containment="true"
   * @generated
   */
  EList<TextArea> getTextboxes();

  /**
   * Returns the value of the '<em><b>Contains</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Contains</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Contains</em>' reference.
   * @see #setContains(OrthogonalState)
   * @see Statecharts.StatechartsPackage#getStateMachine_Contains()
   * @model required="true"
   * @generated
   */
  OrthogonalState getContains();

  /**
   * Sets the value of the '{@link Statecharts.StateMachine#getContains <em>Contains</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Contains</em>' reference.
   * @see #getContains()
   * @generated
   */
  void setContains(OrthogonalState value);

} // StateMachine
