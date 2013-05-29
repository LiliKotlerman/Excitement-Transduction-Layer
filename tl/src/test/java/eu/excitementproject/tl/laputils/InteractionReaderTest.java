package eu.excitementproject.tl.laputils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.tl.structures.Interaction;

public class InteractionReaderTest {
	
	@Test 
	public void test_reader2() {
		
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);   
		Logger testlogger = Logger.getLogger("eu.excitementproject.tl.laputils"); 

		testlogger.info("Reading interaction & fragment with single modifier"); 
		try {
			File f1 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/100771.txt"); 
			File f2 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/100771.txt_1.xml.graphf1output.xml"); 
			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(f1,  f2,  aJCas); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		testlogger.info("Reading interaction & fragment with two modifiers"); 

		try {
			File f1 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/427784.txt"); 
			File f2 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/427784.txt_1.xml.graphf1output.xml"); 
			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(f1,  f2,  aJCas); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		testlogger.info("Reading multiple fragments, with one interaction, by calling the reader method multiple times"); 
		
		try {
			File i = new File("./src/test/resources/WP2_public_data/NICE_email_data1/427784.txt"); 
			File g1 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/427784.txt_1.xml.graphf1output.xml"); 
			File g3 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/427784.txt_3.xml.graphf3output.xml"); 
			File g5 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/427784.txt_5.xml.graphf5output.xml"); 

			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(i,  g1,  aJCas); 
			InteractionReader.readWP2FragGraphDump(i,  g3,  aJCas); 
			InteractionReader.readWP2FragGraphDump(i,  g5,  aJCas); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		testlogger.info("Reading a fragment, with no modifiers"); 
		try {
			File f1 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/450618.txt"); 
			File f2 = new File("./src/test/resources/WP2_public_data/NICE_email_data1/450618.txt_1.xml.graphf1output.xml"); 
			JCas aJCas = CASUtils.createNewInputCas(); 
			
			InteractionReader.readWP2FragGraphDump(f1,  f2,  aJCas); 
			//CASUtils.dumpCAS(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
				
	}

	@Ignore @Test
	public void test_reader1() {
		// This method tests InteractionReader.readInteractionXML(File) 
		
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);   
		Logger testlogger = Logger.getLogger("eu.excitementproject.tl.laputils"); 

		
		File f = new File("./src/test/resources/WP2_confidential_data_partial/nice_email_partial.xml"); 		
		try {
			List<Interaction> iList = InteractionReader.readInteractionXML(f); 
			testlogger.info("The test file `" + f.getPath() + "'has " + iList.size() + " interactions in it."); 
			assertEquals(iList.size(), 76); 

			// check first interaction. 
			Interaction one = iList.get(0); 
			testlogger.info("The first interaction string is:" + one.getInteractionString());
			assertEquals(one.getInteractionString(), "We just love the atmosphere in this store !"); 
			assertEquals(one.getLang(), "EN"); 
			assertEquals(one.getChannel(), "mail"); 
			assertEquals(one.getProvider(), "NICE"); 
			assertNull(one.getCategory()); 
			// and just to be safe, 45th interaction 
			Interaction fortyfifth = iList.get(44); 
			testlogger.info("The fortyfifth interaction string is:" + fortyfifth.getInteractionString());
			assertEquals(fortyfifth.getInteractionString(), "I would have rated higher but on my visit I was only able to pay with cash as the machine was not working . Based upon the card machine not working I was limited to only buying a coffee , if it had been working my I would have made a bigger purchase ."); 
			assertEquals(fortyfifth.getLang(), "EN"); 
			assertEquals(fortyfifth.getChannel(), "mail"); 
			assertEquals(fortyfifth.getProvider(), "NICE"); 
			testlogger.info("testing of readInteractionXML(): Okay"); 			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}

}
