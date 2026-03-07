//stores an undoable action with its type and value
public class Action {
    private String type;   // "EDU" or "EXP"
    private String value;  // the actual text
    
    //create an action with type and value
    public Action(String type, String value) {
        this.type = type;
        this.value = value;
    }
    
    //getters
    public String getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
}
