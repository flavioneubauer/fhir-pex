package com.flavioneubauer.patient.resource;

import com.flavioneubauer.patient.model.PatientRecomendationDto;
import com.flavioneubauer.patient.service.PatientRecomendationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("patients")
public class PatientResource {

	@Inject
	PatientRecomendationService patientRecomendationService;

	@GET
	@Path("{patientId}/recomendations")
	public PatientRecomendationDto getRecomendedExame(@PathParam("patientId") Long id){
		return patientRecomendationService.getRecomendation(id);
	}
}
