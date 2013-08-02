/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.tutorial05;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinBase;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Lcd;
import jfxtras.labs.scene.control.gauge.LcdBuilder;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.StyleModel;
import jfxtras.labs.scene.control.gauge.StyleModelBuilder;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ImageFlowNodeSkin extends FXFlowNodeSkinBase {

    public ImageFlowNodeSkin(FXSkinFactory skinFactory,
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
        
        ScalableContentPane scalableContentPane = new ScalableContentPane();
        
        scalableContentPane.setPadding(new Insets(10));
        
        GridPane nodePane = new GridPane();
        nodePane.setAlignment(Pos.CENTER);
        scalableContentPane.setContentPane(nodePane);

        ImageView view = new ImageView(
                new Image("/eu/mihosoft/vrl/workflow/tutorial05/resources/node-img-01.png"));
        
        scalableContentPane.getContentPane().getChildren().add(view);
        getNode().setContentPane(scalableContentPane);
    }
}
