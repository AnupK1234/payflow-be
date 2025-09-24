package com.payflow.app.dto.response;

import lombok.Data;

@Data
public class DocumentResponse {
	private Long id;
	private String cloudinaryPublicId;
	private String secureUrl;
	private String purpose;
}
