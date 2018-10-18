package sranges.tree;

public class NodeSymmetryException extends RuntimeException {
    public NodeSymmetryException(String message){
        super(message);
    }

    public String toString(){
        return "Error when attempting to access node symmetry: " + super.toString();
    }
}
