package drake.thedrake;

import java.util.*;

/**
 * třída bude představovat dlaždici, na které stojí jednotka
 */
public final class TroopTile implements Tile {

    private Troop troop;
    private PlayingSide side;
    private TroopFace face;

    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    // Vrací barvu, za kterou hraje jednotka na této dlaždici
    public PlayingSide side(){
        return side;
    }

    // Vrací stranu, na kterou je jednotka otočena
    public TroopFace face(){
        return face;
    }

    // Jednotka, která stojí na této dlaždici
    public Troop troop(){
        return troop;
    }

    // Vytvoří novou dlaždici, s jednotkou otočenou na opačnou stranu
    // (z rubu na líc nebo z líce na rub)
    public TroopTile flipped(){
        if(this.face==TroopFace.AVERS)
            return new TroopTile(troop, side, TroopFace.REVERS);
            else return new TroopTile(troop, side, TroopFace.AVERS);
    }

    // Vrací False, protože na dlaždici s jednotkou se nedá vstoupit
    @Override
    public boolean canStepOn() {
        return false;
    }

    // Vrací True
    @Override
    public boolean hasTroop() {
        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<Move> list = new ArrayList<>();
        for (TroopAction action:troop.actions(face)) {
            list.addAll(action.movesFrom(pos,side, state));
        }
        return list;
    }


}
