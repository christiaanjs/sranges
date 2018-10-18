package sranges.tree;

public class StratigraphicRangeException extends RuntimeException {
    public StratigraphicRangeException(String message){
        super(message);
    }

    public String toString(){
        return "Error initialising stratigraphic ranges: " + super.toString();
    }
}
