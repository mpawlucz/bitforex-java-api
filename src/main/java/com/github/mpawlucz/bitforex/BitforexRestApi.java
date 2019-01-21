package com.github.mpawlucz.bitforex;

import com.github.mpawlucz.bitforex.domain.request.TradeRequest;
import com.github.mpawlucz.bitforex.domain.response.BalanceResponse;
import com.github.mpawlucz.bitforex.domain.response.OpenOrdersResponse;
import com.github.mpawlucz.bitforex.domain.response.TradeResponse;
import com.github.mpawlucz.bitforex.rest.RestClient;
import com.github.mpawlucz.bitforex.sign.ApiKey;
import com.github.mpawlucz.bitforex.sign.Hmac;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BitforexRestApi {

    public static final String BASE_URL = "https://api.bitforex.com";

    final static DecimalFormat decimalFormat;
    static {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("#0.########", symbols);
        decimalFormat.setDecimalSeparatorAlwaysShown(true);
        decimalFormat.setGroupingUsed(false);
    }

    private final Gson gson = new Gson();
    private final RestClient restClient = new RestClient();
    private final ApiKey apiKey;

    public BitforexRestApi(ApiKey apiKey) {
        this.apiKey = apiKey;
    }

    public BalanceResponse getBalance() throws IOException {
        final HashMap<String, String> params = new HashMap<>();

        final String responseText = authorizedPost(
                BASE_URL,
                "/api/v1/fund/allAccount",
                params
        );

        Type typeToken = new TypeToken<BalanceResponse>() {}.getType();
        final BalanceResponse response = gson.fromJson(responseText, typeToken);
        return response;
    }

    public TradeResponse trade(TradeRequest trade) throws IOException {
        final HashMap<String, String> params = new HashMap<>();
        params.put("tradeType", trade.getIsBuy() ? "1" : "2");
        params.put("price", formatSatoshi(trade.getPrice()));
        params.put("amount", formatSatoshi(trade.getVolume()));
        params.put("symbol", baseQuoteToSymbol(trade.getBase(), trade.getQuote()));

        final String responseText = authorizedPost(
                BASE_URL,
                "/api/v1/trade/placeOrder",
                params
        );

        System.out.println(responseText);

        Type typeToken = new TypeToken<TradeResponse>() {}.getType();
        final TradeResponse response = gson.fromJson(responseText, typeToken);
        return response;
    }

    public OpenOrdersResponse getOpenOrders(String base, String quote) throws IOException {
        final HashMap<String, String> params = new HashMap<>();
        params.put("symbol", baseQuoteToSymbol(base, quote));
        params.put("state", "0");

        final String responseText = authorizedPost(
                BASE_URL,
                "/api/v1/trade/orderInfos",
                params
        );

        Type typeToken = new TypeToken<OpenOrdersResponse>() {}.getType();
        final OpenOrdersResponse response = gson.fromJson(responseText, typeToken);
        return response;
    }

    private String baseQuoteToSymbol(String base, String quote) {
        return "coin-" + quote.toLowerCase() + "-" + base.toLowerCase();
    }

    private String authorizedPost(String baseUrl, String path, HashMap<String, String> additionalParams) throws IOException {
        final HashMap<String, String> params = getBaseParams();
        params.putAll(additionalParams);

        final String signString =
                path + "?"
                + params.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

//        System.out.println("signString: " + signString);
        final String sign;
        try {
            sign = Hmac.calculateHMAC(signString, apiKey.getSecret());
//            System.out.println("signData: " + sign);

            params.put("signData", sign);

            final List<NameValuePair> postParams = params.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            return restClient.post(baseUrl + path, postParams, new HashMap<>());
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> getBaseParams() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("accessKey", apiKey.getKey());
        params.put("nonce", ""+(System.currentTimeMillis()));
        return params;
    }

    protected static String formatSatoshi(BigDecimal bigDecimal){
        if (bigDecimal == null){
            return null;
        } else {
            return decimalFormat.format(bigDecimal);
        }
    }

}
