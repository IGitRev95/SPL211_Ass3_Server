package bgu.spl.net.impl.rci;

public class ErrorServer {
    public int opcodeError;
    public ErrorServer(int opcodeError){
        this.opcodeError=opcodeError;
    }
    public String toString(){
        return "Error"+opcodeError;
    }
}
