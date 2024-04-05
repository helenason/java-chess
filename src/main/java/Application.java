import controller.GameManager;
import service.GameService;
import view.InputView;
import view.OutputView;

public class Application {

    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        GameService gameService = new GameService();
        GameManager gameManager = new GameManager(inputView, outputView, gameService);
        gameManager.start();
    }
}
