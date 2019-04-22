import java.net.*;
import java.io.*;

public class EchoCS {
    EchoCS(InetAddress addr, int port) {
        boolean host = false;
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
                host = true;
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
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader stdIn = null;
        String input = "";
        String output = "";
        boolean turn = false;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println(e);
        }

        if (host) {
            try {
                while (true) {
                    input = in.readLine();
                    System.out.println(input);
                    output = stdIn.readLine();
                    out.println(output);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        else {
            try {
                while (true) {
                    output = stdIn.readLine();
                    out.println(output);
                    input = in.readLine();
                    System.out.println(input);
                }
            } catch (Exception e) { System.out.println(e); }
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