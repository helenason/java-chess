package data;

import java.util.Objects;

public class BoardData {

    private final int fileColumn;
    private final int rankRow;
    private final String piece;

    public BoardData(int fileColumn, int rankRow, String piece) {
        this.fileColumn = fileColumn;
        this.rankRow = rankRow;
        this.piece = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoardData boardData = (BoardData) o;
        return fileColumn == boardData.fileColumn && rankRow == boardData.rankRow && Objects.equals(piece,
                boardData.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileColumn, rankRow, piece);
    }
}
