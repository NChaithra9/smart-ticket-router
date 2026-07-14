package com.example.smart_ticket_router.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
//marks this class as a Spring component, allowing it to be automatically detected and registered as a bean in the Spring application context
@Component
public class OpenAIClient {
    //Store the openai api key and model in application.properties file and inject them here using @Value annotation
    @Value("${openai.api.key}")
    private String apiKey;
    //Store the openai model in application.properties file and inject it here using @Value annotation
    @Value("${openai.model}")
    private String model;
    @Value("${openai.embedding.model}")
    private String embeddingModel;
    //create a RestClient instance to make HTTP requests to the OpenAI API
    private final RestClient restClient = RestClient.create();
    //Accepts the prompt returns string response from the openai api
    public String askOpenAI(String prompt) {
        //create a map representing the JSON
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(//creates JSON array
                        Map.of(
                                "role", "system",//ex:role:system 
                                "content", "Return only valid JSON."//ex:content:Return only valid JSON.
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0//temperature controls the randomness of the output. A temperature of 0 means the model will always choose the most likely next word, while a higher temperature will result in more random output.
        );
        //sending a http post request
        Map<?, ?> response = restClient.post()
                 //sets endpoint URL
                .uri("https://api.openai.com/v1/chat/completions")
                //Adds headers to the request, including the content type
                .contentType(MediaType.APPLICATION_JSON)
                //adds authorization header with the API key for authentication
                .header("Authorization", "Bearer " + apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);

        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");

        return message.get("content").toString();

    }
    @SuppressWarnings("unchecked")
public List<Float> getEmbedding(String text) {

    Map<String, Object> request = Map.of(
            "model", embeddingModel,
            "input", text
    );

    Map<String, Object> response = restClient.post()
            .uri("https://api.openai.com/v1/embeddings")
            .header("Authorization", "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(Map.class);

    List<Map<String, Object>> data =
            (List<Map<String, Object>>) response.get("data");

    Map<String, Object> firstEmbedding = data.get(0);

    List<Double> embedding =
            (List<Double>) firstEmbedding.get("embedding");

    return embedding.stream()
            .map(Double::floatValue)
            .toList();
}

}