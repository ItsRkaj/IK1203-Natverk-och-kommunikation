package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    //Class variables
    public Integer BUFFERSIZE = 1024;
    public Integer timeout;
    public Integer limit;
    public boolean shutdown;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        
        this.timeout = timeout;
        this.shutdown = shutdown;
        this.limit = limit;

        if (limit != null){                          
            BUFFERSIZE = limit;
        } else {
            limit = 1024;
        }
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        //Create and Connect Socket
        Socket clientSocket = new Socket(hostname, port);

        //Send bytes to server
        clientSocket.getOutputStream().write(toServerBytes);

        //Create server buffer
        byte[] fromServerBuffer = new byte[BUFFERSIZE];
        
        //Create a ByteArrayOutputStream for dynamic storage
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            //Server timeout
            if(timeout != null){ clientSocket.setSoTimeout(this.timeout);}

            //Server shutdown
            if (this.shutdown){ clientSocket.shutdownOutput();}
                
            //Server limit
            if(limit != null){
                int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
                output.write(fromServerBuffer, 0, fromServerLength);
            } else {
                while(true){
                    int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
                    if (fromServerLength == -1) { break;}
                    output.write(fromServerBuffer, 0, fromServerLength);
                }
            }
        } catch (SocketTimeoutException e){}
        clientSocket.close();
        return output.toByteArray();
    }
}
