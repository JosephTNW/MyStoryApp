package com.example.mystoryapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Api
import com.example.mystoryapp.ui.story.StoryListAdapter
import com.example.mystoryapp.utils.DataDummy
import com.example.mystoryapp.utils.MainDispatcherRule
import com.example.mystoryapp.utils.StoryListPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var api: Api
    @Mock
    private lateinit var database: StoryDatabase
    @Mock
    private lateinit var dao: StoryDao
    @Mock
    private lateinit var pref: SharedPref
    private val dummyResponse = DataDummy.usualResponse()
    private val dummyFailResp = DataDummy.usualFailed()
    private val dummyLogin = DataDummy.loginResponse()
    private val dummyFailLogin = DataDummy.failLoginResponse()
    private val dummyEmail = "test@test.com"
    private val dummyPass = "123456"
    private val dummyName = "testing"
    private val dummyStory = DataDummy.generateDummyStoryEntity()
    private val dummyFile = DataDummy.generateMultipartFile()
    private val dummyDesc = DataDummy.reqBody("Description")
    private val dummyLat = DataDummy.reqBody("35.6586")
    private val dummyLon = DataDummy.reqBody("139.7454")

    @Before
    fun setUp(){
        storyRepository = StoryRepository(api, dao, database, pref)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Login Success`() = runTest {
        `when`(api.login(dummyEmail, dummyPass)).thenReturn(
            dummyLogin
        )
        storyRepository.login(dummyEmail, dummyPass).observeForever {
            runTest { verify(api).login(dummyEmail, dummyPass) }
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
            if (it is Result.Success) {
                assertNotNull(it.data)
                assertFalse(it.data.error)
                assertEquals(it.data.message, dummyLogin.message)
            }
        }
    }

    @Test
    fun `when Login Failed`() = runTest {
        `when`(api.login(dummyEmail, dummyPass)).thenReturn(
            dummyFailLogin
        )
        storyRepository.login(dummyEmail, dummyPass).observeForever {
            runTest { verify(api).login(dummyEmail, dummyPass) }
            assertTrue(it is Result.Error)
            assertFalse(it is Result.Success)
            if (it is Result.Error){
                assertNotNull(it.error)
                assertEquals(it.error, dummyFailLogin.message)
            }
        }
    }

    @Test
    fun `when Register Success`() = runTest {
        `when`(api.register(dummyName, dummyEmail, dummyPass)).thenReturn(
            dummyResponse
        )
        storyRepository.register(dummyEmail, dummyPass, dummyName).observeForever {
            runTest { verify(api).register(dummyName, dummyEmail, dummyPass)}
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
            if (it is Result.Success){
                assertNotNull(it.data)
                assertFalse(it.data.error)
                assertEquals(it.data.message, dummyResponse.message)
            }
        }
    }

    @Test
    fun `when Register Failed`() = runTest {
        `when`(api.register(dummyName, dummyEmail, dummyPass)).thenReturn(
            dummyFailResp
        )
        storyRepository.register(dummyEmail, dummyPass, dummyName).observeForever {
            runTest { verify(api).register(dummyName, dummyEmail, dummyPass) }
            assertTrue(it is Result.Error)
            assertFalse(it is Result.Success)
            if (it is Result.Error){
                assertNotNull(it.error)
                assertEquals(it.error, dummyFailLogin.message)
            }
        }
    }

    @Test
    fun `when Get Story Success`() = runTest {
        val data = StoryListPagingSource.snapshot(dummyStory)
        val expectedResponse : LiveData<PagingData<StoryEntity>> = liveData {
            emit(data)
        }

        `when`(storyRepository.getStories()).thenReturn(
            expectedResponse
        )
        storyRepository.getStories().observeForever {
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryListAdapter.DIFFUTIL_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            CoroutineScope(Dispatchers.IO).launch{
                differ.submitData(it)
            }
            advanceUntilIdle()

            verify(storyRepository).getStories()
            assertNotNull(differ.snapshot())
            assertEquals(differ.snapshot().size, dummyStory.size)
            assertEquals(differ.snapshot()[0]?.id, dummyStory[0].id)
        }
    }

    @Test
    fun `when Get Local Story Success`() = runTest {
        val expectedResponse : LiveData<List<StoryEntity>> = liveData {
            emit(dummyStory)
        }
        `when`(dao.getLocalStory()).thenReturn(
            expectedResponse
        )
        storyRepository.getStoryFromDb().observeForever {
            verify(dao).getLocalStory()
            assertNotNull(it)
            assertTrue(it.isNotEmpty())
            assertEquals(it.size, dummyStory.size)
            assertEquals(it[0].id, dummyStory[0].id)
        }
    }

    @Test
    fun `when Add Story Success`() = runTest {
        `when`(api.addStory(dummyFile, dummyDesc, dummyLat, dummyLon)).thenReturn(
            dummyResponse
        )
        storyRepository.addStory(dummyFile, dummyDesc, dummyLat, dummyLon).observeForever {
            verify(api).addStory(dummyFile, dummyDesc, dummyLat, dummyLon)
            assertNotNull(it)
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
            if (it is Result.Success){
                assertNotNull(it.data)
                assertFalse(it.data.error)
                assertEquals(it.data.message, dummyResponse.message)
            }
        }
    }

    @Test
    fun `when Add Story Fail`() = runTest {
        `when`(api.addStory(dummyFile, dummyDesc, dummyLat, dummyLon)).thenReturn(
            dummyFailResp
        )
        storyRepository.addStory(dummyFile, dummyDesc, dummyLat, dummyLon).observeForever {
            verify(api).addStory(dummyFile, dummyDesc, dummyLat, dummyLon)
            assertNotNull(it)
            assertTrue(it is Result.Error)
            assertFalse(it is Result.Success)
            if (it is Result.Error){
                assertNotNull(it.error)
                assertEquals(it.error, dummyFailResp.message)
            }
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}