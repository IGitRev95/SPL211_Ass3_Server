package bgu.spl.net.impl.rci;

/**
 * this Exception is uniq to the Server
 * when thrown up, caught ,stopping the current process and return ERROR according to the opcode
 */

public class MyServerError extends Error{
    public MyServerError(String error){
        super(error);
    }
}
