import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Christopher Ijams
 * 2019/4/20
 * Online Tic-Tac-Toe game.
 *
 * Game has CheckIfWin different modes.
 */
public class OnlineTicTacToe {

    private final int INTERVAL = 1000;      // Represents 1 second.
    private final int NBUTTONS = 9;         // represents the 3x3 grid.

    private ObjectInputStream input;        // input from counterpart.
    private ObjectOutputStream output;      // output from counterpart.

    private PrintWriter out = null;

    private static String myMark;           // "O" or "X".
    private static String yourMark;         // "X" or "O".

    private JFrame window;                  // The tic-tac-toe window.
    private JButton[] button
            = new JButton[NBUTTONS];        // buttons[0] - buttons[9].
    private boolean[] myTurn = new boolean[1]; // T: My turn, F: your turn.
    private boolean host = false;
    private boolean p1turn = true;



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
                    } catch (NullPointerException | IOException e) {

                    }
                    if (client != null) {
                        host = true;
                        System.out.println("You are a server");
                        p1turn = false;
                        break;
                    }

                    try {
                        client = new Socket(addr, port);
                    } catch(IOException e) {

                    }
                    if (client != null) {
                        System.out.println("You are a client");
                        p1turn = true;
                        break;
                    }
                }
                makeWindow(host);
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
                            new Thread(new checkWinCondition()).start();
                            while (true) {
                                input = in.readLine();
                                button[Integer.parseInt(input)].setText("O");
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                else {
                        try {
                            new Thread(new checkWinCondition()).start();
                            while (true) {
                                input = in.readLine();
                                button[Integer.parseInt(input)].setText("X");
                        }
                    } catch (Exception e) { System.out.println(e); }
                }
            }

    private void makeWindow( boolean host) {
        myMark = (host) ? "O" : "X"; // 1st person uses "O"
        yourMark = (host) ? "X" : "O"; // 2nd person uses "X"
        window = new JFrame("OnlineTicTacToe(" +
                ((host) ? "server)" : "client)") + myMark);
        window.setSize(300, 300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new GridLayout(3, 3));

        for (int i = 0; i < NBUTTONS; i++) {
            button[i] = new JButton();
            window.add(button[i]);
            button[i].addActionListener(l);
        }
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (host)
            window.setLocation(d.width/2-window.getSize().width/2, d.height/2-window.getSize().height/2);
        else
            window.setLocation((d.width/2-window.getSize().width/2)-500, d.height/2-window.getSize().height/2);
        window.setAlwaysOnTop (true);
        window.setVisible(true);
    }

    private ActionListener l = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int i = whichButtonClicked(e);

                if (host) {
                    if (button[i].getText() != "O") {
                        button[i].setText("X");
                        out.println(i);
                    }
                }
            else  {
                if (button[i].getText() != "X") {
                    button[i].setText("O");
                    out.println(i);
                }
            }
        }
    };

    private int whichButtonClicked(ActionEvent e) {
        for (int i = 0; i < NBUTTONS; i++) {
            if (e.getSource() == button[i])
                return i;
        }
        return -1;
    }

    private void showWon(String mark) {
        JOptionPane.showMessageDialog(window, mark + " Won!");
        System.exit(0);
    }

    private class checkWinCondition implements Runnable {
        String oWinCondition = "OOO";
        String xWinCondition = "XXX";
        boolean oWinner = false;
        boolean xWinner = false;
        String CheckIfWin = "";
        boolean gameOver = false;

        @Override
        public void run() {
            try {
                while(true) {
                    Thread.sleep(500);
                    checkWin();
                    if (gameOver)
                        break;
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        private void checkWin() {
            // rows
            for(int i = 0; i < 9;) {
                CheckIfWin = "";
                CheckIfWin = button[i].getText()+button[i+1].getText()+button[i+2].getText();
                if (CheckIfWin.equals(oWinCondition))
                    oWinner = true;
                if (CheckIfWin.equals(xWinCondition))
                    xWinner = true;
                i += 3;
            }
            // cols
            for(int i = 0; i < 3; i++) {
                CheckIfWin = "";
                CheckIfWin = button[i].getText()+button[i+3].getText()+button[i+6].getText();
                if (CheckIfWin.equals(oWinCondition))
                    oWinner = true;
                if (CheckIfWin.equals(xWinCondition))
                    xWinner = true;
            }
            // diagonal
            CheckIfWin = button[0].getText()+button[4].getText()+button[8].getText();
            if (CheckIfWin.equals(oWinCondition))
                oWinner = true;
            if (CheckIfWin.equals(xWinCondition))
                xWinner = true;
            // diagonal
            CheckIfWin = button[2].getText()+button[4].getText()+button[6].getText();
            if (CheckIfWin.equals(oWinCondition))
                oWinner = true;
            if (CheckIfWin.equals(xWinCondition))
                xWinner = true;

            if (xWinner){
                showWon("X");
                gameOver = true;
            }
            if (oWinner){
                showWon("O");
                gameOver = true;
            }
        }
    }
}