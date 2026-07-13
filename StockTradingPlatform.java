import java.io.*;
import java.util.*;

public class StockTradingPlatform {
    
    // Inner Class 1: Stock Class
    static class Stock {
        String symbol;
        String name;
        double price;

        public Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
        }
    }

    // Inner Class 2: Portfolio Class
    static class Portfolio implements Serializable {
        private static final long serialVersionUID = 1L;
        double balance = 10000.0; 
        Map<String, Integer> holdings = new HashMap<>();
    }

    private static final String DATA_FILE = "portfolio.txt";
    private static Map<String, Stock> market = new HashMap<>();
    private static Portfolio userPortfolio = new Portfolio();

    public static void main(String[] args) {
        initializeMarket();
        loadPortfolio();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== STOCK TRADING PLATFORM ===");
            System.out.println("1. Display Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. Track Portfolio Performance");
            System.out.println("5. Exit System");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    displayMarket();
                    break;
                case 2:
                    buyStock(scanner);
                    break;
                case 3:
                    sellStock(scanner);
                    break;
                case 4:
                    trackPortfolio();
                    break;
                case 5:
                    savePortfolio();
                    System.out.println("Exiting system. Data saved successfully!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }

    private static void initializeMarket() {
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 150.0));
        market.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 2800.0));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 700.0));
        market.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 3300.0));
    }

    private static void displayMarket() {
        System.out.println("\n--- MARKET DATA ---");
        for (Stock s : market.values()) {
            System.out.println(s.symbol + " (" + s.name + ") - $" + s.price);
        }
    }

    private static void buyStock(Scanner scanner) {
        displayMarket();
        System.out.print("Enter Stock Symbol to Buy: ");
        String symbol = scanner.next().toUpperCase();
        if (!market.containsKey(symbol)) {
            System.out.println("Stock not found!");
            return;
        }
        System.out.print("Enter Quantity: ");
        int qty = scanner.nextInt();
        double cost = market.get(symbol).price * qty;

        if (userPortfolio.balance >= cost) {
            userPortfolio.balance -= cost;
            userPortfolio.holdings.put(symbol, userPortfolio.holdings.getOrDefault(symbol, 0) + qty);
            System.out.println("Successfully bought " + qty + " shares of " + symbol);
            savePortfolio();
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    private static void sellStock(Scanner scanner) {
        System.out.print("Enter Stock Symbol to Sell: ");
        String symbol = scanner.next().toUpperCase();
        if (!userPortfolio.holdings.containsKey(symbol) || userPortfolio.holdings.get(symbol) == 0) {
            System.out.println("You don't own this stock!");
            return;
        }
        System.out.print("Enter Quantity to Sell: ");
        int qty = scanner.nextInt();
        int owned = userPortfolio.holdings.get(symbol);

        if (qty <= owned) {
            userPortfolio.balance += market.get(symbol).price * qty;
            userPortfolio.holdings.put(symbol, owned - qty);
            if (userPortfolio.holdings.get(symbol) == 0) {
                userPortfolio.holdings.remove(symbol);
            }
            System.out.println("Successfully sold " + qty + " shares of " + symbol);
            savePortfolio();
        } else {
            System.out.println("You don't have enough shares!");
        }
    }

    private static void trackPortfolio() {
        System.out.println("\n--- PORTFOLIO PERFORMANCE ---");
        System.out.println("Available Cash Balance: $" + userPortfolio.balance);
        System.out.println("Current Holdings:");
        double totalValue = userPortfolio.balance;
        for (Map.Entry<String, Integer> entry : userPortfolio.holdings.entrySet()) {
            double stockValue = market.get(entry.getKey()).price * entry.getValue();
            totalValue += stockValue;
            System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " shares (Value: $" + stockValue + ")");
        }
        System.out.println("Total Portfolio Value: $" + totalValue);
    }

    private static void savePortfolio() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(userPortfolio);
        } catch (IOException e) {
            System.out.println("Error saving portfolio data.");
        }
    }

    private static void loadPortfolio() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                userPortfolio = (Portfolio) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Starting with a new portfolio.");
            }
        }
    }
}