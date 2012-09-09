/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package Statecharts.impl;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.OrthogonalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StatechartsFactory;
import Statecharts.StatechartsPackage;
import Statecharts.TextArea;
import Statecharts.Transition;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StatechartsPackageImpl extends EPackageImpl implements StatechartsPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stateMachineEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass initialStateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass finalStateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass normalStateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orthogonalStateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass regionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass transitionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass textAreaEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see Statecharts.StatechartsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private StatechartsPackageImpl()
  {
    super(eNS_URI, StatechartsFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link StatechartsPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static StatechartsPackage init()
  {
    if (isInited) return (StatechartsPackage)EPackage.Registry.INSTANCE.getEPackage(StatechartsPackage.eNS_URI);

    // Obtain or create and register package
    StatechartsPackageImpl theStatechartsPackage = (StatechartsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof StatechartsPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new StatechartsPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theStatechartsPackage.createPackageContents();

    // Initialize created meta-data
    theStatechartsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theStatechartsPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(StatechartsPackage.eNS_URI, theStatechartsPackage);
    return theStatechartsPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStateMachine()
  {
    return stateMachineEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateMachine_Transitions()
  {
    return (EReference)stateMachineEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateMachine_States()
  {
    return (EReference)stateMachineEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateMachine_Textboxes()
  {
    return (EReference)stateMachineEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getStateMachine_Contains()
  {
    return (EReference)stateMachineEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getState()
  {
    return stateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInitialState()
  {
    return initialStateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getInitialState_Name()
  {
    return (EAttribute)initialStateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFinalState()
  {
    return finalStateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFinalState_Name()
  {
    return (EAttribute)finalStateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNormalState()
  {
    return normalStateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNormalState_Name()
  {
    return (EAttribute)normalStateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNormalState_EntryAction()
  {
    return (EAttribute)normalStateEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNormalState_ExitAction()
  {
    return (EAttribute)normalStateEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNormalState_DuringAction()
  {
    return (EAttribute)normalStateEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOrthogonalState()
  {
    return orthogonalStateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOrthogonalState_Name()
  {
    return (EAttribute)orthogonalStateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOrthogonalState_Regions()
  {
    return (EReference)orthogonalStateEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRegion()
  {
    return regionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRegion_Name()
  {
    return (EAttribute)regionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRegion_Containedstates()
  {
    return (EReference)regionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTransition()
  {
    return transitionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTransition_Source()
  {
    return (EReference)transitionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTransition_Destination()
  {
    return (EReference)transitionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTransition_Trigger()
  {
    return (EAttribute)transitionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTextArea()
  {
    return textAreaEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextArea_Name()
  {
    return (EAttribute)textAreaEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextArea_Include()
  {
    return (EAttribute)textAreaEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextArea_Function()
  {
    return (EAttribute)textAreaEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextArea_Variable()
  {
    return (EAttribute)textAreaEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StatechartsFactory getStatechartsFactory()
  {
    return (StatechartsFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    stateMachineEClass = createEClass(STATE_MACHINE);
    createEReference(stateMachineEClass, STATE_MACHINE__TRANSITIONS);
    createEReference(stateMachineEClass, STATE_MACHINE__STATES);
    createEReference(stateMachineEClass, STATE_MACHINE__TEXTBOXES);
    createEReference(stateMachineEClass, STATE_MACHINE__CONTAINS);

    stateEClass = createEClass(STATE);

    initialStateEClass = createEClass(INITIAL_STATE);
    createEAttribute(initialStateEClass, INITIAL_STATE__NAME);

    finalStateEClass = createEClass(FINAL_STATE);
    createEAttribute(finalStateEClass, FINAL_STATE__NAME);

    normalStateEClass = createEClass(NORMAL_STATE);
    createEAttribute(normalStateEClass, NORMAL_STATE__NAME);
    createEAttribute(normalStateEClass, NORMAL_STATE__ENTRY_ACTION);
    createEAttribute(normalStateEClass, NORMAL_STATE__EXIT_ACTION);
    createEAttribute(normalStateEClass, NORMAL_STATE__DURING_ACTION);

    orthogonalStateEClass = createEClass(ORTHOGONAL_STATE);
    createEAttribute(orthogonalStateEClass, ORTHOGONAL_STATE__NAME);
    createEReference(orthogonalStateEClass, ORTHOGONAL_STATE__REGIONS);

    regionEClass = createEClass(REGION);
    createEAttribute(regionEClass, REGION__NAME);
    createEReference(regionEClass, REGION__CONTAINEDSTATES);

    transitionEClass = createEClass(TRANSITION);
    createEReference(transitionEClass, TRANSITION__SOURCE);
    createEReference(transitionEClass, TRANSITION__DESTINATION);
    createEAttribute(transitionEClass, TRANSITION__TRIGGER);

    textAreaEClass = createEClass(TEXT_AREA);
    createEAttribute(textAreaEClass, TEXT_AREA__NAME);
    createEAttribute(textAreaEClass, TEXT_AREA__INCLUDE);
    createEAttribute(textAreaEClass, TEXT_AREA__FUNCTION);
    createEAttribute(textAreaEClass, TEXT_AREA__VARIABLE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    initialStateEClass.getESuperTypes().add(this.getState());
    finalStateEClass.getESuperTypes().add(this.getState());
    normalStateEClass.getESuperTypes().add(this.getState());
    orthogonalStateEClass.getESuperTypes().add(this.getState());

    // Initialize classes and features; add operations and parameters
    initEClass(stateMachineEClass, StateMachine.class, "StateMachine", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getStateMachine_Transitions(), this.getTransition(), null, "transitions", null, 0, -1, StateMachine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateMachine_States(), this.getState(), null, "states", null, 0, -1, StateMachine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateMachine_Textboxes(), this.getTextArea(), null, "textboxes", null, 0, -1, StateMachine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStateMachine_Contains(), this.getOrthogonalState(), null, "contains", null, 1, 1, StateMachine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stateEClass, State.class, "State", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(initialStateEClass, InitialState.class, "InitialState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getInitialState_Name(), ecorePackage.getEString(), "name", "start", 1, 1, InitialState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(finalStateEClass, FinalState.class, "FinalState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFinalState_Name(), ecorePackage.getEString(), "name", "end", 0, 1, FinalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(normalStateEClass, NormalState.class, "NormalState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getNormalState_Name(), ecorePackage.getEString(), "name", null, 0, 1, NormalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNormalState_EntryAction(), ecorePackage.getEString(), "entryAction", null, 0, -1, NormalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNormalState_ExitAction(), ecorePackage.getEString(), "exitAction", null, 0, -1, NormalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNormalState_DuringAction(), ecorePackage.getEString(), "duringAction", null, 0, -1, NormalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orthogonalStateEClass, OrthogonalState.class, "OrthogonalState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getOrthogonalState_Name(), ecorePackage.getEString(), "name", null, 0, 1, OrthogonalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrthogonalState_Regions(), this.getRegion(), null, "regions", null, 0, 2, OrthogonalState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(regionEClass, Region.class, "Region", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRegion_Name(), ecorePackage.getEString(), "name", null, 0, 1, Region.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRegion_Containedstates(), this.getState(), null, "containedstates", null, 0, -1, Region.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(transitionEClass, Transition.class, "Transition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTransition_Source(), this.getState(), null, "source", null, 1, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTransition_Destination(), this.getState(), null, "destination", null, 1, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTransition_Trigger(), ecorePackage.getEString(), "trigger", null, 1, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(textAreaEClass, TextArea.class, "TextArea", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTextArea_Name(), ecorePackage.getEString(), "name", null, 1, 1, TextArea.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTextArea_Include(), ecorePackage.getEString(), "include", null, 0, -1, TextArea.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTextArea_Function(), ecorePackage.getEString(), "function", null, 0, -1, TextArea.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTextArea_Variable(), ecorePackage.getEString(), "variable", null, 0, -1, TextArea.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // gmf.diagram
    createGmfAnnotations();
    // gmf.node
    createGmf_1Annotations();
    // gmf.label
    createGmf_2Annotations();
    // gmf.compartment
    createGmf_3Annotations();
    // gmf.link
    createGmf_4Annotations();
  }

  /**
   * Initializes the annotations for <b>gmf.diagram</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createGmfAnnotations()
  {
    String source = "gmf.diagram";		
    addAnnotation
      (stateMachineEClass, 
       source, 
       new String[] 
       {
       "foo", "bar"
       });															
  }

  /**
   * Initializes the annotations for <b>gmf.node</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createGmf_1Annotations()
  {
    String source = "gmf.node";			
    addAnnotation
      (initialStateEClass, 
       source, 
       new String[] 
       {
       "label", "name",
       "label.icon", "false",
       "figure", "ellipse",
       "color", "0,0,0",
       "label.placement", "outer",
       "border.color", "0,0,0",
       "size", "1,1"
       });		
    addAnnotation
      (finalStateEClass, 
       source, 
       new String[] 
       {
       "label", " name ",
       "label.icon", "false",
       "figure", "ellipse",
       "color", "255,255,255",
       "label.placement", "outer",
       "border.color", "0,0,0",
       "size", "1,1"
       });		
    addAnnotation
      (normalStateEClass, 
       source, 
       new String[] 
       {
       "label", "name",
       "label.icon", "false",
       "figure", "rounded",
       "color", "230,230,250",
       "border.color", "0,0,0"
       });					
    addAnnotation
      (orthogonalStateEClass, 
       source, 
       new String[] 
       {
       "label", "name",
       "label.icon", "false",
       "figure", "polygon",
       "polygon.x", "0 40 40 0 0",
       "polygon.y", "0 0 20 20 0",
       "color", "255,240,245",
       "border.color", "0,0,0"
       });			
    addAnnotation
      (regionEClass, 
       source, 
       new String[] 
       {
       "label", "name",
       "label.icon", "false",
       "figure", "polygon",
       "polygon.x", "0 40 40 0 0",
       "polygon.y", "0 0 10 10 0",
       "border.color", "0,0,0"
       });				
    addAnnotation
      (textAreaEClass, 
       source, 
       new String[] 
       {
       "label", "name",
       "label.icon", "false",
       "figure", "rectangle",
       "label.placement", "internal",
       "color", "245,245,245",
       "border.color", "0,0,0"
       });			
  }

  /**
   * Initializes the annotations for <b>gmf.label</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createGmf_2Annotations()
  {
    String source = "gmf.label";						
    addAnnotation
      (getNormalState_EntryAction(), 
       source, 
       new String[] 
       {
       "foo", "bar"
       });		
    addAnnotation
      (getNormalState_ExitAction(), 
       source, 
       new String[] 
       {
       "foo", "bar"
       });		
    addAnnotation
      (getNormalState_DuringAction(), 
       source, 
       new String[] 
       {
       "foo", "bar"
       });								
    addAnnotation
      (getTextArea_Include(), 
       source, 
       new String[] 
       {
       "foo", "bar"
       });		
    addAnnotation
      (getTextArea_Function(), 
       source, 
       new String[] 
       {
       "foo", "bar"
       });		
    addAnnotation
      (getTextArea_Variable(), 
       source, 
       new String[] 
       {
       "foo", "bar"
       });
  }

  /**
   * Initializes the annotations for <b>gmf.compartment</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createGmf_3Annotations()
  {
    String source = "gmf.compartment";										
    addAnnotation
      (getOrthogonalState_Regions(), 
       source, 
       new String[] 
       {
       "collapsible", "true"
       });			
    addAnnotation
      (getRegion_Containedstates(), 
       source, 
       new String[] 
       {
       "collapsible", "true"
       });					
  }

  /**
   * Initializes the annotations for <b>gmf.link</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createGmf_4Annotations()
  {
    String source = "gmf.link";													
    addAnnotation
      (transitionEClass, 
       source, 
       new String[] 
       {
       "label", "name",
       "source", "source",
       "target", "destination",
       "target.decoration", "arrow",
       "color", "0,0,0"
       });				
  }

} //StatechartsPackageImpl
