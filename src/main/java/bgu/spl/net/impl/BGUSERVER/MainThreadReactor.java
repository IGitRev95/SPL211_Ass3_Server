package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.srv.Server;

public class MainThreadReactor {
    public static void main (String[] args) {
        Server server = Server.reactor(Integer.parseInt(args[0]),Integer.parseInt(args[1]), () -> new MessagingProtocolStudent(), () -> new EncoderDecoderBGU());
        server.serve();
    }
}
