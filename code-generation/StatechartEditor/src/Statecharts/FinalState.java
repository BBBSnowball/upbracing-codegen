/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Final State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link Statecharts.FinalState#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see Statecharts.StatechartsPackage#getFinalState()
 * @model annotation="gmf.node label=' name ' label.icon='false' figure='ellipse' color='255,255,255' label.placement='outer' border.color='0,0,0' size='1,1'"
 * @generated
 */
public interface FinalState extends State
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * The default value is <code>"end"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see Statecharts.StatechartsPackage#getFinalState_Name()
   * @model default="end"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link Statecharts.FinalState#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // FinalState
