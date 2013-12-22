//package eu.mihosoft.vrl.workflow.tutorial05;
//
//import de.sciss.scalainterpreter.CodePane;
//import eu.mihosoft.vrl.workflow.VFlow;
//import eu.mihosoft.vrl.workflow.VFlowModel;
//import eu.mihosoft.vrl.workflow.VNode;
//import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinBase;
//import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
//import javafx.application.Platform;
//import javafx.concurrent.Task;
//import javafx.embed.swing.SwingNode;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.control.*;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TextField;
//import javafx.scene.input.ContextMenuEvent;
//import javafx.scene.layout.*;
//import javafx.scene.text.Text;
//import org.controlsfx.control.PopOver;
//import org.lodsb.phantasmatron.core.Code;
//import test.*;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
// * Created by lodsb on 12/17/13.
// */
//public class JCodeFlowSkin extends FXFlowNodeSkinBase {
//
//    private final double width = 400;
//
//    public JCodeFlowSkin(FXSkinFactory skinFactory,
//                         VNode model, VFlow controller) {
//        super(skinFactory, model, controller);
//    }
//
//    protected Pane createCodeMenu(final Code code, final CodePane cp) {
//        Region reg = new Region();
//        HBox.setHgrow(reg, Priority.ALWAYS);
//
//        HBox hbox = new HBox();
//        hbox.setPadding(new Insets(20));
//        hbox.setAlignment(Pos.CENTER);
//
//        Button compileButton = new Button("Compile");
//        //compileButton.setPrefSize(100, 20);
//        final Text textArea = new Text("OFOFOFOF");
//        GridPane tg = new GridPane();
//        tg.setAlignment(Pos.CENTER);
//        tg.add(textArea, 0,0);
//
//
//        final PopOver popOver = new PopOver(tg);
//
//        final ProgressIndicator pi = new ProgressIndicator();
//        pi.setProgress(1);
//        pi.setStyle(" -fx-progress-color: green;");
//        pi.setMinSize(40,40);
//        pi.setMaxSize(41, 41);
//        GridPane p = new GridPane();
//        p.setAlignment(Pos.CENTER);
//        compileButton.setMinHeight(40);
//        compileButton.setMaxHeight(41);
//        p.add(pi, 0, 0);
//        hbox.setMaxHeight(70);
//
//        pi.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
//            @Override
//            public void handle(ContextMenuEvent contextMenuEvent) {
//                popOver.show(pi, contextMenuEvent.getScreenX()+15, contextMenuEvent.getScreenY()+15);
//            }
//        });
//
//        compileButton.setOnAction( new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//
//                Task<Void> task = new Task<Void>(){
//                    @Override
//                    protected Void call() throws Exception {
//                        String text = cp.editor().getText();
//
//                        pi.setStyle(" -fx-progress-color: orange;");
//
//                        CompileResult res = code.compile(text);
//
//
//                        System.out.println("WHAT???"+ res+ " | "+(res instanceof CompileSuccess)+ " | "+(res instanceof CompileError));
//
//                        // finish progress indication
//
//                        updateProgress(10, 10);
//
//                        try {
//                            Platform.runLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getModel().getConnectors().clear();
//                                }
//                            });
//                        } catch(Exception e){
//                            e.printStackTrace();
//                        }
//                        if(res instanceof CompileSuccess) {
//                            pi.setStyle(" -fx-progress-color: green;");
//                            CompileSuccess succ = (CompileSuccess) res;
//                            textArea.setText(succ.message());
//
//
//                        } else if(res instanceof CompileError) {
//                            pi.setStyle(" -fx-progress-color: red;");
//                            CompileError err = (CompileError) res;
//                            textArea.setText(err.message());
//                        }
//
//                        //pi.progressProperty().unbind();
//
//
//                        return null;
//                    }
//                };
//
//                pi.progressProperty().bind(task.progressProperty());
//
//                new Thread(task).start();
//            }
//
//        });
//
//
//        hbox.getChildren().addAll(reg, compileButton,  p);
//
//
//        return hbox;
//
//    }
//
//    protected Accordion createView(Code code) {
//
//        final Accordion accordion = new Accordion();
//
//        System.out.println("CODEFLOW");
//        final GridPane grid = new GridPane();
//        SwingNode node = new SwingNode();
//
//        TitledPane gridTitlePane = new TitledPane();
//        grid.setVgap(4);
//        grid.setPadding(new Insets(5, 5, 5, 5));
//        grid.add(new Label("First Name: "), 0, 0);
//        grid.add(new TextField(), 1, 0);
//        grid.add(new Label("Last Name: "), 0, 1);
//        grid.add(new TextField(), 1, 1);
//        grid.add(new Label("Email: "), 0, 2);
//        grid.add(new TextField(), 1, 2);
//        Slider sl = new Slider(10.0, 20.90, 40.0);
//        grid.add(new Label("some slider"), 0, 3);
//        grid.add(sl, 1,3);
//        gridTitlePane.setText("Controls");
//        gridTitlePane.setContent(grid);
//
//
//        TitledPane codePane = new TitledPane();
//        codePane.setText("Code");
//        GridPane codeGrid = new GridPane();
//        CodePane cp = createSwingContent(node);
//
//        codeGrid.add(createCodeMenu(code, cp),0,0);
//        //codeGrid.add(node, 0, 1);
//
//        ScrollPane sp = new ScrollPane();
//        sp.setContent(node);
//        codeGrid.add(sp, 0, 1);
//
//        codePane.setContent(codeGrid);
//        sp.setMinHeight(200);
//        sp.setMinWidth(this.width);
//
//
//
//        accordion.getPanes().add(gridTitlePane);
//        accordion.getPanes().add(codePane);
//        accordion.setMinWidth(this.width);
//
//        return accordion;
//    }
//
//    public void updateView() {
//
//        super.updateView();
//
//        // we don't create custom view for flows
//        if (getModel() instanceof VFlowModel) {
//            return;
//        }
//
//        // we don't create a custom view if no value has been defined
//        if (getModel().getValueObject().getValue() == null) {
//            return;
//        }
//
//        Code code = (Code)getModel().getValueObject().getValue();
//
//        // create the view
//        Node view = createView(code);
//
//        // add the view to scalable content pane
//        if (view != null) {
//
//            //ScalableContentPane scalableContentPane = new ScalableContentPane();
//            //scalableContentPane.setPadding(new Insets(10));
//
//            GridPane nodePane = new GridPane();
//           // ScrollPane sc = new ScrollPane();
//
//           // sc.setContent(view);
//
//            //nodePane.setAlignment(Pos.CENTER);
//            nodePane.getChildren().add(view);
//            //scalableContentPane.setContentPane(nodePane);
//
//            //scalableContentPane.getContentPane().getChildren().add(view);
//            getNode().setContentPane(nodePane);
//            getNode().setMinWidth(this.width);
//        }
//    }
//
//    private CodePane createSwingContent(final SwingNode swingNode) {
//        CodePane cp = Test.codePane();
//        JComponent comp = cp.component();
//
//        final JPanel panel = new JPanel(new FlowLayout());
//
//        System.out.println("my TEXT: " + cp.editor().getText());
//
//        panel.add(cp.component());
//
//        comp.setPreferredSize(new Dimension(450,450));
//
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                swingNode.setContent(panel);
//            }
//        });
//
//        return cp;
//    }
//}
