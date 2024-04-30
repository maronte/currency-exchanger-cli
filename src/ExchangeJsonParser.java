import com.google.gson.JsonParser;

public class ExchangeJsonParser {
    private JsonParser parser = new JsonParser();

    public ExchangeRate parseFromPair(String json) {
        // Example argument: { "base_code": "USD", "target_code": "EUR", "conversion_rate": 0.85 }
        // Return value: new ExchangeRate("USD", "EUR", 0.85)
        var root = parser.parse(json).getAsJsonObject();
        var fromCurrency = root.get("base_code").getAsString();
        var toCurrency = root.get("target_code").getAsString();
        var rate = root.get("conversion_rate").getAsDouble();
        return new ExchangeRate(fromCurrency, toCurrency, rate);
    }

    public ExchangeRate[] parseFromRates(String json) {
        // Example argument: { "conversionRates": { "USD": 1.0, "EUR": 0.85 } }
        // Return value: [ new ExchangeRate("USD", 1.0), new ExchangeRate("EUR", 0.85) ]
        var root = parser.parse(json).getAsJsonObject();
        var fromCurrency = root.get("base_code").getAsString();
        var rates = root.getAsJsonObject("conversionRates");
        var entries = rates.entrySet();
        var result = new ExchangeRate[entries.size()];
        var i = 0;
        for (var entry : entries) {
            var toCurrencyCode = entry.getKey();
            var rate = entry.getValue().getAsDouble();
            result[i] = new ExchangeRate(fromCurrency, toCurrencyCode, rate);
            i++;
        }
        return result;
    }

    public Currency[] parseFromCurrencies(String json) {
        // Example argument: { "supported_codes: [["USD", "American Dollar"], ["MXN", "Mexican Peso"]] }
        // Return value: [ new Currency("USD", "American Dollar"), new Currency("MXN", "Mexican Peso") ]
        var root = parser.parse(json).getAsJsonObject();
        var supportedCodes = root.getAsJsonArray("supported_codes");
        var result = new Currency[supportedCodes.size()];
        for (var i = 0; i < supportedCodes.size(); i++) {
            var code = supportedCodes.get(i).getAsJsonArray().get(0).getAsString();
            var name = supportedCodes.get(i).getAsJsonArray().get(1).getAsString();
            result[i] = new Currency(code, name);
        }
        return result;
    }
}
