package com.flavioneubauer.patient.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PatientEmbedingStore {

	private static final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
	@Inject
	EmbeddingModel embeddingModel;

	public void ingest(List<Document> documents) {
		var ingestor = EmbeddingStoreIngestor.builder()
				.embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel)
				.documentSplitter(DocumentSplitters.recursive(500, 0))
				.build();
		ingestor.ingest(documents);
	}
}
