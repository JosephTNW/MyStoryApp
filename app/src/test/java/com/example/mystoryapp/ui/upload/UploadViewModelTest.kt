package com.example.mystoryapp.ui.upload

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.UsualResponse
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
class UploadViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var uploadViewModel: UploadViewModel
    private val dataDummy = DataDummy.usualResponse()
    private val dummyFile = DataDummy.generateMultipartFile()
    private val dummyDesc = DataDummy.reqBody("dummy description")
    private val dummyLat = DataDummy.reqBody("35.6586")
    private val dummyLon = DataDummy.reqBody("139.7454")
    private val dummyError = "error"

    @Before
    fun setUp() {
        uploadViewModel = UploadViewModel(storyRepository)
        uploadViewModel.sendStory(dummyFile, dummyDesc, dummyLat, dummyLon)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Add Story Success`() = runTest{
        val expectedResponse : LiveData<Result<UsualResponse>> = liveData {
            emit(Result.Success(dataDummy))
        }
        `when`(storyRepository.addStory(dummyFile, dummyDesc, dummyLat, dummyLon)).thenReturn(
            expectedResponse
        )
        uploadViewModel.addStory().observeForever{
            verify(storyRepository).addStory(dummyFile, dummyDesc, dummyLat, dummyLon)
            if (it is Result.Success){
                assertNotNull(it.data)
                assertFalse(it.data.error)
            }
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
        }
    }

    @Test
    fun `when Add Story Failed`() = runTest{
        val expectedResponse : LiveData<Result<UsualResponse>> = liveData {
            emit(Result.Error(dummyError))
        }
        `when`(storyRepository.addStory(dummyFile, dummyDesc, dummyLat, dummyLon)).thenReturn(
            expectedResponse
        )
        uploadViewModel.addStory().observeForever{
            verify(storyRepository).addStory(dummyFile, dummyDesc, dummyLat, dummyLon)
            if (it is Result.Error){
                assertNotNull(it.error)
                assertEquals(it.error, dummyError)
            }
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
        }
    }
}