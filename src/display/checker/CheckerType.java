package display.checker;

public enum CheckerType {
    DARK(1), LIGHT(-1);

    final int moveDir;

    CheckerType(int moveDir) {
        this.moveDir = moveDir;
    }


}
