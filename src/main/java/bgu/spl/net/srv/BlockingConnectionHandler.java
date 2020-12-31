package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.BGUSERVER.EncoderDecoderBGU;
import bgu.spl.net.impl.BGUSERVER.MessagingProtocolStudent;
import bgu.spl.net.impl.rci.CSCommand;
import bgu.spl.net.impl.rci.TypeOfUser;
import bgu.spl.net.impl.rci.User;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {
// change protocol
    private final MessagingProtocolStudent protocol;
    private final EncoderDecoderBGU encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, EncoderDecoderBGU reader, MessagingProtocolStudent protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }
    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                CSCommand nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    CSCommand response = protocol.process(nextMessage);
                    if (response != null) {
                        byte[] responsebytes=encdec.encode(response);
                        out.write(responsebytes);
                        out.flush();
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
    public void send(T msg){
        //my implemention

    }
}
