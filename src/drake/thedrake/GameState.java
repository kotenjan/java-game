package drake.thedrake;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class GameState implements JSONSerializable {

    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    private Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    private Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(BoardPos pos) {
        if (blueArmy.boardTroops().at(pos).isPresent()) return blueArmy.boardTroops().at(pos).get();
        else if (orangeArmy.boardTroops().at(pos).isPresent()) return orangeArmy.boardTroops().at(pos).get();
        else return board.at(pos);
    }

    private boolean canStepFrom(TilePos origin) {
        if(!result.equals(GameResult.IN_PLAY)){
            return false;
        }
        if(origin.equals(TilePos.OFF_BOARD))
            return false;

        if(blueArmy.boardTroops().isPlacingGuards() || orangeArmy.boardTroops().isPlacingGuards()){
            return false;
        }
        if(!blueArmy.boardTroops().isLeaderPlaced() || !orangeArmy.boardTroops().isLeaderPlaced()){
            return false;
        }
        return armyOnTurn().boardTroops().at(origin).isPresent() && armyNotOnTurn().boardTroops().at(origin).isEmpty();
    }

    private boolean canStepTo(TilePos target) {
        if(!result.equals(GameResult.IN_PLAY)){
            return false;
        }
        if(target.equals(TilePos.OFF_BOARD))
            return false;
        if(!tileAt((BoardPos) target).canStepOn()){
            return false;
        }
        return armyOnTurn().boardTroops().at(target).isEmpty() && armyNotOnTurn().boardTroops().at(target).isEmpty();
    }

    private boolean canCaptureOn(TilePos target) {
        if(!result.equals(GameResult.IN_PLAY))
            return false;
        if(target.equals(TilePos.OFF_BOARD))
            return false;
        return armyNotOnTurn().boardTroops().at(target).isPresent();
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if(!result.equals(GameResult.IN_PLAY))
            return false;

        if(target.equals(TilePos.OFF_BOARD))
            return false;

        if(!board.at((BoardPos) target).canStepOn())
            return false;

        if(armyOnTurn().boardTroops().at(target).isPresent() || armyNotOnTurn().boardTroops().at(target).isPresent())
            return false;

        if(armyOnTurn().stack().isEmpty())
            return false;

        if(!armyOnTurn().boardTroops().isLeaderPlaced()){
            if(armyOnTurn().side()==PlayingSide.BLUE){
                return target.row() == 1;
            } else return target.row() == board.dimension();
        }else if(armyOnTurn().boardTroops().isPlacingGuards()){
            return target.isNextTo(armyOnTurn().boardTroops().leaderPosition());
        }

        return true;
    }

    GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    public Army getBlueArmy() {
        return blueArmy;
    }

    public Army getOrangeArmy() {
        return orangeArmy;
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{");
        writer.print("\"result\":\"" + result + "\",");
        writer.print("\"board\":{");
        board.toJSON(writer);
        writer.print("},");
        writer.print("\"blueArmy\":{");
        blueArmy.toJSON(writer);
        writer.print("},");
        writer.print("\"orangeArmy\":{");
        orangeArmy.toJSON(writer);
        writer.print("}");
        writer.print("}");
    }
}
