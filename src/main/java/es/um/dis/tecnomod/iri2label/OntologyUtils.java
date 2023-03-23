package es.um.dis.tecnomod.iri2label;

import org.openrdf.model.vocabulary.RDFS;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyUtils {
	public void addRDFSLabel(OWLOntology ontology, IRI owlEntityIRI, String value, String lang) {
		this.addAnnotation(ontology, owlEntityIRI, IRI.create(RDFS.LABEL.stringValue()), value, lang);
	}
	
	public void addAnnotation(OWLOntology ontology, IRI owlEntityIRI, IRI annotationPropertyIRI, String value, String lang) {
		OWLAnnotationProperty annotationProperty = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(annotationPropertyIRI);
		
		ontology.getEntitiesInSignature(owlEntityIRI).stream().forEach(owlEntity -> {
			this.addAnnotation(ontology, owlEntity, annotationProperty, value, lang);
		});
	}
	
	public void addAnnotation(OWLOntology ontology, OWLEntity owlEntity, OWLAnnotationProperty annotationProperty, String value, String lang) {
		OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLLiteral literal = dataFactory.getOWLLiteral(value, lang);
		OWLAxiom axiom = dataFactory.getOWLAnnotationAssertionAxiom(annotationProperty, owlEntity.getIRI(), literal);
		AddAxiom addAxiom = new AddAxiom(ontology, axiom);
		ontology.getOWLOntologyManager().applyChange(addAxiom);
	}
}
