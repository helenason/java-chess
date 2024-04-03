import controller.GameManager;
import dao.BoardDao;
import dao.GameDao;
import dao.RealBoardDao;
import dao.RealGameDao;
import view.InputView;
import view.OutputView;

public class Application {

    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        GameDao gameDao = new RealGameDao();
        BoardDao boardDao = new RealBoardDao();
        GameManager gameManager = new GameManager(inputView, outputView, gameDao, boardDao);
        gameManager.start();
    }
}
