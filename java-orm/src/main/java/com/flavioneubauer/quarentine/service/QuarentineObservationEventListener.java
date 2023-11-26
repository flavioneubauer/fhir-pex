package com.flavioneubauer.quarentine.service;

import com.flavioneubauer.patient.service.PatientService;
import com.flavioneubauer.quarentine.model.MonitorEventDto;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class QuarentineObservationEventListener {

	@Inject
	PatientService patientService;

	@Inject
	EventBus eventBus;

	@Transactional
	@Incoming("observation_quarantine")
	public CompletionStage<Void> onIncomingMessage(Message<QuarentineObservation> quarentineObservationMessage) {
		var quarentineObservation = quarentineObservationMessage.getPayload();
		var patientId = quarentineObservation.getSubject()
				.getReference();
		var patient = patientService.addObservation(patientId, quarentineObservation);
		publishSockJsEvent(patient.getId(), quarentineObservation.getCode()
				.getText());
		return quarentineObservationMessage.ack();
	}

	private void publishSockJsEvent(Long patientId, String text) {
		eventBus.publish("monitor", MonitorEventDto.builder()
				.id(patientId)
				.message(" is on quarentine list by observation ." + text)
				.build());
	}

}
