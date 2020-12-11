package test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import test.api.resource.PersonResource;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class RESTServiceClient {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URI_STRING = "http://localhost:8080/rest";
    private static final String GET_REQUEST = "GET";
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;
    private HttpURLConnection connection;

	public static void main(String[] args) throws Exception {
        RESTServiceClient app = new RESTServiceClient();

        // Get JSON builder
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        // List of persons
        List<PersonResource> persons = new ArrayList<>();

        // Set initial parameters
        Map<String, String> parameters = new HashMap<>();

        // Get first page
        parameters.put("page", "5");
        String json = app.getRequest(GET_REQUEST, BASE_URI_STRING, parameters);
        PersonResource person = gson.fromJson(json, PersonResource.class);
        persons.add(person);

        // Repeat while number of pages is greater than 0
        while (person.getNumberOfPages() > 0) {
            Integer page = person.getNumberOfPages();

            parameters.put("page", page.toString());
            json = app.getRequest(GET_REQUEST, BASE_URI_STRING, parameters);
            person = gson.fromJson(json, PersonResource.class);
            persons.add(person);

            parameters.clear();
        }

        // List of persons whose name starts with 'H'
        List<PersonResource> list = persons.stream().filter(p -> p.getFirstName().startsWith("H")).collect(Collectors.toList());
        // Average age
        Double averageAge = persons.stream().collect(Collectors.averagingInt(p -> p.getAge()));
        // Several statistics like min, max, average and count
        IntSummaryStatistics ageSummary = persons.stream().collect(Collectors.summarizingInt(p -> p.getAge()));
        // The oldest person
        persons.stream().reduce((p1, p2) -> p1.getAge() > p2.getAge() ? p1 : p2).ifPresent(System.out::println);
        // Making a phrase with all persons
        String phrase = persons
            .stream()
            .filter(p -> p.getAge() > 18)
            .map(p -> p.getFirstName() + " " + p.getLastName())
            .collect(
                Collectors
                    .joining(" and ", "In USA ", " are of legal age."));
    }

	private String getRequest(String method, String baseUri, Map<String, String> parameters) throws Exception {
	    URI uri = new URI(baseUri);
        uri = setParamenters(uri, parameters);

	    // Get http connection
        connection = (HttpURLConnection) uri.toURL().openConnection();
        // Set REST method
        connection.setRequestMethod(method);
        // Set request header
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");
        // Configure timeout
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        // Get result
        String result = connection.getResponseCode() == 200 ? readFromConnection() : "";

        // Close connection
        connection.disconnect();

        return result;
    }

    @NotNull
    private String readFromConnection() throws Exception {
        InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(inputStream)) {
            String output;

            while ((output = reader.readLine()) != null)
                response.append(output);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return response.toString();
    }

//    private void setParamenters(@NotNull Map<String, String> parameters) throws Exception {
//        connection.setDoOutput(true);
//
//        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
//            outputStream.writeBytes(getParametersString(parameters));
//        }
//    }

    private URI setParamenters(@NotNull URI uri, @NotNull Map<String, String> parameters) throws Exception {
        return new URI(
            uri.getScheme(),
            uri.getAuthority(),
            uri.getPath(),
            getParametersString(parameters),
            null);
    }

    private String getParametersString(@NotNull Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();

        return resultString.length() > 0
            ? resultString.substring(0, resultString.length() - 1)
            : resultString;
    }
}
