package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main (String[] args) {
        Database.getInstance(); //just fot initialize
        Server server = Server.reactor(Integer.parseInt(args[0]),Integer.parseInt(args[1]), () -> new MessagingProtocolUser(), () -> new EncoderDecoderBGU());
        server.serve();
    }
}
