import java.net.*;
import java.io.*;

public class EchoCS {
    EchoCS(InetAddress addr, int port) {
        System.out.println(addr);
        System.out.println(port);

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(10000);
        } catch (IOException e) {
            // Intentionally caught and allowed to continue.
        }

        Socket client = null;
        while(true) {
            try {
                client = server.accept();
            } catch (Exception e) {

            }
            if (client != null) {
                System.out.println("You are a server");
                break;
            }

            try {
                client = new Socket(addr, port);
            } catch(IOException e) {

            }
            if (client != null) {
                System.out.println("You are a client");
                break;
            }

        }



        try (
                PrintWriter out =
                        new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }




    }

    public static void main(String[] args) {
        InetAddress addr = null;
        int port = 0;
        try {
            addr = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println( "Usage: java P2P ipAddr port message" );
            System.exit( -1 );
        }
        new EchoCS(addr, port);
    }
}