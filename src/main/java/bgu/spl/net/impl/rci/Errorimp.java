package bgu.spl.net.impl.rci;

public class Errorimp extends CSCommand{
    public Errorimp(short opcode){
        super(opcode);
        SetArgument1("ERROR "+opcode);
    }

}
