import java.util.HashMap;
import java.util.Map;

public class Handler {
    private final Exchanger exchanger = new Exchanger();
    private final Currency[] currencies;
    private final Map<Integer, String> indexToCurrencyCodeMap;
    private final Map<String, String> currencyCodeToNameMap;
    private boolean exit = false;


    public Handler() {
        this.currencies = exchanger.getCurrencies();
        this.indexToCurrencyCodeMap = buildIndexToCurrencyCodeMap(currencies);
        this.currencyCodeToNameMap = buildCurrencyCodeToNameMap(currencies);
    }

    public void run() {
        printWelcomeMessage();
        while (!exit) {
            System.out.println("Please enter a command:");
            var commands = getCommands();
            var input = System.console().readLine();
            if (commands.containsValue(input)) {
                switch (input) {
                    case "list":
                        printListCurrencies();
                        break;
                    case "convert":
                        handleConvertOption();
                        break;
                    case "exit":
                        exit = true;
                        printGoodbyeMessage();
                        break;
                }
            } else {
                System.out.println("Invalid command");
            }
        }
    }

    private void handleConvertOption() {
        var fromCurrencyCodeIsValid = false;
        var toCurrencyCodeIsValid = false;
        var fromCurrencyInput = "";
        var toCurrencyInput = "";
        do {
            System.out.println("Please enter the index of the currency you want to convert from:");
            var fromIndex = Integer.parseInt(System.console().readLine());
            fromCurrencyInput = indexToCurrencyCodeMap.get(fromIndex);
            fromCurrencyCodeIsValid = validateCurrencyCode(fromCurrencyInput, false);
            if (!fromCurrencyCodeIsValid) {
                System.out.println("Invalid currency code");
            }
        } while (!fromCurrencyCodeIsValid);

        do {
            System.out.println("Please enter the index of the currency you want to convert to:");
            System.out.println("If you want to convert to all currencies, type 'all");
            var toIndex = System.console().readLine();
            if (toIndex.equals("all")) {
                toCurrencyInput = "all";
                toCurrencyCodeIsValid = true;
            } else {
                toCurrencyInput = indexToCurrencyCodeMap.get(Integer.parseInt(toIndex));
                toCurrencyCodeIsValid = validateCurrencyCode(toCurrencyInput, true);
            }
            if (!toCurrencyCodeIsValid) {
                System.out.println("Invalid currency code");
            }
        } while (!toCurrencyCodeIsValid);

        System.out.println("Please enter the amount you want to convert:");
        var amount = Double.parseDouble(System.console().readLine());

        if (toCurrencyInput.equals("all")) {
            handleGenericConvertCommand(fromCurrencyInput, amount);
        } else {
            handleConvertCommand(fromCurrencyInput, toCurrencyInput, amount);
        }
    }

    private void printWelcomeMessage() {
        System.out.println("Welcome to the currency converter!");
        System.out.println("For a list of available currencies, type 'list'");
        System.out.println("To convert currencies, type 'convert'");
    }

    private void printGoodbyeMessage() {
        System.out.println("Goodbye!");
    }

    private void printListCurrencies() {
        for (var i = 0; i < currencies.length; i++) {
            var currency = currencies[i];
            System.out.println(i + ". " + currency.code() + " - " + currency.name());
        }
    }

    private void handleConvertCommand(String fromCurrencyCode, String toCurrencyCode, double amount) {
        var result = exchanger.convertPair(fromCurrencyCode, toCurrencyCode, amount);
        System.out.println("The result is: " + result);
    }

    private void handleGenericConvertCommand(String fromCurrencyCode, double amount) {
        var results = exchanger.convertAll(fromCurrencyCode, amount);
        for (var entry : results.entrySet()) {
            System.out.println("The result for " + currencyCodeToNameMap.get(entry.getKey()) + " is: " + entry.getValue());
        }
    }

    private boolean validateCurrencyCode(String currencyCode, boolean isAllAllowed) {
        if (!currencyCodeToNameMap.containsKey(currencyCode)) {
            return false;
        } else return !currencyCode.equals("all") || isAllAllowed;
    }

    private Map<Integer, String> getCommands() {
        var commands = new HashMap<Integer, String>();
        commands.put(1, "list");
        commands.put(2, "convert");
        commands.put(3, "exit");
        return commands;
    }

    private Map<String, String> buildCurrencyCodeToNameMap(Currency[] currencies) {
        var result = new HashMap<String, String>();
        for (var currency : currencies) {
            result.put(currency.code(), currency.name());
        }
        return result;
    }

    private Map<Integer, String> buildIndexToCurrencyCodeMap(Currency[] currencies) {
        var result = new HashMap<Integer, String>();
        for (var i = 0; i < currencies.length; i++) {
            result.put(i, currencies[i].code());
        }
        return result;
    }
}
