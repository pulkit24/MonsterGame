package monsterRun.common.model;

import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = 6558317648399035725L;

	private int row;
	private int column;

	public Position(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return row + "," + column;
	}

	public boolean equals(Position pos) {
		return ((pos.getRow() == getRow()) && (pos.getColumn() == getColumn()));
	}
}
