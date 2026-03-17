import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class DotComGUI extends JFrame {

    private final int SIZE = 7;
    private final JButton[][] buttons = new JButton[SIZE][SIZE];
    private final char[][] board = new char[SIZE][SIZE];   // S = ship, . = empty
    private final boolean[][] clicked = new boolean[SIZE][SIZE];

    private final java.util.List<Ship> ships = new ArrayList<>();

    private JLabel statusLabel;
    private JLabel guessLabel;
    private int guessCount = 0;
    private int shipsRemaining = 3;

    public DotComGUI() {
        setTitle("Sink a Dot Com - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initBoard();
        placeAllShips();
        initUI();

        setVisible(true);
    }

    private void initBoard() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = '.';
                clicked[r][c] = false;
            }
        }
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        statusLabel = new JLabel("Welcome! Sink all 3 dot coms.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        guessLabel = new JLabel("Guesses: 0 | Ships Remaining: 3", SwingConstants.CENTER);
        guessLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        topPanel.add(statusLabel);
        topPanel.add(guessLabel);

        add(topPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(SIZE + 1, SIZE + 1, 4, 4));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gridPanel.add(new JLabel(""));

        for (int c = 0; c < SIZE; c++) {
            JLabel colLabel = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            colLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            gridPanel.add(colLabel);
        }

        for (int r = 0; r < SIZE; r++) {
            JLabel rowLabel = new JLabel(String.valueOf((char) ('A' + r)), SwingConstants.CENTER);
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            gridPanel.add(rowLabel);

            for (int c = 0; c < SIZE; c++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("SansSerif", Font.BOLD, 20));
                btn.setFocusPainted(false);

                final int row = r;
                final int col = c;
                btn.addActionListener(e -> handleClick(row, col));

                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        restartButton.addActionListener(e -> restartGame());

        JButton revealButton = new JButton("Reveal Ships");
        revealButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        revealButton.addActionListener(e -> revealShips());

        bottomPanel.add(restartButton);
        bottomPanel.add(revealButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleClick(int r, int c) {
        if (clicked[r][c]) {
            statusLabel.setText("这个格子已经点过了，别鞭尸了 😭");
            return;
        }

        clicked[r][c] = true;
        guessCount++;

        if (board[r][c] == 'S') {
            buttons[r][c].setText("X");
            buttons[r][c].setEnabled(false);

            Ship hitShip = findShipByCell(r, c);
            if (hitShip != null) {
                hitShip.hit(r, c);

                if (hitShip.isSunk()) {
                    shipsRemaining--;
                    statusLabel.setText("KILL! You sunk " + hitShip.name);
                    markSunkShip(hitShip);

                    if (shipsRemaining == 0) {
                        guessLabel.setText("Guesses: " + guessCount + " | Ships Remaining: 0");
                        endGame();
                        return;
                    }
                } else {
                    statusLabel.setText("HIT!");
                }
            }
        } else {
            buttons[r][c].setText("O");
            buttons[r][c].setEnabled(false);
            statusLabel.setText("MISS!");
        }

        guessLabel.setText("Guesses: " + guessCount + " | Ships Remaining: " + shipsRemaining);
    }

    private Ship findShipByCell(int r, int c) {
        for (Ship ship : ships) {
            if (ship.contains(r, c)) {
                return ship;
            }
        }
        return null;
    }

    private void markSunkShip(Ship ship) {
        for (Point p : ship.cells) {
            buttons[p.x][p.y].setText("X");
            buttons[p.x][p.y].setEnabled(false);
        }
    }

    private void endGame() {
        statusLabel.setText("All dot coms are dead!");
        int option = JOptionPane.showConfirmDialog(
                this,
                "You won in " + guessCount + " guesses!\nPlay again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            dispose();
        }
    }

    private void restartGame() {
        getContentPane().removeAll();

        ships.clear();
        guessCount = 0;
        shipsRemaining = 3;

        initBoard();
        placeAllShips();
        initUI();

        revalidate();
        repaint();
    }

    private void revealShips() {
        for (Ship ship : ships) {
            for (Point p : ship.cells) {
                if (!clicked[p.x][p.y]) {
                    buttons[p.x][p.y].setText("S");
                }
            }
        }
        statusLabel.setText("Ships revealed. 开透了属于是。");
    }

    private void placeAllShips() {
        ships.add(createShip("Pets.com"));
        ships.add(createShip("eToys.com"));
        ships.add(createShip("Go2.com"));
    }

    private Ship createShip(String name) {
        Random random = new Random();

        while (true) {
            boolean horizontal = random.nextBoolean();
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            ArrayList<Point> cells = new ArrayList<>();

            if (horizontal) {
                if (col + 2 >= SIZE) continue;
                for (int i = 0; i < 3; i++) {
                    if (board[row][col + i] == 'S') {
                        cells.clear();
                        break;
                    }
                    cells.add(new Point(row, col + i));
                }
            } else {
                if (row + 2 >= SIZE) continue;
                for (int i = 0; i < 3; i++) {
                    if (board[row + i][col] == 'S') {
                        cells.clear();
                        break;
                    }
                    cells.add(new Point(row + i, col));
                }
            }

            if (cells.size() == 3) {
                for (Point p : cells) {
                    board[p.x][p.y] = 'S';
                }
                return new Ship(name, cells);
            }
        }
    }

    static class Ship {
        String name;
        ArrayList<Point> cells;
        ArrayList<Point> hits = new ArrayList<>();

        Ship(String name, ArrayList<Point> cells) {
            this.name = name;
            this.cells = cells;
        }

        boolean contains(int r, int c) {
            for (Point p : cells) {
                if (p.x == r && p.y == c) return true;
            }
            return false;
        }

        void hit(int r, int c) {
            Point target = new Point(r, c);
            for (Point p : hits) {
                if (p.equals(target)) return;
            }
            hits.add(target);
        }

        boolean isSunk() {
            return hits.size() == cells.size();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DotComGUI::new);
    }
}