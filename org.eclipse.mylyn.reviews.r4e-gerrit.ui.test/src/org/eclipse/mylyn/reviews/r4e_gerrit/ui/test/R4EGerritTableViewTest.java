package org.eclipse.mylyn.reviews.r4e_gerrit.ui.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.mylyn.reviews.r4e_gerrit.trace.Tracer;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.R4EGerritUi;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.model.ReviewTableData;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGerritServerUtility;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.reviews.r4egerrit.ui.views.R4EGerritTableView;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.*;

import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({R4EGerritServerUtility.class, GerritCorePlugin.class, R4EGerritUi.class})


public class R4EGerritTableViewTest {
	
	private R4EGerritTableView r4eGerritTableView;
	@Mock
    private R4EGerritServerUtility fServerUtil; 
	R4EGerritUi R4EGUi;
	public Tracer Ftracer;

	@Test
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// Use case testing when no previously Gerrit server exists (getLastSavedGerritServer()==null)
	public void testProcessCommandsNoSavedServer() {
		R4EGUi = new R4EGerritUi();
		R4EGUi.Ftracer = new Tracer();
		PowerMockito.mockStatic(R4EGerritUi.class);
		Mockito.when(R4EGerritUi.getDefault()).thenReturn(new R4EGerritUi());	
		PowerMockito.mockStatic(R4EGerritServerUtility.class);
		GerritCorePlugin gcp = new GerritCorePluginStub();		
		
		PowerMockito.mockStatic(GerritCorePlugin.class);
		
		Mockito.when(GerritCorePlugin.getDefault()).thenReturn(gcp);		
		GerritConnector fConnector = mock(GerritConnector.class);

		r4eGerritTableView = new R4EGerritTableView();
		R4EGerritServerUtility fServerUtil = mock(R4EGerritServerUtility.class);

		r4eGerritTableView.setGerritServerUtility(fServerUtil);	
		
		String ret = null;
		when(fServerUtil.getLastSavedGerritServer()).thenReturn(ret);	
		
		Map<TaskRepository, String> fMapRepoServer = new HashMap<TaskRepository, String>();
		
		fMapRepoServer.put( new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"), " ");
		Mockito.when(R4EGerritServerUtility.getInstance()).thenReturn(fServerUtil);
		Mockito.when(R4EGerritServerUtility.getInstance().getGerritMapping()).thenReturn(fMapRepoServer);		
		Mockito.when(fServerUtil.getGerritMapping()).thenReturn(fMapRepoServer);		
		
		// Last saved is null, a repository must be defined.
		r4eGerritTableView.processCommands(GerritQuery.CUSTOM);
		
		assertNotNull(r4eGerritTableView.getTaskRepository());


	}

	@Test
	// Use case testing when no previously Gerrit server exists (fServerUtil.getTaskRepo(lastSaved)==null)
	public void testProcessCommandsNoGerritRepo() {
		R4EGUi = new R4EGerritUi();
		R4EGUi.Ftracer = new Tracer();
		PowerMockito.mockStatic(R4EGerritUi.class);
		Mockito.when(R4EGerritUi.getDefault()).thenReturn(new R4EGerritUi());		
		
		PowerMockito.mockStatic(R4EGerritServerUtility.class);
		GerritCorePlugin gcp = new GerritCorePluginStub();		
		
		PowerMockito.mockStatic(GerritCorePlugin.class);
		
		Mockito.when(GerritCorePlugin.getDefault()).thenReturn(gcp);
		GerritConnector fConnector = mock(GerritConnector.class);

		r4eGerritTableView = new R4EGerritTableView();
		ReviewTableData fReviewTable = mock(ReviewTableData.class);
		R4EGerritServerUtility fServerUtil = mock(R4EGerritServerUtility.class);
		r4eGerritTableView.setConnector(fConnector);
		r4eGerritTableView.setReviewTableData(fReviewTable);
		r4eGerritTableView.setGerritServerUtility(fServerUtil);	
		
		
        // last saved exists
		fReviewTable = new ReviewTableData();
		r4eGerritTableView.setReviewTableData(fReviewTable);		
		when(fServerUtil.getLastSavedGerritServer()).thenReturn("lastSavedGerritServer");
		
		
		when(fServerUtil.getTaskRepo(anyString())).thenReturn( null );
		
		// no server defined
		Map<TaskRepository, String> fMapRepoServer = new HashMap<TaskRepository, String>();
		
		fMapRepoServer.put( new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"), " ");
		Mockito.when(R4EGerritServerUtility.getInstance()).thenReturn(fServerUtil);
		Mockito.when(R4EGerritServerUtility.getInstance().getGerritMapping()).thenReturn(fMapRepoServer);		
		Mockito.when(fServerUtil.getGerritMapping()).thenReturn(fMapRepoServer);		

		r4eGerritTableView.processCommands(GerritQuery.CUSTOM);
	
		Iterator it = fMapRepoServer.entrySet().iterator();
		while (it.hasNext()) {
		    assertEquals(r4eGerritTableView.getTaskRepository(),((Map.Entry) it.next()).getKey());
		}	


	}	
	
	@Test
	// Use case testing when one previously Gerrit server exists 
	public void testProcessCommandsOneGerritRepo() {
		R4EGUi = new R4EGerritUi();
		R4EGUi.Ftracer = new Tracer();
		PowerMockito.mockStatic(R4EGerritUi.class);
		Mockito.when(R4EGerritUi.getDefault()).thenReturn(new R4EGerritUi());
		
		PowerMockito.mockStatic(R4EGerritServerUtility.class);

		GerritCorePlugin gcp = new GerritCorePluginStub();		
		
		PowerMockito.mockStatic(GerritCorePlugin.class);
		
		Mockito.when(GerritCorePlugin.getDefault()).thenReturn(gcp);
		GerritConnector fConnector = mock(GerritConnector.class);		
		
		r4eGerritTableView = new R4EGerritTableView();
//		GerritConnector fConnector = mock(GerritConnector.class);
		ReviewTableData fReviewTable = mock(ReviewTableData.class);
		R4EGerritServerUtility fServerUtil = mock(R4EGerritServerUtility.class);
		r4eGerritTableView.setConnector(fConnector);
		r4eGerritTableView.setReviewTableData(fReviewTable);
		r4eGerritTableView.setGerritServerUtility(fServerUtil);	
		
		
        // last saved exists, so there is a repository, then the table gets updated
		fReviewTable = new ReviewTableData();
		r4eGerritTableView.setReviewTableData(fReviewTable);		
		Mockito.when(fServerUtil.getLastSavedGerritServer()).thenReturn( (String) "lastSavedGerritServer");

		when(fServerUtil.getTaskRepo(anyString())).thenReturn( new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository") );
		
		// one server defined
		Map<TaskRepository, String> fMapRepoServer = new HashMap<TaskRepository, String>();
		fMapRepoServer.put( new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"), " ");

		r4eGerritTableView.processCommands(GerritQuery.CUSTOM);

		Iterator it = fMapRepoServer.entrySet().iterator();
		while (it.hasNext()) {
		    assertEquals(r4eGerritTableView.getTaskRepository(),((Map.Entry) it.next()).getKey());
		}

		

	}



}
