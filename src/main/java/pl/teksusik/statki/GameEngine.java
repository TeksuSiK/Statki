package pl.teksusik.statki;

public class GameEngine {
    private final Board board;
    private boolean myTurn;
    private GameState gameState;

    public GameEngine(Board board) {
        this.board = board;
        this.myTurn = false;
        this.gameState = GameState.SETUP;
    }

    public void setReady(boolean goFirst) {
        this.myTurn = goFirst;
        if (goFirst) {
            this.gameState = GameState.MY_TURN;
        } else {
            this.gameState = GameState.OPPONENT_TURN;
        }
    }

    public ShotResult processShot(int x, int y) {
        if (!myTurn) {
            throw new IllegalStateException("Not my turn");
        }
        myTurn = false;
        gameState = GameState.OPPONENT_TURN;
        return board.receiveShot(x, y);
    }

    public ShotResult applyOpponentShot(int x, int y) {
        ShotResult result = board.receiveShot(x, y);
        if (board.isGameOver()) {
            gameState = GameState.GAME_OVER;
        } else {
            myTurn = true;
            gameState = GameState.MY_TURN;
        }
        return result;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public GameState getGameState() {
        return gameState;
    }
}
