package org.lodsb.phantasmatron.ui;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.fx.FXConnectionSkin;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Path;

/**
 * Created by lodsb on 4/12/14.
 */
public class PConnectionSkin extends FXConnectionSkin {
    public PConnectionSkin(FXSkinFactory skinFactory, Parent parent, Connection connection, VFlow flow, String type) {
        super(skinFactory, parent, connection, flow, type);

        Path path = this.getNode();
        path.getStyleClass().clear();
        path.setStroke(Util.typeString2Color(type));
        path.setStrokeWidth(5.0);

        Tooltip t = new Tooltip(type);
        Tooltip.install(path, t);
    }
}
