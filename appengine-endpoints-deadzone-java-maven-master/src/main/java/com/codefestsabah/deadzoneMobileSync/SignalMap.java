package com.codefestsabah.deadzoneMobile;

import com.google.appengine.api.NamespaceManager;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;

import org.apache.commons.collections.CollectionUtils;
import java.util.List;
import java.util.ArrayList;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(name = "deadzoneMobile",
     version = "v1",
	 clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
     namespace = @ApiNamespace(ownerDomain = "deadzoneMobile.codefestsabah.com",
                                ownerName = "deadzoneMobile.codefestsabah.com",
                                packagePath=""))

public class SignalMap {
    /** A simple endpoint method that takes a name and says Hi back */
	
	private boolean kindExists;
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public SignalMap() {
		Entity dzHeatmap = new Entity("deadzone_heatmap");
		kindExists = kindExists("deadzone_heatmap");
	}
		
	@ApiMethod(name = "signalMap.get", httpMethod = "get")
	public List<SignalMapModel> getSignalMap() {
		List<SignalMapModel> list = new ArrayList<SignalMapModel>();
    	Query q = new Query("deadzone_heatmap");
    	PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		for(Entity en : result) {
			SignalMapModel model = new SignalMapModel();
			model.setOperator((String) en.getProperty("operator"));
			model.setNetwork((String) en.getProperty("network"));
			model.setTimestamp((String) en.getProperty("timestamp"));
			model.setLatitude(Float.parseFloat((String) en.getProperty("latitude")));
			model.setLongitude(Float.parseFloat((String) en.getProperty("longitude")));
			model.setSignalStrength(Float.parseFloat((String) en.getProperty("signalStrength")));
		    list.add(model);
		}
		return list;
	}
	
	public static boolean kindExists (String kind)
	{
		Query q = new Query(kind).setKeysOnly();
		PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
		return (CollectionUtils.isNotEmpty(pq.asList(FetchOptions.Builder.withLimit(1))));
	}	
	
	@ApiMethod(name = "signalMap.add", httpMethod = "post" )
	public SignalMapModel insertSignalMap(SignalMapModel model) {
		Entity dzHeatmap = new Entity("deadzone_heatmap");
		dzHeatmap.setProperty("operator", model.getOperator());
		dzHeatmap.setProperty("network", model.getNetwork());
		dzHeatmap.setProperty("timestamp", model.getTimestamp());
		dzHeatmap.setProperty("latitude", model.getLatitude());
		dzHeatmap.setProperty("longitude", model.getLongitude());
		dzHeatmap.setProperty("signalStrength", model.getSignalStrength());
		datastore.put(dzHeatmap);
		return model;
	}
}
