package es.um.dis.tecnomod.iri2label;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class StringUtilsTest {

	@Test
	public void test1Iri2string() {
		StringUtils stringUtils = new StringUtils();
		IRI iri = IRI.create("http://www.semanticweb.org/adrian/ontologies/2024/3/Archery#Category");
		String label = stringUtils.iri2string(iri);
		String expectedLabel = "category";
		assertEquals(expectedLabel, label);
	}
	
	@Test
	public void test2Iri2string() {
		StringUtils stringUtils = new StringUtils();
		IRI iri = IRI.create("http://www.semanticweb.org/adrian/ontologies/2024/3/Archery#PopulatedArea");
		String label = stringUtils.iri2string(iri);
		String expectedLabel = "populated area";
		assertEquals(expectedLabel, label);
	}
	
	@Test
	public void test3Iri2string() {
		StringUtils stringUtils = new StringUtils();
		IRI iri = IRI.create("http://www.semanticweb.org/adrian/ontologies/2024/3/Archery#MenW1Open(RecComp)");
		String label = stringUtils.iri2string(iri);
		String expectedLabel = "";
		assertEquals(expectedLabel, label);
	}
	
	@Test
	public void test4Iri2string() {
		StringUtils stringUtils = new StringUtils();
		IRI iri = IRI.create("http://www.semanticweb.org/adrian/ontologies/2024/3/Archery#VisuallyImpaired2/3");
		String label = stringUtils.iri2string(iri);
		String expectedLabel = "";
		assertEquals(expectedLabel, label);
	}
	
	@Test
	public void test5Iri2string() {
		StringUtils stringUtils = new StringUtils();
		IRI iri = IRI.create("http://www.semanticweb.org/adrian/ontologies/2024/3/Archery#10000");
		String label = stringUtils.iri2string(iri);
		String expectedLabel = "";
		assertEquals(expectedLabel, label);
	}
	
	@Test
	public void test6Iri2string() {
		StringUtils stringUtils = new StringUtils();
		IRI iri = IRI.create("http://www.wurvoc.org/vocabularies/om-1.8/Measurement_scale");
		String label = stringUtils.iri2string(iri);
		String expectedLabel = "measurement scale";
		assertEquals(expectedLabel, label);
	}

}
