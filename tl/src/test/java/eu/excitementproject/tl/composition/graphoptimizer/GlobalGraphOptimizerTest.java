package eu.excitementproject.tl.composition.graphoptimizer;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.MaxEntClassificationEDA;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.tl.composition.api.GraphMerger;
import eu.excitementproject.tl.composition.api.GraphOptimizer;
import eu.excitementproject.tl.composition.exceptions.EntailmentGraphCollapsedException;
import eu.excitementproject.tl.composition.exceptions.EntailmentGraphRawException;
import eu.excitementproject.tl.composition.exceptions.GraphMergerException;
import eu.excitementproject.tl.composition.exceptions.GraphOptimizerException;
import eu.excitementproject.tl.composition.graphmerger.StructureBasedGraphMerger;
import eu.excitementproject.tl.composition.graphmerger.LegacyAutomateWP2ProcedureGraphMerger;
import eu.excitementproject.tl.laputils.CachedLAPAccess;
import eu.excitementproject.tl.structures.collapsedgraph.EntailmentGraphCollapsed;
import eu.excitementproject.tl.structures.fragmentgraph.FragmentGraph;
import eu.excitementproject.tl.structures.rawgraph.EntailmentGraphRaw;

/**
 * 
 * @author ??
 *
 */
@SuppressWarnings("unused")
public class GlobalGraphOptimizerTest {
	
	private final Logger logger = Logger.getLogger(this.getClass());

	@Test
	public void test() {

					try {
						logger.info("**** Test graph optimizer: merged graph ****");

						CachedLAPAccess lap = new CachedLAPAccess(new TreeTaggerEN());
						File configFile = new File("./src/test/resources/EOP_configurations/MaxEntClassificationEDA_Base_EN.xml");				
						CommonConfig config = null;
						config = new ImplCommonConfig(configFile);
						MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();	
						meceda.initialize(config);  
						GraphMerger merger = new StructureBasedGraphMerger(lap,meceda); 
						
						Set<FragmentGraph> fragmentGraphs = FragmentGraph.getSampleOutput();
						logger.info("Merged raw graph:");			
						EntailmentGraphRaw rawGraph = merger.mergeGraphs(fragmentGraphs);
						logger.info(rawGraph.toString());
						rawGraph.toDOT("./src/test/outputs/rawGraph.txt");
						
						logger.info("**** Optimizing the the raw graph ****");
						GraphOptimizer collapser = new GlobalGraphOptimizer();
						EntailmentGraphCollapsed finalGraph = collapser.optimizeGraph(rawGraph);
						logger.info("Done:\n"+finalGraph.toString());
						finalGraph.toDOT("./src/test/outputs/collapsedGraph.txt");
						
						logger.info("**** Test graph optimizer: sample graph ****");
						rawGraph = EntailmentGraphRaw.getSampleOuput(false);
						logger.info(rawGraph.toString());
						rawGraph.toDOT("./src/test/outputs/sampleRawGraph.txt");
						logger.info("**** Optimizing the the sample graph ****");
						finalGraph = collapser.optimizeGraph(rawGraph, 0.0);
						logger.info("Done:\n"+finalGraph.toString());
						finalGraph.toDOT("./src/test/outputs/collapsedGraphFromSample.txt");						
					} catch (ConfigurationException | EDAException | ComponentException |
							GraphMergerException | GraphOptimizerException | EntailmentGraphRawException | EntailmentGraphCollapsedException e) {
						e.printStackTrace();
						fail(e.getMessage());
					}
				
			
			
	}

}
