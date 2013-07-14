/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.tutorial05;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Skin;
import eu.mihosoft.vrl.workflow.SkinFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VNodeSkin;
import eu.mihosoft.vrl.workflow.fx.FXConnectionSkin;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
import eu.mihosoft.vrl.workflow.fx.FXSkin;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Parent;

/**
 *
 * @author Michael
 */
public class CustomSkinFactory extends FXSkinFactory {

    public CustomSkinFactory(Parent parent) {
        super(parent);
    }

    public CustomSkinFactory(Parent parent, FXSkinFactory factory) {
        super(parent, factory);
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow flow) {
        return new CustomFlowNodeSkin(this, getFxParent(), n, flow);
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return new FXConnectionSkin(this, getFxParent(), c, flow, type/*, clipboard*/);
    }

    @Override
    public SkinFactory<FXConnectionSkin, FXFlowNodeSkin> createChild(Skin parent) {

        FXSkinFactory result = new CustomSkinFactory(((FXSkin) parent).getContentNode(), this);

        return result;
    }

    @Override
    public FXSkinFactory newInstance(Parent parent, FXSkinFactory parentFactory) {

        FXSkinFactory result = new CustomSkinFactory(parent, parentFactory);

        return result;
    }
}
