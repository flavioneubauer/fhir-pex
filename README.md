# FHIR-PEX

The FHIR-Kafka application leverages the interoperability capabilities of the PEX in JAVA from InterSystems IRIS to categorize examinations into quarantine based on configured rules, dynamically directing JSON FHIR structures related to the "Observation" resource to specific topics in Apache Kafka. It capitalizes on the flexibility of PEX to define advanced quarantine criteria, enabling seamless integration between healthcare systems and the Kafka messaging infrastructure.

### How it works

When the system receives a FHIR message through Kafka, it is processed within a Java PEX, and then forwarded to topics based on rules configured in globals.

 ![](https://raw.githubusercontent.com/flavioneubauer/fhir-pex/pex/docs/pex_flow.png)

sample:
```
Set ^quarantineRule("59462-2","http://loinc.org") = $LB(">=","500") 
```

The "Observation" exams with code "59462-2" and system "http://loinc.org", in the project [testosterone.json](/data/testosterone.json), with "valueQuantity"."value" greater or equal to 500, will be sent to quarantine

the same behavior can be done for observations that use valueCodeableConcept

```
set ^quarantineRule("94531-1","http://loinc.org") = $LB("valueCodeableConcept","10828004", "http://snomed.info/sct") \
```


The project also includes a Java application that consumes messages from the "observation_quarantine" topic and persists them in the IRIS database using ORM with Quarkus, specifically employing the Quarkus Hibernate ORM dialect: quarkus.hibernate-orm.dialect=io.github.yurimarx.hibernateirisdialect.InterSystemsIRISDialect.

### Prerequisites

Make sure you have [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) and [Docker desktop](https://www.docker.com/products/docker-desktop) installed.

### Installation

Clone/git pull the repo into any local directory

```sh
$ git clone https://github.com/flavioneubauer/fhir-pex.git
```

Open the terminal in this directory and run:

```sh
$ docker-compose build && docker-compose up -d
```

### What's inside

U.I:
- InterSystems IRIS: http://localhost:52774/csp/user/EnsPortal.ProductionConfig.zen
- Eco Kafka Manager: http://localhost:8082/
- Monitor quarantine: http://localhost:8080/

### Other ports:

- SuperServer: 51774
- Java Gateway: 55555
- Zookeeper: 2181
- Kafka: 9092

### Video demo

[VIDEO](https://www.youtube.com/watch?v=73vGRVbndi8)

### authors
- [Davi Massaru Teixeira Muta](https://community.intersystems.com/user/davi-massaru-teixeira-muta)
- [Flavio Neubauer](https://community.intersystems.com/user/flavio-neubauer)