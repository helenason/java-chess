package data;

import java.util.Objects;

public class BoardData {

    private final int fileColumn;
    private final int rankRow;
    private final String pieceType;
    private final String pieceColor;

    public BoardData(int fileColumn, int rankRow, String pieceType, String pieceColor) {
        this.fileColumn = fileColumn;
        this.rankRow = rankRow;
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
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
        return fileColumn == boardData.fileColumn && rankRow == boardData.rankRow && Objects.equals(pieceType,
                boardData.pieceType) && Objects.equals(pieceColor, boardData.pieceColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileColumn, rankRow, pieceType, pieceColor);
    }
}
