package drake.thedrake.gui;

import drake.thedrake.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.Random;

public class BoardGraphic {

    private int dimension = 5;
    private PositionFactory pf;
    private GameState state;
    private Boolean selected = false;
    private BoardPos origin;
    private List<Move> moves;
    private Board board;

    public BoardGraphic(){

        board = new Board(dimension);
        pf = board.positionFactory();

        generateMountains();

        state = new StandardDrakeSetup().startState(board);

    }

    private void generateMountains(){
        Random rand = new Random();
        int n = rand.nextInt(dimension);

        for(int i = 0; i < n; ++i){
            board = board.withTiles(new Board.TileAt(pf.pos(rand.nextInt(dimension), rand.nextInt(dimension)), BoardTile.MOUNTAIN));
        }

    }

    public void displayBoard(GridPane grid){

        grid.getChildren().removeAll(grid.getChildren());
        Pane pane;

        for(int i = 0; i < dimension; ++i){
            for(int j = 0; j < dimension; ++j){
                pane = constructPane();
                fillPaneWithImage(i, dimension-1-j, pane);
                grid.add(pane, i, dimension-1-j);
            }
        }

        if(selected){
            highlightSelected(grid);
            showValidMoves(grid);
        }

    }

    //Adds graphic content to grid
    private void highlightSelected(GridPane grid){

        Pane pane = constructClickedPane();
        fillPaneWithImage(origin.i(), dimension-1-origin.j(), pane);
        grid.add(pane, origin.i(), dimension-1-origin.j());

    }

    //Adds graphic content to grid
    private void showValidMoves(GridPane grid){

        Pane pane;

        for(Move move : moves){
            pane = constructPane();
            fillPaneWithImage(move.target().i(), dimension-1-move.target().j(), pane);
            pane.getChildren().add(getArrowImage());
            grid.add(pane, move.target().i(), dimension-1-move.target().j());
        }

    }

    //Adds graphic content to pane
    private void fillPaneWithImage(int i, int j, Pane pane){

        BoardPos pos = new BoardPos(dimension, i, dimension-1-j);
        Tile tile = state.tileAt(pos);

        if (tile.hasTroop()) pane.getChildren().add(getTroopImage(tile));
        else if (tile == BoardTile.MOUNTAIN) pane.getChildren().add(getMountainImage());

    }

    public void clickedOnBoard(int x, int y){

        BoardPos target = pf.pos(x,dimension-1-y);
        Tile tile = state.tileAt(target);

        if(selected){
            performAction(target);
            selected = false;
            return;
        }

        if (tile.hasTroop()) {
            origin = target;
            selected = true;
            getMovesList();
            return;
        }

        if (tile != BoardTile.MOUNTAIN) {
            state = new PlaceFromStack(target).execute(state);
        }

    }

    private void performAction(BoardPos target){

        selected= false;
        System.out.println("Target is: " + target.toString());

        for(Move move : moves){
            if(move.target().equals(target)){
                state = move.execute(state);
            }
        }
    }

    private void getMovesList(){
        Tile tile = state.tileAt(origin);
        moves = tile.movesFrom(origin, state);
    }

    public void displayStacks(Pane bluePane, Pane orangePane){

        if(checkPanesEmpty(bluePane, orangePane)) return;

        ImageView blueImage = new ImageView(getTroop(state.getBlueArmy().stack().get(0), PlayingSide.BLUE, TroopFace.AVERS));
        ImageView orangeImage = new ImageView(getTroop(state.getOrangeArmy().stack().get(0), PlayingSide.ORANGE, TroopFace.AVERS));
        blueImage.setFitHeight(60);
        orangeImage.setFitHeight(60);
        blueImage.setFitWidth(60);
        orangeImage.setFitWidth(60);
        blueImage.relocate(5, 5);
        orangeImage.relocate(5, 5);

        bluePane.getChildren().add(blueImage);
        orangePane.getChildren().add(orangeImage);

    }

    private boolean checkPanesEmpty(Pane bluePane, Pane orangePane){

        boolean empty = false;

        if(state.getOrangeArmy().stack().isEmpty()){
            System.out.println("Right stack is empty");
            orangePane.getChildren().removeAll(orangePane.getChildren());
            empty = true;
        }

        if(state.getBlueArmy().stack().isEmpty()){
            System.out.println("Left stack is empty");
            bluePane.getChildren().removeAll(bluePane.getChildren());
            empty = true;
        }

        return empty;
    }


    private Image getTroop(Troop info, PlayingSide side, TroopFace face) {
        TroopImageSet images = new TroopImageSet(info.name());
        return images.get(side,face);
    }

    private ImageView getTroopImage(Tile tile){
        TroopTile armyTile = ((TroopTile) tile);
        ImageView imageView = new ImageView(getTroop(armyTile.troop(), armyTile.side(), armyTile.face()));
        imageView.setFitHeight(320/dimension-10);
        imageView.setFitWidth(320/dimension-10);
        imageView.relocate(5, 5);
        return imageView;
    }

    private ImageView getMountainImage(){
        ImageView imageView = new ImageView(new Image("/assets/mountain.png"));
        imageView.setFitHeight(320/dimension-10);
        imageView.setFitWidth(320/dimension-10);
        imageView.relocate(5, 5);
        return imageView;
    }

    private ImageView getArrowImage(){
        ImageView imageView = new ImageView( new Image("/assets/move.png"));
        imageView.setFitHeight(320/dimension);
        imageView.setFitWidth(320/dimension);
        return imageView;
    }

    private Pane constructPane(){
        Pane pane = new Pane();
        pane.setStyle(
                "-fx-background-color: #AAAAAA;\n" +
                        "    -fx-border-color: #CCCCCC #666666 #666666 #CCCCCC;\n" +
                        "    -fx-border-style: solid;\n" +
                        "    -fx-padding: 2px;\n" +
                        "    -fx-border-width: 2px;\n" +
                        "    -fx-font-family: \"Droid Sans Mono\";\n" +
                        "    -fx-font-weight: bold;");
        pane.setPrefSize(320/dimension, 320/dimension);
        return pane;
    }

    private Pane constructClickedPane(){
        Pane pane = new Pane();
        pane.setStyle(
                "-fx-background-color: #222222;\n" +
                        "    -fx-border-color: #222222 #222222 #222222 #222222;\n" +
                        "    -fx-border-style: solid;\n" +
                        "    -fx-padding: 2px;\n" +
                        "    -fx-border-width: 2px;\n" +
                        "    -fx-font-family: \"Droid Sans Mono\";\n" +
                        "    -fx-font-weight: bold;");
        pane.setPrefSize(320/dimension, 320/dimension);
        return pane;
    }

    public void changeOrangeStackOrder(){
        if(state.getOrangeArmy().boardTroops().isLeaderPlaced() && !state.getOrangeArmy().boardTroops().isPlacingGuards()){
            if(!state.getOrangeArmy().stack().isEmpty()){
                state.getOrangeArmy().stack().add(state.getOrangeArmy().stack().get(0));
                state.getOrangeArmy().stack().remove(0);
            }
        }
    }

    public void changeBlueStackOrder(){
        if(state.getBlueArmy().boardTroops().isLeaderPlaced() && !state.getBlueArmy().boardTroops().isPlacingGuards()){
            if(!state.getBlueArmy().stack().isEmpty()){
                state.getBlueArmy().stack().add(state.getBlueArmy().stack().get(0));
                state.getBlueArmy().stack().remove(0);
            }
        }
    }

    public void checkGameResult(Pane upperBar, Button homeButton, Button endButton, Label text){
        if(state.result() == GameResult.VICTORY){
            if(state.sideOnTurn() == PlayingSide.BLUE){
                upperBar.setStyle("-fx-background-color: #FF7F50;\n");
                homeButton.setStyle("-fx-background-color: #FF7F50;\n");
                endButton.setStyle("-fx-background-color: #FF7F50;\n");
            }
            else{
                upperBar.setStyle("-fx-background-color: #00BFFF;\n");
                homeButton.setStyle("-fx-background-color: #00BFFF;\n");
                endButton.setStyle("-fx-background-color: #00BFFF;\n");
            }
            text.setText("Victory!");
            return;
        }
        else if(state.result() == GameResult.DRAW){
            upperBar.setStyle("-fx-background-color: #779FA7;\n");
            homeButton.setStyle("-fx-background-color: #779FA7;\n");
            endButton.setStyle("-fx-background-color: #779FA7;\n");
            text.setText("Draw!");
            return;
        }
        text.setText(state.getBlueArmy().captured().size() + "|" + state.getOrangeArmy().captured().size());
    }

}
