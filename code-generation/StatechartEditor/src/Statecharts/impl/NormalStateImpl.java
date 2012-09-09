/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts.impl;

import Statecharts.NormalState;
import Statecharts.StatechartsPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Normal State</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link Statecharts.impl.NormalStateImpl#getName <em>Name</em>}</li>
 *   <li>{@link Statecharts.impl.NormalStateImpl#getEntryAction <em>Entry Action</em>}</li>
 *   <li>{@link Statecharts.impl.NormalStateImpl#getExitAction <em>Exit Action</em>}</li>
 *   <li>{@link Statecharts.impl.NormalStateImpl#getDuringAction <em>During Action</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NormalStateImpl extends StateImpl implements NormalState
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
   * The cached value of the '{@link #getEntryAction() <em>Entry Action</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEntryAction()
   * @generated
   * @ordered
   */
  protected EList<String> entryAction;

  /**
   * The cached value of the '{@link #getExitAction() <em>Exit Action</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExitAction()
   * @generated
   * @ordered
   */
  protected EList<String> exitAction;

  /**
   * The cached value of the '{@link #getDuringAction() <em>During Action</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDuringAction()
   * @generated
   * @ordered
   */
  protected EList<String> duringAction;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected NormalStateImpl()
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
    return StatechartsPackage.Literals.NORMAL_STATE;
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
      eNotify(new ENotificationImpl(this, Notification.SET, StatechartsPackage.NORMAL_STATE__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getEntryAction()
  {
    if (entryAction == null)
    {
      entryAction = new EDataTypeUniqueEList<String>(String.class, this, StatechartsPackage.NORMAL_STATE__ENTRY_ACTION);
    }
    return entryAction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getExitAction()
  {
    if (exitAction == null)
    {
      exitAction = new EDataTypeUniqueEList<String>(String.class, this, StatechartsPackage.NORMAL_STATE__EXIT_ACTION);
    }
    return exitAction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getDuringAction()
  {
    if (duringAction == null)
    {
      duringAction = new EDataTypeUniqueEList<String>(String.class, this, StatechartsPackage.NORMAL_STATE__DURING_ACTION);
    }
    return duringAction;
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
      case StatechartsPackage.NORMAL_STATE__NAME:
        return getName();
      case StatechartsPackage.NORMAL_STATE__ENTRY_ACTION:
        return getEntryAction();
      case StatechartsPackage.NORMAL_STATE__EXIT_ACTION:
        return getExitAction();
      case StatechartsPackage.NORMAL_STATE__DURING_ACTION:
        return getDuringAction();
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
      case StatechartsPackage.NORMAL_STATE__NAME:
        setName((String)newValue);
        return;
      case StatechartsPackage.NORMAL_STATE__ENTRY_ACTION:
        getEntryAction().clear();
        getEntryAction().addAll((Collection<? extends String>)newValue);
        return;
      case StatechartsPackage.NORMAL_STATE__EXIT_ACTION:
        getExitAction().clear();
        getExitAction().addAll((Collection<? extends String>)newValue);
        return;
      case StatechartsPackage.NORMAL_STATE__DURING_ACTION:
        getDuringAction().clear();
        getDuringAction().addAll((Collection<? extends String>)newValue);
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
      case StatechartsPackage.NORMAL_STATE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StatechartsPackage.NORMAL_STATE__ENTRY_ACTION:
        getEntryAction().clear();
        return;
      case StatechartsPackage.NORMAL_STATE__EXIT_ACTION:
        getExitAction().clear();
        return;
      case StatechartsPackage.NORMAL_STATE__DURING_ACTION:
        getDuringAction().clear();
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
      case StatechartsPackage.NORMAL_STATE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StatechartsPackage.NORMAL_STATE__ENTRY_ACTION:
        return entryAction != null && !entryAction.isEmpty();
      case StatechartsPackage.NORMAL_STATE__EXIT_ACTION:
        return exitAction != null && !exitAction.isEmpty();
      case StatechartsPackage.NORMAL_STATE__DURING_ACTION:
        return duringAction != null && !duringAction.isEmpty();
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
    result.append(", entryAction: ");
    result.append(entryAction);
    result.append(", exitAction: ");
    result.append(exitAction);
    result.append(", duringAction: ");
    result.append(duringAction);
    result.append(')');
    return result.toString();
  }

} //NormalStateImpl
