package dc.rmq;

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

        iris = GatewayContext.getIRIS();
        LOGINFO("Initialized IRIS");

        LOGINFO(String.format("SERVERS: %s CLIENTID: %s", SERVERS, CLIENTID));
        producer = createProducer();
        LOGINFO("Initialized Kafka Producer");

        return;
    }

    public void OnTearDown() throws Exception {
        producer.flush();
        producer.close();
        return;
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
        Properties props = new Properties();
        if (CONFIG == null || CONFIG.isEmpty() || CONFIG.trim().isEmpty()) {
            LOGINFO("Trying settings config");
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS);
            props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENTID);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        } else {
            LOGINFO("Trying file config");
            FileInputStream in = new FileInputStream(CONFIG);
            props.load(in);
         }
        return new KafkaProducer<>(props);
    }

    private String getTopicPush(IRISObject req){


        String value = req.getString("Text");

        LOGINFO("Observation Full Text: " + value);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fhir = objectMapper.readTree(value);

            JsonNode codingArray = fhir.path("code").path("coding");
            for (JsonNode coding : codingArray) {
                String code = coding.path("code").asText();
                String system = coding.path("system").asText();
                LOGINFO("validating rule to: " + code + " - " + system);

                IRISList quarantineRule = iris.getIRISList("quarantineRule",code,system);
                if (quarantineRule != null){
                    LOGINFO("Found rule: " + code + " - " + system);
                    String quarantineRuleReference = quarantineRule.getString(1);
                    String quarantineRuleValue = quarantineRule.getString(2);
                    LOGINFO("quarantine rule reference/value: " + quarantineRuleReference + quarantineRuleValue);
                    if (quarantineObservation(quarantineRuleReference, quarantineRuleValue, fhir.path("valueQuantity").path("value").asText() )){
                        return "observation_quarantine";
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "observation_pass";
    }

    private boolean quarantineObservation(String reference, String value, String observationValue) {
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

}
