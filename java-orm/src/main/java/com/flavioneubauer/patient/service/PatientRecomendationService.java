package com.flavioneubauer.patient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flavioneubauer.patient.model.PatientRecomendationDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class PatientRecomendationService {

	@Inject
	PatientAI patientAI;

	public PatientRecomendationDto getRecomendation(Long id){
		try {
			var result = patientAI.recommendExams(id);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, PatientRecomendationDto.class);
		}catch(Exception exception){
			log.error("Fail to create exam recommendation", exception);
			return null;
		}
	}
}
