package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main (String[] args) {
        //TODO:: change to arg0, arg1
        Database.getInstance(); //just fot initialize
        Server server = Server.reactor(/*Integer.parseInt(args[0])*/3,/*Integer.parseInt(args[1])*/7777, () -> new MessagingProtocolUser(), () -> new EncoderDecoderBGU());
        server.serve();
    }
}
