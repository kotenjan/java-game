package drake.thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction {

    public SlideAction(Offset2D offset) {
        super(offset);
    }

    SlideAction(int offsetX, int offsetY) {
        super(offsetX, offsetY);
    }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();
        TilePos target = origin.stepByPlayingSide(offset(), side);


        while( state.canStep(origin, target) || state.canCapture(origin, target) ) {
            if(state.canStep(origin, target) ) {
                result.add(new StepOnly(origin, (BoardPos) target));
                target = target.stepByPlayingSide(offset(), side);
            }
            else if (state.canCapture(origin, target)){
                result.add(new StepAndCapture(origin, (BoardPos) target));
                break;
            }
        }
        return result;
    }
}
