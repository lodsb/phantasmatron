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
import java.awt.Rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import jfxtras.labs.scene.control.gauge.SixteenSegment;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class StringFlowNodeSkin extends FXFlowNodeSkinBase {

    public StringFlowNodeSkin(FXSkinFactory skinFactory,
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

        // value
        String string = getModel().getValueObject().getValue().toString();

        // Layout
        final GridPane pane = new GridPane();
        pane.getStyleClass().setAll("segment-background");
        pane.setPadding(new Insets(5));
        pane.setHgap(0);
        pane.setVgap(5);
        pane.setAlignment(Pos.TOP_CENTER);

        // Create some controls and add them to the layout
        for (int i = 0; i < string.length(); i++) {
            SixteenSegment segment = new SixteenSegment();
            segment.setCharacter(string.charAt(i));
            segment.setPrefSize(50, 100);
            pane.add(segment, i, 1);
        }

        scalableContentPane.getContentPane().getChildren().add(pane);
        getNode().setContentPane(scalableContentPane);
    }
}
