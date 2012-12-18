package decomposition;

import java.util.List;

import org.apache.uima.jcas.JCas;

import decomposition.entities.EntailmentUnit;
import decomposition.entities.TextualInput;

/**
 * This interface needs to be implemented for each scenario to create entailment units from a textual input CAS. 
 * 
 * @author Kathrin
 *
 */
public interface EntailmentUnitCreator {
	
	/**
	 * This method adds fragment annotation to the textual input CAS (e.g., based on linguistic annotation) 
	*/
	public JCas addFragmentAnnotation(JCas in);

	/**
	 * This method generates entailment units from the textual input CAS (e.g., based on fragment annotation) 
	 */ 
	public List<EntailmentUnit> createEntailmentUnits(JCas in); 

}