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
 * A representation of the model object '<em><b>Region</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.Region#getName <em>Name</em>}</li>
 *   <li>{@link Statecharts.Region#getContainedstates <em>Containedstates</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getRegion()
 * @model annotation="gmf.node label='name' label.icon='false' figure='polygon' polygon.x='0 40 40 0 0' polygon.y='0 0 10 10 0' border.color='0,0,0'"
 * @generated
 */
public interface Region extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see Statecharts.StatechartsPackage#getRegion_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link Statecharts.Region#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Containedstates</b></em>' containment reference list.
   * The list contents are of type {@link Statecharts.State}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containedstates</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Containedstates</em>' containment reference list.
   * @see Statecharts.StatechartsPackage#getRegion_Containedstates()
   * @model containment="true"
   *        annotation="gmf.compartment collapsible='true'"
   * @generated
   */
  EList<State> getContainedstates();

} // Region
