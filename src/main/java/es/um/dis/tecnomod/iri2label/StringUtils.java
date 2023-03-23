package es.um.dis.tecnomod.iri2label;

import org.semanticweb.owlapi.model.IRI;

public class StringUtils {
	public String iri2string(IRI iri) {
		String result = iri.getFragment();
		if (result.contains("_")) {
			result = result.replaceAll("_", " ");
		} else {
			result = String.join(" ", org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(result));
		}
		return result.toLowerCase();
	}
}
