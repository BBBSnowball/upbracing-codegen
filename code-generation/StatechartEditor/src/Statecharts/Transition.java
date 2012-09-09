/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.Transition#getSource <em>Source</em>}</li>
 *   <li>{@link Statecharts.Transition#getDestination <em>Destination</em>}</li>
 *   <li>{@link Statecharts.Transition#getTrigger <em>Trigger</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getTransition()
 * @model annotation="gmf.link label='name' source='source' target='destination' target.decoration='arrow' color='0,0,0'"
 * @generated
 */
public interface Transition extends EObject
{
  /**
   * Returns the value of the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' reference.
   * @see #setSource(State)
   * @see Statecharts.StatechartsPackage#getTransition_Source()
   * @model required="true"
   * @generated
   */
  State getSource();

  /**
   * Sets the value of the '{@link Statecharts.Transition#getSource <em>Source</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' reference.
   * @see #getSource()
   * @generated
   */
  void setSource(State value);

  /**
   * Returns the value of the '<em><b>Destination</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Destination</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Destination</em>' reference.
   * @see #setDestination(State)
   * @see Statecharts.StatechartsPackage#getTransition_Destination()
   * @model required="true"
   * @generated
   */
  State getDestination();

  /**
   * Sets the value of the '{@link Statecharts.Transition#getDestination <em>Destination</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Destination</em>' reference.
   * @see #getDestination()
   * @generated
   */
  void setDestination(State value);

  /**
   * Returns the value of the '<em><b>Trigger</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Trigger</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Trigger</em>' attribute.
   * @see #setTrigger(String)
   * @see Statecharts.StatechartsPackage#getTransition_Trigger()
   * @model required="true"
   * @generated
   */
  String getTrigger();

  /**
   * Sets the value of the '{@link Statecharts.Transition#getTrigger <em>Trigger</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Trigger</em>' attribute.
   * @see #getTrigger()
   * @generated
   */
  void setTrigger(String value);

} // Transition
