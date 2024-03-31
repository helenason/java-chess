package domain;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A2;
import static domain.piece.PositionFixture.A3;
import static domain.piece.PositionFixture.A4;
import static domain.piece.PositionFixture.A5;
import static domain.piece.PositionFixture.A6;
import static domain.piece.PositionFixture.A7;
import static domain.piece.PositionFixture.B2;
import static domain.piece.PositionFixture.B4;
import static domain.piece.PositionFixture.C2;
import static domain.piece.PositionFixture.C4;
import static domain.piece.PositionFixture.C5;
import static domain.piece.PositionFixture.C7;
import static domain.piece.PositionFixture.D1;
import static domain.piece.PositionFixture.D4;
import static domain.piece.PositionFixture.D8;
import static domain.piece.PositionFixture.E1;
import static domain.piece.PositionFixture.E2;
import static domain.piece.PositionFixture.E3;
import static domain.piece.PositionFixture.E5;
import static domain.piece.PositionFixture.E7;
import static domain.piece.PositionFixture.E8;
import static domain.piece.PositionFixture.F2;
import static domain.piece.PositionFixture.F3;
import static domain.piece.PositionFixture.F6;
import static domain.piece.PositionFixture.F7;
import static domain.piece.PositionFixture.G2;
import static domain.piece.PositionFixture.G4;
import static domain.piece.PositionFixture.G8;
import static domain.piece.PositionFixture.H2;
import static domain.piece.PositionFixture.H3;
import static domain.piece.PositionFixture.H4;
import static domain.piece.PositionFixture.H5;
import static domain.piece.PositionFixture.H6;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dao.BoardDao;
import dao.GameDao;
import domain.piece.Color;
import domain.result.ChessResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessTest {

    private Chess chess;

    @BeforeEach
    void setUp() {
        BoardDao boardDao = new BoardDao();
        boardDao.deleteAll();
        GameDao gameDao = new GameDao();
        gameDao.deleteAll();
        chess = new Chess();
    }

    @Test
    @DisplayName("출발지와 도착지가 같은 경우 에러를 반환한다.")
    void tryMove_SamePosition() {
        assertThatThrownBy(() -> chess.tryMove(A1, A1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 제자리에 있을 수 없습니다.");
    }

    @Test
    @DisplayName("이동 경로에 다른 기물이 있는 경우 에러를 반환한다.")
    void tryMove_Blocked() {
        assertThatThrownBy(() -> chess.tryMove(A1, A3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 다른 기물에 막혀 이동하지 못했습니다.");
    }

    @Test
    @DisplayName("출발지에 기물이 없는 경우 에러를 반환한다.")
    void tryMove_EmptySource() {
        assertThatThrownBy(() -> chess.tryMove(A4, A3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 출발지에 기물이 없어 이동하지 못했습니다.");
    }

    @Test
    @DisplayName("차례가 아닌 경우 에러를 반환한다.")
    void tryMove_NotTurn() {
        assertThatThrownBy(() -> chess.tryMove(A7, A6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 차례가 아니므로 이동하지 못했습니다.");
    }

    @Test
    @DisplayName("도착지에 같은 편 기물이 있는 경우 에러를 반환한다.")
    void tryMove_SameTeamTarget() {
        assertThatThrownBy(() -> chess.tryMove(A1, A2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 도착지에 같은 편 기물이 있어 이동하지 못했습니다.");
    }

    /*
        ........
        ........
        ........
        ........
        .p.*....
        ........
        ........
        r.......
     */
    @Test
    @DisplayName("기물의 이동 전술에 해당하지 않는 경우 에러를 반환한다.")
    void tryMove_DifferentTactic() {
        chess.tryMove(B2, B4);
        chess.tryMove(A7, A6);

        assertThatThrownBy(() -> chess.tryMove(A1, D4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 해당 기물이 움직일 수 없는 방식입니다.");
    }

    /*
        ........
        ........
        ........
        P.......
        p.......
        ........
        ........
        ........
     */
    @Test
    @DisplayName("공격이 아닌 이동 시 도착지에 기물이 있는 경우 에러를 반환한다.")
    void tryMove_FilledTarget() {
        chess.tryMove(A2, A4);
        chess.tryMove(A7, A5);

        assertThatThrownBy(() -> chess.tryMove(A4, A5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 도착지에 기물이 있어 움직일 수 없습니다.");
    }

    @Test
    @DisplayName("보드에 화이트 킹이 없는 경우 블랙이 승리한다.")
    void judge_NoWhiteKing_BlackWin() {
        chess.tryMove(F2, F3);
        chess.tryMove(E7, E5);
        chess.tryMove(G2, G4);
        chess.tryMove(D8, H4);
        chess.tryMove(H2, H3);
        chess.tryMove(H4, E1);

        ChessResult result = chess.judge();

        Color winner = result.findWinner();
        assertThat(winner).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("보드에 블랙 킹이 없는 경우 화이트가 승리한다.")
    void judge_NoBlackKing_WhiteWin() {
        chess.tryMove(E2, E3);
        chess.tryMove(F7, F6);
        chess.tryMove(D1, H5);
        chess.tryMove(G8, H6);
        chess.tryMove(H5, E8);

        ChessResult result = chess.judge();

        Color winner = result.findWinner();
        assertThat(winner).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("보드에 킹이 모두 있고 화이트 진영 점수가 높다면 화이트가 승리한다.")
    void judge_WhiteScoreIsHigher_WhiteWin() {
        chess.tryMove(C2, C4);
        chess.tryMove(C7, C5);
        chess.tryMove(D1, A4);
        chess.tryMove(D8, A5);
        chess.tryMove(A4, A5);

        ChessResult result = chess.judge();

        Color winner = result.findWinner();
        assertThat(winner).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("보드에 킹이 모두 있고 블랙 진영 점수가 높다면 블랙이 승리한다.")
    void judge_BlackScoreIsHigher_BlackWin() {
        chess.tryMove(C2, C4);
        chess.tryMove(C7, C5);
        chess.tryMove(D1, A4);
        chess.tryMove(D8, A5);
        chess.tryMove(H2, H3);
        chess.tryMove(A5, A4);

        ChessResult result = chess.judge();

        Color winner = result.findWinner();
        assertThat(winner).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("보드에 킹이 모두 있고 화이트와 블랙 진영의 점수가 같을 경우 무승부이다.")
    void judge_SameScore_Draw() {
        ChessResult result = chess.judge();

        Color winner = result.findWinner();
        assertThat(winner).isEqualTo(Color.NONE);
    }
}
