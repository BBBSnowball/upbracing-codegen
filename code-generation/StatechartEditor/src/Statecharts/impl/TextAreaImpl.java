/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts.impl;

import Statecharts.StatechartsPackage;
import Statecharts.TextArea;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Text Area</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link Statecharts.impl.TextAreaImpl#getName <em>Name</em>}</li>
 *   <li>{@link Statecharts.impl.TextAreaImpl#getInclude <em>Include</em>}</li>
 *   <li>{@link Statecharts.impl.TextAreaImpl#getFunction <em>Function</em>}</li>
 *   <li>{@link Statecharts.impl.TextAreaImpl#getVariable <em>Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TextAreaImpl extends EObjectImpl implements TextArea
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getInclude() <em>Include</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInclude()
   * @generated
   * @ordered
   */
  protected EList<String> include;

  /**
   * The cached value of the '{@link #getFunction() <em>Function</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFunction()
   * @generated
   * @ordered
   */
  protected EList<String> function;

  /**
   * The cached value of the '{@link #getVariable() <em>Variable</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVariable()
   * @generated
   * @ordered
   */
  protected EList<String> variable;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TextAreaImpl()
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
    return StatechartsPackage.Literals.TEXT_AREA;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StatechartsPackage.TEXT_AREA__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getInclude()
  {
    if (include == null)
    {
      include = new EDataTypeUniqueEList<String>(String.class, this, StatechartsPackage.TEXT_AREA__INCLUDE);
    }
    return include;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getFunction()
  {
    if (function == null)
    {
      function = new EDataTypeUniqueEList<String>(String.class, this, StatechartsPackage.TEXT_AREA__FUNCTION);
    }
    return function;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getVariable()
  {
    if (variable == null)
    {
      variable = new EDataTypeUniqueEList<String>(String.class, this, StatechartsPackage.TEXT_AREA__VARIABLE);
    }
    return variable;
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
      case StatechartsPackage.TEXT_AREA__NAME:
        return getName();
      case StatechartsPackage.TEXT_AREA__INCLUDE:
        return getInclude();
      case StatechartsPackage.TEXT_AREA__FUNCTION:
        return getFunction();
      case StatechartsPackage.TEXT_AREA__VARIABLE:
        return getVariable();
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
      case StatechartsPackage.TEXT_AREA__NAME:
        setName((String)newValue);
        return;
      case StatechartsPackage.TEXT_AREA__INCLUDE:
        getInclude().clear();
        getInclude().addAll((Collection<? extends String>)newValue);
        return;
      case StatechartsPackage.TEXT_AREA__FUNCTION:
        getFunction().clear();
        getFunction().addAll((Collection<? extends String>)newValue);
        return;
      case StatechartsPackage.TEXT_AREA__VARIABLE:
        getVariable().clear();
        getVariable().addAll((Collection<? extends String>)newValue);
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
      case StatechartsPackage.TEXT_AREA__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StatechartsPackage.TEXT_AREA__INCLUDE:
        getInclude().clear();
        return;
      case StatechartsPackage.TEXT_AREA__FUNCTION:
        getFunction().clear();
        return;
      case StatechartsPackage.TEXT_AREA__VARIABLE:
        getVariable().clear();
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
      case StatechartsPackage.TEXT_AREA__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StatechartsPackage.TEXT_AREA__INCLUDE:
        return include != null && !include.isEmpty();
      case StatechartsPackage.TEXT_AREA__FUNCTION:
        return function != null && !function.isEmpty();
      case StatechartsPackage.TEXT_AREA__VARIABLE:
        return variable != null && !variable.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", include: ");
    result.append(include);
    result.append(", function: ");
    result.append(function);
    result.append(", variable: ");
    result.append(variable);
    result.append(')');
    return result.toString();
  }

} //TextAreaImpl
