import java.util.HashMap;
import java.util.Map;

public class Exchanger {
    private Currency[] currencies;
    private ExChangeApiClient apiExchanger;
    private ExchangeJsonParser parser;

    public Exchanger () {
        apiExchanger = new ExChangeApiClient();
        parser = new ExchangeJsonParser();
        updateCurrencies();
    }

    private void updateCurrencies() {
        var json = apiExchanger.getCurrencies();
        currencies = parser.parseFromCurrencies(json);
    }

    public Currency[] getCurrencies() {
        return currencies;
    }

    public ExchangeRate getLatestRatePair(String from, String to) {
        var json = apiExchanger.getLatestRatePair(from, to);
        return parser.parseFromPair(json);
    }

    public ExchangeRate[] getLatestRates(String from) {
        var json = apiExchanger.getLatestRates(from);
        return parser.parseFromRates(json);
    }

    public double convertPair(String from, String to, double amount) {
        var rate = getLatestRatePair(from, to);
        return amount * rate.rate();
    }

    public Map<String,Double> convertAll(String from, double amount) {
        var rates = getLatestRates(from);
        var result = new HashMap<String, Double>();
        for (var rate : rates) {
            result.put(rate.toCurrency(), amount * rate.rate());
        }
        return result;
    }
}
