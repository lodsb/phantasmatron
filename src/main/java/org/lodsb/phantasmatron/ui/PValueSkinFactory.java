package org.lodsb.phantasmatron.ui;

import eu.mihosoft.vrl.workflow.*;
import eu.mihosoft.vrl.workflow.fx.*;
import javafx.scene.Parent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Value based skin factory.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class PValueSkinFactory extends FXSkinFactory {

    private Map<String, Class<? extends FXFlowNodeSkinBase>> valueSkins = new HashMap<>();
    private final Map<String, Class<? extends FXConnectionSkin>> connectionSkins = new HashMap<>();

    public PValueSkinFactory(Parent parent) {
        super(parent);

        init();
    }

    public PValueSkinFactory(Parent parent, FXSkinFactory factory) {
        super(parent, factory);

        init();
    }

    private void init() {
        valueSkins.put(Object.class.getName(), FXFlowNodeSkinBase.class);
    }

    private FXFlowNodeSkin chooseNodeSkin(VNode n, VFlow flow) {

        System.err.println("choose node skin "+ n.getId() + " > "+ n.getValueObject().getValue());

        Object value = n.getValueObject().getValue();

        if (value == null) {
            return new FXFlowNodeSkinBase(this, n, flow);
        }

        Class<?> valueClass = value.getClass();
        Class<? extends FXFlowNodeSkinBase> skinClass = valueSkins.get(valueClass.getName());

        while (skinClass == null) {
            valueClass = valueClass.getSuperclass();
            skinClass = valueSkins.get(valueClass.getName());
        }

        try {

            Constructor<?> constructor
                    = skinClass.getConstructor(
                    FXSkinFactory.class, VNode.class, VFlow.class);

            FXFlowNodeSkinBase skin
                    = (FXFlowNodeSkinBase) constructor.newInstance(this, n, flow);

            return skin;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return new FXFlowNodeSkinBase(this, n, flow);
    }

    private ConnectionSkin chooseConnectionSkin(Connection c, VFlow flow, String type) {

        String connectionType = c.getType();

        Class<? extends FXConnectionSkin> skinClass = connectionSkins.get(connectionType);

        if (skinClass==null) {
            skinClass = FXConnectionSkin.class;
        }

        try {

            Constructor<?> constructor
                    = skinClass.getConstructor(
                    FXSkinFactory.class, Parent.class,
                    Connection.class, VFlow.class, String.class);

            FXConnectionSkin skin
                    = (FXConnectionSkin) constructor.newInstance(this, getFxParent(), c, flow, type);

            return skin;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return new FXConnectionSkin(this, getFxParent(), c, flow, type/*, clipboard*/);
    }

    public void addSkinClassForValueType(Class<?> valueType,
                                         Class<? extends FXFlowNodeSkinBase> skinClass) {

        boolean notAvailable = true;

        // check whether correct constructor is available
        try {
            Constructor<?> constructor
                    = FXFlowNodeSkinBase.class.getConstructor(
                    FXSkinFactory.class, VNode.class, VFlow.class);
            notAvailable = false;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        // we cannot accept the specified skin class as it does not provide
        // the required constructor
        if (notAvailable) {
            throw new IllegalArgumentException(
                    "Required constructor missing: ("
                            + FXSkinFactory.class.getSimpleName()
                            + ", " + VNode.class.getSimpleName() + ", "
                            + VNode.class.getSimpleName() + ")");
        }

        valueSkins.put(valueType.getName(), skinClass);
    }

    public void addSkinClassForConnectionType(String connectionType,
                                              Class<? extends FXConnectionSkin> skinClass) {

        boolean notAvailable = true;

        // check whether correct constructor is available
        try {

            Constructor<?> constructor
                    = FXConnectionSkin.class.getConstructor(
                    FXSkinFactory.class, Parent.class, Connection.class, VFlow.class, String.class);
            notAvailable = false;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(FXValueSkinFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        // we cannot accept the specified skin class as it does not provide
        // the required constructor
        if (notAvailable) {
            throw new IllegalArgumentException(
                    "Required constructor missing: ("
                            + FXSkinFactory.class.getSimpleName()
                            + ", " + Parent.class.getSimpleName() + ", "
                            + VFlow.class.getSimpleName() + ", "
                            + String.class + ")");
        }

        connectionSkins.put(connectionType, skinClass);
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow flow) {
        return chooseNodeSkin(n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return chooseConnectionSkin(c, flow, type);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXValueSkinFactory result = new FXValueSkinFactory(((FXSkin) parent).getContentNode(), this);
        System.err.println("ERROR NOT SUPPORTED result value skin @ createchild");
        //result.valueSkins = valueSkins;

        return result;
    }

    @Override
    public FXSkinFactory newInstance(Parent parent, FXSkinFactory parentFactory) {

        FXValueSkinFactory result = new FXValueSkinFactory(parent, parentFactory);
        System.err.println("ERROR NOT SUPPORTED result value skin @ new inst");
        //result.valueSkins = valueSkins;

        return result;
    }

}
