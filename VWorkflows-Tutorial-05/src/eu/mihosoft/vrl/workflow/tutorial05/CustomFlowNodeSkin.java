/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.tutorial05;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FlowNodeSkinBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CustomFlowNodeSkin extends FlowNodeSkinBase {

    public CustomFlowNodeSkin(FXSkinFactory skinFactory,
            VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    public void updateView() {

        // we don't create custom view for flows
        if (getModel() instanceof VFlowModel) {
            return;
        }

        // we don't create a custom view if no value has been defined
        if (getModel().getValueObject().getValue() == null) {
            return;
        }

        StackPane nodePane = new StackPane();

        nodePane.getChildren().add(
                new Button(getModel().getValueObject().getValue().toString()));

        getNode().setContentPane(nodePane);
    }

}
