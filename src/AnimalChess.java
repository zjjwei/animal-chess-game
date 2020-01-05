import java.util.*;

public class AnimalChess {
    Item[][] board;
    int size = 4;
    List<Item> items;
    int totalSize = 16;
    boolean lost;
    ItemColor lostColor;

    public void populateItems(List<Item> items) {
        for (ItemAnimal animal : ItemAnimal.values()) {
            items.add(new Item(ItemColor.BLUE, animal));
            items.add(new Item(ItemColor.RED, animal));
        }
    }

    public void shuffleList(List<Item> items) {
        Collections.shuffle(items);
    }

    public AnimalChess() {
        board = new Item[size][size];
        items = new ArrayList<>();
        populateItems(items);
        shuffleList(items);
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = items.get(k);
                k++;
            }
        }
    }

    /**
     * rules of game:
     * 1) must flip a card if none is facing up
     * 2) can either move own card to empty space
     * 3) or flip any card if such card available
     * 4) or eat enemy's card, if adjacent to own card and own card is smaller than enemy's card or own card is 7 and enemy card is 0
     * 5) game ends when no own card is on the board, or cannot make a valid move anymore
     *
     * @param color team's color
     */
    public Option takeAStep(ItemColor color) {
        List<Option> possibleMoves = getOptions(color);
        if (possibleMoves.size() == 0) {
            lost = true;
            lostColor = color;
            System.out.println(color + " lost");
            return null;
        }
        //for now, implement a random player
        int pick = new Random().nextInt(possibleMoves.size());
        Option move = possibleMoves.get(pick);
        if (move.moveType == MoveType.FLIP) {
            flipCard(move.src[0], move.src[1]);
        } else if (move.moveType == MoveType.FLEE) {
            flee(move.src[0], move.src[1], move.dst[0], move.dst[1]);
        } else if (move.moveType == MoveType.ATTACK) {
            attack(move.src[0], move.src[1], move.dst[0], move.dst[1]);
        } else if (move.moveType == MoveType.DIE) {
            die(move.src[0], move.src[1], move.dst[0], move.dst[1]);
        }

        return move;

    }

    public Option getBestMove(ItemColor color, List<Option> ops) {
        Collections.sort(ops);
        return ops.get(0);
    }

    public Option takeBestStep(ItemColor color) {
        List<Option> possibleMoves = getOptions(color);
        if (possibleMoves.size() == 0) {
            lost = true;
            lostColor = color;
            System.out.println(color + " lost");
            return null;
        }
        //for now, implement a random player
        Option move = getBestMove(color, possibleMoves);
        if (move.moveType == MoveType.FLIP) {
            flipCard(move.src[0], move.src[1]);
        } else if (move.moveType == MoveType.FLEE) {
            flee(move.src[0], move.src[1], move.dst[0], move.dst[1]);
        } else if (move.moveType == MoveType.ATTACK) {
            attack(move.src[0], move.src[1], move.dst[0], move.dst[1]);
        } else if (move.moveType == MoveType.DIE) {
            die(move.src[0], move.src[1], move.dst[0], move.dst[1]);
        }
        return move;
    }


    public List<int[]> getValidNeighbors(int[] pos, int size) {
        int i = pos[0];
        int j = pos[1];
        List<int[]> ans = new ArrayList<>();
        if (i > 0) {
            ans.add(new int[]{i - 1, j});
        }
        if (i < size - 1) {
            ans.add(new int[]{i + 1, j});
        }
        if (j > 0) {
            ans.add(new int[]{i, j - 1});
        }
        if (j < size - 1) {
            ans.add(new int[]{i, j + 1});
        }
        return ans;
    }

    public List<Option> getOptions(ItemColor color) {
        List<int[]> downCards = getDownCards();
        List<int[]> ownCards = getOwnUpCards(color);
        List<Option> ans = new ArrayList<>();
        for (int[] p : downCards) {
            ans.add(new Option(MoveType.FLIP, p, p));
        }
        for (int[] o : ownCards) {
            List<int[]> ns = getValidNeighbors(o, size);
            for (int[] n : ns) {
                int i = n[0];
                int j = n[1];
                Item neighbor = board[i][j];
                if (neighbor == null) {
                    ans.add(new Option(MoveType.FLEE, o, n));
                }
                if (neighbor != null && neighbor.isFaceUp() && neighbor.getColor() != color) {
                    int x = o[0];
                    int y = o[1];
                    if (board[x][y].getAnimal().ordinal() == 0 && neighbor.getAnimal().ordinal() == 7) {
                        continue; // elephant cannot attack mouse
                    }
                    if (board[x][y].getAnimal().ordinal() < neighbor.getAnimal().ordinal()) {
                        ans.add(new Option(MoveType.ATTACK, o, n));
                    } else if (board[x][y].getAnimal().ordinal() == neighbor.getAnimal().ordinal()) {
                        ans.add(new Option(MoveType.DIE, o, n));
                    } else if (board[x][y].getAnimal().ordinal() == 7 && neighbor.getAnimal().ordinal() == 0) {
                        ans.add(new Option(MoveType.ATTACK, o, n)); // least strong mouse attack elephant case
                    }
                }
            }
        }

        return ans;
    }

    public List<int[]> getDownCards() {
        List<int[]> downCards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != null && !board[i][j].isFaceUp()) {
                    downCards.add(new int[]{i, j});
                }
            }
        }
        return downCards;
    }

    public List<int[]> getOwnUpCards(ItemColor color) {
        List<int[]> ownCards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Item card = board[i][j];
                if (card != null && card.getColor() == color && card.isFaceUp()) {
                    ownCards.add(new int[]{i, j});
                }
            }
        }
        return ownCards;
    }

    public void flee(int i, int j, int ii, int jj) {
        board[ii][jj] = board[i][j];
        board[i][j] = null;
    }

    public void attack(int i, int j, int ii, int jj) {
        board[ii][jj] = board[i][j];
        board[i][j] = null;
    }

    public void die(int i, int j, int ii, int jj) {
        board[ii][jj] = null;
        board[i][j] = null;
    }

    public void flipCard(int i, int j) {
        if (!board[i][j].isFaceUp()) {
            board[i][j].flipItem();
        } else {
            throw new RuntimeException("Cannot double flip " + i + " " + j);
        }
    }


    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String value;
                if (board[i][j] == null) {
                    value = "E";
                } else {
                    value = board[i][j].isFaceUp() ? board[i][j].toString() : "-";
                }

                if (board[i][j] != null && board[i][j].getColor() == ItemColor.BLUE) {
                    System.out.print(Util.ANSI_BLUE + value);
                } else if (board[i][j] != null && board[i][j].getColor() == ItemColor.RED) {
                    System.out.print(Util.ANSI_RED + value);
                } else {
                    System.out.print(Util.ANSI_RESET + value);
                }
                if (j != size - 1) {
                    System.out.print(Util.ANSI_RESET + "|");
                } else {
                    System.out.print(Util.ANSI_RESET + "\n");
                }
            }
        }

        System.out.println("============================");
    }

    public void peekBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String value;
                if (board[i][j] == null) {
                    value = "E";
                } else {
                    value = board[i][j].toString();
                }
                if (board[i][j] != null && board[i][j].getColor() == ItemColor.BLUE) {
                    System.out.print(Util.ANSI_BLUE + value);
                } else if (board[i][j] != null && board[i][j].getColor() == ItemColor.RED) {
                    System.out.print(Util.ANSI_RED + value);
                }
                if (j != size - 1) {
                    System.out.print(Util.ANSI_RESET + "|");
                } else {
                    System.out.print(Util.ANSI_RESET + "\n");
                }
            }
        }
        System.out.println("----------------------");
    }


    public void startGame() {
        AnimalChess chess = new AnimalChess();
        int turn = 0;
        while (!chess.lost && turn < 2000) {
            Option bo = chess.takeAStep(ItemColor.BLUE);
            String boStep = bo == null ? "no valid moves" : bo.toString();
            System.out.println("blue took step for " + turn + " " + boStep);
            chess.printBoard();

            Option ro = chess.takeAStep(ItemColor.RED);
            String roStep = ro == null ? "no valid moves" : ro.toString();
            System.out.println("red took step for " + turn + " " + roStep);
            chess.printBoard();
            turn += 1;
        }
        System.out.println(chess.lostColor.toString() + " team lost after " + turn + " plays");
    }

    public void startBestGame() {
        AnimalChess chess = new AnimalChess();
        int turn = 0;
        while (!chess.lost && turn < 8000) {
            Option bo = chess.takeBestStep(ItemColor.BLUE);
            String boStep = bo == null ? "no valid moves" : bo.toString();
            System.out.println("blue best took step for " + turn + " " + boStep);
            chess.printBoard();

            Option ro = chess.takeBestStep(ItemColor.RED);
            String roStep = ro == null ? "no valid moves" : ro.toString();
            System.out.println("red best took step for " + turn + " " + roStep);
            chess.printBoard();
            turn += 1;
        }
        System.out.println(chess.lostColor.toString() + " team lost after " + turn + " plays");
    }

    public static void main(String[] args) {
        AnimalChess chess = new AnimalChess();
//        chess.peekBoard();
//        chess.printBoard();
//        chess.takeAStep(ItemColor.BLUE);
//        chess.printBoard();

        chess.startGame();
//        chess.startBestGame();
    }
}
