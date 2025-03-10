import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Map;

public class GPTClient {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions"; // Set your actual API URL
    private static final String API_KEY = "your_api_key_here"; // Replace with your API Key
    private final WebClient webClient;

    public GPTClient() {
        this.webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getDoc(String codeChunk) {
        String prompt = "Create a developer-friendly documentation based on the given code:\n\n" + codeChunk;

        Map<String, Object> reqBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        try {
            String response = webClient.post()
                    .bodyValue(reqBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray choices = jsonObject.getJSONArray("choices");

            if (choices.length() > 0) {
                JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                return message.getString("content");
            } else {
                return "Error: No response from API";
            }
        } catch (WebClientResponseException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected Error: " + e.getMessage();
        }
    }
}
