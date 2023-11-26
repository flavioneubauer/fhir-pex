package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Observation;
import com.flavioneubauer.patient.model.Patient;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import dev.langchain4j.agent.tool.Tool;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PatientService {

	@Inject
	ObservationMapperService observationMapperService;

	@Inject
	EventBus eventBus;

	@Transactional
	public Patient addObservation(String id, QuarentineObservation quarentineObservation) {
		Optional<Patient> patientOptional = Patient.find("reference = ?1", quarentineObservation.getSubject()
						.getReference())
				.firstResultOptional();
		if (patientOptional.isPresent()) {
			Patient patient = patientOptional.get();
			patient.getObservationList()
					.add(observationMapperService.fromQuarentine(quarentineObservation));
			patient.persistAndFlush();
			eventBus.publish("observation-changed", patient.getId());
			return patient;
		}
		return null;
	}

	@Tool("Get anamnesis information for a given patient")
	public Patient getAnamenisis(Long patientId){
		log.info("getAnamenisis called with id " + patientId);
		Patient patient = Patient.findById(patientId);
		return patient;
	}

	@Tool("Get the last clinical results for a given patient")
	public List<Observation> getObservations(Long patientId){
		log.info("getObservations called with id " + patientId);
		Patient patient = Patient.findById(patientId);
		return patient.getObservationList();
	}

}
