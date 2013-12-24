//package org.lodsb.phantasmatron.ui;
//
//import eu.mihosoft.vrl.workflow.FlowFactory;
//import eu.mihosoft.vrl.workflow.VFlow;
//import eu.mihosoft.vrl.workflow.VNode;
//import eu.mihosoft.vrl.workflow.VNodeSkin;
//import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
//import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
//import javafx.event.EventHandler;
//import javafx.scene.effect.BlendMode;
//import javafx.scene.input.DragEvent;
//import javafx.scene.input.Dragboard;
//import javafx.scene.input.TransferMode;
//import org.lodsb.phantasmatron.core.Code;
//
//import java.io.File;
//import java.util.Random;
//
///**
// * Created by lodsb on 12/22/13.
// * <p/>
// * taken from vworkflows demo 5 @  https://github.com/miho/VWorkflows
// * original license:
// * /*
// * Copyright 2012 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
// * <p/>
// * Redistribution and use in source and binary forms, with or without modification, are
// * permitted provided that the following conditions are met:
// * <p/>
// * 1. Redistributions of source code must retain the above copyright notice, this list of
// * conditions and the following disclaimer.
// * <p/>
// * 2. Redistributions in binary form must reproduce the above copyright notice, this list
// * of conditions and the following disclaimer in the documentation and/or other materials
// * provided with the distribution.
// * <p/>
// * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
// * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
// * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// * <p/>
// * The views and conclusions contained in the software and documentation are those of the
// * authors and should not be interpreted as representing official policies, either expressed
// * or implied, of Michael Hoffer <info@michaelhoffer.de>.
// */
//
//public class Flow extends ScalableContentPane {
//    private VFlow flow;
//    private FXValueSkinFactory skinFactory;
//
//    public static void register(FXValueSkinFactory f) {
//        f.addSkinClassForValueType(Code.class, CodeFlowSkin.class);
//    }
//
//    public Flow() {
//        flow = FlowFactory.newFlow();
//        // make it visible
//        flow.setVisible(true);
//
//        // create two nodes:
//        // one leaf node and one subflow which is returned by createNodes
//        createFlow(flow, 3, 6);
//
//        VNode n;
//
//        n = flow.newNode();
//        // defines the node title
//        n.setTitle("Node " + n.getId());
//
//        System.out.println("ADDING CODE");
//        n.getValueObject().setValue(new Code("", null));
//
//        // show the main stage/window
//        skinFactory = createFlowPane(flow);
//
//        this.getStylesheets().setAll((new File("default.css").toURI().toString()));
//
//        this.setOnDragEntered(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent dragEvent) {
//                System.out.println("DRAG ENTERED" + dragEvent.getDragboard().getContentTypes());
//
//                dragEvent.acceptTransferModes(TransferMode.ANY);
//                setBlendMode(BlendMode.RED);
//            }
//        });
//
//        this.setOnDragOver(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent dragEvent) {
//                dragEvent.acceptTransferModes(TransferMode.ANY);
//            }
//        });
//
//        this.setOnDragExited(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent dragEvent) {
//                System.out.println("DRAG Exit" + dragEvent.getDragboard().getContentTypes());
//                setBlendMode(BlendMode.MULTIPLY);
//            }
//        });
//
//        this.setOnDragDropped(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent dragEvent) {
//                System.out.println("DRAG Drop" + dragEvent.getDragboard().getContentTypes());
//
//                Dragboard db = dragEvent.getDragboard();
//
//                if (db.getContentTypes().contains(ObjectPalette.dataFormat())) {
//
//                    ObjectPalette.ObjectDescriptor o = (ObjectPalette.ObjectDescriptor) db
//                            .getContent(ObjectPalette.dataFormat());
//
//                    System.out.println("correct drop format");
////                    Code c = CodeCrea.apply(o);
////
////                    VNode v = flow.newNode();
////                    v.getValueObject().setValue(c);
////                    v.setTitle("Node " + v.getId());
////
////                    v.setX(dragEvent.getX());
////                    v.setY(dragEvent.getY());
////
////                    v.setWidth(300);
////                    v.setHeight(200);
////
////                    // BUG IN VWORKFLOW?! creating nodes after init doesnt show any node's content
////                    VNodeSkin skin = skinFactory.createSkin(v, flow);
////                    skin.add();
//
//                    dragEvent.setDropCompleted(true);
//                }
//
//
//                setBlendMode(BlendMode.MULTIPLY);
//            }
//        });
//    }
//
//    private FXValueSkinFactory createFlowPane(final VFlow flow) {
//
//        // create scalable root pane
//
//
//        // define it as background (css class)
//        this.getStyleClass().setAll("vflow-background");
//
//        // create skin factory for flow visualization
//        FXValueSkinFactory fXSkinFactory = new FXValueSkinFactory(this.getContentPane());
//
//        fXSkinFactory.addSkinClassForValueType(Code.class, CodeFlowSkin.class);
//
//        // generate the ui for the flow
//        flow.addSkinFactories(fXSkinFactory);
//
//        // the usual application setup
//        //Scene scene = new Scene(canvas, 1024, 600);
//        //scene.getStylesheets().setAll((new File("default.css").toURI().toString()));
//
//
//        // add css style
//        //canvas.getStylesheets().setAll((new File("default.css").toURI().toString()));
//
//        return fXSkinFactory;
//
//    }
//
//    private void createFlow(VFlow workflow, int depth, int width) {
//
//        if (depth < 1) {
//            return;
//        }
//
//        // connection types
//        String[] connectionTypes = {"control", "data", "event"};
//
//        Random rand = new Random();
//
//        for (int i = 0; i < width/2; i++) {
//
//            VNode n;
//
//            n = workflow.newNode();
//            // defines the node title
//            n.setTitle("Node " + n.getId());
//
//            System.out.println("ADDING CODE");
//            n.getValueObject().setValue(new Code("", null));
//
//
//
//            /*
//            for (int k = 0; k < connectionTypes.length; k++) {
//                String type = connectionTypes[k % connectionTypes.length];
//                for (int j = 0; j < 3; j++) {
//
//                    // adds an input to the node
//                    Connector input = n.addInput(type);
//
//                    // adds an output to the node
//                    Connector output = n.addOutput(type);
//
//                    // the first input/output of each type shall be defined
//                    // as main/default connector that will be connected if
//                    // we drop on the node instead of connectors
//                    if (j == 0) {
//                        n.setMainInput(input);
//                        n.setMainOutput(output);
//                    }
//                }
//            } */
//
//            // defines how many nodes per row
//            int numNodesPerRow = 3;
//
//            // defines the gap between the nodes
//            double gap = 30;
//
//            // defines the node dimensions
//            n.setWidth(300);
//            //
//            n.setHeight(200);
//            // defines the node location
//            n.setX(gap + (i % numNodesPerRow) * (n.getWidth() + gap));
//            n.setY(gap + (i / numNodesPerRow) * (n.getHeight() + gap));
//        }
//    }
//}
