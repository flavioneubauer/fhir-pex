# Sending Kafka messages via JAVA PEX for processing quarantine exam prognoses.

[Read the article published in the community](https://community.intersystems.com/post/sending-kafka-messages-java-pex-processing-quarantine-exam-prognoses)

# Introduction

This article aims to explore how the FHIR-PEX system operates and was developed, leveraging the capabilities of InterSystems IRIS.

Streamlining the identification and processing of medical examinations in clinical diagnostic centers, our system aims to enhance the efficiency and accuracy of healthcare workflows. By  integrating FHIR standards with InterSystems IRIS database Java-PEX, the system help healthcare professionals with  validation and routing capabilities, ultimately contributing to improved decision-making and patient care.

## how it works

- IRIS Interoperability:
Receives messages in the FHIR standard, ensuring integration and compatibility with healthcare data.

- Information Processing with 'PEX Java':
Processes FHIR-formatted messages and directs them to Kafka topics based on globally configured rules in the database, facilitating efficient data processing and routing, especially for examinations directed to quarantine.

- Handling Kafka Returns via External Java Backend:
Interprets only the examinations directed to quarantine, enabling the system to handle returns from Kafka through an external Java backend. It facilitates the generation of prognostic insights for healthcare professionals through Generative AI, relying on consultations of previous examination results for the respective patients.

 <img src="https://raw.githubusercontent.com/flavioneubauer/fhir-pex/master/docs/app.flow.png" alt="" />

## Development

Through the PEX (Production EXtension) by InterSystems, a extensibility tool enabling enhancement and customization of system behavior, we crafted a Business Operation. This component is tasked with processing incoming messages in the FHIR format within the system. As  example follows:

    import com.intersystems.enslib.pex.*;
    import com.intersystems.jdbc.IRISObject;
    import com.intersystems.jdbc.IRIS;
    import com.intersystems.jdbc.IRISList;
    import com.intersystems.gateway.GatewayContext;
    
    import org.apache.kafka.clients.producer.*;
    import org.apache.kafka.common.serialization.*;
    
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    
    import java.io.FileInputStream;
    import java.io.IOException;
    import java.util.Properties;

    public class KafkaOperation extends BusinessOperation {
    // Connection to InterSystems IRIS
    private IRIS iris;
    
    // Connection to Kafka
    private Producer<Long, String> producer;
    
    // Kafka server address (comma separated if several)
    public String SERVERS;
    
    // Name of our Producer
    public String CLIENTID;
    
    /// Path to Config File
    public String CONFIG;
    
    public void OnInit() throws Exception {
    [...]
    }
    
    public void OnTearDown() throws Exception {
    [...]
    }
    
    public Object OnMessage(Object request) throws Exception {
        IRISObject req = (IRISObject) request;
        LOGINFO("Received object: " + req.invokeString("%ClassName", 1));
    
        // Create record
        String value = req.getString("Text");
        String topic = getTopicPush(req);
        final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, value);
    
        // Send new record
        RecordMetadata metadata = producer.send(record).get();
    
        // Return record info
        IRISObject response = (IRISObject)(iris.classMethodObject("Ens.StringContainer","%New",topic+"|"+metadata.offset()));
        return response;
    }
    
    private Producer<Long, String> createProducer() throws IOException {
    [...]
    }
    
    private String getTopicPush(IRISObject req) {
    [...]
    }
    
    [...]
    }
`

Within the application, the getTopicPush method takes on the responsibility of identifying the topic to which the message will be sent.

The determination of which topic the message will be sent to is contingent upon the existence of a rule in the "quarantineRule" global, as read within IRIS.
    
    String code = FHIRcoding.path("code").asText();
    String system = FHIRcoding.path("system").asText();

    IRISList quarantineRule = iris.getIRISList("quarantineRule",code,system);

     String reference = quarantineRule.getString(1);
     String value = quarantineRule.getString(2);

     String observationValue = fhir.path("valueQuantity").path("value").asText()

When the global `^quarantineRule` exists, validation of the FHIR object can be validated.

    private boolean quarantineValueQuantity(String reference, String value, String observationValue) {
        LOGINFO("quarantine rule reference/value: " + reference + "/" + value);
        double numericValue = Double.parseDouble(value);
        double numericObservationValue = Double.parseDouble(observationValue);

        if ("<".equals(reference)) {
            return numericObservationValue < numericValue;
        }
        else if (">".equals(reference)) {
            return numericObservationValue > numericValue;
        }
        else if ("<=".equals(reference)) {
            return numericObservationValue <= numericValue;
        }
        else if (">=".equals(reference)) {
            return numericObservationValue >= numericValue;
        }
        
        return false;
    }

Practical Example:

When defining a global, such as:
 
    Set ^quarantineRule("59462-2","http://loinc.org") = $LB(">","500") 

This establishes a rule to "59462-2" code and  ""http://loinc.org"" system in the global ^quarantineRule, specifying a condition where the value when greater than 500 is defined as quarantine. In the application, the getTopicPush method can then use this rule to determine the appropriate topic for sending the message based on the validation outcome.

Given the assignment, the JSON below would be sent to quarantine since it matches the condition specified by having:
```
 {
          "system": "http://loinc.org",
          "code": "59462-2",
          "display": "Testosterone"
}

"valueQuantity": { "value": 550, "unit": "ng/dL", "system": "http://unitsofmeasure.org", "code": "ng/dL" }
```

FHIR Observation:
```
{
    "resourceType": "Observation",
    "id": "3a8c7d54-1a2b-4c8f-b54a-3d2a7efc98c9",
    "status": "final",
    "category": [
      {
        "coding": [
          {
            "system": "http://terminology.hl7.org/CodeSystem/observation-category",
            "code": "laboratory",
            "display": "laboratory"
          }
        ]
      }
    ],
    "code": {
      "coding": [
        {
          "system": "http://loinc.org",
          "code": "59462-2",
          "display": "Testosterone"
        }
      ],
      "text": "Testosterone"
    },
    "subject": {
      "reference": "urn:uuid:274f5452-2a39-44c4-a7cb-f36de467762e"
    },
    "encounter": {
      "reference": "urn:uuid:100b4a8f-5c14-4192-a78f-7276abdc4bc3"
    },
    "effectiveDateTime": "2022-05-15T08:45:00+00:00",
    "issued": "2022-05-15T08:45:00.123+00:00",
    "valueQuantity": {
      "value": 550,
      "unit": "ng/dL",
      "system": "http://unitsofmeasure.org",
      "code": "ng/dL"
    }
}

```

### The Quarkus Java application

After sending to the desired topic, a Quarkus Java application was built to receive examinations in quarantine.

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

This segment of the system is tasked with persisting the information received from Kafka, storing it in the patient's observations within the database, and notifying the occurrence to the monitor.

### The monitor

Finally, the system's monitor is responsible for providing a simple front-end visualization. This allows healthcare professionals to review patient/examination data and take necessary actions.

<img src="https://raw.githubusercontent.com/flavioneubauer/fhir-pex/master/docs/monitor.png" alt="" />

### Implementation of langchainPT

Through the monitor, the system enables healthcare professionals to request recommendations from the Generative AI.

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


segue implementação de langchain4j

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

In this way, the system can assist healthcare professionals in making decisions and carrying out actions.

### Video demo

[VIDEO](https://www.youtube.com/watch?v=73vGRVbndi8)

## Authors
- [Davi Massaru Teixeira Muta](https://community.intersystems.com/user/davi-massaru-teixeira-muta)
- [Flavio Neubauer](https://community.intersystems.com/user/flavio-neubauer)


NOTE: 

The application [https://openexchange.intersystems.com/package/fhir-pex](https://openexchange.intersystems.com/package/fhir-pex) is currently participating in the InterSystems Java Contest 2023. Feel free to explore the solution further, and please don't hesitate to reach out if you have any questions or need additional information. We recommend running the application in your local environment for a hands-on experience. 
Thank you for the opportunity &#x1f600;!