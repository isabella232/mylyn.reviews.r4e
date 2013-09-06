package org.eclipse.mylyn.reviews.r4e_gerrit.ui.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import static org.junit.Assert.assertEquals;

import org.powermock.modules.junit4.PowerMockRunner;


//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
//
@PrepareForTest(R4EGerritServerUtility.class)


public class R4EGerritTableViewTest {

	
	private R4EGerritTableView r4eGerritTableView;
	@Mock
    private R4EGerritServerUtility fServerUtil; 


	@Test
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void testProcessCommandsNoSavedServer() {
		r4eGerritTableView = new R4EGerritTableView();
		GerritConnector fConnector = mock(GerritConnector.class);
		ReviewTableData fReviewTable = mock(ReviewTableData.class);
		R4EGerritServerUtility fServerUtil = mock(R4EGerritServerUtility.class);
		r4eGerritTableView.setConnector(fConnector);
		r4eGerritTableView.setReviewTableData(fReviewTable);
		r4eGerritTableView.setGerritServerUtility(fServerUtil);	
		
		String ret = null;
		when(fServerUtil.getLastSavedGerritServer()).thenReturn(ret);	
		
		// Last saved is null, a repository must be defined.
		r4eGerritTableView.processCommands(GerritQuery.CUSTOM);
		
	    assertEquals(r4eGerritTableView.getTaskRepository(),null);


	

	}

//	@Test
	public void testProcessCommandsNoGerritRepo() {
		PowerMockito.mockStatic(R4EGerritServerUtility.class);
		PowerMockito.mockStatic(GerritCorePlugin.class);
		
		Mockito.when(GerritCorePlugin.getDefault().getConnector()).thenReturn(null);		

		r4eGerritTableView = new R4EGerritTableView();
		GerritConnector fConnector = mock(GerritConnector.class);
		ReviewTableData fReviewTable = mock(ReviewTableData.class);
		R4EGerritServerUtility fServerUtil = mock(R4EGerritServerUtility.class);
		r4eGerritTableView.setConnector(fConnector);
		r4eGerritTableView.setReviewTableData(fReviewTable);
		r4eGerritTableView.setGerritServerUtility(fServerUtil);	
		
		
        // last saved exists
		fReviewTable = new ReviewTableData();
		r4eGerritTableView.setReviewTableData(fReviewTable);		
		when(fServerUtil.getLastSavedGerritServer()).thenReturn("lastSavedGerritServer");
		
		
		when(fServerUtil.getTaskRepo(anyString())).thenReturn( new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository") );
		
		// no server defined
		Map<TaskRepository, String> fMapRepoServer = new HashMap<TaskRepository, String>();
		Mockito.when(R4EGerritServerUtility.getInstance().getGerritMapping()).thenReturn(fMapRepoServer);		

		r4eGerritTableView.processCommands(GerritQuery.CUSTOM);
		
		// check what now ?

	}	
	
//	@Test
	public void testProcessCommandsOneGerritRepo() {
		
		PowerMockito.mockStatic(R4EGerritServerUtility.class);
		String msg = "testProcessCommands2";
		String reason = "testProcessCommands2.";
		R4EGerritUi.Ftracer.traceInfo(msg );
		UIUtils.showErrorDialog(msg, reason);
		
		r4eGerritTableView = new R4EGerritTableView();
		GerritConnector fConnector = mock(GerritConnector.class);
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
		Mockito.when(R4EGerritServerUtility.getInstance().getGerritMapping()).thenReturn(fMapRepoServer);

		r4eGerritTableView.processCommands(GerritQuery.CUSTOM);

		Iterator it = fMapRepoServer.entrySet().iterator();
		while (it.hasNext()) {
		    assertEquals(r4eGerritTableView.getTaskRepository(),((Map.Entry) it.next()).getKey());
		}

		

	}



}
