package com.kinship.mobile.app.data.source.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.remote.repository.ApiServices
import com.kinship.mobile.app.model.domain.response.Meta
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class KinshipGroupChatPagingSource(
    private val apiService: ApiServices,
    private var id: String,
    private var type: Int,
    private var imageLinkType: Int,
    private var search:String?,
    private val apiCallback: ApiCallback?,
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
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1
            val response = apiService.getChatListForKinshipGroup(
                id = id,
                type = type,
                imageLinkType = imageLinkType,
                search = "",
                page = pageNo,
                perPage = PAGE_LIMIT
            )
            val nextKey = if (response.body()?.data.isNullOrEmpty()) null else pageNo + 1
            Log.d("TAGPla", "load: $nextKey" + " pgnb $pageNo")
            if (response.isSuccessful && response.body() != null) {
                Log.d("UseCaseChat", "load: call back logged")
                apiCallback?.getFlag(response.body()!!.flag)
                apiCallback?.metaData(response.body()!!.meta)
            }
            LoadResult.Page(
                data = response.body()?.data?: emptyList(),
                prevKey = prevKey,
                nextKey = nextKey
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
abstract class ApiCallback() {
    abstract fun getFlag(flag: Boolean)
    abstract fun metaData(meta: Meta)
}