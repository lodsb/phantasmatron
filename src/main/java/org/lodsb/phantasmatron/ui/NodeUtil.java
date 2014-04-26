package org.lodsb.phantasmatron.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import org.lodsb.phantasmatron.ui.dataflow.ConnectorVisualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lodsb on 4/24/14.
 */
public class NodeUtil {
    public static Node getDeepestNode(Parent p, double sceneX, double sceneY, Class<?>... nodeClasses) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                Node result = null;

                if (n instanceof Parent) {
                    result = getDeepestNode((Parent) n, sceneX, sceneY, nodeClasses);
                }

                if (result == null) {
                    result = n;
                }

                for (Class<?> nodeClass : nodeClasses) {

                    if (nodeClass.isAssignableFrom(result.getClass())) {

                        return result;

                    }
                }
            }
        }

        return null;
    }

    public static Node getNode(Parent p, double sceneX, double sceneY, Class<?>... nodeClasses) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            System.err.println(n)  ;
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                Node result = null;
                /*
                if (n instanceof Parent) {
                    result = getDeepestNode((Parent) n, sceneX, sceneY, nodeClasses);
                } */

                if (result == null) {
                    result = n;
                }


                for (Class<?> nodeClass : nodeClasses) {

                    if (nodeClass.isAssignableFrom(result.getClass())) {

                        return result;

                    }
                }
            }
        }

        return null;
    }

    public static Node getDeepestConnectorVisualization(Parent p, double sceneX, double sceneY) {

        // dammit! javafx uses "wrong" children order.
        List<Node> rightOrder = new ArrayList<>();
        rightOrder.addAll(p.getChildrenUnmodifiable());
        Collections.reverse(rightOrder);

        for (Node n : rightOrder) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {

                Node result = null;

                if (n instanceof Parent) {
                    result = getDeepestConnectorVisualization((Parent) n, sceneX, sceneY);
                }

                if (result == null) {
                    result = n;
                }

               if(n instanceof ConnectorVisualization) {
                   return result;
               }
            }
        }

        return null;
    }
}
