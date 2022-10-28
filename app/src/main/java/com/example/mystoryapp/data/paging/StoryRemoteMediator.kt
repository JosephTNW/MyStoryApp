package com.example.mystoryapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mystoryapp.data.local.entity.RemoteKeysEntity
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Api
import com.example.mystoryapp.utils.Constants.LOCATION

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val apiService: Api
) : RemoteMediator<Int, StoryEntity>() {

    private companion object {
        const val FIRST_PAGE = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: FIRST_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStory(page.toString(), state.config.pageSize.toString(), LOCATION).listStory

            val endOfPaginationReached = responseData.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyDatabase.StoryDao().clearStory()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeysEntity(it.id, prevKey, nextKey)
                }
                storyDatabase.remoteKeysDao().insertAll(keys)
                val storyMap = responseData.map {
                    with(it){
                        StoryEntity(
                            id, photoUrl, name, desc, lon, lat, createdAt
                        )
                    }
                }
                storyDatabase.StoryDao().addStory(storyMap)
            }
            return MediatorResult.Success(endOfPaginationReached)
        } catch (exc: Exception) {
            return MediatorResult.Error(exc)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            storyDatabase.remoteKeysDao().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            storyDatabase.remoteKeysDao().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { pos ->
            state.closestItemToPosition(pos)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}