package guttmanlab.core.rnasprite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import guttmanlab.core.annotation.Annotation;
import guttmanlab.core.annotation.DerivedAnnotation;
import guttmanlab.core.annotation.SingleInterval;
import guttmanlab.core.annotationcollection.AnnotationCollection;
import guttmanlab.core.datastructures.MatrixWithHeaders;
import net.sf.samtools.util.CloseableIterator;

public class LocalRNARNAContactMatrix {

	int windowSize=100;
	
	public LocalRNARNAContactMatrix(BarcodingDataStreaming data, String gene1, String gene2, String save) throws IOException{
		
		Collection<Cluster> clusters=data.getRNAClusters(gene1, gene2);
		
		System.err.println(clusters.size());
		
		Map<String, SingleInterval> rnaRegions=getRegions(clusters);
		
		
		AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows1=getRegions(rnaRegions, gene1, windowSize);
		AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows2=getRegions(rnaRegions, gene2, windowSize);
		
		
		List<String> rowNames=getNames(windows1);
		List<String> columnNames=getNames(windows2);
		
		
		MatrixWithHeaders mwh=new MatrixWithHeaders(rowNames, columnNames);
		
		for(Cluster c: clusters){
			for(SingleInterval region1: c.getAllRNARegions()){
				if(region1.getName().equals(gene1)){
					for(SingleInterval region2: c.getAllRNARegions()){
						if(region2.getName().equals(gene2)){
							//TODO get regions that overlap and populate matrix
							update(mwh, windows1, windows2, region1, region2, c.getAllRNARegions().size());
						}
					}
				}
			}
			
			
		}
		
		mwh.write(save);
	}
	
	

	private Map<String, SingleInterval> getRegions(Collection<Cluster> clusters) {
		Map<String, SingleInterval> rtrn=new TreeMap<String, SingleInterval>();
		
		for(Cluster c: clusters){
			for(SingleInterval rna: c.getAllRNARegions()){
				String name=rna.getName();
				SingleInterval other=null;
				if(rtrn.containsKey(name)){
					other=rtrn.get(name);
				}
				other=merge(other, rna);
				rtrn.put(name, other);
			}
		}
		
		return rtrn;
	}



	private SingleInterval merge(SingleInterval other, SingleInterval rna) {
		if(other==null){return rna;}
		return update(other, rna);
	}
	
	private SingleInterval update(SingleInterval rna1, SingleInterval rna2) {
		String chr=rna1.getReferenceName();
		int start=Math.min(rna1.getReferenceStartPosition(), rna2.getReferenceStartPosition());
		int end=Math.max(rna1.getReferenceEndPosition(), rna2.getReferenceEndPosition());
		SingleInterval rtrn=new SingleInterval(chr, start, end);
		rtrn.setName(rna1.getName());
		return rtrn;
	}



	private void update(MatrixWithHeaders mwh, AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows1,
			AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows2, SingleInterval region1,
			SingleInterval region2, int clusterSize) {
		
		CloseableIterator<DerivedAnnotation<? extends Annotation>> iter1=windows1.sortedIterator(region1, false);
		
		while(iter1.hasNext()){
			String row=iter1.next().toUCSC();
			CloseableIterator<DerivedAnnotation<? extends Annotation>> iter2=windows2.sortedIterator(region2, false);
			while(iter2.hasNext()){
				String column=iter2.next().toUCSC();
				double score=mwh.get(row, column);
				//score+=(2.0/(double)clusterSize);
				score++;
				mwh.set(row, column, score);
			}
			iter2.close();
		}
		
		iter1.close();
		
	}



	



	private List<String> getNames(AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows) {
		List<String> rtrn=new ArrayList<String>();
		
		CloseableIterator<DerivedAnnotation<? extends Annotation>> iter=windows.sortedIterator();
		while(iter.hasNext()){
			String line=iter.next().toUCSC();
			rtrn.add(line);
		}
		
		return rtrn;
	}



	private AnnotationCollection<DerivedAnnotation<? extends Annotation>> getRegions(Map<String, SingleInterval> rnaRegions, String gene1, int windowSize) {
		SingleInterval region=rnaRegions.get(gene1);
		System.err.println(gene1+" "+region.toUCSC());
		AnnotationCollection<DerivedAnnotation<? extends Annotation>> windows=region.getWindows(windowSize, windowSize);
		return windows;
	}

	public static void main(String[] args) throws IOException{
		if(args.length>3){
			BarcodingDataStreaming data=new BarcodingDataStreaming(new File(args[0]));
			String gene1=args[1];
			String gene2=args[2];
			String save=args[3];
			new LocalRNARNAContactMatrix(data, gene1, gene2, save);
		} 
		else{System.err.println(usage);}
	}
	
	static String usage=" args[0]=clusters \n args[1]=gene 1 \n args[2]=gene 2 \n args[3]=save";
	
}