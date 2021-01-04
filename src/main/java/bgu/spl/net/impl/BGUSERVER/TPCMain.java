package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.Database;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args){
        //TODO: change to arg 0
        Database.getInstance(); //just fot initialize
        Server server= Server.threadPerClient(/*Integer.parseInt(args[0])*/7777,()-> new MessagingProtocolUser(),()-> new EncoderDecoderBGU());
        server.serve();
    }
}
