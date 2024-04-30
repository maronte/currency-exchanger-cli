import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExChangeApiClient {
    final private String baseURL;
    final private HttpClient client = HttpClient.newHttpClient();

    public ExChangeApiClient () {
        var dotenv = Dotenv.configure().load();
        this.baseURL = dotenv.get("EXCHANGE_API_URL") + "/" + dotenv.get("EXCHANGE_API_KEY");
    }

    private String doRequest(String endpointUrl) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .build();
        try {
            var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public String getLatestRatePair(String from, String to) {
        var endpointUrl = baseURL + "/pair/" + from + "/" + to;
        return doRequest(endpointUrl);
    }

    public String getLatestRates(String currencyCode) {
        var endpointUrl = baseURL + "/latest/" + currencyCode;
        return doRequest(endpointUrl);
    }

    public String getCurrencies() {
        var endpointUrl = baseURL + "/codes";
        return doRequest(endpointUrl);
    }
}
