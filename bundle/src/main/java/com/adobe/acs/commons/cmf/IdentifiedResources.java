package com.adobe.acs.commons.cmf;

import java.util.List;

import org.apache.sling.api.resource.Resource;



public class IdentifiedResources {
	
	List <String> paths; // the paths only
	String contentMigrationStep;
	
	public IdentifiedResources (List<String> paths, String name) {
		this.paths = paths;
		this.contentMigrationStep = name;
	}
	
	
	public List <String> getPaths () {
		return paths;
	}
	
	
	/**
	 * identifies the step which created this IdentifiedResource
	 * @return the label of the CM Step
	 */
	public String getContentMigrationStep () {
		return contentMigrationStep;
	}
	
	

}
