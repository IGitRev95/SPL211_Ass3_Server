package bgu.spl.net.impl.rci;

public class CSCommand {
    private final short opcode;
    private final String[] args= new String[2];
    public CSCommand(short opcode){
        this.opcode=opcode;
    }
    public void SetArgument1(String arg1){
        args[0]=arg1;
    }
    public void SetArgument2(String arg2){
        args[1]=arg2;
    }
    public short getOpcode(){
        return opcode;
    }
    public String[] getArgs(){
        return args;
    }
}
