package display.checker;

public class MoveResult {

    private MoveType type;
    private Checker checker;


    public MoveResult(MoveType type, Checker checker) {
        this.type = type;
        this.checker = checker;
    }

    public MoveResult(MoveType type) {
        this(type,null);
    }

    public MoveType getType() {
        return type;
    }

    public Checker getChecker() {
        return checker;
    }

}
