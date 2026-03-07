import java.util.*;

//A custom stack that is used to store actions in InfoAdder so that the user can undo their recent actions
public class UndoStack {

    //stores undo actions in stack order
    private ArrayList<Action> elements;

    //create empty undo stack
    public UndoStack() {
        elements = new ArrayList<Action>();
    }

    //push new action to top of stack
    public void push(String type, String value) {
        if (type == null || value == null) {
            throw new IllegalArgumentException("Cannot push null item.");
        }
        elements.add(new Action(type, value));
    }

    //pop most recent action from stack
    public Action pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot pop from an empty stack.");
        }
        return elements.remove(elements.size() - 1);
    }

    //get the top of the stack 
    public Action top() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot read top of an empty stack.");
        }
        return elements.get(elements.size() - 1);
    }

    //check if the stack is empty
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    //get number of actions in stack
    public int size() {
        return elements.size();
    }
}
