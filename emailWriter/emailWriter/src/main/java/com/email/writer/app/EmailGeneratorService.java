package com.email.writer.app;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Service
public class EmailGeneratorService {

	private final WebClient webClient;

	public EmailGeneratorService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	@Value("${gemini.api.url}")
	private String geminiApiUrl;
	@Value("${gemini.api.key}")
	private String geminiApiKey;

	public String generateEmailReply(EmailRequest emailRequest) {

		// Build the prompt
		String prompt = buildPrompt(emailRequest);

		// Craft a request
		Map<String, Object> requestBody = Map.of("contents",
				new Object[] { Map.of("parts", new Object[] { Map.of("text", prompt) }) });

		// Do request and get response
		String response = webClient.post().uri(geminiApiUrl + "?key=" + geminiApiKey)
				.header("Content-Type", "application/json").bodyValue(requestBody).retrieve().bodyToMono(String.class)
				.block();

		// Extract response and Return
		return extractResponseContent(response);
	}

	private String extractResponseContent(String response) {
		try {

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

		} catch (Exception e) {
			return "Error Processing request : " + e.getMessage();
		}
	}

	private String buildPrompt(EmailRequest emailRequest) {
		StringBuilder prompt = new StringBuilder();
		prompt.append(
				"Generate a professional email reply for the following email content.please don't generate a subject line. ");
		if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
			prompt.append("Use a ").append(emailRequest.getTone()).append("tone.");

		}
		prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
		return prompt.toString();
	}

}
