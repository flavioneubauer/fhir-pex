package com.flavioneubauer.patient.model;

import lombok.Data;

import java.util.List;

@Data
public class PatientRecomendationDto {
	private String patientId;
	private List<String> recommendations;
	private String explanation;
}
