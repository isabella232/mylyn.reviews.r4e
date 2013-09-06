package org.eclipse.mylyn.reviews.r4e_gerrit.ui.test;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import org.powermock.modules.junit4.PowerMockRunner;


import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGerritServerUtility;
import org.eclipse.mylyn.tasks.core.TaskRepository;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(TasksUiPlugin.class)

public class R4EGerritServerUtilityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testR4EGerritServerUtility() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);
		Mockito.when(TasksUiPlugin.getRepositoryTemplateManager()).thenReturn(null);

		R4EGerritServerUtility r4eGerritServerUtility = R4EGerritServerUtility.getInstance();
		
		assertNotNull(r4eGerritServerUtility);
	}

//	@Test
	public void testMapConfiguredGerritServerNoRepoManager() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);
		Mockito.when(TasksUiPlugin.getRepositoryTemplateManager()).thenReturn(null);		
		R4EGerritServerUtility r4eGerritServerUtility = R4EGerritServerUtility.getInstance();

		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(null);		

	
		Map<TaskRepository, String> ret;
		
		ret = r4eGerritServerUtility.getGerritMapping();
		
		assertEquals(ret,null);
	}

//	@Test
	public void testMapConfiguredGerritServer() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
//		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);
		Mockito.when(TasksUiPlugin.getRepositoryTemplateManager()).thenReturn(null);		
		R4EGerritServerUtility r4eGerritServerUtility = R4EGerritServerUtility.getInstance();

//		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
//		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);		
		Map<TaskRepository, String> ret;
		
		ret = r4eGerritServerUtility.getGerritMapping();
		
		assertNotNull(ret);

	
	}

	@Test
	public void testGetGerritMappingOneServer() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);

		RepositoryTemplateManager templateManager = new RepositoryTemplateManager();		
		Mockito.when(TasksUiPlugin.getRepositoryTemplateManager()).thenReturn(templateManager);
		
		
		Map<TaskRepository, String> ret;
		
		ret = R4EGerritServerUtility.getInstance().getGerritMapping();
		
		assertEquals(ret.size(),1);
	}

	
	@Test
	public void testGetGerritMappingRepoManagerNull() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(null);
		
		Map<TaskRepository, String> ret;
		
		ret = R4EGerritServerUtility.getInstance().getGerritMapping();
		
		assertEquals(ret.size(),0);
	}
	
//	@Test
	public void testGetGerritMappingRepositoriesNull() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		
		
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));

		TaskRepositoryManager repoManager = mock(TaskRepositoryManager.class);		
		
	    Mockito.when(repoManager.getRepositories(anyString())).thenReturn(null);
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);		
		
		Map<TaskRepository, String> ret;
		
		ret = R4EGerritServerUtility.getInstance().getGerritMapping();
		
		assertEquals(ret.size(),0);
	}	
	
//	@Test
	public void testSaveLastGerritServer() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGetLastSavedGerritServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMenuSelectionURL() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);

		RepositoryTemplateManager templateManager = new RepositoryTemplateManager();		
		Mockito.when(TasksUiPlugin.getRepositoryTemplateManager()).thenReturn(templateManager);
		
		
		Map<TaskRepository, String> ret;
		
		ret = R4EGerritServerUtility.getInstance().getGerritMapping();
		
		assertEquals(ret.size(),1);
		
		String res = R4EGerritServerUtility.getInstance().getMenuSelectionURL("http://repository");
		
		assertTrue(res.compareTo( new String ("http://repository")) == 0);
		
	}

	@Test
	public void testGetTaskRepo() {
		PowerMockito.mockStatic(TasksUiPlugin.class);
		TaskRepositoryManager repositoryManager = new  TaskRepositoryManager();
		repositoryManager.addRepository(new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"));
		
		Mockito.when(TasksUiPlugin.getRepositoryManager()).thenReturn(repositoryManager);

		RepositoryTemplateManager templateManager = new RepositoryTemplateManager();		
		Mockito.when(TasksUiPlugin.getRepositoryTemplateManager()).thenReturn(templateManager);
		
		
		Map<TaskRepository, String> ret;
		
		ret = R4EGerritServerUtility.getInstance().getGerritMapping();
		
		assertEquals(ret.size(),1);
		
		TaskRepository res = R4EGerritServerUtility.getInstance().getTaskRepo("http://repository");
		
		assertTrue(res.getRepositoryUrl().compareTo( new String ("http://repository")) == 0);
	}

}
