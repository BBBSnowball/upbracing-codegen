/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see Statecharts.StatechartsFactory
 * @model kind="package"
 * @generated
 */
public interface StatechartsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "Statecharts";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://statechart";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "Statecharts";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  StatechartsPackage eINSTANCE = Statecharts.impl.StatechartsPackageImpl.init();

  /**
   * The meta object id for the '{@link Statecharts.impl.StateMachineImpl <em>State Machine</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.StateMachineImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getStateMachine()
   * @generated
   */
  int STATE_MACHINE = 0;

  /**
   * The feature id for the '<em><b>Transitions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_MACHINE__TRANSITIONS = 0;

  /**
   * The feature id for the '<em><b>States</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_MACHINE__STATES = 1;

  /**
   * The feature id for the '<em><b>Textboxes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_MACHINE__TEXTBOXES = 2;

  /**
   * The feature id for the '<em><b>Contains</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_MACHINE__CONTAINS = 3;

  /**
   * The number of structural features of the '<em>State Machine</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_MACHINE_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link Statecharts.impl.StateImpl <em>State</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.StateImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getState()
   * @generated
   */
  int STATE = 1;

  /**
   * The number of structural features of the '<em>State</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link Statecharts.impl.InitialStateImpl <em>Initial State</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.InitialStateImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getInitialState()
   * @generated
   */
  int INITIAL_STATE = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INITIAL_STATE__NAME = STATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Initial State</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INITIAL_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link Statecharts.impl.FinalStateImpl <em>Final State</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.FinalStateImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getFinalState()
   * @generated
   */
  int FINAL_STATE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FINAL_STATE__NAME = STATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Final State</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FINAL_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link Statecharts.impl.NormalStateImpl <em>Normal State</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.NormalStateImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getNormalState()
   * @generated
   */
  int NORMAL_STATE = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NORMAL_STATE__NAME = STATE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Entry Action</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NORMAL_STATE__ENTRY_ACTION = STATE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Exit Action</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NORMAL_STATE__EXIT_ACTION = STATE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>During Action</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NORMAL_STATE__DURING_ACTION = STATE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Normal State</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NORMAL_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link Statecharts.impl.OrthogonalStateImpl <em>Orthogonal State</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.OrthogonalStateImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getOrthogonalState()
   * @generated
   */
  int ORTHOGONAL_STATE = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORTHOGONAL_STATE__NAME = STATE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Regions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORTHOGONAL_STATE__REGIONS = STATE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Orthogonal State</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORTHOGONAL_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link Statecharts.impl.RegionImpl <em>Region</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.RegionImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getRegion()
   * @generated
   */
  int REGION = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REGION__NAME = 0;

  /**
   * The feature id for the '<em><b>Containedstates</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REGION__CONTAINEDSTATES = 1;

  /**
   * The number of structural features of the '<em>Region</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REGION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link Statecharts.impl.TransitionImpl <em>Transition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.TransitionImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getTransition()
   * @generated
   */
  int TRANSITION = 7;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRANSITION__SOURCE = 0;

  /**
   * The feature id for the '<em><b>Destination</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRANSITION__DESTINATION = 1;

  /**
   * The feature id for the '<em><b>Trigger</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRANSITION__TRIGGER = 2;

  /**
   * The number of structural features of the '<em>Transition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRANSITION_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link Statecharts.impl.TextAreaImpl <em>Text Area</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see Statecharts.impl.TextAreaImpl
   * @see Statecharts.impl.StatechartsPackageImpl#getTextArea()
   * @generated
   */
  int TEXT_AREA = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_AREA__NAME = 0;

  /**
   * The feature id for the '<em><b>Include</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_AREA__INCLUDE = 1;

  /**
   * The feature id for the '<em><b>Function</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_AREA__FUNCTION = 2;

  /**
   * The feature id for the '<em><b>Variable</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_AREA__VARIABLE = 3;

  /**
   * The number of structural features of the '<em>Text Area</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_AREA_FEATURE_COUNT = 4;


  /**
   * Returns the meta object for class '{@link Statecharts.StateMachine <em>State Machine</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>State Machine</em>'.
   * @see Statecharts.StateMachine
   * @generated
   */
  EClass getStateMachine();

  /**
   * Returns the meta object for the containment reference list '{@link Statecharts.StateMachine#getTransitions <em>Transitions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Transitions</em>'.
   * @see Statecharts.StateMachine#getTransitions()
   * @see #getStateMachine()
   * @generated
   */
  EReference getStateMachine_Transitions();

  /**
   * Returns the meta object for the containment reference list '{@link Statecharts.StateMachine#getStates <em>States</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>States</em>'.
   * @see Statecharts.StateMachine#getStates()
   * @see #getStateMachine()
   * @generated
   */
  EReference getStateMachine_States();

  /**
   * Returns the meta object for the containment reference list '{@link Statecharts.StateMachine#getTextboxes <em>Textboxes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Textboxes</em>'.
   * @see Statecharts.StateMachine#getTextboxes()
   * @see #getStateMachine()
   * @generated
   */
  EReference getStateMachine_Textboxes();

  /**
   * Returns the meta object for the reference '{@link Statecharts.StateMachine#getContains <em>Contains</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Contains</em>'.
   * @see Statecharts.StateMachine#getContains()
   * @see #getStateMachine()
   * @generated
   */
  EReference getStateMachine_Contains();

  /**
   * Returns the meta object for class '{@link Statecharts.State <em>State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>State</em>'.
   * @see Statecharts.State
   * @generated
   */
  EClass getState();

  /**
   * Returns the meta object for class '{@link Statecharts.InitialState <em>Initial State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Initial State</em>'.
   * @see Statecharts.InitialState
   * @generated
   */
  EClass getInitialState();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.InitialState#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see Statecharts.InitialState#getName()
   * @see #getInitialState()
   * @generated
   */
  EAttribute getInitialState_Name();

  /**
   * Returns the meta object for class '{@link Statecharts.FinalState <em>Final State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Final State</em>'.
   * @see Statecharts.FinalState
   * @generated
   */
  EClass getFinalState();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.FinalState#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see Statecharts.FinalState#getName()
   * @see #getFinalState()
   * @generated
   */
  EAttribute getFinalState_Name();

  /**
   * Returns the meta object for class '{@link Statecharts.NormalState <em>Normal State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Normal State</em>'.
   * @see Statecharts.NormalState
   * @generated
   */
  EClass getNormalState();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.NormalState#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see Statecharts.NormalState#getName()
   * @see #getNormalState()
   * @generated
   */
  EAttribute getNormalState_Name();

  /**
   * Returns the meta object for the attribute list '{@link Statecharts.NormalState#getEntryAction <em>Entry Action</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Entry Action</em>'.
   * @see Statecharts.NormalState#getEntryAction()
   * @see #getNormalState()
   * @generated
   */
  EAttribute getNormalState_EntryAction();

  /**
   * Returns the meta object for the attribute list '{@link Statecharts.NormalState#getExitAction <em>Exit Action</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Exit Action</em>'.
   * @see Statecharts.NormalState#getExitAction()
   * @see #getNormalState()
   * @generated
   */
  EAttribute getNormalState_ExitAction();

  /**
   * Returns the meta object for the attribute list '{@link Statecharts.NormalState#getDuringAction <em>During Action</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>During Action</em>'.
   * @see Statecharts.NormalState#getDuringAction()
   * @see #getNormalState()
   * @generated
   */
  EAttribute getNormalState_DuringAction();

  /**
   * Returns the meta object for class '{@link Statecharts.OrthogonalState <em>Orthogonal State</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Orthogonal State</em>'.
   * @see Statecharts.OrthogonalState
   * @generated
   */
  EClass getOrthogonalState();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.OrthogonalState#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see Statecharts.OrthogonalState#getName()
   * @see #getOrthogonalState()
   * @generated
   */
  EAttribute getOrthogonalState_Name();

  /**
   * Returns the meta object for the containment reference list '{@link Statecharts.OrthogonalState#getRegions <em>Regions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Regions</em>'.
   * @see Statecharts.OrthogonalState#getRegions()
   * @see #getOrthogonalState()
   * @generated
   */
  EReference getOrthogonalState_Regions();

  /**
   * Returns the meta object for class '{@link Statecharts.Region <em>Region</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Region</em>'.
   * @see Statecharts.Region
   * @generated
   */
  EClass getRegion();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.Region#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see Statecharts.Region#getName()
   * @see #getRegion()
   * @generated
   */
  EAttribute getRegion_Name();

  /**
   * Returns the meta object for the containment reference list '{@link Statecharts.Region#getContainedstates <em>Containedstates</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Containedstates</em>'.
   * @see Statecharts.Region#getContainedstates()
   * @see #getRegion()
   * @generated
   */
  EReference getRegion_Containedstates();

  /**
   * Returns the meta object for class '{@link Statecharts.Transition <em>Transition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Transition</em>'.
   * @see Statecharts.Transition
   * @generated
   */
  EClass getTransition();

  /**
   * Returns the meta object for the reference '{@link Statecharts.Transition#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source</em>'.
   * @see Statecharts.Transition#getSource()
   * @see #getTransition()
   * @generated
   */
  EReference getTransition_Source();

  /**
   * Returns the meta object for the reference '{@link Statecharts.Transition#getDestination <em>Destination</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Destination</em>'.
   * @see Statecharts.Transition#getDestination()
   * @see #getTransition()
   * @generated
   */
  EReference getTransition_Destination();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.Transition#getTrigger <em>Trigger</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Trigger</em>'.
   * @see Statecharts.Transition#getTrigger()
   * @see #getTransition()
   * @generated
   */
  EAttribute getTransition_Trigger();

  /**
   * Returns the meta object for class '{@link Statecharts.TextArea <em>Text Area</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Text Area</em>'.
   * @see Statecharts.TextArea
   * @generated
   */
  EClass getTextArea();

  /**
   * Returns the meta object for the attribute '{@link Statecharts.TextArea#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see Statecharts.TextArea#getName()
   * @see #getTextArea()
   * @generated
   */
  EAttribute getTextArea_Name();

  /**
   * Returns the meta object for the attribute list '{@link Statecharts.TextArea#getInclude <em>Include</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Include</em>'.
   * @see Statecharts.TextArea#getInclude()
   * @see #getTextArea()
   * @generated
   */
  EAttribute getTextArea_Include();

  /**
   * Returns the meta object for the attribute list '{@link Statecharts.TextArea#getFunction <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Function</em>'.
   * @see Statecharts.TextArea#getFunction()
   * @see #getTextArea()
   * @generated
   */
  EAttribute getTextArea_Function();

  /**
   * Returns the meta object for the attribute list '{@link Statecharts.TextArea#getVariable <em>Variable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Variable</em>'.
   * @see Statecharts.TextArea#getVariable()
   * @see #getTextArea()
   * @generated
   */
  EAttribute getTextArea_Variable();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  StatechartsFactory getStatechartsFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link Statecharts.impl.StateMachineImpl <em>State Machine</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.StateMachineImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getStateMachine()
     * @generated
     */
    EClass STATE_MACHINE = eINSTANCE.getStateMachine();

    /**
     * The meta object literal for the '<em><b>Transitions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_MACHINE__TRANSITIONS = eINSTANCE.getStateMachine_Transitions();

    /**
     * The meta object literal for the '<em><b>States</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_MACHINE__STATES = eINSTANCE.getStateMachine_States();

    /**
     * The meta object literal for the '<em><b>Textboxes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_MACHINE__TEXTBOXES = eINSTANCE.getStateMachine_Textboxes();

    /**
     * The meta object literal for the '<em><b>Contains</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATE_MACHINE__CONTAINS = eINSTANCE.getStateMachine_Contains();

    /**
     * The meta object literal for the '{@link Statecharts.impl.StateImpl <em>State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.StateImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getState()
     * @generated
     */
    EClass STATE = eINSTANCE.getState();

    /**
     * The meta object literal for the '{@link Statecharts.impl.InitialStateImpl <em>Initial State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.InitialStateImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getInitialState()
     * @generated
     */
    EClass INITIAL_STATE = eINSTANCE.getInitialState();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INITIAL_STATE__NAME = eINSTANCE.getInitialState_Name();

    /**
     * The meta object literal for the '{@link Statecharts.impl.FinalStateImpl <em>Final State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.FinalStateImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getFinalState()
     * @generated
     */
    EClass FINAL_STATE = eINSTANCE.getFinalState();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FINAL_STATE__NAME = eINSTANCE.getFinalState_Name();

    /**
     * The meta object literal for the '{@link Statecharts.impl.NormalStateImpl <em>Normal State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.NormalStateImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getNormalState()
     * @generated
     */
    EClass NORMAL_STATE = eINSTANCE.getNormalState();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NORMAL_STATE__NAME = eINSTANCE.getNormalState_Name();

    /**
     * The meta object literal for the '<em><b>Entry Action</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NORMAL_STATE__ENTRY_ACTION = eINSTANCE.getNormalState_EntryAction();

    /**
     * The meta object literal for the '<em><b>Exit Action</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NORMAL_STATE__EXIT_ACTION = eINSTANCE.getNormalState_ExitAction();

    /**
     * The meta object literal for the '<em><b>During Action</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NORMAL_STATE__DURING_ACTION = eINSTANCE.getNormalState_DuringAction();

    /**
     * The meta object literal for the '{@link Statecharts.impl.OrthogonalStateImpl <em>Orthogonal State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.OrthogonalStateImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getOrthogonalState()
     * @generated
     */
    EClass ORTHOGONAL_STATE = eINSTANCE.getOrthogonalState();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ORTHOGONAL_STATE__NAME = eINSTANCE.getOrthogonalState_Name();

    /**
     * The meta object literal for the '<em><b>Regions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ORTHOGONAL_STATE__REGIONS = eINSTANCE.getOrthogonalState_Regions();

    /**
     * The meta object literal for the '{@link Statecharts.impl.RegionImpl <em>Region</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.RegionImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getRegion()
     * @generated
     */
    EClass REGION = eINSTANCE.getRegion();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REGION__NAME = eINSTANCE.getRegion_Name();

    /**
     * The meta object literal for the '<em><b>Containedstates</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REGION__CONTAINEDSTATES = eINSTANCE.getRegion_Containedstates();

    /**
     * The meta object literal for the '{@link Statecharts.impl.TransitionImpl <em>Transition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.TransitionImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getTransition()
     * @generated
     */
    EClass TRANSITION = eINSTANCE.getTransition();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TRANSITION__SOURCE = eINSTANCE.getTransition_Source();

    /**
     * The meta object literal for the '<em><b>Destination</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TRANSITION__DESTINATION = eINSTANCE.getTransition_Destination();

    /**
     * The meta object literal for the '<em><b>Trigger</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TRANSITION__TRIGGER = eINSTANCE.getTransition_Trigger();

    /**
     * The meta object literal for the '{@link Statecharts.impl.TextAreaImpl <em>Text Area</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see Statecharts.impl.TextAreaImpl
     * @see Statecharts.impl.StatechartsPackageImpl#getTextArea()
     * @generated
     */
    EClass TEXT_AREA = eINSTANCE.getTextArea();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_AREA__NAME = eINSTANCE.getTextArea_Name();

    /**
     * The meta object literal for the '<em><b>Include</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_AREA__INCLUDE = eINSTANCE.getTextArea_Include();

    /**
     * The meta object literal for the '<em><b>Function</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_AREA__FUNCTION = eINSTANCE.getTextArea_Function();

    /**
     * The meta object literal for the '<em><b>Variable</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_AREA__VARIABLE = eINSTANCE.getTextArea_Variable();

  }

} //StatechartsPackage
