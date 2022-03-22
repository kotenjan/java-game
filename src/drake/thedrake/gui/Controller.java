package drake.thedrake.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Controller {

    BoardGraphic control = new BoardGraphic();

    @FXML
    private Button drakeButt;

    @FXML
    private Pane hppPane;

    @FXML
    private Button hppButt;

    @FXML
    private Pane hdhPane;

    @FXML
    private Button hdhButt;

    @FXML
    private Pane hpiPane;

    @FXML
    private Button hpiButt;

    @FXML
    private Button endButt;

    @FXML
    private Pane playerA;

    @FXML
    private Pane playerB;

    @FXML
    private Pane upperBar;

    @FXML
    public GridPane board;

    @FXML
    public Label statusText;

    @FXML
    public void endApplication(){
        System.out.println("CLOSING");
        Stage stage = (Stage) endButt.getScene().getWindow();
        stage.close();
    }

    public void boardScene(){

        control = new BoardGraphic();

        hppButt.setVisible(false);
        hppPane.setVisible(false);
        hpiButt.setVisible(false);
        hpiPane.setVisible(false);
        hdhButt.setVisible(false);
        hdhPane.setVisible(false);

        control.displayStacks(playerA, playerB);
        control.displayBoard(board);

        playerA.setVisible(true);
        playerB.setVisible(true);
        board.setVisible(true);

        statusText.setText("0|0");

    }

    public void homeScreen(){

        hppButt.setVisible(true);
        hppPane.setVisible(true);
        hpiButt.setVisible(true);
        hpiPane.setVisible(true);
        hdhButt.setVisible(true);
        hdhPane.setVisible(true);

        playerA.setVisible(false);
        playerB.setVisible(false);
        board.setVisible(false);

        statusText.setText("");

        drakeButt.setStyle("-fx-background-color: #00FF00;\n");
        endButt.setStyle("-fx-background-color: #00FF00;\n");
        upperBar.setStyle("-fx-background-color: #00FF00;\n");

    }

    public void clickGrid(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();

        if (clickedNode != board) {
            Node parent = clickedNode.getParent();
            while (parent != board) {
                clickedNode = parent;
                parent = clickedNode.getParent();
            }
            int colIndex = GridPane.getColumnIndex(clickedNode);
            int rowIndex = GridPane.getRowIndex(clickedNode);
            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
            control.clickedOnBoard(colIndex, rowIndex);
            control.displayStacks(playerA, playerB);
            control.displayBoard(board);
            control.checkGameResult(upperBar, endButt, drakeButt, statusText);
        }
    }

    public void clickRightStack(){
        System.out.println("Mouse clicked cell: Right stack");
        control.changeOrangeStackOrder();
        control.displayStacks(playerA, playerB);
    }

    public void clickLeftStack(){
        System.out.println("Mouse clicked cell: Left stack");
        control.changeBlueStackOrder();
        control.displayStacks(playerA, playerB);
    }
}
