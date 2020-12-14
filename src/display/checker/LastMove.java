package display.checker;

public class LastMove {
    CheckerType lastType;

    LastMove() {
        this.lastType = CheckerType.DARK;
    }

    public CheckerType getLastType() {
        return lastType;
    }

    public void setLastType(CheckerType lastType) {
        this.lastType = lastType;
    }
}
