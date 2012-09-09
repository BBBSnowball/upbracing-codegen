/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Orthogonal State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.OrthogonalState#getName <em>Name</em>}</li>
 *   <li>{@link Statecharts.OrthogonalState#getRegions <em>Regions</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getOrthogonalState()
 * @model annotation="gmf.node label='name' label.icon='false' figure='polygon' polygon.x='0 40 40 0 0' polygon.y='0 0 20 20 0' color='255,240,245' border.color='0,0,0'"
 * @generated
 */
public interface OrthogonalState extends State
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
   * @see Statecharts.StatechartsPackage#getOrthogonalState_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link Statecharts.OrthogonalState#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Regions</b></em>' containment reference list.
   * The list contents are of type {@link Statecharts.Region}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Regions</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Regions</em>' containment reference list.
   * @see Statecharts.StatechartsPackage#getOrthogonalState_Regions()
   * @model containment="true" upper="2"
   *        annotation="gmf.compartment collapsible='true'"
   * @generated
   */
  EList<Region> getRegions();

} // OrthogonalState
