public class Card {
    private static final boolean[] deck = new boolean[52];   // deck ~ Q1: ♠, Q2: ♣, Q3: ♥, Q4: ♦
    private final int index;      // 0 - 51
    private final int number;     // 1 - 13
    private final int symbol;     // 1: ♠, 2: ♣, 3: ♥, 4: ♦

    public Card() {
        int[] data = generateCard();
        index = data[0];
        number = data[1];
        symbol = data[2];
    }

    private static int[] generateCard() {
        int i;
        do i = (int) (Math.random() * 52); while(deck[i]);
        deck[i] = true;
        int index = i;
        int number = i % 13 + 1;
        int symbol = i / 13 + 1;
        int colour = i / 26;
        return new int[]{index, number, symbol, colour};
    }

    public void setDeckToNull() {
        deck[index] = false;
    }

    public int getNumber() {
        return number;
    }

    public int getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        StringBuilder card = new StringBuilder();
        switch (number) {
            case 1 -> card.append("A");
            case 11 -> card.append("J");
            case 12 -> card.append("Q");
            case 13 -> card.append("K");
            default -> card.append(number);
        }
        switch (symbol) {
            case 1 -> card.append("♠");
            case 2 -> card.append("♣");
            case 3 -> card.append("♥");
            case 4 -> card.append("♦");
        }
        return card.toString();
    }
}