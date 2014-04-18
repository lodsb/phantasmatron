package org.lodsb.phantasmatron.core;

import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.skin.SkinFactory;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;

/**
 * Created by lodsb on 4/17/14.
 */
public class PFlowFactory {

    public static VFlow newFlow() {

        VFlowModel model = PFlowFactory.newFlowModel();

        VFlow flow = new PFlow(model);

        return flow;
    }

    public static VFlow newFlow(
            SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory) {


        VFlowModel model = PFlowFactory.newFlowModel();

        VFlow flow = new PFlow(model,skinFactory);

        return flow;
    }

    public static VFlowModel newFlowModel() {
        VFlowModel result = new PFlowModel(null);
        result.setId("ROOT");
        return result;
    }

    public static IdGenerator newIdGenerator() {
        return new PIDGenerator();
    }
}