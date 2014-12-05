package eu.excitementproject.tl.experiments.NICE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.transform.TransformerException;

import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.FNR_EN;
import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.EditDistanceEDA;
import eu.excitementproject.eop.core.MaxEntClassificationEDA;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.tl.composition.api.GraphOptimizer;
import eu.excitementproject.tl.composition.exceptions.EntailmentGraphCollapsedException;
import eu.excitementproject.tl.composition.exceptions.EntailmentGraphRawException;
import eu.excitementproject.tl.composition.exceptions.GraphMergerException;
import eu.excitementproject.tl.composition.exceptions.GraphOptimizerException;
import eu.excitementproject.tl.composition.graphoptimizer.GlobalGraphOptimizer;
import eu.excitementproject.tl.composition.graphoptimizer.SimpleGraphOptimizer;
import eu.excitementproject.tl.decomposition.exceptions.FragmentAnnotatorException;
import eu.excitementproject.tl.decomposition.exceptions.FragmentGraphGeneratorException;
import eu.excitementproject.tl.decomposition.exceptions.ModifierAnnotatorException;
import eu.excitementproject.tl.edautils.ProbabilisticEDA;
import eu.excitementproject.tl.edautils.RandomEDA;
import eu.excitementproject.tl.evaluation.exceptions.GraphEvaluatorException;
import eu.excitementproject.tl.evaluation.graphoptimizer.EvaluatorGraphOptimizer;
import eu.excitementproject.tl.evaluation.utils.EvaluationAndAnalysisMeasures;
import eu.excitementproject.tl.experiments.AbstractExperimentNotAnnotXMIs;
import eu.excitementproject.tl.experiments.ResultsContainer;
import eu.excitementproject.tl.structures.collapsedgraph.EntailmentGraphCollapsed;
import eu.excitementproject.tl.structures.rawgraph.EntailmentGraphRaw;

/** 
 * Class to load NICE data, build the graphs with WP2 merge procedure and evaluate them
 *
 * @author Lili Kotlerman
 * 
 */
public class ExperimentNiceFullPipeline extends AbstractExperimentNotAnnotXMIs {
	
	public String configFileFullName = "";
	public String configFileName = "";
	
	public ExperimentNiceFullPipeline(String configFileFullName, String dataDir,
			int fileNumberLimit, String outputFolder, Class<?> lapClass,
			Class<?> edaClass, MergerType mergerType) throws ConfigurationException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, EDAException, ComponentException, FragmentAnnotatorException, ModifierAnnotatorException, GraphMergerException, GraphOptimizerException, FragmentGraphGeneratorException, IOException, EntailmentGraphRawException, TransformerException, ClassNotFoundException {
		
		super(configFileFullName, dataDir, fileNumberLimit, outputFolder, lapClass,
				edaClass);
		
		this.configFileFullName = configFileFullName;
		this.configFileName = (new File(configFileFullName)).getName();

		m_optimizer = new SimpleGraphOptimizer();
		setMerger(mergerType);
	}
	
	public static ExperimentNiceFullPipeline initExperiment(EdaName edaName, MergerType mergerType, String tlDir, String dataDir, int fileLimit, String outDir) throws ConfigurationException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, EDAException, ComponentException, FragmentAnnotatorException, ModifierAnnotatorException, GraphMergerException, GraphOptimizerException, FragmentGraphGeneratorException, IOException, EntailmentGraphRawException, TransformerException, ClassNotFoundException{
		
		if (edaName.equals(EdaName.BIUTEE)) {
			return new ExperimentNiceFullPipeline(
			tlDir+"src/test/resources/NICE_experiments/biutee.xml",
			dataDir, fileLimit, outDir,			
			BIUFullLAP.class,
			BiuteeEDA.class,
			mergerType
			);
			
		}
		
		if (edaName.equals(EdaName.EDIT_DIST)) {

			return new ExperimentNiceFullPipeline(
//					tlDir+"src/test/resources/NICE_experiments/EditDistanceEDA_NonLexRes_EN.xml",
					tlDir+"src/test/resources/NICE_experiments/EditDistanceEDA_EN_nice.xml",
					dataDir, fileLimit, outDir,
					TreeTaggerEN.class,
					EditDistanceEDA.class,
					mergerType
					);					
		}

		if (edaName.equals(EdaName.PROBABILISTIC)) {
			return 	new ExperimentNiceFullPipeline(
					tlDir+"src/test/resources/NICE_experiments/MaxEntClassificationEDA_Base_EN.xml", //not used, just some existing conf file
					dataDir, fileLimit, outDir,
					TreeTaggerEN.class, //not used, just some available LAP
					ProbabilisticEDA.class, // to assign desired probability go to the EDA code (hard-coded in the beginning)
					mergerType
					);
		}

		if (edaName.equals(EdaName.RANDOM)) {
			return new ExperimentNiceFullPipeline(
					tlDir+"src/test/resources/NICE_experiments/MaxEntClassificationEDA_Base_EN.xml", //not used, just some existing conf file
					dataDir, fileLimit, outDir,
					TreeTaggerEN.class, //not used, just some available LAP
					RandomEDA.class,
					mergerType
					);
		}

		if (edaName.equals(EdaName.TIE_PARSE_RES)) {
			return new ExperimentNiceFullPipeline(
			tlDir+"/src/test/resources/NICE_experiments/MaxEntClassificationEDA_Base+WN+VO+TP+TPPos+TS_EN.xml",
			dataDir, fileLimit, outDir,
			MaltParserEN.class,
			MaxEntClassificationEDA.class,
			mergerType
			);			
		}
		
		if (edaName.equals(EdaName.TIE_POS)) {
			return new ExperimentNiceFullPipeline(
					tlDir+"src/test/resources/NICE_experiments/MaxEntClassificationEDA_Base_EN.xml",
					dataDir, fileLimit, outDir,
					TreeTaggerEN.class,
					MaxEntClassificationEDA.class,
					mergerType
					);
		}
		
		if (edaName.equals(EdaName.TIE_POS_RES)) {
			return new ExperimentNiceFullPipeline(
			tlDir+"src/test/resources/NICE_experiments/MaxEntClassificationEDA_Base+WN+VO_EN.xml",
			dataDir, fileLimit, outDir,
			TreeTaggerEN.class,
			MaxEntClassificationEDA.class,
			mergerType
			);
		}
	
		if (edaName.equals(EdaName.P1EDA)) {
			return new ExperimentNiceFullPipeline(
					tlDir+"src/test/resources/EOP_configurations/P1EDA_Base_EN.xml",
					dataDir, fileLimit, outDir,
					TreeTaggerEN.class,
					FNR_EN.class,
					mergerType
					);
		}
		
		
		return null;
	}

	
	/**
	 * @param args
	 * @throws TransformerException 
	 * @throws EntailmentGraphRawException 
	 * @throws IOException 
	 * @throws FragmentGraphGeneratorException 
	 * @throws GraphOptimizerException 
	 * @throws GraphMergerException 
	 * @throws ModifierAnnotatorException 
	 * @throws FragmentAnnotatorException 
	 * @throws ComponentException 
	 * @throws EDAException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws ConfigurationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ConfigurationException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, EDAException, ComponentException, FragmentAnnotatorException, ModifierAnnotatorException, GraphMergerException, GraphOptimizerException, FragmentGraphGeneratorException, IOException, EntailmentGraphRawException, TransformerException, ClassNotFoundException {

		// ============= SET UP THE EXPERIMENT CONFIGURATION ============
		String tlDir = "C:/Users/Lili/Git/Excitement-Transduction-Layer/tl/";
//		String tlDir = "./";
		String dataDir = tlDir+"src/main/resources/exci/nice/xmi_perFragmentGraph/test";
		String gsAnnotationsDir = tlDir+"src/main/resources/exci/nice/goldStandardAnnotation/test";
		int fileLimit = Integer.MAX_VALUE;
		String outDir = dataDir.replace("resources", "outputs");

		MergerType mergerType = MergerType.WP2_MERGE; // which merger to use
		boolean includeFragmentGraphEdges = false; // whether to include FG edges in the evaluations

		// which EDA(s) to use
//		EdaName[] names = {EdaName.EDIT_DIST, EdaName.TIE_POS, EdaName.TIE_POS_RES, EdaName.RANDOM};	
//		EdaName[] names = {EdaName.TIE_POS_RES};	
//		EdaName[] names = {EdaName.EDIT_DIST};	
//		EdaName[] names = {EdaName.BIUTEE, EdaName.TIE_POS_RES};	
//		EdaName[] names = {EdaName.BIUTEE};	
//		EdaName[] names = {EdaName.TIE_POS_RES};	
		EdaName[] names = {EdaName.P1EDA};	


		// ===== END OF SET-UP
		
		ResultsContainer combinedExperimet = new ResultsContainer(); 
		System.out.println(tlDir);
		GraphOptimizer globalOptimizer = new GlobalGraphOptimizer();	
		boolean isSingleClusterGS = true;
		String ress = "";
		File gsDir = new File(gsAnnotationsDir);
		System.out.print(gsDir.getAbsolutePath());
	
	for(EdaName name : names)	
		for (String clusterDir : gsDir.list()){
			String gsClusterDir = gsAnnotationsDir;
			if (isSingleClusterGS) gsClusterDir = gsAnnotationsDir+"/"+clusterDir;
			if (!isSingleClusterGS) clusterDir="";
			File clustGS = new File(gsClusterDir);
			if (!clustGS.isDirectory()) continue;
			System.out.println(gsClusterDir);
				

			ExperimentNiceFullPipeline e = initExperiment(name, mergerType, tlDir, dataDir+"/"+clusterDir, fileLimit, outDir); 
			EntailmentGraphRaw rawGraph = e.buildRawGraph();
				
/*				Set<Pair<String, String>> entailings = new HashSet<Pair<String, String>>();
				Set<Pair<String, String>> nonentailings = new HashSet<Pair<String, String>>();
				for (EntailmentRelation fge : e.m_rfg.edgeSet()){
					if (fge.getLabel().is(DecisionLabel.Entailment)){
						entailings.add(new Pair<String, String>(fge.getSource().getText(), fge.getTarget().getText()));
					}
					else{
						nonentailings.add(new Pair<String, String>(fge.getSource().getText(), fge.getTarget().getText()));
					}
				}
				e.m_optimizer = new GlobalGraphOptimizer(entailings, nonentailings);
				System.out.print(entailings);
				System.out.print(nonentailings);
*/
				
			try {
				e.m_rawGraph.toXML(outDir+"/"+e.configFileName+"_"+clusterDir+"_rawGraph.xml");
				e.m_rawGraph.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_rawGraph.dot");
			} catch (EntailmentGraphRawException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			for (Double confidenceThreshold : e.getConfidenceThresholds()){
				// apply threshold - this is the "local" graph for all-pairs merging
				EntailmentGraphRaw rawGraphWithThreshold = e.applyThreshold(rawGraph, confidenceThreshold);
				EntailmentGraphCollapsed rawGraphWithThresholdCollapsed = e.collapseGraph(rawGraphWithThreshold);

				String setting = e.getSettingName(name, "local", includeFragmentGraphEdges, gsClusterDir); 
				try {
					rawGraphWithThreshold.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_"+confidenceThreshold.toString()+"_local_allPair.dot");
					rawGraphWithThresholdCollapsed.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_"+confidenceThreshold.toString()+"_local_allPair_collapsed.dot");
				} catch (EntailmentGraphCollapsedException | EntailmentGraphRawException e1) {
					e1.printStackTrace();
				}
				
				System.out.println("### "+ setting+ "###");
				System.out.println(rawGraphWithThreshold.toString());
				EvaluationAndAnalysisMeasures res = e.evaluateRawGraph(rawGraphWithThreshold, gsClusterDir, !includeFragmentGraphEdges, isSingleClusterGS);		
				System.out.println(setting+"\t"+confidenceThreshold+"\t"+res.getRecall()+"\t"+res.getPrecision()+"\t"+res.getF1());
				try {
					EvaluationAndAnalysisMeasures consistencyCheck = e.checkGraphConsistency(rawGraphWithThreshold);
					res.setViolations(consistencyCheck.getViolations());
					res.setExtraFGedges(consistencyCheck.getExtraFGedges());
					res.setMissingFGedges(consistencyCheck.getMissingFGedges());
					res.setEdaCalls(e.getEdaCallsNumber());

				} catch (GraphEvaluatorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.addResult(setting, confidenceThreshold, res);
				combinedExperimet.addResult(setting, confidenceThreshold, res);

				if (mergerType.equals(MergerType.ALL_PAIRS_MERGE)){
					// now get plusClosure graph for current rawGraphWithThreshold  - this is the "localClosure" graph for all-pairs merging
					setting = e.getSettingName(name, "localClosure", includeFragmentGraphEdges, gsClusterDir); 
					EntailmentGraphRaw plusClosureRawGraph = e.getPlusClosureGraph(rawGraphWithThreshold);
					EntailmentGraphCollapsed plusClosureCollapsedGraph = e.collapseGraph(plusClosureRawGraph);
					try {
						plusClosureRawGraph.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_"+confidenceThreshold.toString()+"_localClosure_allPairs.dot");
						plusClosureCollapsedGraph.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_"+confidenceThreshold.toString()+"_localClosure_allPairs_collapsed.dot");
					} catch (EntailmentGraphCollapsedException | EntailmentGraphRawException e1) {
						e1.printStackTrace();
					}
					
					System.out.println("### "+ setting+ "###");
					System.out.println(plusClosureRawGraph.toString());
					res = e.evaluateRawGraph(plusClosureRawGraph, gsClusterDir, !includeFragmentGraphEdges, isSingleClusterGS);		
					System.out.println(setting+"\t"+confidenceThreshold+"\t"+res.getRecall()+"\t"+res.getPrecision()+"\t"+res.getF1());
					try {
						EvaluationAndAnalysisMeasures consistencyCheck = e.checkGraphConsistency(plusClosureRawGraph);
						res.setViolations(consistencyCheck.getViolations());
						res.setExtraFGedges(consistencyCheck.getExtraFGedges());
						res.setMissingFGedges(consistencyCheck.getMissingFGedges());
						res.setEdaCalls(e.getEdaCallsNumber());
	
					} catch (GraphEvaluatorException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.addResult(setting, confidenceThreshold, res);
					combinedExperimet.addResult(setting, confidenceThreshold, res);

					// now optimize the localClosure graph with global optimizer
					setting = e.getSettingName(name, "localClosure+global", includeFragmentGraphEdges, gsClusterDir);				
					EntailmentGraphCollapsed localClosureGloballyOptimizedGraph = e.collapseGraph(plusClosureRawGraph, globalOptimizer);
					
					try {
						localClosureGloballyOptimizedGraph.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_"+confidenceThreshold.toString()+"_localClosure+global_allPairs_collapsed.dot");
					} catch (EntailmentGraphCollapsedException  e1) {
						e1.printStackTrace();
					}
					
					System.out.println("### "+ setting+ "###");
					res = e.evaluateCollapsedGraph(localClosureGloballyOptimizedGraph, gsClusterDir, !includeFragmentGraphEdges, isSingleClusterGS);
					System.out.println(setting+"\t"+confidenceThreshold+"\t"+res.getRecall()+"\t"+res.getPrecision()+"\t"+res.getF1());
					try {
						EvaluationAndAnalysisMeasures consistencyCheck = e.checkGraphConsistency(localClosureGloballyOptimizedGraph);
						res.setViolations(consistencyCheck.getViolations());
						res.setExtraFGedges(consistencyCheck.getExtraFGedges());
						res.setMissingFGedges(consistencyCheck.getMissingFGedges());
						res.setEdaCalls(e.getEdaCallsNumber());
					} catch (GraphEvaluatorException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}				
					e.addResult(setting, confidenceThreshold, res);
					combinedExperimet.addResult(setting, confidenceThreshold, res);				
				}	
				
				// now optimize the local graph with global optimizer
				setting = e.getSettingName(name, "local+global", includeFragmentGraphEdges, gsClusterDir);
				EntailmentGraphCollapsed localGloballyOptimizedGraph = e.collapseGraph(rawGraphWithThreshold, globalOptimizer);
				
				try {
					localGloballyOptimizedGraph.toDOT(outDir+"/"+e.configFileName+"_"+clusterDir+"_"+confidenceThreshold.toString()+"_local+global_allPairs_collapsed.dot");
				} catch (EntailmentGraphCollapsedException  e1) {
					e1.printStackTrace();
				}
				
				System.out.println("### "+ setting+ "###");
				res = e.evaluateCollapsedGraph(localGloballyOptimizedGraph, gsClusterDir, !includeFragmentGraphEdges, isSingleClusterGS);
				System.out.println(setting+"\t"+confidenceThreshold+"\t"+res.getRecall()+"\t"+res.getPrecision()+"\t"+res.getF1());
				try {
					EvaluationAndAnalysisMeasures consistencyCheck = e.checkGraphConsistency(localGloballyOptimizedGraph);
					res.setViolations(consistencyCheck.getViolations());
					res.setExtraFGedges(consistencyCheck.getExtraFGedges());
					res.setMissingFGedges(consistencyCheck.getMissingFGedges());
					res.setEdaCalls(e.getEdaCallsNumber());
				} catch (GraphEvaluatorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
				e.addResult(setting, confidenceThreshold, res);
				combinedExperimet.addResult(setting, confidenceThreshold, res);
			
				// addition for full-pipeline collapsed-graph evaluation
				EvaluatorGraphOptimizer collapsedEvaluator = new EvaluatorGraphOptimizer();
				EntailmentGraphCollapsed collapsedGoldStandardGraph = e.getCollapdedGSClusterGraph(gsClusterDir); //e.collapseGraph(e.gsloader.getRawGraph(), 0.0, new SimpleGraphOptimizer());
				collapsedEvaluator.evaluateEdges(collapsedGoldStandardGraph.vertexSet(), localGloballyOptimizedGraph.vertexSet(), collapsedGoldStandardGraph.edgeSet(), localGloballyOptimizedGraph.edgeSet(), true);
			}
			

			
			ress+=e.printResults()+"\n";
			
			System.out.println("Done");
			
			if (!isSingleClusterGS) break; // if not by cluster - break after the first run
		}
		try {
			BufferedWriter outWriter = new BufferedWriter(new FileWriter(outDir+"/_NICE_experiment_results.txt"));
			outWriter.write(ress);
			System.out.println("======= Error examples =======");
			outWriter.write(combinedExperimet.printErrorExamples(5));			
			System.out.println("======= AVG =======");
			outWriter.write(combinedExperimet.printAvgResults());
			outWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
