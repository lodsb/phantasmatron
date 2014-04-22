package org.lodsb.phantasmatron.core;

import eu.mihosoft.vrl.workflow.*;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Created by lodsb on 4/17/14.
 */
interface IPFlowModel extends Model, VisibleState {

    public ConnectionResult tryConnect(VNode s, VNode r, String flowType);

    public ConnectionResult connect(VNode s, VNode r, String flowType);

    public ConnectionResult tryConnect(Connector s, Connector r);

    public ConnectionResult connect(Connector s, Connector r);

    public VNode remove(VNode n);

    public void clear();

    public ObservableList<VNode> getNodes();

    public VNode getSender(Connection c);
    public VNode getReceiver(Connection c);

    public void addConnections(Connections connections, String flowType);
    public Connections getConnections(String flowType);
    public ObservableMap<String,Connections> getAllConnections();

    public void setFlowNodeClass(Class<? extends VNode> cls);

    public Class<? extends VNode> getFlowNodeClass();

    public void setIdGenerator(IdGenerator generator);
    public IdGenerator getIdGenerator();

    public void setNodeLookup(NodeLookup nodeLookup);
    public NodeLookup getNodeLookup();


}