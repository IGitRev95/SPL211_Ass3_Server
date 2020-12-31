package bgu.spl.net.impl.rci;

public class ACKimp extends CSCommand{
    public ACKimp(short opcode){
        super(opcode);
        SetArgument1("ACK "+opcode);
    }
}
