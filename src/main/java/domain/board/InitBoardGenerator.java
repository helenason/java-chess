package domain.board;

import dao.BoardDao;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class InitBoardGenerator implements BoardGenerator {

    private final BoardDao boardDao;

    public InitBoardGenerator() {
        this.boardDao = new BoardDao();
    }

    @Override
    public void generate() {
        initPiece(PiecePosition.PAWN, Pawn::new);
        initPiece(PiecePosition.ROOK, Rook::new);
        initPiece(PiecePosition.KNIGHT, Knight::new);
        initPiece(PiecePosition.BISHOP, Bishop::new);
        initPiece(PiecePosition.QUEEN, Queen::new);
        initPiece(PiecePosition.KING, King::new);
        initPiece(PiecePosition.NONE, None::new);
    }

    private void initPiece(PiecePosition piecePosition, Function<Color, Piece> makePiece) {
        List<Position> initPositions = piecePosition.initPositions();
        for (Position initPosition : initPositions) {
            Color color = ColorPosition.asColor(initPosition);
            Piece piece = makePiece.apply(color);
            saveData(initPosition, piece);
        }
    }

    private void saveData(Position position, Piece piece) {
        boardDao.save(position, piece);
    }

    private enum PiecePosition {

        PAWN(List.of(File.values()), List.of(Rank.TWO, Rank.SEVEN)),
        ROOK(List.of(File.A, File.H), List.of(Rank.ONE, Rank.EIGHT)),
        KNIGHT(List.of(File.B, File.G), List.of(Rank.ONE, Rank.EIGHT)),
        BISHOP(List.of(File.C, File.F), List.of(Rank.ONE, Rank.EIGHT)),
        QUEEN(List.of(File.D), List.of(Rank.ONE, Rank.EIGHT)),
        KING(List.of(File.E), List.of(Rank.ONE, Rank.EIGHT)),
        NONE(List.of(File.values()), List.of(Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX)),
        ;

        private final List<File> files;
        private final List<Rank> ranks;

        PiecePosition(List<File> files, List<Rank> ranks) {
            this.files = files;
            this.ranks = ranks;
        }

        private List<Position> initPositions() {
            return this.files.stream()
                    .flatMap(file -> ranks.stream().map(rank -> PositionGenerator.generate(file, rank)))
                    .toList();
        }
    }

    private enum ColorPosition {

        WHITE(Color.WHITE, List.of(Rank.ONE, Rank.TWO)),
        BLACK(Color.BLACK, List.of(Rank.SEVEN, Rank.EIGHT)),
        NONE(Color.NONE, List.of(Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX)),
        ;

        private final Color color;
        private final List<Rank> ranks;

        ColorPosition(Color color, List<Rank> ranks) {
            this.color = color;
            this.ranks = ranks;
        }

        private static Color asColor(Position position) {
            return Arrays.stream(values())
                    .filter(colorPosition -> position.hasRank(colorPosition.ranks))
                    .findFirst()
                    .orElse(ColorPosition.NONE)
                    .color;
        }
    }
}
