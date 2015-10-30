package guttmanlab.core.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import guttmanlab.core.annotation.MappedFragment;
import guttmanlab.core.annotation.SingleInterval;
import guttmanlab.core.annotation.Annotation.Strand;
import guttmanlab.core.annotationcollection.AbstractAnnotationCollection;
import guttmanlab.core.annotationcollection.BAMFragmentCollectionFactory;
import net.sf.samtools.util.CloseableIterator;

import org.junit.Test;

public class BamCollectionFactoryTest {

	private static SingleInterval malat1 = new SingleInterval("chr19", 5795689, 5802671, Strand.BOTH, "Malat1");
	
	private URL singleBamUrl = this.getClass().getResource("/guttmanlab/core/test/SingleCollectionTest.bam");
	private URL pairedBamUrl = this.getClass().getResource("/guttmanlab/core/test/PairedCollectionTest.bam");
	private File singleBam = new File(singleBamUrl.getPath());
	private File pairedBam = new File(pairedBamUrl.getPath());	

	@Test
	public void testSingleDetection() {
		assertTrue(!BAMFragmentCollectionFactory.isPairedEnd(singleBam));
	}

	@Test
	public void testPairedDetection() {
		assertTrue(BAMFragmentCollectionFactory.isPairedEnd(pairedBam));
	}
	
	@Test
	public void testSingleImplementation() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(singleBam);
		CloseableIterator<? extends MappedFragment> iter = data.sortedIterator();
		assertEquals("guttmanlab.core.annotation.SAMFragment", iter.next().getClass().getName());
		iter.close();
	}
	
	@Test
	public void testForceSingleImplementation() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam, true);
		CloseableIterator<? extends MappedFragment> iter = data.sortedIterator();
		assertEquals("guttmanlab.core.annotation.SAMFragment", iter.next().getClass().getName());
		iter.close();
	}
	
	@Test
	public void testPairedImplementation() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		CloseableIterator<? extends MappedFragment> iter = data.sortedIterator();
		assertEquals("guttmanlab.core.annotation.PairedMappedFragment", iter.next().getClass().getName());
		iter.close();
	}
	
	@Test
	public void testPairedIteratorCount() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		assertEquals(8, data.getNumAnnotations());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testPairedIteratorRemove() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		CloseableIterator<? extends MappedFragment> iter = data.sortedIterator();
		iter.remove();
		iter.close();
	}

	@Test
	public void testPairedInitialNext() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		CloseableIterator<? extends MappedFragment> iter = data.sortedIterator();
		assertTrue(iter.next() != null);
		iter.close();
	}

	@Test
	public void testPairedIteration() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		CloseableIterator<? extends MappedFragment> iter = data.sortedIterator();
		for (int i = 0; i < 8; i++) {
			assertTrue(iter.hasNext());
			iter.next();			
		}
		assertFalse(iter.hasNext());
		iter.close();
	}

	@Test
	public void testSingleNumOverlappersPartial() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(singleBam);
		assertEquals(4, data.numOverlappers(malat1, false));
	}
	
	@Test
	public void testSingleNumOverlappersFull() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(singleBam);
		assertEquals(2, data.numOverlappers(malat1, true));
	}

	@Test
	public void testPairedNumOverlappersPartial() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		assertEquals(6, data.numOverlappers(malat1, false));
	}
	
	@Test
	public void testPairedNumOverlappersFull() {
		AbstractAnnotationCollection<? extends MappedFragment> data = BAMFragmentCollectionFactory.createFromBam(pairedBam);
		assertEquals(4, data.numOverlappers(malat1, true));
	}
}
