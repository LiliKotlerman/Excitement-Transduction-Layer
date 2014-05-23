package eu.excitementproject.tl.laputils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
//import org.junit.Ignore;
import org.junit.Test;

import eu.excitement.type.tl.Metadata;
import eu.excitementproject.tl.structures.Interaction;

public class InteractionReaderTest {
	
	@Test
	public void test_aligner() {
		
		CASUtils.Region[] r1 = InteractionReader.alignBtoA("Although this is a terrible idea,  it would work", "this is a idea"); 
		assertTrue(r1 != null); 
			
		CASUtils.Region[] r2 = InteractionReader.alignBtoA("I could have used some more legroom", "more  legroom"); 
		assertTrue(r2 != null); 
			
		CASUtils.Region[] r3 = InteractionReader.alignBtoA("V good exp at security with pushchair - baby allowed to sleep , etc . Would welcome extra luggage space and/or priority boarding for pushchairs .", "Would welcome priority boarding for pushchairs");
		assertTrue(r3 != null); 
		assertTrue(r3[0].getBegin() > 10); 
		
		CASUtils.Region[] r4 = InteractionReader.alignBtoA("That was a great idea for us", "That is a idea"); 
		assertTrue(r4 == null); 
		
		// Known problem: 
		// This is not trivial problem. (from Right side would be better?) 
		// Full text "Telefonica blah blah [something not relevant], ... Telefonica is here but others are also really good". 
		// Fragment "Telefonica is here but others are good". 
		// (maps     ^1st Telefonica, ^last Okay. ) 
				
	}
	@Test 
	public void test_reader2() {
		
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);   
		Logger testlogger = Logger.getLogger("eu.excitementproject.tl.laputils"); 

		testlogger.info("Reading interaction & fragment with single modifier"); 
		try {
			File f1 = new File("./src/test/resources/WP2_public_data/nice_email_1/100771.txt"); 
			File f2 = new File("./src/test/resources/WP2_public_data/nice_email_1/100771.txt_1.xml.graphf1output.xml"); 
			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(f1,  f2,  aJCas, "EN"); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		testlogger.info("Reading interaction & fragment with two modifiers"); 

		try {
			File f1 = new File("./src/test/resources/WP2_public_data/nice_email_1/427784.txt"); 
			File f2 = new File("./src/test/resources/WP2_public_data/nice_email_1/427784.txt_1.xml.graphf1output.xml"); 
			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(f1,  f2,  aJCas, "EN"); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		testlogger.info("Reading multiple fragments, with one interaction, by calling the reader method multiple times"); 
		
		try {
			File i = new File("./src/test/resources/WP2_public_data/nice_email_1/427784.txt"); 
			File g1 = new File("./src/test/resources/WP2_public_data/nice_email_1/427784.txt_1.xml.graphf1output.xml"); 
			File g3 = new File("./src/test/resources/WP2_public_data/nice_email_1/427784.txt_3.xml.graphf3output.xml"); 
			File g5 = new File("./src/test/resources/WP2_public_data/nice_email_1/427784.txt_5.xml.graphf5output.xml"); 

			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(i,  g1,  aJCas, "EN"); 
			InteractionReader.readWP2FragGraphDump(i,  g3,  aJCas, "EN"); 
			InteractionReader.readWP2FragGraphDump(i,  g5,  aJCas, "EN"); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		testlogger.info("Reading a fragment, with no modifiers"); 
		try {
			File f1 = new File("./src/test/resources/WP2_public_data/nice_email_1/450618.txt"); 
			File f2 = new File("./src/test/resources/WP2_public_data/nice_email_1/450618.txt_1.xml.graphf1output.xml"); 
			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(f1,  f2,  aJCas, "EN"); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		
		testlogger.info("Reading two fragments from ALMA data for focus testing");
		try {
			File i = new File("./src/test/resources/WP2_public_data/alma_social_media/0004.txt"); 
			File f1 = new File("./src/test/resources/WP2_public_data/alma_social_media/0004.txt_1.xml.graphf1output.xml");
//			File f2 = new File("./src/test/resources/WP2_public_data/alma_social_media/0004.txt_2.xml.graphf2output.xml"); 

			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(i,  f1,  aJCas, "IT");
//			InteractionReader.readWP2FragGraphDump(i,  f2,  aJCas, "IT"); 

			CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}

    @Test
	public void test_reader1() {
		// This method tests InteractionReader.readInteractionXML(File) 
		
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);   
		Logger testlogger = Logger.getLogger("eu.excitementproject.tl.laputils"); 

		
		//File f = new File("./src/test/resources/WP2_public_data_XML/nice_email_partial.xml"); 		
		File f = new File("./src/test/resources/WP2_public_data_XML/Public Dataset D2.1.1 - Interactions Italian-Social media.xml"); 		
		try {
			List<Interaction> iList = InteractionReader.readInteractionXML(f); 
			testlogger.info("The test file `" + f.getPath() + "'has " + iList.size() + " interactions in it."); 
			assertEquals(iList.size(), 323); 

			// check first interaction. 
			Interaction one = iList.get(0); 
			testlogger.info("The first interaction (id: " + one.getInteractionId() + ") text is:" + one.getInteractionString());
			assertEquals(one.getLang(), "IT"); 
			assertEquals(one.getChannel(), "social"); 
			assertEquals(one.getProvider(), "ALMA"); 
			assertNotNull(one.getInteractionId()); 
			// and just to be safe, 45th interaction 
			Interaction fortyfifth = iList.get(44); 
			testlogger.info("The fortyfifth interaction (id: "+ fortyfifth.getInteractionId() +") text is:" + fortyfifth.getInteractionString());
			assertEquals(fortyfifth.getLang(), "IT"); 
			assertEquals(fortyfifth.getChannel(), "social"); 
			assertEquals(fortyfifth.getProvider(), "ALMA"); 
			assertNotNull(fortyfifth.getInteractionId()); 
			
			// okay this one has interaction id set from XML. 
			// make sure if we convert this to CAS, will holds the same id in 
			// metadata annotation. 
			
			JCas a = fortyfifth.createAndFillInputCAS(); 
			Metadata m = CASUtils.getTLMetaData(a); 
			assertEquals(fortyfifth.getInteractionId(), m.getInteractionId()); 
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		// Lets read another file 
		f = new File("./src/test/resources/WP2_public_data_XML/Public Dataset D2.1.1 - Interactions Italian-Speech.xml"); 		
		try {
			List<Interaction> iList = InteractionReader.readInteractionXML(f); 
			testlogger.info("The test file `" + f.getPath() + "'has " + iList.size() + " interactions in it."); 
			assertEquals(iList.size(), 50); 

			// check first interaction. 
			Interaction one = iList.get(0); 
			testlogger.info("The first interaction (id: " + one.getInteractionId() +") text is:" + one.getInteractionString());
			assertEquals(one.getLang(), "IT"); 
			assertEquals(one.getChannel(), "speech"); 
			assertEquals(one.getProvider(), "ALMA"); 
			
			testlogger.info("testing of readInteractionXML(): Okay"); 	
			
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// Lets read another file 
		f = new File("./src/test/resources/WP2_public_data_XML/D2.1.1 English-Email.xml"); 		
		try {
			List<Interaction> iList = InteractionReader.readInteractionXML(f); 
			testlogger.info("The test file `" + f.getPath() + "'has " + iList.size() + " interactions in it."); 
			assertEquals(iList.size(), 224); 

			// check first interaction. 
			Interaction one = iList.get(0); 
			testlogger.info("The first interaction (id: " + one.getInteractionId() +") text is:" + one.getInteractionString());
			assertEquals(one.getLang(), "EN"); 
			assertEquals(one.getChannel(), "email"); 
			assertEquals(one.getProvider(), "NICE"); 
			
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}	
		
		// Lets read another file 
		// Kathrin often changes the file, so don't test the content of this file 
		f = new File("./src/test/resources/WP2_public_data_XML/OMQ_dummy_data_small.xml"); 		
		try {
			List<Interaction> iList = InteractionReader.readInteractionXML(f); 
			testlogger.info("The test file `" + f.getPath() + "'has " + iList.size() + " interactions in it."); 
			
			//assertEquals(iList.size(), 1); 

			// check first interaction. 
			Interaction one = iList.get(0); 
			testlogger.info("The first interaction (id: " + one.getInteractionId() +") text is:" + one.getInteractionString());
//			assertEquals(one.getLang(), "DE"); 
//			assertEquals(one.getChannel(), "email"); 
//			assertEquals(one.getProvider(), "OMQ"); 
//			assertEquals(one.getCategory(), "100"); 
			
			// check CAS does holds category metadata 
			JCas aJCas = one.createAndFillInputCAS(); 
			Metadata m = CASUtils.getTLMetaData(aJCas); 
			assertNotNull(m.getCategory()); 
//			assertEquals(m.getCategory(), "100"); 
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}	

		testlogger.info("testing of readInteractionXML(): Okay"); 	

	}
    
    @Test
	public void test_relevant_text() {
		// This method tests InteractionReader.readInteractionXML(File) with "relevantText" annotation
    			
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);   
		Logger testlogger = Logger.getLogger("eu.excitementproject.tl.laputils"); 

		File f = new File("./src/test/resources/WP2_public_data_XML/OMQ/omq_public_1_emails.xml"); 		
		try {
			List<Interaction> iList = InteractionReader.readInteractionXML(f); 
			assertTrue(iList.get(0).getRelevantText().startsWith("Ich habe jetzt einen neuen PC mit Windows7 und"));			
			assertTrue(iList.get(0).getRelevantText().endsWith("kompatibel."));
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}	

		testlogger.info("testing of readInteractionXML() with relevant text: Okay"); 	
    }

}
