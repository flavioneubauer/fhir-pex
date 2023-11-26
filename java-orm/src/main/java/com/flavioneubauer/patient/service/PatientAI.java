package com.flavioneubauer.patient.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(chatMemoryProviderSupplier = RegisterAiService.BeanChatMemoryProviderSupplier.class, tools = {PatientRepository.class})
public interface PatientAI {

	@SystemMessage("""
			You are a health care assistant AI. You have to recommend exams for patients based on history information.
			""")
	@UserMessage("""
			 Your task is to recommend clinical exams for the patient id {patientId}.

			 To complete this task, perform the following actions:
			 1 - Retrieve anamnesis information for patient id {patientId}.
			 2 - Retrieve the last clinical results for patient id {patientId}, using the property 'name' as the name of exam and 'value' as the value.
			 3 - Analyse results against well known conditions of health care.

			 Answer with a **single** JSON document containing:
			 - the patient id in the 'patientId' key
			 - the patient weight in the 'weight' key
			 - the exam recommendation list in the 'recommendations' key, with properties exam, reason and condition.
			 - the 'explanation' key containing an explanation of your answer, especially about well known diseases.

			Your response must be just the raw JSON document, without ```json, ``` or anything else.
			 """)
	String recommendExams(Long patientId);
}
