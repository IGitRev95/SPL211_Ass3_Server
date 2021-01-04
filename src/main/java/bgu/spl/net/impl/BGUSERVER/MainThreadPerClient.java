package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.Database;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Server;

public class MainThreadPerClient {
    public static void main(String[] args){
        Server server= Server.threadPerClient(Integer.parseInt(args[0])/*7777*/,()-> new MessagingProtocolStudent(),()-> new EncoderDecoderBGU());
        server.serve();
    }
}
