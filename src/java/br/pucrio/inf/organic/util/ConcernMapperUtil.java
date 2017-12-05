package br.pucrio.inf.organic.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.ConcernMapperPreferenceInitializer;
import ca.mcgill.cs.serg.cm.model.io.IProgressMonitor;
import ca.mcgill.cs.serg.cm.model.io.ModelIOException;
import ca.mcgill.cs.serg.cm.model.io.ModelReader;

public class ConcernMapperUtil {

	/**
	 * Read the elements (concerns or components) defined in the mapping file.
	 * Return true if the file was read successfully and false otherwise.
	 * @return True or False
	 */
	public static boolean readConcernMapperFile(IProject eclipseProject, String fileName) {
		final IFile  mappingFile = (IFile) eclipseProject.getProject().getFile(fileName); 

		if (!mappingFile.exists())
			return false;
		
		if (ConcernMapper.getDefault() == null) {
			new ConcernMapper();	
		}
		
		try
		{
			ConcernMapper.getDefault().getConcernModel().startStreaming();
			ConcernMapper.getDefault().setDefaultResource( mappingFile );
		    ConcernMapper.getDefault().getConcernModel().reset();
		    ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
		        
		    final int lSkipped = lReader.read( mappingFile, new IProgressMonitor() {
				@Override public void worked(int arg0) {}
				@Override public void setTotal(int arg0) {}
			});
		}
		catch( ModelIOException lException )
		{
			System.err.println("Exception while reading Concern Mapper file: " + lException.getMessage());
			ConcernMapper.getDefault().getConcernModel().reset();
			return false;
		}
		catch (Exception ex) {
			System.err.println("Exception while reading Concern Mapper file: " + ex.getMessage());
			return false;
		}
		finally
		{
			ConcernMapper.getDefault().getConcernModel().stopStreaming();
			ConcernMapper.getDefault().resetDirty();
		}
		
		return true;
	}	
}
