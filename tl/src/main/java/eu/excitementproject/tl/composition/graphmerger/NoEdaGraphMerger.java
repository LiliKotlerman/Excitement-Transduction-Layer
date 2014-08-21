package eu.excitementproject.tl.composition.graphmerger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.tl.composition.exceptions.GraphMergerException;
import eu.excitementproject.tl.laputils.CachedLAPAccess;
import eu.excitementproject.tl.structures.fragmentgraph.FragmentGraph;
import eu.excitementproject.tl.structures.rawgraph.EntailmentGraphRaw;

/**
 * This graph merger performs the merge by only copying all fragment graphs into one raw graph (with unification of EU mentions under one EU). 
 * Note that absence of an edge in the merged graph should be interpreted as "don't know", but can also be understood as "no entailment"  

 * @author Lili Kotlerman
 *
 */public class NoEdaGraphMerger extends AbstractGraphMerger {

	public NoEdaGraphMerger(CachedLAPAccess lap, EDABasic<?> eda)
			throws GraphMergerException {
		super(lap, eda);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EntailmentGraphRaw mergeGraphs(Set<FragmentGraph> fragmentGraphs,
			EntailmentGraphRaw workGraph) throws GraphMergerException, LAPException {
		List<FragmentGraph> fg = new LinkedList<FragmentGraph>(fragmentGraphs);
		Collections.sort(fg, new FragmentGraph.CompleteStatementComparator());
		// Iterate over the list of fragment graphs and merge them one by one
		for (FragmentGraph fragmentGraph : fg){
			workGraph=mergeGraphs(fragmentGraph, workGraph);
		}
		return workGraph;
	}

	@Override
	public EntailmentGraphRaw mergeGraphs(FragmentGraph fragmentGraph,
			EntailmentGraphRaw workGraph) throws GraphMergerException, LAPException {
		
		// If the work graph is empty or null - just copy the fragment graph nodes/edges (there's nothing else to merge) and return the resulting graph
		if (workGraph==null) return new EntailmentGraphRaw(fragmentGraph, false);
		if (workGraph.isEmpty()) return new EntailmentGraphRaw(fragmentGraph, false);
		
		 
		// else - merge new fragment graph into work graph 		
		workGraph.copyFragmentGraphNodesAndEntailingEdges(fragmentGraph);
		
		return workGraph;		
	}

}
