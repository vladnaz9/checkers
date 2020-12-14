package display.checker;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

import static display.checker.CheckersApp.TILE_SIZE;

/////////////////////////////////////
public class Checker extends StackPane {

    private CheckerType type;
    private boolean isKing = false;

    private double mouseX;
    private double mouseY;
    private double oldX;
    private double oldY;

    public boolean isKing() {
        return isKing;
    }

    public void setKing() {
        this.isKing = false; //true (
    }

    public CheckerType getType() {
        return type;
    }

    public Checker(CheckerType type, int x, int y, Boolean isKing) {
        this.type = type;
        this.isKing = isKing;
        move(x, y);
        createChecker(type);
        workWithMouse(); // onClick
    }


    public void workWithMouse() {
        setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        setOnMouseDragged(event -> {
            relocate(event.getSceneX() - mouseX + oldX,
                    event.getSceneY() - mouseY + oldY);
        });
    }

    public void createChecker(CheckerType type) {
        Circle circle = new Circle(TILE_SIZE * 0.3125);
        circle.setFill(type == CheckerType.DARK ? Color.valueOf("#262525") : Color.WHITE);
        circle.setStroke(type == CheckerType.DARK ? Color.BLACK : Color.valueOf("#c4c0c0"));
        circle.setStrokeWidth(TILE_SIZE * 0.03);

        circle.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        circle.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);

        getChildren().addAll(circle);
    }



    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }

}

