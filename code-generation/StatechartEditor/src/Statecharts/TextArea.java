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
 * A representation of the model object '<em><b>Text Area</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.TextArea#getName <em>Name</em>}</li>
 *   <li>{@link Statecharts.TextArea#getInclude <em>Include</em>}</li>
 *   <li>{@link Statecharts.TextArea#getFunction <em>Function</em>}</li>
 *   <li>{@link Statecharts.TextArea#getVariable <em>Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getTextArea()
 * @model annotation="gmf.node label='name' label.icon='false' figure='rectangle' label.placement='internal' color='245,245,245' border.color='0,0,0'"
 * @generated
 */
public interface TextArea extends EObject
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
   * @see Statecharts.StatechartsPackage#getTextArea_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link Statecharts.TextArea#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Include</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Include</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Include</em>' attribute list.
   * @see Statecharts.StatechartsPackage#getTextArea_Include()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getInclude();

  /**
   * Returns the value of the '<em><b>Function</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Function</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Function</em>' attribute list.
   * @see Statecharts.StatechartsPackage#getTextArea_Function()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getFunction();

  /**
   * Returns the value of the '<em><b>Variable</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Variable</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Variable</em>' attribute list.
   * @see Statecharts.StatechartsPackage#getTextArea_Variable()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getVariable();

} // TextArea
