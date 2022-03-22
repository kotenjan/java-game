package drake.thedrake;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements  JSONSerializable{

    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        this.playingSide = playingSide;
        this.troopMap = Collections.emptyMap();
        this.leaderPosition = TilePos.OFF_BOARD;
        this.guards = 0;
    }

    private BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        if(troopMap.get(pos) == null)
            return Optional.empty();
        return Optional.of(troopMap.get(pos));
    }

    public PlayingSide playingSide() {
        return playingSide;
    }

    public TilePos leaderPosition() {
        return leaderPosition;
    }

    public int guards() {
        return guards;
    }

    public boolean isLeaderPlaced() {
        return this.leaderPosition != TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        return isLeaderPlaced() && (guards < 2);
    }

    public Set<BoardPos> troopPositions() {
        return troopMap.keySet();
    }

    public BoardTroops placeTroop(Troop troop, BoardPos target) {
        if (troopPositions().contains(target)) {
            throw new IllegalArgumentException("");
        }
        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.put(target, new TroopTile(troop, playingSide, TroopFace.AVERS));
        return new BoardTroops(playingSide, newTroops, isLeaderPlaced() ? leaderPosition : target, isPlacingGuards() ? guards + 1 : guards);
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        if(isPlacingGuards() || !isLeaderPlaced()){
            throw new IllegalStateException();
        }

        if(at(origin).isEmpty() || at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);

        TroopTile originTile = newTroops.get(origin);
        newTroops.put(target, originTile.flipped());
        newTroops.remove(origin);

        if(leaderPosition.equals(origin)){
            return new BoardTroops(playingSide(), newTroops, target, guards);
        }

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    private void CheckTroopMovable(BoardPos pos){
        if(!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if(isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if(at(pos).isEmpty())
            throw new IllegalArgumentException();
    }

    public BoardTroops troopFlip(BoardPos origin) {

        CheckTroopMovable(origin);

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {

        CheckTroopMovable(target);

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.remove(target);
        if(leaderPosition.equals(target)){
            return new BoardTroops(playingSide(), newTroops, TilePos.OFF_BOARD, guards);
        }
        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    @Override
    public void toJSON(PrintWriter writer) {
            writer.print("\"boardTroops\":{");
            if (playingSide().equals(PlayingSide.BLUE)) {
                writer.print("\"side\":\"BLUE\",");
            } else {
                writer.print("\"side\":\"ORANGE\",");
            }
            if(leaderPosition().equals(TilePos.OFF_BOARD)){
                writer.print("\"leaderPosition\":\"off-board\",");
            }else{
                writer.print("\"leaderPosition\":\"" + ( (char)( leaderPosition.i() + 97 ) ) + ( leaderPosition.j() + 1 ) +"\",");
            }
            writer.print("\"guards\":" + guards() +",");
            writer.print("\"troopMap\":{");
            //print troopmap
            boolean[] first = {true};
            troopMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey((BoardPos a, BoardPos b)->
                    {
                        if(a.i() != b.i()){
                            return a.i() - b.i();
                        } else {
                            return a.j() - b.j();}
                    }))
                    .forEachOrdered(e->{
                        if(! first[0]) {
                            writer.print(",");
                        }
                        first[0] = false;

                        writer.print("\"" + ((char) (e.getKey().i() + 97)) + (e.getKey().j() + 1) + "\":{");
                        writer.print("\"troop\":\"" + e.getValue().troop().name() + "\",");
                        writer.print("\"side\":\"" + e.getValue().side() + "\",");
                        writer.print("\"face\":\"" + e.getValue().face() + "\"");
                        writer.print("}");
                    });

            writer.print("}");
            writer.print("},");
    }
}
