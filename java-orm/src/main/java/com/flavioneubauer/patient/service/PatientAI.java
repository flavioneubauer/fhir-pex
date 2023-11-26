package com.flavioneubauer.patient.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = PatientService.class)
public interface PatientAI {

	@SystemMessage("""
			You are a health care assistant AI. You have to recommend exams for patients based on history information.
			""")
	@UserMessage("""
			 Your task is to recommend clinical exams for the patient {{patientId}}.

			 To perform this recommendation, perform the following actions:
			 1 - Retrieve anamnesis information of the patient {{patientId}}
			 2 - Retrieve the last clinical results for the patient {{patientId}}.
			 3 - Eval results against well known conditions of health care.

			 Answer with a **single** JSON document containing:
			 - the patient id in the 'patientId' key
			 - the exam recommendation list in the 'recommendations' key
			 - the 'explanation' key containing a explanation of your answer, especially about well known diseases.

			Your response must be just the raw JSON document, without ```json, ``` or anything else.
			 """)
	String recommendExams(Long patientId);
}
