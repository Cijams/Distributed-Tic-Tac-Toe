import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Christopher Ijams
 * 2019/4/20
 * Online Tic-Tac-Toe game.
 *
 * Game has three different modes.
 */
public class OnlineTicTacToe {
    private final int INTERVAL = 1000;      // Represents 1 second.
    private final int NBUTTONS = 9;         // represents the 3x3 grid.

    private ObjectInputStream input;        // input from counterpart.
    private ObjectOutputStream output;      // output from counterpart.

    private static String myMark;           // "O" or "X".
    private static String yourMark;         // "X" or "O".

    private JFrame window;                  // The tic-tac-toe window.
    private JButton[] button
            = new JButton[NBUTTONS];        // buttons[0] - buttons[9].
    private boolean myTurn;                  // T: My turn, F: your turn.



    /**
     * Prints the output of the stack trace upon a given error
     * and quits the application.
     * @param e an exception.
     */
    private static void error(Exception e) {
        e.printStackTrace();
        System.exit(-1);
    }

    /**
     * Instructs the user on how to invoke the program.
     */
    private static void usage() {
        System.err.println(
                "Usages: \njava OnlineTicTacToe <ip address> <port> [auto] " +
                        "\njava OnlineTicTacToe\n"
        );
        System.exit(-1);
    }

    /**
     * Enforces proper usage of the program.
     */
    private static void check_usage(String[] args) {
        if (args.length != 0 && args.length != 2 && args.length != 3)
            usage();
    }

    /**
     * Starts the online tic-tac-toe game.
     * param args[0]: Counterpart's IP address.
     * param args[1]: Counterpart's Port.
     * param arts[2]: If "auto", bot controlled Counterpart. Else ignored.
     *
     * If args.length == 0, program is remotely launched by JSCH.
     * @param args
     */
    public static void main(String[] args) {
        check_usage(args);

        if (args.length == 0) {
            new OnlineTicTacToe();
        }

        InetAddress addr = null;
        int port = 0;
        try {
            addr = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
        } catch (UnknownHostException | NumberFormatException e) {
            error(e);
        }

        if (args.length == 2) {
            new OnlineTicTacToe(addr, port);
        }
        if (args.length == 3 && args[2].equals("auto")) {
            new OnlineTicTacToe(args[0]);
        }
    }

    /**
     * TODO This method needs to implement JSCH Mode.
     */
    private OnlineTicTacToe() {
        System.out.println("Filler for JSCH");
    }

    /**
     * TODO This method implements autoplay.
     * @param arg
     */
    private OnlineTicTacToe(String arg) {

    }

    /**
     *
     * TODO This method is the two player impl. Local or server for addr
     *
     * @param addr
     * @param port
     */
    private OnlineTicTacToe(InetAddress addr, int port) {
        System.out.println("Filler for 2 args");
        // Set up a TCP connection with my counterpart
        // set up a window
        // start counterpart thread
    }
}
