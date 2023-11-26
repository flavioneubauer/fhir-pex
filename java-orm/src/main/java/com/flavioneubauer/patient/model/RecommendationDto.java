package com.flavioneubauer.patient.model;

import lombok.Data;

@Data
public class RecommendationDto {
	private String exam;
	private String reason;
	private String condition;
}
