package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Patient;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PatientObservationListener {

	@Inject
	EventBus eventBus;

	@ConsumeEvent(value = "observation-changed", blocking = true)
	public void onObservationChanged(Long patientId){
		Optional<Patient> patientOptional = Patient.findByIdOptional(patientId);
		if(patientOptional.isPresent()){
			Patient patient = patientOptional.get();
			log.info("obversation-changed " + patient);
			eventBus.publish("monitor", "update to " + patient.getId());
		}
	}
}
