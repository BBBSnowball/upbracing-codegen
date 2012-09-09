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
 * A representation of the model object '<em><b>Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.Function#getFunction <em>Function</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getFunction()
 * @model annotation="gmf.node label='name' label.icon='false' figure='rectangle' color='245,245,245' border.color='0,0,0'"
 * @generated
 */
public interface Function extends TextArea
{
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
   * @see Statecharts.StatechartsPackage#getFunction_Function()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getFunction();

} // Function
