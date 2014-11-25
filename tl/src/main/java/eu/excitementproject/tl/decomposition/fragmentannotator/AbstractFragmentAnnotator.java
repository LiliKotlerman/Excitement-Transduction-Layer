/**
 * 
 */
package eu.excitementproject.tl.decomposition.fragmentannotator;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.tl.decomposition.api.FragmentAnnotator;
import eu.excitementproject.tl.decomposition.exceptions.FragmentAnnotatorException;



/**
 * 
An implementation of the {@link FragmentAnnotator} interface.
May need to call LAP. The needed LAP should be passed via Constructor. 
Also, any additional configurable parameters of this module implementation should be clearly exposed in the Constructor.
 
 * If we decide to keep abstract implementations, they can hold the methods 
 * which we expect to be common for different implementations 

 * @author Lili && vivi@fbk
 */
public abstract class AbstractFragmentAnnotator implements FragmentAnnotator{

	private final LAPAccess lap;
	
	/**
	 * @param lap - The implementation may need to call LAP. The needed LAP should be passed via Constructor.
	 * @throws FragmentAnnotatorException
	 */
	public AbstractFragmentAnnotator(LAPAccess lap) throws FragmentAnnotatorException{
		this.lap=lap;
	}
	
	/**
	 * @return the LAP passed in the constructor to the FragmentAnnotator implementation
	 */
	public LAPAccess getLap() {
		return this.lap;
	}
	
	/**
	 * The method all classes in the hierarchy should implement
	 * 
	 * @param aJCas -- the CAS object that should have the interaction text (at least, possibly also keywords)
	 */
	public abstract void annotateFragments(JCas aJCas) throws FragmentAnnotatorException ;
}
