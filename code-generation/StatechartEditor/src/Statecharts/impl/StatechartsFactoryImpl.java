/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts.impl;

import Statecharts.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StatechartsFactoryImpl extends EFactoryImpl implements StatechartsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static StatechartsFactory init()
  {
    try
    {
      StatechartsFactory theStatechartsFactory = (StatechartsFactory)EPackage.Registry.INSTANCE.getEFactory("http://statechart"); 
      if (theStatechartsFactory != null)
      {
        return theStatechartsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new StatechartsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StatechartsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case StatechartsPackage.STATE_MACHINE: return createStateMachine();
      case StatechartsPackage.INITIAL_STATE: return createInitialState();
      case StatechartsPackage.FINAL_STATE: return createFinalState();
      case StatechartsPackage.NORMAL_STATE: return createNormalState();
      case StatechartsPackage.ORTHOGONAL_STATE: return createOrthogonalState();
      case StatechartsPackage.REGION: return createRegion();
      case StatechartsPackage.TRANSITION: return createTransition();
      case StatechartsPackage.TEXT_AREA: return createTextArea();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateMachine createStateMachine()
  {
    StateMachineImpl stateMachine = new StateMachineImpl();
    return stateMachine;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InitialState createInitialState()
  {
    InitialStateImpl initialState = new InitialStateImpl();
    return initialState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FinalState createFinalState()
  {
    FinalStateImpl finalState = new FinalStateImpl();
    return finalState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NormalState createNormalState()
  {
    NormalStateImpl normalState = new NormalStateImpl();
    return normalState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrthogonalState createOrthogonalState()
  {
    OrthogonalStateImpl orthogonalState = new OrthogonalStateImpl();
    return orthogonalState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Region createRegion()
  {
    RegionImpl region = new RegionImpl();
    return region;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Transition createTransition()
  {
    TransitionImpl transition = new TransitionImpl();
    return transition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TextArea createTextArea()
  {
    TextAreaImpl textArea = new TextAreaImpl();
    return textArea;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StatechartsPackage getStatechartsPackage()
  {
    return (StatechartsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static StatechartsPackage getPackage()
  {
    return StatechartsPackage.eINSTANCE;
  }

} //StatechartsFactoryImpl
