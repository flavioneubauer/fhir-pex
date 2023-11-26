package com.flavioneubauer.patient.resource;

import com.flavioneubauer.patient.model.PatientRecomendationDto;
import com.flavioneubauer.patient.service.PatientRecomendationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("patients")
public class PatientResource {

	@Inject
	PatientRecomendationService patientRecomendationService;

	@GET
	public PatientRecomendationDto getRecomendedExame(Long id){
		return patientRecomendationService.getRecomendation(id);
	}
}
