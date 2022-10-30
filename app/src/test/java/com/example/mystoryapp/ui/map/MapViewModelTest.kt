package com.example.mystoryapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.utils.DataDummy
import com.example.mystoryapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class MapViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapViewModel: MapViewModel
    private val dataDummy = DataDummy.generateDummyStoryEntity()

    @Before
    fun setUp() {
        mapViewModel = MapViewModel(storyRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Get Local Story Success`() = runTest {
        val expectedResponse : LiveData<List<StoryEntity>> = liveData {
            dataDummy
        }
        `when`(storyRepository.getStoryFromDb()).thenReturn(expectedResponse)
        mapViewModel.getStoryList().observeForever {
            verify(storyRepository).getStoryFromDb()
            assertNotNull(it)
            assertTrue(it.isNotEmpty())
            assertEquals(it.size, dataDummy.size)
        }
    }

    @Test
    fun `when Get Local Story Fail`() = runTest {
        val expectedResponse : LiveData<List<StoryEntity>> = liveData {
            emit(listOf())
        }
        `when`(storyRepository.getStoryFromDb()).thenReturn(
            expectedResponse
        )
        mapViewModel.getStoryList().observeForever {
            verify(storyRepository).getStoryFromDb()
            assertNotNull(it)
            assertTrue(it.isEmpty())
        }
    }
}