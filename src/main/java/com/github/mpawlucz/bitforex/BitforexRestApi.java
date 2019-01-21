package com.github.mpawlucz.bitforex;

import com.github.mpawlucz.bitforex.domain.response.BalanceResponse;
import com.github.mpawlucz.bitforex.rest.RestClient;
import com.github.mpawlucz.bitforex.sign.ApiKey;
import com.github.mpawlucz.bitforex.sign.Hmac;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
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

    final static DecimalFormat volumeFormat;
    final static DecimalFormat priceFormat;
    public static final String BASE_URL = "https://api.bitforex.com";

    static {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        volumeFormat = new DecimalFormat("#0.########", symbols);
        volumeFormat.setMinimumFractionDigits(8);
        volumeFormat.setMaximumFractionDigits(8);
        volumeFormat.setDecimalSeparatorAlwaysShown(true);
        volumeFormat.setGroupingUsed(false);
    }
    static {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        priceFormat = new DecimalFormat("#0.##########", symbols);
        priceFormat.setMinimumFractionDigits(10);
        priceFormat.setMaximumFractionDigits(10);
        priceFormat.setDecimalSeparatorAlwaysShown(true);
        priceFormat.setGroupingUsed(false);
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

}
