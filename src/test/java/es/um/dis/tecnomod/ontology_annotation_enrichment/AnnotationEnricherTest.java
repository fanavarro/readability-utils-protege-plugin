package es.um.dis.tecnomod.ontology_annotation_enrichment;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import static org.junit.Assert.assertTrue;

public class AnnotationEnricherTest {

	private static final String TEST_ONTOLOGY = "/test1.owl";
	private static final String ENTITY_TO_ENRICH = "http://purl.obolibrary.org/obo/IAO_0000115";
	@Test
	public void testEnrichEntity() throws OWLOntologyCreationException {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(TEST_ONTOLOGY));
		OWLEntity entityToEnrich = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(IRI.create(ENTITY_TO_ENRICH));
		int initialAnnotations = EntitySearcher.getAnnotationAssertionAxioms(entityToEnrich, ontology).size();
		AnnotationEnricher enricher = new AnnotationEnricher(ontology);
		enricher.enrichEntity(entityToEnrich);
		
		int finalAnnotations = EntitySearcher.getAnnotationAssertionAxioms(entityToEnrich, ontology).size();
		assertTrue(finalAnnotations > initialAnnotations);
	}

}
