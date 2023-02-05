import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JackOrBetter {
    private static final Scanner sc = new Scanner(System.in);
    private static final Card[] cards = new Card[5];
    private static int credits;
    private static int bet;
    private static boolean end = false;

    public static void main(String[] args) {
        welcome();
        do {
            printPrizes();
            selectBet();
            generateCards();
            selectHolds();
            generateCards();
            checkCombinations();
            resetCardsAndDeck();
        } while (!end);
        bye();
    }

    public static void welcome() {
        System.out.println("""
                  █ ▄▀█ █▀▀ █▄▀   █▀█ █▀█   █▄▄ █▀▀ ▀█▀ ▀█▀ █▀▀ █▀█
                █▄█ █▀█ █▄▄ █ █   █▄█ █▀▄   █▄█ ██▄  █   █  ██▄ █▀▄
                """);
    }
    public static void bye() {
        System.out.println("See you next time, bye!");
        sc.close();
    }

    public static void printPrizes() {
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "ROYAL FLUSH", 250, 500, 750, 1000, 4000);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "STRAIGHT FLUSH", 50, 100, 150, 200, 250);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "FOUR OF A KIND", 25, 50, 75, 100, 125);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "FULL HOUSE", 9, 18, 27, 36, 45);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "FLUSH", 6, 12, 18, 24, 30);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "STRAIGHT", 4, 8, 12, 16, 20);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "THREE OF A KIND", 3, 6, 9, 12, 15);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "TWO PAIR", 2, 4, 6, 8, 10);
        System.out.printf("%-15s %-4d %-4d %-4d %-4d %-4d\n", "JACK OR BETTER", 1, 2, 3, 4, 5);
        System.out.println();
    }

    public static void selectBet() {
        while (true) {
            if (credits == 0) insertCredits();          // Must have > 0 credits to bet
            System.out.print("Select Bet (1 - 5): ");
            String b = sc.nextLine();
            if (notInt(b)) continue;                     // String must be a number
            bet = Integer.parseInt(b);
            if (bet >= 6 || bet <= 0 ) continue;        // Bet must be from interval [1,5]
            if (credits - bet < 0) continue;            // Bet must be smaller or same than credits
            credits -= bet;
            printBet();
            printCredits();
            break;
        }
    }
    public static boolean notInt(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
    public static void printBet() {
        System.out.println("Your Bet: " + bet + "\n");

    }

    public static void insertCredits() {
        while (true) {
            System.out.print("Insert Credits: ");
            String c = sc.nextLine();
            if (notInt(c)) continue;        // String must be a number
            credits = Integer.parseInt(c);
            if (credits < 1) continue;      // You must insert > 0 credits
            break;
        }
        printCredits();
    }
    public static void printCredits() {
        System.out.println("Your Credits: " + credits + "€\n");

    }

    public static void generateCards() {
        IntStream.range(0, cards.length).filter(i -> cards[i] == null).forEach(i -> cards[i] = new Card());
        printCards();
    }
    public static void printCards() {
        for (Card c : cards) System.out.print(c + "  ");
        System.out.println("\n");
    }

    public static void selectHolds() {
        System.out.print("Select Cards (e.g. \"1 2\" holds 1st and 2nd card): ");
        String holds = sc.nextLine();
        for (int i = 0; i < cards.length; i++)
            if (!holds.contains((i + 1) + "")) {
                cards[i].setDeckToNull();     // Not selected card can be generated again
                cards[i] = null;                    // Marks which card has to be generated in 2nd round
            }
        System.out.println();
    }

    public static void checkCombinations() {
        int win = 0;
        boolean isFlush = isFlush();            // Optimized checking for Royal Flush, Straight Flush and Flush
        boolean isStraight = isStraight();      // Optimized checking for Straight Flush and Straight
        if (isFlush && isRoyal()) {
            System.out.println("ROYAL FLUSH");
            win = 250 * bet;
        } else if (isFlush && isStraight) {
            System.out.println("STRAIGHT FLUSH");
            win = 50 * bet;
        }
        else if (isFourOfAKind()) {
            System.out.println("FOUR OF A KIND");
            win = 25 * bet;
        }
        else if (isFullHouse()) {
            System.out.println("FULL HOUSE");
            win = 9 * bet;
        }
        else if (isFlush) {
            System.out.println("FLUSH");
            win = 6 * bet;
        }
        else if (isStraight) {
            System.out.println("STRAIGHT");
            win = 4 * bet;
        }
        else if (isThreeOfAKind()) {
            System.out.println("THREE OF A KIND");
            win = 3 * bet;
        }
        else if (isTwoPair()) {
            System.out.println("TWO PAIR");
            win = 2 * bet;
        }
        else if (isJackOrBetter()) {
            System.out.println("JACK OR BETTER");
            win = bet;
        }
        else  {
            System.out.println("BETTER LUCK NEXT TIME");
            System.out.println();
        }
        System.out.println("Winnings: " + win + "€");
        credits += win;
        System.out.println("Your Credits: " + credits + "€\n");
        end();
    }
    public static boolean isFlush() {
        // Check if all symbols are the same
        return IntStream.range(0, 4).noneMatch(i -> cards[i].getSymbol() != cards[i + 1].getSymbol());
    }
    public static boolean isRoyal() {
        // Check if number permutation is royal
        StringBuilder numbers = new StringBuilder();
        String royalNumbers = "101112131";
        for (Card c : cards) numbers.append(c.getNumber());
        char[] a = numbers.toString().toCharArray();
        char[] b = royalNumbers.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }
    public static boolean isStraight() {
        // Sort number permutation and check if next number is equal to increment of current number
        Arrays.sort(cards, Comparator.comparingInt(Card::getNumber));
        return IntStream.range(0, 4).noneMatch(i -> cards[i].getNumber() + 1 != cards[i + 1].getNumber());
    }
    public static boolean isFourOfAKind() {
        // Get counters of numbers and check if there is 4
        Integer[] c = cardNumberCounter();
        return Arrays.stream(c).anyMatch(num -> num == 4);
    }
    public static boolean isFullHouse() {
        // Get counters of numbers and check if there is 2 and 3
        Integer[] c = cardNumberCounter();
        for (Integer number : c) {
            boolean r = number != 2 && number != 3;
            if (r) return false;
        }
        return true;
    }
    public static boolean isThreeOfAKind() {
        // Get counters of numbers and check if there is 4
        Integer[] c = cardNumberCounter();
        return Arrays.stream(c).anyMatch(num -> num == 3);
    }
    public static boolean isTwoPair() {
        // Get counters of numbers and check if there are 2 twos
        Integer[] c = cardNumberCounter();
        return (int) Arrays.stream(c).filter(num -> num == 2).count() == 2;
    }
    public static boolean isJackOrBetter() {
        // Get dict of counters, check if there is a Jack's num or better key with value of 2
        HashMap<Integer, Integer> counter = Arrays.stream(cards).collect(Collectors.toMap(Card::getNumber, c -> 1, Integer::sum, HashMap::new));
        return counter.keySet().stream().anyMatch(num -> (num > 10 || num == 1) && counter.get(num) == 2);
    }
    public static Integer[] cardNumberCounter() {
        HashMap<Integer, Integer> counter = Arrays.stream(cards).collect(Collectors.toMap(Card::getNumber, c -> 1, Integer::sum, HashMap::new));
        return counter.values().toArray(new Integer[0]);
    }
    public static void end() {
        System.out.print("Press ENTER to continue, type \"checkout\" to checkout: ");
        end = sc.nextLine().equals("checkout");
        System.out.println();
    }

    public static void resetCardsAndDeck() {
        IntStream.range(0, cards.length).forEachOrdered(i -> { cards[i].setDeckToNull(); cards[i] = null; });

    }
}