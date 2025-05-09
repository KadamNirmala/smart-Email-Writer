package com.email.writer.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*") // Allow all origins
public class EmailGeneratorController {

	private final EmailGeneratorService emailGeneratorService;

	@Autowired
	public EmailGeneratorController(EmailGeneratorService emailGeneratorService) {
		this.emailGeneratorService = emailGeneratorService;
	}

	@PostMapping("/generate")
	public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
		String response = emailGeneratorService.generateEmailReply(emailRequest);
		return ResponseEntity.ok(response);

	}

}
