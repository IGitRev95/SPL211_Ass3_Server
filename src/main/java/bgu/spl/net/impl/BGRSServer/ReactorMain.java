package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main (String[] args) {
        Database.getInstance(); //just fot initialize
        Server server = Server.reactor(Integer.parseInt(args[1]),Integer.parseInt(args[0]), () -> new MessagingProtocolUser(), () -> new EncoderDecoderBGU());
        server.serve();
    }
}
