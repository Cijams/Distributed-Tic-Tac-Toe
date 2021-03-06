import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Christopher Ijams
 * 2019/4/20
 * Online Tic-Tac-Toe game.
 * <p>
 * This is an online Internet program that involves two users in the same tic-tac-toe game.
 * The users can either play locally or remotely across different servers.
 */
public class OnlineTicTacToe {

    private static String myMark;               // "O" or "X".
    private static String yourMark;             // "X" or "O".
    private final int INTERVAL = 10000;          // Represents 1 second.
    private final int NBUTTONS = 9;             // represents the 3x3 grid.
    private ObjectInputStream input;            // input from counterpart.
    private ObjectOutputStream output;          // output from counterpart.
    private PrintWriter out = null;             // wrapper object.
    private JFrame window;                      // The tic-tac-toe window.
    private JButton[] button
            = new JButton[NBUTTONS];            // buttons[0] - buttons[9].
    private boolean[] myTurn = new boolean[1];  // T: My turn, F: your turn.
    private boolean host = false;               // Assists in determining who starts
    private boolean p1turn;                     // Assists in keeping track of player turn.

    /**
     * Two player version of the game. Non-blocking attempt to create a server.
     * Peer to peer, program does no care which instance is the server and which
     * is the client. Both players have their own GUI and their moves interact
     * with each other. The first player to get all in a row, column, or
     * diagonally wins the game.
     *
     * @param addr The public address of the opponent. [I.P. address or localhost]
     * @param port The port number to bind to.
     */
    private OnlineTicTacToe(InetAddress addr, int port) {
        // Try to set up the server
        ServerSocket server = null;
        Socket client = null;
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(INTERVAL);
        } catch (IOException e) {
            // Intentionally caught and allowed to continue.
        }

        // continuously try to become either a client or server
        while (true) {
            try {
                client = server.accept();
            } catch (NullPointerException | IOException e) {
            }

            if (client != null) {
                host = true;
                p1turn = false;
                break;
            }

            try {
                client = new Socket(addr, port);
            } catch (IOException e) {
                error(e);
            }
            if (client != null) {
                System.out.println("You are a client");
                p1turn = true;
                break;
            }
        }

        makeWindow();
        BufferedReader in = null;
        String input;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e) {
            System.err.println(e);
        }

        if (host) {
            // logic for running the game if in server mode.
            try {
                new Thread(new checkWinCondition()).start();
                while (true) {
                    input = in.readLine();
                    button[Integer.parseInt(input)].setText("O");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // logic for running the game in client mode.
            try {
                new Thread(new checkWinCondition()).start();
                while (true) {
                    input = in.readLine();
                    button[Integer.parseInt(input)].setText("X");
                }
            } catch (Exception e) {
                error(e);
            }
        }
    }

    /**
     * Instructs the user on how to invoke the program.
     */
    private static void usage() {
        System.err.println(
                "Usages: \njava OnlineTicTacToe <ip address> <port> [auto] " +
                        "\njava OnlineTicTacToe\n");
        System.exit(-1);
    }

    /**
     * Starts the online tic-tac-toe game.
     * param args[0]: Counterpart's IP address.
     * param args[1]: Counterpart's Port.
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2)
            usage();

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
    }

    /**
     * Prints the output of the stack trace upon a given error
     * and quits the application.
     *
     * @param e an exception.
     */
    private static void error(Exception e) {
        e.printStackTrace();
        System.exit(-1);
    }

    /**
     * Initializes and sets up the GUI, buttons, and actionListener.
     */
    private void makeWindow() {
        initBar();
        initButtons();
        initWindow();
    }

    /**
     * Initializes the top drag bar to display the current user.
     */
    private void initBar() {
        myMark = (host) ? "O" : "X";
        yourMark = (host) ? "X" : "O";
        window = new JFrame("OnlineTicTacToe(" +
                ((host) ? "server)" : "client)") + myMark);
    }

    /**
     * Initializes all of the condition to setup and display
     * the GUI to the user.
     */
    private void initWindow() {
        window.setSize(300, 300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new GridLayout(3, 3));
        window.setAlwaysOnTop(true);
        window.setVisible(true);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        if (host)
            window.setLocation(d.width / 2 - window.getSize().width / 2,
                    d.height / 2 - window.getSize().height / 2);
        else
            window.setLocation((d.width / 2 - window.getSize().width / 2) - 500,
                    d.height / 2 - window.getSize().height / 2);
    }

    /**
     * Method initialized all of the buttons and attaches the actionListener.
     */
    private void initButtons() {
        for (int i = 0; i < NBUTTONS; i++) {
            button[i] = new JButton();
            window.add(button[i]);
            button[i].addActionListener(e -> {
                int j = whichButtonClicked(e);
                if (host) {
                    if (!button[j].getText().equals("O")) {
                        button[j].setText("X");
                        out.println(j);
                    }
                } else {
                    if (!button[j].getText().equals("X")) {
                        button[j].setText("O");
                        out.println(j);
                    }
                }
            });
        }
    }

    /**
     * Gets the source to find out which button was clicked.
     */
    private int whichButtonClicked(ActionEvent e) {
        for (int i = 0; i < NBUTTONS; i++) {
            if (e.getSource() == button[i])
                return i;
        }
        return -1;
    }

    /**
     * Pop up to tell the players of the game who has won.
     *
     * @param mark
     */
    private void showWon(String mark) {
        JOptionPane.showMessageDialog(window, mark + " Won!");
        System.exit(0);
    }

    /**
     * Checks if there is a win condition. If so, ends the game.
     */
    private class checkWinCondition implements Runnable {
        String checkIfWin = "";
        boolean gameOver = false;

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(500);
                    checkWin();
                    if (gameOver)
                        break;
                }
            } catch (Exception e) {
                error(e);
            }
        }

        /**
         * Performs the actual checking of each row, column and diagonal line
         * for a win condition.
         */
        private void checkWin() {
            // rows
            for (int i = 0; i < 9; ) {
                checkIfWin = "";
                checkIfWin = button[i].getText() + button[i + 1].getText() + button[i + 2].getText();
                test(checkIfWin);
                i += 3;
            }
            // cols
            for (int i = 0; i < 3; i++) {
                checkIfWin = "";
                checkIfWin = button[i].getText() + button[i + 3].getText() + button[i + 6].getText();
                test(checkIfWin);
            }

            // diagonals
            checkIfWin = button[0].getText() + button[4].getText() + button[8].getText();
            test(checkIfWin);
            checkIfWin = button[2].getText() + button[4].getText() + button[6].getText();
            test(checkIfWin);
        }

        /**
         * Checks to see if there is a column, row, or diagonal line that
         * satisfies the criteria of win condition.
         *
         * @param CheckIfWin
         */
        private void test(String CheckIfWin) {
            if (CheckIfWin.equals("XXX"))
                showWon("X");
            if (CheckIfWin.equals("OOO"))
                showWon("O");
        }
    }
}