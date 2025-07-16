package com.kinship.mobile.app.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kinship.mobile.app.data.source.remote.repository.ApiServices
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import retrofit2.HttpException
import java.io.IOException
class GetKinshipDataPagingSource(
    private val apiService: ApiServices,
    private var id: String,
    private var type: Int,
    private var imageLinkType: Int,
    private var search: String?
) : PagingSource<Int, KinshipGroupChatListData>() {
    companion object {
        private const val STARTING_KEY: Int = 1
        private const val PAGE_LIMIT = 200
    }
    override fun getRefreshKey(state: PagingState<Int, KinshipGroupChatListData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, KinshipGroupChatListData> {
        return try {
            val from = params.key ?: STARTING_KEY
            val prevKey = if (from == STARTING_KEY) null else from - PAGE_LIMIT
            val response = apiService.getKinshipChatListData(
                id = id,
                type = type,
                imageLinkType = imageLinkType,
                search = "",
                page = from,
                perPage = PAGE_LIMIT
            )
            LoadResult.Page(
                data = response.body()?.data ?: emptyList(),
                prevKey = prevKey,
                nextKey = null
            )
        } catch (e: IOException) {
            return LoadResult.Error(IOException(e))
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}