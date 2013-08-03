/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.tutorial05;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import jfxtras.labs.scene.control.gauge.SixteenSegment;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class StringFlowNodeSkin extends CustomFlowNodeSkin {

    public StringFlowNodeSkin(FXSkinFactory skinFactory,
            VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    protected Node createView() {
        // value
        String string = getModel().getValueObject().getValue().toString();

        // Layout
        final GridPane view = new GridPane();
        view.getStyleClass().setAll("segment-background");
        view.setPadding(new Insets(5));
        view.setHgap(0);
        view.setVgap(5);
        view.setAlignment(Pos.TOP_CENTER);

        // Create some controls and add them to the layout
        for (int i = 0; i < string.length(); i++) {
            SixteenSegment segment = new SixteenSegment();
            segment.setCharacter(string.charAt(i));
            segment.setPrefSize(50, 100);
            view.add(segment, i, 1);
        }
        
        return view;
    }


}
