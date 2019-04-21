import java.net.*;
import java.io.*;

class EchoServer {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(1000);
        } catch ( IOException e) {
            e.printStackTrace();
        }

        Socket client = null;
        while(true) {
            try {
                client = server.accept();
            } catch (Exception e) {

            }
            System.out.println("Didn't accept yet");
            if (client != null)
                break;
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
}