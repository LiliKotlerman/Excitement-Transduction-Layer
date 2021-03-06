package  eu.excitementproject.tl.laputils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import edu.stanford.nlp.util.StringUtils;
import eu.excitement.type.tl.AssumedFragment;
import eu.excitement.type.tl.CategoryAnnotation;
import eu.excitement.type.tl.CategoryDecision;
import eu.excitement.type.tl.DeterminedFragment;
import eu.excitement.type.tl.FragmentAnnotation;
import eu.excitement.type.tl.FragmentPart;
import eu.excitement.type.tl.KeywordAnnotation;
import eu.excitement.type.tl.Metadata;
import eu.excitement.type.tl.ModifierAnnotation;
import eu.excitement.type.tl.ModifierPart;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
//import java.util.Set;

/**
 * The class holds various small utility static methods that might be 
 * useful in handling CASes: Like getting a new CAS, serialize, deserialize, 
 * adding some annotations of TL inputCAS, etc. 
 * 
 * @author Tae-Gil Noh and Vivi Nastase
 */

// Things to be considered as future improvements 
// TODO: [#B] define CASUtils Exception and replace/wrap LAPException 
// TODO: [#C] set typepath convention for UIMAFit based CAS generation. 
// TODO: [#C] Check UIMAFit CASUtil, and wrap some needed things.  

public final class CASUtils {
	/**
	 * <P>
	 * This method generates a new JCas and returns it. 
	 * The resulting CAS is empty, and holds nothing. No text, no language id, etc. The caller has to set them up. 
	 * <P>
	 * Note: Generally, making a new CAS is not really a good thing to do. It is a heavy and big object. If you can work on CAS sequentially, you should do that. This means that, using one CAS, if the work is done, reset the CAS (calling .reset()), and set a new document on the CAS, etc. The following is from UIMA JavaDoc.
	 * <P><I>
	 * Important: CAS creation is expensive, so if at all possible an application should reuse CASes. When a JCas instance is no longer being used, call its JCas.reset() method, which will remove all prior analysis information, and then reuse that same JCas instance for another call to process(JCas). </I>
	 * @return JCas that can express/hold all CAS types that is known to EOP & TL Layer 
	 */
	static public JCas createNewInputCas() throws LAPException
	{
		JCas a = null; 
		AnalysisEngine typeAE = null; 
		try {
			InputStream s = CASUtils.class.getResourceAsStream("/desc/TLDummyAE.xml"); // This AE does nothing, but holding all types.
			XMLInputSource in = new XMLInputSource(s, null); 
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
			typeAE = UIMAFramework.produceAnalysisEngine(specifier); 
		} 
		catch (InvalidXMLException e)
		{
			throw new LAPException("AE descriptor is not a valid XML, or unable to find the XML file /desc/TLDummyAE.xml in the Jar! (Wrong Jar configuration?) ", e);			
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to initialize the AE.", e); 
		}		

		try {
			a = typeAE.newJCas(); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create new JCas.", e); 
		}
		
		return a; 
	}
	
	/**
	 * This methods dumps the give type of annotations to the console 
	 * 
	 * @param aJCas
	 * @param annotType type id of the Annotation. (e.g. Lemma.type, CategoryAnnotation.type)  
	 */
	static public void dumpAnnotationsInCAS(JCas aJCas, int annotType)
	{		
		AnnotationIndex<Annotation> annotIndex = aJCas.getAnnotationIndex(annotType);
		Iterator<Annotation> annotIter = annotIndex.iterator(); 		
		if(annotIter.hasNext())
		{
			Annotation a = annotIter.next(); 
			Type t = a.getType(); 
			PlatformCASProber.printAnnotations(aJCas.getCas(), t, System.out);
		}
	}

	/**
	 * This methods retrieves the category annotations on the given CAS 
	 * 
	 * @param aJCas
	 */
	static public Set<CategoryDecision> getCategoryAnnotationsInCAS(JCas aJCas)
	{	
		Set<CategoryDecision> decisions = new HashSet<CategoryDecision>();
		AnnotationIndex<Annotation> annotIndex = aJCas.getAnnotationIndex(CategoryAnnotation.type);
		Iterator<Annotation> annotIter = annotIndex.iterator(); 
		while (annotIter.hasNext()) {
			CategoryAnnotation a = (CategoryAnnotation) annotIter.next(); 
			FSArray categories = a.getCategories();
			for (int i=0; i<categories.size(); i++) {
				decisions.add((CategoryDecision) categories.get(i));
			}
		}
		return decisions;
	}

	/**
	 *  This method dumps the content of CAS to Console  
	 *  
	 */
	static public void dumpCAS(JCas aJCas)
	{
		PlatformCASProber.printAnnotations(aJCas.getCas(), System.out); 
	}

	/**
	 * This static method serializes the given JCAS into an XMI file. 
	 * 
	 * @param JCas aJCas: the JCas to be serialized 
	 * @param File f: file path, where XMI file will be written 
	 */
	static public void serializeToXmi(JCas aJCas, File f) throws LAPException 
	{
		// Serializing formula.
		try {
			FileOutputStream out; 
			out = new FileOutputStream(f);
			XmiCasSerializer ser = new XmiCasSerializer(aJCas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(aJCas.getCas(), xmlSer.getContentHandler());
			out.close();
		}
		catch (FileNotFoundException e) 
		{
			throw new LAPException("Unable to open the file for the serialization", e); 
		}
		catch(IOException e)
		{
			throw new LAPException("IOException while closing the serialization file ", e);
		}
		catch(SAXException e)
		{
			throw new LAPException("Failed in generating XML for the serialization", e);
		}
	}
	
	/**
	 * This static method loads a serialized XMI file and fill up the JCAS. 
	 * Note that this method will first clear (by calling .reset()) the given CAS, and will fill it with given File, assuming the File is an XMI-zed CAS. If not, it will raise an exception. 
	 * 
	 * @param aJCas
	 * @param f
	 */
	static public void deserializeFromXmi(JCas aJCas, File f) throws LAPException
	{
		aJCas.reset(); 
//		if (!f.canRead())
//		{
//			throw new LAPException("Unable to open the file for deserialization" + f.toString()); 
//		}
		
		try {
			FileInputStream inputStream = new FileInputStream(f);
			XmiCasDeserializer.deserialize(inputStream, aJCas.getCas());
			inputStream.close();
		}
		catch(FileNotFoundException e)
		{
			throw new LAPException("Unable to open file for deserialization", e); 
		}
		catch(IOException e)
		{
			throw new LAPException("IOException happenes while accessing XMI file",e); 
		}
		catch(SAXException e)
		{
			throw new LAPException("XML parsing failed while reading XMI file", e); 
		}
		
	}
	
	/**
	 * This static method gets one JCAS, and an array of (begin, end) tuple (in forms of CASUtils.Region) 
	 * and annotate those regions with "AssumedFragment"
	 * The method is provided to easily annotate and generate some CAS without really concerning about CAS internals. 
	 * 
	 * <P> 
	 * Note that this method annotates only "one" fragment, that may have multiple (maybe non continuous) sub areas.
	 * This means that Region[] r is treated as "FragmentPart". See Fragment Annotation Type definition (TLFragment.xml) for more detail. 
	 * 
	 * @param aJCas
	 * @param r
	 */
	static public FragmentAnnotation annotateOneAssumedFragment(JCas aJCas, Region[] r ) throws LAPException
	{
		// we will blindly follow the given region and add annotation. 
		
		int leftmost = r[0].getBegin(); 
		int rightmost = r[(r.length -1)].getEnd(); 

		AssumedFragment af = new AssumedFragment(aJCas); 
		af.setBegin(leftmost);
		af.setEnd(rightmost); 
		FSArray v = new FSArray(aJCas, r.length); 
		af.setFragParts(v); 

		String fragText=""; 
		// Generate fragment parts 
		for (int i=0; i < r.length; i++)
		{		
			FragmentPart p = new FragmentPart(aJCas); 
			p.setBegin(r[i].getBegin()); 
			p.setEnd(r[i].getEnd());
			af.setFragParts(i, p); 
			fragText = fragText + p.getCoveredText() + " ";  
		}
		// get covered texts from those parts 
		af.setText(fragText); 
		af.addToIndexes(); 
		
		Logger l = Logger.getLogger("eu.excitementproject.tl.laputils"); 
		l.debug("Generated an AssummedFragment annotation. Fragment text is: " + fragText); 
		
		return af;
	}
	
	/**
	 * This static method gets one JCAS, and an array of (begin, end) tuple (in forms of CASUtils.Region) 
	 * and annotate those regions with "DeterminedFragment"
	 * The method is provided to easily annotate and generate some CAS without really concerning about CAS internals. 
	 * 
	 * <P> 
	 * Note that this method annotates only "one" fragment, that may have multiple (maybe non continuous) sub areas.
	 * This means that Region[] r is treated as "FragmentPart". See Fragment Annotation Type definition (TLFragment.xml) for more detail. 
	 * 
	 * @param aJCas
	 * @param r
	 */
	static public FragmentAnnotation annotateOneDeterminedFragment(JCas aJCas, Region[] r ) throws LAPException
	{
		// we will blindly follow the given region and add annotation. 		
		int leftmost = getMinBegin(r); 
		int rightmost = getMaxEnd(r); 

		DeterminedFragment df = new DeterminedFragment(aJCas); 
		df.setBegin(leftmost);
		df.setEnd(rightmost); 
		FSArray v = new FSArray(aJCas, r.length); 
		df.setFragParts(v); 

		String fragText=""; 
		// Generate fragment parts 
		for (int i=0; i < r.length; i++)
		{		
			FragmentPart p = new FragmentPart(aJCas); 
			p.setBegin(r[i].getBegin()); 
			p.setEnd(r[i].getEnd());
			df.setFragParts(i, p); 
			fragText = fragText + p.getCoveredText() + " ";  
		}
		// get covered texts from those parts 
		df.setText(fragText); 
		df.addToIndexes(); 
		
		Logger l = Logger.getLogger("eu.excitementproject.tl.laputils"); 
		l.debug("Generated a DeterminedFragment annotation. Fragment text is: " + fragText); 
		
		return df;
	}

	/**
	 * Get the upper bound for the end position from an array of Region-s
	 *  
	 * @param r -- an array of Region-s
	 * 
	 * @return the maximum end position from all given Region-s
	 */
	private static int getMaxEnd(Region[] r) {
		
		int maxEnd = 0;
		
		for(int i = 0; i < r.length; i++) {
			if (r[i].getEnd() > maxEnd)
				maxEnd = r[i].getEnd();
		}
		
		return maxEnd;
	}

	
	/** 
	 * Get the lower bound for the start position from an array of Region-s
	 * 
	 * @param r -- an array of Region-s
	 * 
	 * @return the minimum start position from all the given Region-s 
	 */
	private static int getMinBegin(Region[] r) {
		int minBegin = Integer.MAX_VALUE;
	
		for(int i = 0; i < r.length; i++) {
			if (r[i].getBegin() < minBegin)
				minBegin = r[i].getBegin();
		}
		
		return minBegin;
	}

	/**
	 * This method adds one modifier annotation to the given CAS with given Region. Note that this method 
	 * only annotates one modifier, where the given array of Region is treated as non-continuous region if more than one region is given.  
	 * 
	 * <P> 
	 * It gets one additional argument "dependOn". If this is given, the newly generated modifier will dependOn this given one. (See UIMA type definition for what dependOn pointer does/means with an example). This value can be null 
	 * 
	 *
	 * @param aJCas JCAS that would be annotated 
	 * @param r 	the region[] 
	 * @param dependOn	Another (previous) Modifier Annotation that this modifier depends on. can be null, and null means none. 
	 * @return ModifierAnnotation Returns current (newly generated) modifier annotation for future use (e.g. dependOn). Be careful not to be used in other JCAS. 
	 * @throws LAPException
	 */
	static public ModifierAnnotation annotateOneModifier(JCas aJCas, Region[] r, ModifierAnnotation dependOn) throws LAPException 
	{		
		// annotate them, just like frags. 
		// we will blindly follow the given region and add annotation. 		
		int leftmost = r[0].getBegin(); 
		int rightmost = r[(r.length -1)].getEnd(); 

		ModifierAnnotation ma = new ModifierAnnotation(aJCas); 
		ma.setBegin(leftmost);
		ma.setEnd(rightmost); 
		FSArray v = new FSArray(aJCas, r.length); 
		ma.setModifierParts(v); 

		String modText=""; 		
		// Generate modifier parts 
		for (int i=0; i < r.length; i++)
		{		
			ModifierPart p = new ModifierPart(aJCas); 
			p.setBegin(r[i].getBegin()); 
			p.setEnd(r[i].getEnd());
			ma.setModifierParts(i,p); 
			modText = modText + p.getCoveredText() + " ";  
		}

		// set "depending on", if given. 
		if (dependOn != null)
		{
			ma.setDependsOn(dependOn); 
		}
		ma.addToIndexes(); 

		Logger l = Logger.getLogger("eu.excitementproject.tl.laputils"); 
//		l.debug("Generated an ModifierAnnotation annotation. Modifier text is: " + modText); 
		l.info("Generated a ModifierAnnotation annotation. Modifier text is: *" + modText + "*"); 

		// return Modifier Annotation itself, so the caller can easily make next 
		// modifier that depends on this modifier annotation
		return ma; 
	}
	
	/**
	 * An overriden version for annotateOneModifer, without dependOn 
	 * 
	 * @param aJCas
	 * @param r
	 * @return
	 * @throws LAPException
	 */
	static public ModifierAnnotation annotateOneModifier(JCas aJCas, Region[] r) throws LAPException
	{
		return annotateOneModifier(aJCas, r, null); 
	}
	
	
	/**
	 * This simply static method adds one TLKeyword annotation on the given Region. 
	 * @param aJCas The input JCas that holds the input (interaction) text 
	 * @param r The region (begin - end) that will be annotated by a new TLkeyword annotation 
	 */
	static public void annotateOneKeyword(JCas aJCas, Region r)
	{
		Logger l = Logger.getLogger("eu.excitementproject.tl.laputils"); 

		KeywordAnnotation key = new KeywordAnnotation(aJCas); 
		key.setBegin(r.getBegin()); 
		key.setEnd(r.getEnd()); 
		key.addToIndexes(); 
		
		l.debug("Generated a KeywordAnnotation. The annotated part is: " + key.getCoveredText());
	}
	
	/**
	 * This static method gets one JCAS, a (begin, end) tuple (in form of CASUtils.Region), 
	 * and a set of category decisions (category ID and confidence), and adds category annotation 
	 * to the region.  
	 * 
	 * @param aJCas
	 * @param r
	 * @param text 
	 * @param categoryId
	 * @param score
	 */
	static public void annotateCategories(JCas aJCas, Region r, String text, Map<String, Double> decisions) throws LAPException
	{
		CategoryAnnotation ca = new CategoryAnnotation(aJCas); 
		ca.setBegin(r.getBegin());
		ca.setEnd(r.getEnd()); 
		ca.setText(text);
		FSArray v = new FSArray(aJCas, decisions.size()); 
		ca.setCategories(v);
	
		int i=0; 
		for (String decision : decisions.keySet()) {
			CategoryDecision cd = new CategoryDecision(aJCas);
			cd.setCategoryId(decision);
			cd.setConfidence(decisions.get(decision));
			ca.setCategories(i, cd);
			i++;
		}
		ca.addToIndexes(); 
		
		Logger l = Logger.getLogger("eu.excitementproject.tl.laputils"); 
		l.debug("Generated Category annotation."); 
		
	}

	/**
	 * An inner class that simply holds "begin" and "end" as int. 
	 * The class is used as an argument on some of the methods. 
	 */
	public static class Region 
	{
		public Region(int begin, int end)
		{
			this.begin = begin; 
			this.end = end; 
		}
		public int getBegin()
		{
			return begin; 
		}
		public int getEnd()
		{
			return end; 
		}
		
		@Override
		public String toString() {
			return " (" + begin + "," + end + ") ";
		}
		
		private final int begin; 
		private final int end; 
	}
	
	/**
	 * A temporary code: This will generate some XMI files in the /target dir.  
	 * Those XMI files holds inputCASes agreed previously within WP6 for development phase. 
	 * The files generated can be loaded by deserealizeFromXmi(). 
	 * 
	 * <P> The code will be called as normal build procedure, within CASUtilsTest class.
	 */
	public static void generateExamples() throws LAPException 
	{
		try {
			
		JCas aJCas = createNewInputCas(); 

		// input CAS 1: Example A & B  
		//    0         1         2
		//    012345678901234567890
		// A: Food was really bad. 
		// B: Food was bad. 
		aJCas.setDocumentText("Food was really bad."); 
		aJCas.setDocumentLanguage("EN"); 
		
		CASUtils.Region[] rf = new CASUtils.Region[1]; 
		rf[0] = new CASUtils.Region(0, 20); 
		annotateOneDeterminedFragment(aJCas, rf); 
		
		CASUtils.Region[] rm = new CASUtils.Region[1]; 
		rm[0] = new CASUtils.Region(9, 15); // "really" 
		annotateOneModifier(aJCas, rm);
		
		serializeToXmi(aJCas, new File("./target/CASInput_example_1.xmi")); 
		
		// input CAS 2: Example C 
		//     0         1         2 
		//     01234567890123456789012345
		// C: "I didn't like the food."
		aJCas.reset(); 
		aJCas.setDocumentText("I didn't like the food."); 
		aJCas.setDocumentLanguage("EN"); 

		rf = new CASUtils.Region[1]; 
		rf[0] = new CASUtils.Region(0, 23); 
		annotateOneDeterminedFragment(aJCas, rf); 

		// no modifier for sentence C 
		serializeToXmi(aJCas, new File("./target/CASInput_example_2.xmi")); 

		// input CAS 3: Example D, E
		//     0         1         2         3         4 
		//     01234567890123456789012345678901234567890123456
		// D: "a little more leg room would have been perfect" (modifier: "a little")
		// E: "more leg room would have been perfect"
		aJCas.reset(); 
		aJCas.setDocumentText("a little more leg room would have been perfect"); 
		aJCas.setDocumentLanguage("EN"); 

		// fragment annotation as whole. 
		rf = new CASUtils.Region[1]; 
		rf[0] = new CASUtils.Region(0, 46); 
		annotateOneDeterminedFragment(aJCas, rf); 
		
		// one modifier annotation 
		rm = new CASUtils.Region[1]; 
		rm[0] = new CASUtils.Region(0, 8); // "a little"  
		annotateOneModifier(aJCas, rm);

		serializeToXmi(aJCas, new File("./target/CASInput_example_3.xmi")); 

		// input CAS 4: Example F, G, H, I 
		//    0         1         2         3         4         5         6 
		//    0123456789012345678901234567890123456789012345678901234567890123456
		// F "Disappointed with the amount of legroom compared with other trains" (modifiers: "the amount of", "compared with other trains")
		// G "Disappointed with legroom compared with other trains" (modifier: "compared with other trains")
		// H "Disappointed with the amount of legroom" (modifier: "the amount of")
		// I "Disappointed with legroom"
		aJCas.reset(); 
		aJCas.setDocumentText("Disappointed with the amount of legroom compared with other trains"); 
		aJCas.setDocumentLanguage("EN"); 
		
		// fragment annotation as whole. 
		rf = new CASUtils.Region[1]; 
		rf[0] = new CASUtils.Region(0, 66); 
		annotateOneDeterminedFragment(aJCas, rf); 

		// two modifiers: "the amount of", "compared with other trains"
		rm = new CASUtils.Region[1]; 
		rm[0] = new CASUtils.Region(18, 31); // "the amount of"  
		annotateOneModifier(aJCas, rm);
		
		rm = new CASUtils.Region[1]; 
		rm[0] = new CASUtils.Region(40, 66); // "compared with other trains"   
		annotateOneModifier(aJCas, rm);
		
		serializeToXmi(aJCas, new File("./target/CASInput_example_4.xmi")); 

		// TODO: consider, adding something not whole is a fragment 
		// TODO: consider, non continuous fragment & non continous modifier 
		// TODO: consider, example with modifier dependOn. 
		
		}
		catch (LAPException e)
		{
			throw e; 
		}

	}
	
	/**
	 * This static method annotates the given JCas with one TL metadata ( type.tl.metadata, as defined in TLMetadata.xml). 
	 * Note that all metadata are strings, and null values are also acceptable --- null values won't be set, and if not set, those values will 
	 * stay null in CAS.  
	 * 
	 * <P> The method adds one metadata annotation on span [0 - end] with the given fields. It does so blindly (does not check content of those fields. only treat them as string). Also it does not check the existence of (already annotated) metadata.  
	 * 
	 * @param aJCas The JCas that will be annotated with 
	 * @param interactionId interaction ID of the interaction, contained in the CAS.
	 * @param channel channel of the interaction (e.g. e-mail, web forum, speech, telephone transcript, etc)
	 * @param provider This value holds the provider as string (ALMA, OMQ or NICE, etc)
	 * @param date The date of the interaction: as ISO format of Year-Month-Day (YYYY-MM-DD)
	 * @param businessScenario This string holds the business scenario of the interaction (like coffeeshop, internet shopping, train claims, etc)
	 * @param author this field holds the name of the author, if it is applicable (e.g. web forums)
	 * @param category This type describes cateogry of the interaction. It corresponds to input XML file <Interaction> <metadata> <category>, and will hold that metadata string as it is.  
	 */
	public static void addTLMetaData(JCas aJCas, String interactionId, String channel, String provider, String date, String businessScenario, String author, String category) 
	{
		// Generate a new type, fill in  
		Metadata meta = new Metadata(aJCas);
		if (interactionId != null)
			meta.setInteractionId(interactionId); 
			
		if (channel != null)
			meta.setChannel(channel); 
		
		if (provider != null)
			meta.setProvider(provider); 
		
		if (date != null)
			meta.setDate(date); 

		if (businessScenario != null)
			meta.setBusinessScenario(businessScenario); 
		
		if (author != null)
			meta.setAuthor(author); 
		
		if (category != null)
			meta.setCategory(category); 
		
		// get length of the document and annotate over the whole SOFA 
		int begin = 0; 
		int end = 1; 
		if (aJCas.getDocumentText() != null) 
		{
			end = aJCas.getDocumentText().length(); 
		}
		
		meta.setBegin(begin); 
		meta.setEnd(end); 
		
		meta.addToIndexes(); 
		Logger l = Logger.getLogger("eu.excitementproject.tl.laputils"); 
		l.debug("Generated one metadata annotation with interaction id: " + interactionId ); 
		
	}
	
	/**
	 * 
	 * This static method reads in one Metadata annotation given on the input JCAS. You can use the getters within this returned Metadata type to access actual metadata string fields. (- interactionId, - channel, - provider, - date (string as YYYY-MM-DD), - businessScenario, - author) 
	 * If the annotation is not present, it will return null 
	 * 
	 * @param aJCas the JCas with metadata. 
	 * @return the Metadata annotation type. you can use "get methods" to get various values. will return null, if no metadata is annotated. If there is more than one metadata annotation, this method will return blindly the first one.   
	 */
	public static Metadata getTLMetaData(JCas aJCas)
	{
		AnnotationIndex<Annotation> metaIndex = aJCas.getAnnotationIndex(Metadata.type);
		Iterator<Annotation> mIter = metaIndex.iterator(); 		
		
		if (mIter.hasNext())
		{
			Metadata meta = (Metadata) mIter.next(); 
		
			return meta; 
		}
		else
		{
			return null; 
		}
	}

	/**
	 * Add a set of keywords to the CAS object
	 * 
	 * @param aJCas CAS object
	 * @param keywords an array of keywords that will be added as annotations
	 */
	public static void addTLKeywords(JCas aJCas, String[] keywords) {
		
		Logger logger = Logger.getLogger("eu.excitementproject.tl.laputils.CASUtils.addTLKeywords");
		
		if (keywords != null) {
			
			logger.info("adding " + keywords.length + " keywords : " + StringUtils.join(keywords));
			
			String text = aJCas.getDocumentText();
			for (int i = 0; i < keywords.length; i++) {
				
//				Pattern p = Pattern.compile("\\b" + keywords[i] + "\\b",Pattern.CASE_INSENSITIVE); // did not work because of tokenization (e.g. "File-Optionen" which in the context does not refer to the compound
				Pattern p = Pattern.compile(keywords[i],Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(text);
				while (m.find()) {
					annotateOneKeyword(aJCas, new Region(m.start(),m.end()));
				}
			}
		}
	}

	
	/**
	 * Builds the text fragment -- when the fragment is made up of fragment parts, the in-between pieces
	 * 							   will be blank spaces to allow for proper position computations later on
	 * 							   (when removing modifiers for example)
	 * @param frag -- a fragment annotation
	 * @return the text (including spaces for missing non-contiguous pieces with respect to the full interaction text)
	 */
	public static CharSequence getCompleteTextFragment(FragmentAnnotation frag) {
		
		Logger logger = Logger.getLogger("eu.excitementproject.tl.laputils.CASUtils.getCompleteTextFragment");		

		if (frag.getFragParts() == null || frag.getFragParts().size() == 0) {
			return frag.getText();
		}

//		logger.info("Processing FragmentAnnotation for :" + frag.getCoveredText());
		
		String text = "";
		FragmentPart f, prev = null;
		for(int i = 0; i < frag.getFragParts().size(); i++) {
			f = frag.getFragParts(i);
			
			if (prev != null) {
				text += org.apache.commons.lang.StringUtils.rightPad(" ", f.getBegin() - prev.getEnd());
			} 
			text += f.getCoveredText();
			prev = f;
		}
		
		logger.info("Fragment text: " + frag.getText() + "\nWith spaces: " + text);

		return text;
	}
	
}
