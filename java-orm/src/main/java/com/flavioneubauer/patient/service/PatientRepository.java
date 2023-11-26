package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Observation;
import com.flavioneubauer.patient.model.Patient;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Unremovable
@Slf4j
@ApplicationScoped
public class PatientRepository {
	@Tool("Get anamnesis information for a given patient id")
	public Patient getAnamenisis(Long patientId) {
		log.info("getAnamenisis called with id " + patientId);
		Patient patient = Patient.findById(patientId);
		return patient;
	}

	@Tool("Get the last clinical results for a given patient id")
	public List<Observation> getObservations(Long patientId) {
		log.info("getObservations called with id " + patientId);
		Patient patient = Patient.findById(patientId);
		return patient.getObservationList();
	}

}
