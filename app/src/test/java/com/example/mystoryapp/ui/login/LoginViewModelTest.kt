package com.example.mystoryapp.ui.login

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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dataDummy = DataDummy.usualResponse()
    private val dummyEmail = "test@test.com"
    private val dummyPass = "123456"
    private val dummyError = "error"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
        loginViewModel.login(dummyEmail, dummyPass)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Login Success`() = runTest {
        val expectedResponse : LiveData<Result<UsualResponse>> = liveData {
            emit(Result.Success(dataDummy))
        }
        `when`(storyRepository.login(dummyEmail, dummyPass)).thenReturn(
            expectedResponse
        )
        loginViewModel.getLoginResult().observeForever{
            verify(storyRepository).login(dummyEmail, dummyPass)
            if (it is Result.Success){
                assertNotNull(it.data)
                assertFalse(it.data.error)
            }
            assertTrue(it is Result.Success)
            assertFalse(it is Result.Error)
        }
    }

    @Test
    fun `when Login Fail`() = runTest {
        val expectedResponse : LiveData<Result<UsualResponse>> = liveData {
            emit(Result.Error(dummyError))
        }
        `when`(storyRepository.login(dummyEmail, dummyPass)).thenReturn(
            expectedResponse
        )
        loginViewModel.getLoginResult().observeForever{
            verify(storyRepository).login(dummyEmail, dummyPass)
            if (it is Result.Error){
                assertNotNull(it.error)
                assertEquals(it.error, dummyError)
            }
            assertFalse(it is Result.Success)
            assertTrue(it is Result.Error)
        }
    }
}