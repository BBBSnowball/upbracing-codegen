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
 * A representation of the model object '<em><b>Include</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.Include#getInclude <em>Include</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getInclude()
 * @model annotation="gmf.node label='name' label.icon='false' figure='rectangle' label.placement='internal' color='245,245,245' border.color='0,0,0'"
 * @generated
 */
public interface Include extends TextArea
{
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
   * @see Statecharts.StatechartsPackage#getInclude_Include()
   * @model annotation="gmf.label foo='bar'"
   * @generated
   */
  EList<String> getInclude();

} // Include
