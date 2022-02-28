import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

public class ConcHTTPAsk {
    public static void main( String[] args) throws IOException {

        //Server socket
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        
        //Buffersize
        int BUFFERSIZE = 1024;

        while(true){
            //Client socket
            Socket clientSocket = serverSocket.accept();
            
            //Outputstream
            OutputStream out = clientSocket.getOutputStream();
            
            //Create server buffer
            byte[] fromServerBuffer = new byte[BUFFERSIZE];

            //Create a ByteArrayOutputStream for dynamic storage
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            //Save input to output
            int fromServerLength;
            do{
                fromServerLength = clientSocket.getInputStream().read(fromServerBuffer, 0, BUFFERSIZE);
                output.write(fromServerBuffer, 0, fromServerLength);
            } while(fromServerLength == BUFFERSIZE);

            //Sparar GET strängen (Första raden)
            String s = output.toString();
            System.out.println(s);

            //Splitar strängen och sparar i array
            String[] params = s.split("[? &=/]");

            //Hanterar GET request
            //Variabler till TCPClient
            boolean shutdown = false;               // True if client should shutdown connection
            Integer timeout = null;			        // Max time to wait for data from server (null if no limit)
            Integer limit = null;			        // Max no. of bytes to receive from server (null if no limit)
            String hostname = null;			        // Domain name of server
            int port = 0;					        // Server port number
            byte[] userInputBytes = new byte[0];    // Data to send to server
            Boolean ask = false;                    // If ask
            Boolean get = false;                    // If get
            Boolean http = false;                   // If http
            

            //Binder värden till variabler
            for (int i = 0; i < params.length; i++) {
                switch(params[i]){
                    case "hostname":
                        hostname = params[++i];
                        break;
                    case "string":
                        userInputBytes = params[++i].getBytes("UTF-8");
                        break;
                    case "shutdown":
                        shutdown = Boolean.parseBoolean(params[++i]);
                        break;
                    case "limit":
                        limit = Integer.parseInt(params[++i]);
                        break;
                    case "timeout":
                        timeout = Integer.parseInt(params[++i]);
                        break;
                    case "port":
                        port = Integer.parseInt(params[++i]);
                        break;
                    case "ask":
                        ask = true;
                        break;
                    case "GET":
                        get = true;
                        break;
                    case "HTTP":
                        http = true;
                        break;
                }
            }

            //Hanterar olika utfall
            try{
                if(ask){
                    if(hostname != null && port != 0 && get && http){
                        //Constructs TCPClient
                        TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
                        
                        //Get bytes from askServer
                        byte[] fromClient = tcpClient.askServer(hostname, port, userInputBytes);
                        
                        //Browser
                        //Head
                        out.write("HTTP/1.1 200 OK\r\n".getBytes());
                        //Använder text/plain istället för text/html för snyggare utskrift
                        out.write("Content-Type: text/plain\r\n".getBytes());
                        out.write("\r\n".getBytes());

                        //Body
                        out.write(new String(fromClient, 0, fromClient.length, "UTF-8").getBytes());
                    } else {
                        out.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
                        out.write("Content-Type: text/html\r\n".getBytes());
                        out.write("\r\n".getBytes());
                        out.write("<h1>400 Bad Request</h1>".getBytes());
                    }
                } else {
                    out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                    out.write("Content-Type: text/html\r\n".getBytes());
                    out.write("\r\n".getBytes());
                    out.write("<h1>404 Not Found</h1>".getBytes());
                }
            } catch(UnknownHostException e){
                System.out.println(e);
                out.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
                out.write("Content-Type: text/html\r\n".getBytes());
                out.write("\r\n".getBytes());
                out.write("<h1>400 Bad Request</h1>".getBytes());
            }

            //Flush and close
            out.flush();
            out.close();
        }
    }
}
