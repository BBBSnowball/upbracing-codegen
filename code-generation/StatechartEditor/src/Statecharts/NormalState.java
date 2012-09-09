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
 * A representation of the model object '<em><b>Normal State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.NormalState#getName <em>Name</em>}</li>
 *   <li>{@link Statecharts.NormalState#getEntryAction <em>Entry Action</em>}</li>
 *   <li>{@link Statecharts.NormalState#getExitAction <em>Exit Action</em>}</li>
 *   <li>{@link Statecharts.NormalState#getDuringAction <em>During Action</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getNormalState()
 * @model annotation="gmf.node label='name' label.icon='false' figure='rounded' color='230,230,250' border.color='0,0,0'"
 * @generated
 */
public interface NormalState extends State
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
   * @see Statecharts.StatechartsPackage#getNormalState_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link Statecharts.NormalState#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Entry Action</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Entry Action</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Entry Action</em>' attribute list.
   * @see Statecharts.StatechartsPackage#getNormalState_EntryAction()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getEntryAction();

  /**
   * Returns the value of the '<em><b>Exit Action</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Exit Action</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exit Action</em>' attribute list.
   * @see Statecharts.StatechartsPackage#getNormalState_ExitAction()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getExitAction();

  /**
   * Returns the value of the '<em><b>During Action</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>During Action</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>During Action</em>' attribute list.
   * @see Statecharts.StatechartsPackage#getNormalState_DuringAction()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getDuringAction();

} // NormalState
