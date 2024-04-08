package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dao.BoardDao;
import dao.GameDao;
import dao.fake.FakeBoardDao;
import dao.fake.FakeGameDao;
import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GameServiceTest {

    private GameDao gameDao;
    private BoardDao boardDao;

    @BeforeEach
    void setUp() {
        gameDao = new FakeGameDao();
        boardDao = new FakeBoardDao();
    }

    @Test
    @DisplayName("데이터베이스에서 게임방 전체를 찾아 반환한다.")
    void findRooms() {
        int game1 = gameDao.save(new Turn(Color.WHITE));
        int game2 = gameDao.save(new Turn(Color.BLACK));
        int game3 = gameDao.save(new Turn(Color.NONE));
        GameService gameService = new GameService(gameDao, boardDao);

        Set<Integer> rooms = gameService.findRooms();

        assertThat(rooms).containsOnly(game1, game2, game3);
    }

    @Test
    @DisplayName("하나의 게임을 생성해 데이터베이스에 저장한다.")
    void createGame() {
        GameService gameService = new GameService(gameDao, boardDao);

        int gameId = gameService.createGame();

        assertThat(gameId).isEqualTo(1);
    }

    @Test
    @DisplayName("새로운 체스판을 생성해 데이터베이스에 저장한다.")
    void createBoard() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = gameService.createGame();

        Board board = gameService.createBoard(gameId);

        Map<Position, Piece> squares = boardDao.findSquaresByGame(gameId).squares();
        assertThat(squares).hasSize(64);
        assertThat(squares).isEqualTo(board.getSquares());
    }

    @Test
    @DisplayName("데이터베이스에서 특정 게임에 해당하는 체스판을 조회하여 반환한다.")
    void findBoard() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = gameService.createGame();
        gameService.createBoard(gameId);

        Board board = gameService.findBoard(gameId);

        assertThat(board.getSquares()).hasSize(64);
    }

    @Test
    @DisplayName("데이터베이스에서 특정 게임에 해당하는 체스판이 없는 경우 빈 체스판을 반환한다.")
    void findBoard_Empty() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = gameService.createGame();

        Board board = gameService.findBoard(gameId);

        assertThat(board.getSquares()).hasSize(0);
    }

    @Test
    @DisplayName("해당 게임의 차례를 데이터베이스에서 찾아 반환한다.")
    void findTurn() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = gameService.createGame();

        Turn turn = gameService.findTurn(gameId);

        assertThat(turn.isWhite()).isTrue();
    }

    @Test
    @DisplayName("해당 게임의 차례를 데이터베이스에서 찾지 못할 경우 에러를 반환한다.")
    void findTurn_Empty() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = 0;

        assertThatThrownBy(() -> gameService.findTurn(gameId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 유효하지 않은 차례입니다.");
    }

    @Test
    @DisplayName("Board 객체의 기물을 이동시키고, 데이터베이스의 값을 수정한다.")
    void updateMovement() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = gameService.createGame();
        Board board = gameService.createBoard(gameId);
        Position source = PositionGenerator.generate(File.A, Rank.TWO);
        Position target = PositionGenerator.generate(File.A, Rank.FOUR);
        gameService.initChess(board, new Turn(Color.WHITE));

        gameService.updateMovement(gameId, source, target);

        assertThat(board.findPieceByPosition(source)).isEqualTo(new None(Color.NONE));
        assertThat(board.findPieceByPosition(target)).isEqualTo(new Pawn(Color.WHITE));
    }

    @Test
    @DisplayName("객체와 데이터베이스 내 데이터를 초기화한다.")
    void resetGame() {
        GameService gameService = new GameService(gameDao, boardDao);
        int gameId = gameService.createGame();
        Board board = gameService.createBoard(gameId);
        gameService.initChess(board, new Turn(Color.WHITE));

        gameService.resetGame(gameId);

//        assertThat(board.getSquares()).hasSize(0);
        assertThat(boardDao.findSquaresByGame(gameId).squares()).hasSize(0);
        assertThat(gameDao.findTurnById(gameId)).isEqualTo(Optional.empty());
    }
}
