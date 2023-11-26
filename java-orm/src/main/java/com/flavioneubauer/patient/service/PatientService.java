package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Observation;
import com.flavioneubauer.patient.model.Patient;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import dev.langchain4j.agent.tool.Tool;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

	@Tool("get patient anamnesis information for patient id")
	public Patient getAnamenisis(Long id){
		Patient patient = Patient.findById(id);
		return patient;
	}

	@Tool("get patient exams data for patient id")
	public List<Observation> getObservations(Long id){
		Patient patient = Patient.findById(id);
		return patient.getObservationList();
	}

}
