package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import annotation.Annotation;
import annotation.DerivedAnnotation;
import annotation.Gene;
import annotation.Annotation.Strand;
import annotation.io.BEDFileIO;
import annotationcollection.AnnotationCollection;
import annotationcollection.FeatureCollection;

public class AnnotationGetWindowsTest {
	
	private static String bedFile = "/storage/Annotations/RefSeq/mm9/RefSeq.bed";
	private static String referenceSizes = "/storage/Genomes/mm9/sizes";
	
	private Map<String, FeatureCollection<Gene>> genesByReference;
	private FeatureCollection<Gene> genesChr1;
	private Gene geneMinus;
	private Gene genePlus;
	
	@Before
	public void setUp() {
		try {
			genesByReference = BEDFileIO.loadFromFileByReferenceName(bedFile, referenceSizes);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		genesChr1 = genesByReference.get("chr1");
		Iterator<Gene> iter = genesChr1.iterator();
		iter.hasNext();
		geneMinus = iter.next();
		System.out.println(geneMinus.toBED());
		while(iter.hasNext()) {
			genePlus = iter.next();
			if(genePlus.getOrientation().equals(Strand.POSITIVE)) break;
		}
		System.out.println(genePlus.toBED());
	}

	@Test
	public void testWindowsMinusStrand() {
		AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows = geneMinus.getWindows(100, 50);
		assertEquals(71, windows.getNumAnnotations());
		Iterator<DerivedAnnotation<? extends Annotation>> iter = windows.sortedIterator();
		iter.hasNext();
		assertEquals("chr1:3204562-3204662", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:3204612-3204712", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:3204662-3204762", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:3204712-3204812", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:3204762-3204862", iter.next().toUCSC());
		iter.hasNext();
		assertEquals(Strand.NEGATIVE, iter.next().getOrientation());
		DerivedAnnotation<? extends Annotation> lastWindow = null;
		while(iter.hasNext()) {
			lastWindow = iter.next();
		}
		assertEquals("chr1:3661445-3661545", lastWindow.toUCSC());
	}
	
	@Test
	public void testWindowsPlusStrand() {
		AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows = genePlus.getWindows(200, 100);
		assertEquals(23, windows.getNumAnnotations());
		Iterator<DerivedAnnotation<? extends Annotation>> iter = windows.sortedIterator();
		iter.hasNext();
		assertEquals("chr1:4797973-4820360", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:4798545-4822455", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:4820360-4829486", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:4822455-4831053", iter.next().toUCSC());
		iter.hasNext();
		assertEquals("chr1:4829486-4831153", iter.next().toUCSC());
		iter.hasNext();
		assertEquals(Strand.POSITIVE, iter.next().getOrientation());
		DerivedAnnotation<? extends Annotation> lastWindow = null;
		while(iter.hasNext()) {
			lastWindow = iter.next();
		}
		assertEquals("chr1:4836583-4836783", lastWindow.toUCSC());
	}
	
	

	
}
