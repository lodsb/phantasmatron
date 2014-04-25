package org.lodsb.phantasmatron.core;

/**
 * Created by lodsb on 4/17/14.
 */
/*
 * VFlowModelImpl.java
 *
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */

import eu.mihosoft.vrl.workflow.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class PFlowModel implements VFlowModel {

    private final PNode node;
    private final FlowModel flow;

    @Override
    public BooleanProperty visibleProperty() {
        return flow.visibleProperty();
    }

    @Override
    public void setVisible(boolean b) {
        flow.setVisible(b);
    }

    @Override
    public boolean isVisible() {
        return flow.isVisible();
    }

    public PFlowModel(IPFlowModel parentFlow) {

        flow = new FlowModel();

        VFlowModel pFlow = null;

        if (parentFlow != null) {
            if (!(parentFlow instanceof VFlowModel)) {
                throw new IllegalArgumentException("Only " + VFlowModel.class.getName() + " objects are supported. Given type: " + parentFlow.getClass());
            } else {
                pFlow = (VFlowModel) parentFlow;
            }
            if (parentFlow.getIdGenerator() == null) {
                throw new IllegalStateException("Please define an id generator before creating subflows!");
            }

            setIdGenerator(parentFlow.getIdGenerator().newChild());
        }

        node = new PNode(pFlow);
        setTitle("Node");

    }

    @Override
    public ConnectionResult tryConnect(VNode s, VNode r, String flowType) {
        return flow.tryConnect(s, r, flowType);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String flowType) {
        return flow.connect(s, r, flowType);
    }

    @Override
    public ConnectionResult tryConnect(Connector s, Connector r) {
        return flow.tryConnect(s, r);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r) {
        return flow.connect(s, r);
    }

    @Override
    public VNode remove(VNode n) {
        return flow.remove(n);
    }

    @Override
    public void clear() {
        flow.clear();
    }

    @Override
    public ObservableList<VNode> getNodes() {
        return flow.getNodes();
    }

    @Override
    public VNode getSender(Connection c) {
        return flow.getSender(c);
    }

    @Override
    public VNode getReceiver(Connection c) {
        return flow.getReceiver(c);
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        flow.addConnections(connections, flowType);
    }

    @Override
    public Connections getConnections(String flowType) {
        return flow.getConnections(flowType);
    }

    @Override
    public ObservableMap<String, Connections> getAllConnections() {
        return flow.getAllConnections();
    }

    @Override
    public void setFlowNodeClass(Class<? extends VNode> cls) {
        flow.setFlowNodeClass(cls);
    }

    @Override
    public Class<? extends VNode> getFlowNodeClass() {
        return flow.getFlowNodeClass();
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return null;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StringProperty titleProperty() {
        return node.titleProperty();
    }

    @Override
    public final void setTitle(String title) {
        node.setTitle(title);
    }

    @Override
    public String getTitle() {
        return node.getTitle();
    }

    @Override
    public StringProperty idProperty() {
        return node.idProperty();
    }

    @Override
    public void setId(String id) {
        node.setId(id);
    }

    @Override
    public String getId() {
        return node.getId();
    }

    @Override
    public DoubleProperty xProperty() {
        return node.xProperty();
    }

    @Override
    public DoubleProperty yProperty() {
        return node.yProperty();
    }

    @Override
    public void setX(double x) {
        node.setX(x);
    }

    @Override
    public void setY(double x) {
        node.setY(x);
    }

    @Override
    public double getX() {
        return node.getX();
    }

    @Override
    public double getY() {
        return node.getY();
    }

    @Override
    public DoubleProperty widthProperty() {
        return node.widthProperty();
    }

    @Override
    public DoubleProperty heightProperty() {
        return node.heightProperty();
    }

    @Override
    public void setWidth(double w) {
        node.setWidth(w);
    }

    @Override
    public void setHeight(double h) {
        node.setHeight(h);
    }

    @Override
    public double getWidth() {
        return node.getWidth();
    }

    @Override
    public double getHeight() {
        return node.getHeight();
    }

    @Override
    public Connector getConnector(String localId) {
        return node.getConnector(localId);
    }

    @Override
    public void setValueObject(ValueObject obj) {
        node.setValueObject(obj);
    }

    @Override
    public ValueObject getValueObject() {
        return node.getValueObject();
    }

    @Override
    public ObjectProperty<ValueObject> valueObjectProperty() {
        return node.valueObjectProperty();
    }

    @Override
    public VFlowModel getFlow() {
        return node.getFlow();
    }

    //    @Override
//    public boolean isInput() {
//        return node.isInput();
//    }
//
//    @Override
//    public boolean isOutput() {
//        return node.isOutput();
//    }
    @Override
    public VFlowModel newFlowNode(ValueObject obj) {
        PNode flowNode = new PNode(this);

        return (VFlowModel) flow.newNode(flowNode, obj);
    }

    @Override
    public VFlowModel newFlowNode() {
        PNode flowNode = new PNode(this);

        DefaultValueObject valObj = new DefaultValueObject();

        VFlowModel result = (VFlowModel) flow.newNode(flowNode, valObj); // end newNode()

        valObj.setParent(result);

        return result;

    }

    @Override
    public VNode newNode(ValueObject obj) {

        VNode result = null;

        try {
            Constructor constructor = getFlowNodeClass().getConstructor(VFlowModel.class);
            try {
                result = (VNode) constructor.newInstance(this);
                result.setValueObject(obj);

                result = flow.newNode(result, obj);

            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(PConnections.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(PConnections.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public VNode newNode() {
        DefaultValueObject valObj = new DefaultValueObject();
        VNode result = newNode(valObj);
        valObj.setParent(result);
        return result;
    }

    //    @Override
//    public String getGlobalId() {
//        return node.getGlobalId();
//    }
    @Override
    public final void setIdGenerator(IdGenerator generator) {
        flow.setIdGenerator(generator);
    }

    @Override
    public IdGenerator getIdGenerator() {
        return flow.getIdGenerator();
    }

    @Override
    public void setNodeLookup(NodeLookup nodeLookup) {
        flow.setNodeLookup(nodeLookup);
    }

    @Override
    public NodeLookup getNodeLookup() {
        return flow.getNodeLookup();
    }

    //    @Override
//    public ObservableList<String> getInputTypes() {
//        return node.getInputTypes();
//    }
//
//    @Override
//    public ObservableList<String> getOutputTypes() {
//        return node.getOutputTypes();
//    }
//
//    @Override
//    public boolean isInputOfType(String type) {
//        return node.isInputOfType(type);
//    }
//
//    @Override
//    public boolean isOutputOfType(String type) {
//        return node.isOutputOfType(type);
//    }
    @Override
    public Connector getMainInput(String type) {
        return this.node.getMainInput(type);
    }

    @Override
    public Connector getMainOutput(String type) {
        return this.node.getMainOutput(type);
    }

    @Override
    public Connector addInput(String type) {
        return this.node.addInput(this,type);
    }

    @Override
    public Connector addOutput(String type) {
        return this.node.addOutput(this,type);
    }

    @Override
    public Connector addConnector(Connector c) {
        return this.node.addConnector(this,c);
    }

    @Override
    public ObservableList<Connector> getConnectors() {
        return this.node.getConnectors();
    }

    @Override
    public ObservableList<Connector> getInputs() {
        return this.node.getInputs();
    }

    @Override
    public ObservableList<Connector> getOutputs() {
        return this.node.getOutputs();
    }

    @Override
    public void setMainInput(Connector connector) {
        this.node.setMainInput(connector);
    }

    @Override
    public void setMainOutput(Connector connector) {
        this.node.setMainOutput(connector);
    }

    @Override
    public Collection<String> getMainInputTypes() {
        return this.node.getMainInputTypes();
    }

    @Override
    public Collection<String> getMainOutputTypes() {
        return this.node.getMainOutputTypes();
    }
}
