package display.checker;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CheckersApp<T> extends Application {

    public static final int TILE_SIZE = 80;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Tile[][] board = new Tile[WIDTH][HEIGHT];

    private Group tileGroup = new Group();
    private Group checkGroup = new Group();
    private LastMove lastMove = new LastMove();

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, checkGroup);


        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;


                tileGroup.getChildren().add(tile);
                Checker checker = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    checker = makeChecker(CheckerType.DARK, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    checker = makeChecker(CheckerType.LIGHT, x, y);
                }

                if (checker != null) {
                    tile.setChecker(checker);
                    checkGroup.getChildren().add(checker);
                }

            }
        }
        return root;
    }

    private ArrayList<Checker> shouldKill(CheckerType type) {
        Checker checker;
        ArrayList<Checker> checkersList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].hasChecker()) {
                    checker = board[i][j].getChecker();
                    if (checker.getType() == type && canKill(checker)) {
                        checkersList.add(checker);
                    }
                }

            }
        }
        return checkersList;
    }

    private boolean canKill(Checker checker) {
        int x0 = toBoard(checker.getOldX());
        int y0 = toBoard(checker.getOldY());

        if (checker.isKing())
            for (int j = 0; j < HEIGHT; j++) {
                int x1;
                int y1;
                if (x0 + j <= WIDTH && y0 + j <= WIDTH && board[x0 + j][y0 + j].hasChecker()) {
                    return true;

                }
                if (x0 - j >= 0 && y0 + j <= WIDTH && board[x0 - j][y0 + j].hasChecker()) {
                        return true;
                }
                if (x0 + j >= 0 && y0 - j <= WIDTH && board[x0 + j][y0 - j].hasChecker()) {
                    return true;
                }
                if (x0 - j >= 0 && y0 - j <= WIDTH && board[x0 - j][y0 + j].hasChecker()) {
                    return true;
                }
            }
        else
            for (int i = -2; i <= 2; i += 4) {
                int x1 = x0 + i / 2;
                for (int j = -2; j <= 2; j += 4) {
                    int y1 = y0 + j / 2;
                    if (x0 + i >= 0 && x0 + i < 8 && y0 + j >= 0 && y0 + j < 8)
                        if (board[x1][y1].hasChecker() && board[x1][y1].getChecker().getType() != checker.getType() && !board[x0 + i][y0 + j].hasChecker()) {
                            return true;
                        }
                }
            }
        return false;
    }

    private boolean endOfGame(CheckerType type) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].hasChecker() && board[i][j].getChecker().getType() != type) {
                    return false;
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        String winner;
        if (type == CheckerType.DARK) {
            winner = "DARK";
        } else winner = "LIGHT";
        alert.setContentText("END OF GAME, " + winner + " WON!");
        alert.showAndWait();
        return true;
    }

    private MoveResult tryMove(Checker checker, int newX, int newY, LastMove lastMove) {
        if (board[newX][newY].hasChecker() || (newX + newY) % 2 == 0 || lastMove.getLastType() == checker.getType()) {
            return new MoveResult(MoveType.NONE);
        }

        ArrayList<Checker> shouldKill = shouldKill(checker.getType());

        int x0 = toBoard(checker.getOldX());
        int y0 = toBoard(checker.getOldY());

        if (checker.isKing()) {
            System.out.println("isKing checked");
            boolean flag = false;
            for (int i = -7; i < 7; i++) {
                if (checker.getOldX() + i == newX && checker.getOldY() + i == newY) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                if (shouldKill.isEmpty()) {
                    System.out.println("isEmty");
                    lastMove.setLastType(checker.getType());
                    return new MoveResult(MoveType.NORMAL);
                } else if (shouldKill.contains(checker)) {
                    int x1 = 0;
                    int y1 = 0;
                    switch (checkVector(newX, newY, x0, y0)) {
                        case 1:
                            x1 = newX + 1;
                            y1 = newY + 1;
                            break;
                        case 2:
                            x1 = newX - 1;
                            y1 = newY + 1;
                            break;
                        case 3:
                            x1 = newX - 1;
                            y1 = newY - 1;
                            break;
                        case 4:
                            x1 = newX + 1;
                            y1 = newY - 1;
                            break;
                    }
                    if (board[x1][y1].hasChecker() && board[x1][y1].getChecker().getType() != checker.getType()) {
                        lastMove.setLastType(checker.getType());
                        return new MoveResult(MoveType.KILL, board[x1][y1].getChecker());
                    }
                }
            }
        } else if (shouldKill.isEmpty() && Math.abs(newX - x0) == 1 && newY - y0 == checker.getType().moveDir) { //если обычная пешка
            lastMove.setLastType(checker.getType());
            return new MoveResult(MoveType.NORMAL);

        } else if (Math.abs(newX - x0) == 2) {
            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (shouldKill.contains(checker) && board[x1][y1].hasChecker() && board[x1][y1].getChecker().getType() != checker.getType()) {
                lastMove.setLastType(checker.getType());
                return new MoveResult(MoveType.KILL, board[x1][y1].getChecker());
            }
        }
        System.out.println("вы должны есть или ходите не правильно, попробуйте еще раз");
        return new MoveResult(MoveType.NONE);
    }

    private boolean wasCrowned(Checker checker) {
        CheckerType type = checker.getType();
        if (type == CheckerType.DARK && checker.getOldY() == 7) {
            return true;
        } else if (type == CheckerType.LIGHT && checker.getOldY() == 0) {
            return true;
        } else return false;
    }

    private int toBoard(double pixel) {     //переводит координаты поля в пиксели
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("checkersApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int checkVector(int newX, int newY, int oldX, int oldY) {
        if (newX > oldX) {
            if (newY > oldY) {
                return 1;
            } else return 2;
        } else if (newY < oldY) {
            return 3;
        } else return 4;
    }

    private Checker makeChecker(CheckerType type, int x, int y) {
        Checker checker = new Checker(type, x, y, false);

        checker.setOnMouseReleased(event -> {
            int newX = toBoard(checker.getLayoutX());
            int newY = toBoard(checker.getLayoutY());

            MoveResult result;


            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(checker, newX, newY, lastMove);
            }

            int x0 = toBoard(checker.getOldX());
            int y0 = toBoard(checker.getOldY());

            switch (result.getType()) {
                case NONE:
                    checker.abortMove();
                    break;
                case NORMAL:
                    checker.move(newX, newY);
                    board[x0][y0].setChecker(null);
                    board[newX][newY].setChecker(checker);
                    if (wasCrowned(checker)) {
                        checker.setKing();
                        System.out.println("NOW KING");
                    }
                    break;
                case KILL:

                    checker.move(newX, newY);
                    board[x0][y0].setChecker(null);
                    board[newX][newY].setChecker(checker);

                    Checker opponentChecker = result.getChecker();
                    board[toBoard(opponentChecker.getOldX())][toBoard(opponentChecker.getOldY())].setChecker(null);
                    checkGroup.getChildren().remove(opponentChecker);

                    boolean end = endOfGame(checker.getType());
                    if (end) {
                        System.out.println("END");
                    }
                    if (wasCrowned(checker)) {
                        checker.setKing();
                        System.out.println("NOW KING");
                    }
                    if (canKill(checker)) {
                        if (lastMove.getLastType() == CheckerType.DARK) {
                            lastMove.setLastType(CheckerType.LIGHT);
                        } else lastMove.setLastType(CheckerType.DARK);
                    }

                    break;
            }

        });
        return checker;
    }

    public static void main(String[] args) {
        launch(args);
    }


}

