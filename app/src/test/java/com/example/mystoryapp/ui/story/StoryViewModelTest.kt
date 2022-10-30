package com.example.mystoryapp.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.utils.DataDummy
import com.example.mystoryapp.utils.MainDispatcherRule
import com.example.mystoryapp.utils.StoryListPagingSource.Companion.snapshot
import com.example.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Is Not Null and Returns Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data = snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        `when`(storyRepository.getStories()).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<StoryEntity> = storyViewModel.getStoryList().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFFUTIL_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory, differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}