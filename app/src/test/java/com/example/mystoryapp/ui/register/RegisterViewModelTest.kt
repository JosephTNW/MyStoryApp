package com.example.mystoryapp.ui.register

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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dataDummy = DataDummy.usualResponse()
    private val dummyEmail = "test@test.com"
    private val dummyPass = "123456"
    private val dummyUsername = "testing"
    private val dummyError = "error"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(storyRepository)
        registerViewModel.sendRegistration(dummyEmail, dummyPass, dummyUsername)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Register Success`() = runTest {
        val expectedResponse : LiveData<Result<UsualResponse>> = liveData {
            emit(Result.Success(dataDummy))
        }
        Mockito.`when`(storyRepository.register(dummyEmail, dummyPass, dummyUsername)).thenReturn(
            expectedResponse
        )
        registerViewModel.getRegResult().observeForever{
            Mockito.verify(storyRepository).register(dummyEmail, dummyPass, dummyUsername)
            if (it is Result.Success){
                assertNotNull(it.data)
                assertFalse(it.data.error)
            }
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
        }
    }

    @Test
    fun `when Register Fail`() = runTest {
        val expectedResponse : LiveData<Result<UsualResponse>> = liveData {
            emit(Result.Error(dummyError))
        }
        Mockito.`when`(storyRepository.register(dummyEmail, dummyPass, dummyUsername)).thenReturn(
            expectedResponse
        )
        registerViewModel.getRegResult().observeForever{
            Mockito.verify(storyRepository).login(dummyEmail, dummyPass)
            if (it is Result.Error){
                assertNotNull(it.error)
                assertEquals(it.error, dummyError)
            }
            assertFalse(it is Result.Success)
            assertTrue(it is Result.Error)
        }
    }
}