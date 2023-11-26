package com.flavioneubauer.patient.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
@Data
public class PatientRecomendationDto {
	private String patientId;
	private String weight;
	private List<RecommendationDto> recommendations = new ArrayList<>();
	private String explanation;
}
