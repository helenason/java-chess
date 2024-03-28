package dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardDaoTest {

    @Test
    @DisplayName("데이터베이스 연결에 성공한다.")
    void getConnection_Success() {
        BoardDao boardDao = new BoardDao();
        assertThat(boardDao.getConnection()).isNotNull();
    }
}
