package eu.excitementproject.tl.composition.api;

//import java.util.List;
import java.util.Set;

import eu.excitementproject.tl.composition.exceptions.NodeMatcherException;
import eu.excitementproject.tl.structures.fragmentgraph.FragmentGraph;
import eu.excitementproject.tl.structures.search.NodeMatch;

/**
 * OMQ usecase composition-flow "NodeMatcher"
 * 
 * The module matches a given fragment graph against an entailment graph. 
 * It returns a set of node matches (matching nodes in the entailment graph
 * associated to match confidence scores) as the result.
 * 
 * The search is based on indexing the nodes in the entailment graph and 
 * searching in this index.
 *  
 * @author Kathrin Eichler
 *
 */
public interface NodeMatcherWithIndex {

	/**
	 * @param fragmentGraph - fragment graph ({@link FragmentGraph})
	 * @return set of node matches ({@link NodeMatch})
	 * @throws NodeMatcherException if node matching fails
	 */
	public Set<NodeMatch> findMatchingNodesInGraph(FragmentGraph fragmentGraph) 
			throws NodeMatcherException;

	public void indexGraphNodes();

	public void initializeSearch();

}
