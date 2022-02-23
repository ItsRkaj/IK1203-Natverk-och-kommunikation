package tcpclient;
import java.net.*;

import javax.sound.sampled.SourceDataLine;

import java.io.*;

public class TCPClient {

    public TCPClient() {}

    private static int BUFFERSIZE=1024;

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        //Create server buffer
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        //Create and Connect Socket
        Socket clientSocket = new Socket(hostname, port);

        //Send bytes to server
        clientSocket.getOutputStream().write(toServerBytes);

        //Create a ByteArrayOutputStream for dynamic storage
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        while(true){
            int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
            if (fromServerLength == -1) {
                break;
            }
            output.write(fromServerBuffer, 0, fromServerLength);
        }

        clientSocket.close();
        return output.toByteArray();
    }
}
