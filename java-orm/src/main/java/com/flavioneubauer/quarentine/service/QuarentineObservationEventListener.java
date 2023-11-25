package com.flavioneubauer.quarentine.service;

import com.flavioneubauer.patient.model.Patient;
import com.flavioneubauer.patient.service.PatientService;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.*;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class QuarentineObservationEventListener {

	@Inject
	PatientService patientService;

	@Transactional
	@Incoming("observation_quarantine")
	public CompletionStage<Void> onIncomingMessage(Message<QuarentineObservation> quarentineObservationMessage) {

		System.out.println(quarentineObservationMessage.getPayload());
		var quarentineObservation = quarentineObservationMessage.getPayload();
		var patientId = quarentineObservation.getSubject().getReference();
		patientService.addObservation(patientId, quarentineObservation);
		// enviar para o llm o peso e colesterol para avaliar se é um risco alto ou baixo
		return quarentineObservationMessage.ack();
	}

}
