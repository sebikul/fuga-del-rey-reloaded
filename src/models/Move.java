package models;

public class Move {

	private final Point destination;

	private boolean choosen = false;
	private boolean pruned = false;

	private final Point origin;

	public Move(Point origin, Point destination) {
		this.origin = origin;
		this.destination = destination;
	}

	public Point getDestination() {
		return destination;
	}

	public boolean isPruned() {
		return pruned;
	}

	public void setPruned(boolean pruned) {
		this.pruned = pruned;
	}

	public Point getOrigin() {
		return origin;
	}

	public boolean isChoosen() {
		return choosen;
	}

	public void setChoosen(boolean choosen) {
		this.choosen = choosen;
	}

	@Override
	public String toString() {
		return "" + origin + destination;
	}

}
