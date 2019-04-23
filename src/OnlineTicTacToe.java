import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


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
     *
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
     * <p>
     * If args.length == 0, program is remotely launched by JSCH.
     *
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
     *
     * @param
     */
    public OnlineTicTacToe(String hostname) {
        final int JschPort = 22;
        Scanner keyboard = new Scanner( System.in );
        String username = null;
        String password = null;
        String cur_dir = System.getProperty( "user.dir" );

        System.out.println("Enter your user name password " +
                "separated by spaces");
        try {
            String[] info = keyboard.nextLine().split(" ");
            username = info[0];
            password = info[1];
        } catch (Exception e) {
            error(e);
        }
        String command
                = "java -cp " + cur_dir + "/jsch-0.1.54.jar:" + cur_dir +
                " cssmpi2";

        /* USE THIS CODE TO REPLACE THE OTHER
        Connection connection = new Connection( username, password,
                hostname, "command" );

        input = connection.in;
        output = connection.out;
        */
        input = null;
        String bot_move;

        makeWindow();
        Counterpart counterpart = new Counterpart( );
        counterpart.start();

        try {
            new Thread(new checkWinCondition()).start();
            while (true) {
                bot_move = keyboard.nextLine(); // CHANGE TO [[input = connection.in;]]
                button[Integer.parseInt(bot_move)].setText("X");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * TODO This method is the two player impl. Local or server for addr
     *
     * @param addr
     * @param port
     */
    private OnlineTicTacToe(InetAddress addr, int port) {
        ServerSocket server = null;
        Socket client = null;
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(10000);
        } catch (IOException e) {
            // Intentionally caught and allowed to continue.
        }
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
            try {
                new Thread(new checkWinCondition()).start();
                while (true) {
                    input = in.readLine();
                    button[Integer.parseInt(input)].setText("X");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void makeWindow() {
        initBar();
        initButtons();
        initWindow();
    }

    private void initBar() {
        myMark = (host) ? "O" : "X";        // 1st person uses "O"
        yourMark = (host) ? "X" : "O";      // 2nd person uses "X"
        window = new JFrame("OnlineTicTacToe(" +
                ((host) ? "server)" : "client)") + myMark);
    }

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

        private void test(String CheckIfWin) {
            if (CheckIfWin.equals("XXX"))
                showWon("X");
            if (CheckIfWin.equals("OOO"))
                showWon("O");
        }
    }
    private class Counterpart extends Thread {
        Counterpart() {
            try {
                BufferedWriter log = new BufferedWriter(new FileWriter("log.txt"));
            } catch (Exception e) {
                error(e);
            }
        }

        @Override
        public void run( ) {
            button[8].setText("X");
        }
    }
}