package eu.excitementproject.clustering.clustering.impl.chinesewhispers;

import java.util.Set;

import eu.excitementproject.clustering.clustering.exceptions.ClusteringException;
import eu.excitementproject.clustering.clustering.impl.util.DocumentsToVectorsConverter;
import eu.excitementproject.clustering.clustering.impl.util.VectorRepresentation;
import eu.excitementproject.clustering.clustering.impl.util.WeightCalculator.WeightType;
import eu.excitementproject.clustering.data.api.TextCollection;

/**
 * @author Lili Kotlerman
 *
 */
public class DocumentsAsTermVectorsCWClusterer extends AbstractDocumentCWClusterer {


	public DocumentsAsTermVectorsCWClusterer(boolean useExpandedCollection,
			TextCollection textCollection, WeightType weightType,
			String configurationFilename, Double similarityThreshold)
			throws ClusteringException {
		super(useExpandedCollection, textCollection, weightType, configurationFilename,
				similarityThreshold);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Set<VectorRepresentation> representDocuments(TextCollection textCollection) {
		return DocumentsToVectorsConverter.convertDocumentsToDenseTermVectors(textCollection, m_useExpandedCollection, m_weightType);	
	}

	@Override @Deprecated
	public void setNumberOfDocumentClusters(int K) {
	}

}
