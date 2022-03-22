package drake.thedrake;

import java.io.PrintWriter;

public final class Board implements JSONSerializable {

	private final BoardTile[][] board;
	private final int dimension;

	//nastaví se rozměry hrací plochy a nastaví je na prázdné
	public Board(int dimension) {
		this.dimension = dimension;
		board = new BoardTile[dimension][dimension];

		for (int i = 0; i < dimension; i++){
			for (int j = 0; j < dimension; j++){
				board[i][j] = BoardTile.EMPTY;
			}
		}
	}

	public int dimension() {
		return dimension;
	}

	public BoardTile at(BoardPos pos) {
		return board[pos.i()][pos.j()];
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(Board.TileAt ...ats) {
		Board newBoard = new Board(dimension);

		for (int i = 0; i < dimension; i++){
			System.arraycopy(board[i], 0, newBoard.board[i], 0, dimension);
		}

		for (TileAt s : ats) {
			newBoard.board[s.pos.i()][s.pos.j()] = s.tile;
		}
		return newBoard;
	}

	public PositionFactory positionFactory() {
		return new PositionFactory(dimension);
	}

	public static class TileAt {
		public final BoardPos pos;
		final BoardTile tile;

		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print("\"dimension\":" + dimension() + ",\"tiles\":[");
		boolean first = true;
		for(int i = 0; i < dimension(); ++i){
			for(int j = 0; j < dimension(); ++j){
				if(first) {
					first = false;
				}else {
					writer.print(",");
				}
				if(board[j][i].equals(BoardTile.EMPTY)){
					writer.print("\"empty\"");
				}else if(board[j][i].equals(BoardTile.MOUNTAIN)){
					writer.print("\"mountain\"");
				}
			}
		}
		writer.print("]");
	}

}

