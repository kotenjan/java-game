package drake.thedrake;

public abstract class BoardMove extends Move {
	private final BoardPos origin;

	BoardMove(BoardPos origin, BoardPos target) {
		super(target);
		this.origin = origin;
	}

	public BoardPos origin() {
		return origin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardMove other = (BoardMove) obj;
		if (origin == null) {
			return other.origin == null;
		} else return origin.equals(other.origin);
	}
}
