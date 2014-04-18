package org.lodsb.phantasmatron.core;

import eu.mihosoft.vrl.workflow.CompatibilityResult;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


/**
 * Created by lodsb on 4/17/14.
 * based on DefaultValueObject from VWorkFlow-core, original author
 * author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class PValueObject  implements ValueObject {

    private transient VNode parent;
    private ObjectProperty valueProperty = new SimpleObjectProperty();

    public PValueObject() {
    }

    public PValueObject(VNode parent) {
        this.parent = parent;
    }

    @Override
    public VNode getParent() {
        return parent;
    }

    @Override
    public Object getValue() {
        return valueProperty().get();
    }

    @Override
    public void setValue(Object o) {
        this.valueProperty().set(o);
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return valueProperty;
    }

    @Override
    public CompatibilityResult compatible(final ValueObject sender, final String flowType) {
        System.err.println("compat result");
        return new CompatibilityResult() {
            @Override
            public boolean isCompatible() {
                System.err.println("is compat" + sender + " ft "+flowType);
                boolean differentObjects = sender != PValueObject.this;
//                boolean compatibleType = getParent().isInputOfType(flowType)
//                        && sender.getParent().isOutputOfType(flowType);

                return differentObjects /*&& compatibleType*/;
            }

            @Override
            public String getMessage() {
                return "incompatible: " + sender.getParent().getId()  + " -> " +  getParent().getId();
            }

            @Override
            public String getStatus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    private VisualizationRequest vr = null;
    @Override
    public VisualizationRequest getVisualizationRequest() {
        return vr;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        vr = vReq;
    }

    /**
     * @param parent the parent to set
     */
    @Override
    public void setParent(VNode parent) {
        this.parent = parent;
    }
}