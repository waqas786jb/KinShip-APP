package com.kinship.mobile.app.data.source.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.paging.ApiCallback
import com.kinship.mobile.app.data.source.remote.paging.GetKinshipDataPagingSource
import com.kinship.mobile.app.data.source.remote.paging.KinshipGroupChatPagingSource
import com.kinship.mobile.app.data.source.remote.paging.NotificationPagingSource
import com.kinship.mobile.app.model.domain.request.adoptedRequest.AdoptedRequest
import com.kinship.mobile.app.model.domain.request.babyRequest.BabyRequest
import com.kinship.mobile.app.model.domain.request.community.NewSuggestionReq
import com.kinship.mobile.app.model.domain.request.community.commentRequest.AddCommentRequest
import com.kinship.mobile.app.model.domain.request.conceiveRequest.ConceiveRequest
import com.kinship.mobile.app.model.domain.request.contactSupport.ContactSupportRequest
import com.kinship.mobile.app.model.domain.request.kinshipName.KinshipNameEditRequest
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpUpdateVerificationRequest
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpVerificationRequest
import com.kinship.mobile.app.model.domain.request.pregnantRequest.PregnantRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.ChangePasswordRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.EventStatusRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.IsLikeDislikeRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.LogOutRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.RefreshTokenRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.RegisterForPushRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.ResendOtpRequest
import com.kinship.mobile.app.model.domain.request.signUp.SignInRequest
import com.kinship.mobile.app.model.domain.request.signUp.logIn.LogInRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.NewPasswordOtpVerificationReq
import com.kinship.mobile.app.model.domain.request.userForgetPassword.NewPasswordRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.UserForgetPasswordRequest
import com.kinship.mobile.app.model.domain.request.userGroup.AddGroupMemberRequest
import com.kinship.mobile.app.model.domain.request.userProfileRequest.UserProfileRequest
import com.kinship.mobile.app.model.domain.request.versionRequest.KinshipVersionRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike.IsLikeDislikeResponse
import com.kinship.mobile.app.model.domain.response.chat.sendMessage.SendMessage
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.AddGroupMember
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.groupMember.UserGroupMember
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.model.domain.response.communities.communityPost.commentRedirect.CommentRedirectResponse
import com.kinship.mobile.app.model.domain.response.createEvent.CreateEventResponse
import com.kinship.mobile.app.model.domain.response.evenrName.EventNameData
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.hobbiesData.HobbiesData
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationListResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationSettingResponse
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.utils.ext.extractError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class ApiRepositoryImpl @Inject constructor(
    private val apiServices: ApiServices
) : ApiRepository {
    override fun register(signInRequest: SignInRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.doSignIn(signInRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun logIn(loginInRequest: LogInRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.logInAsync(loginInRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun otpVerification(otpVerificationRequest: OtpVerificationRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.otpVerificationAsync(otpVerificationRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun otpUpdateContactVerification(otpVerificationRequest: OtpUpdateVerificationRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.otpUpdateContactVerificationAsync(otpVerificationRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }

        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun resendOtp(resendOtpRequest: ResendOtpRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.resendOtpAsync(resendOtpRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun forgetPassword(resendOtpRequest: ResendOtpRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.forgetPasswordAsync(resendOtpRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun refreshToken(request: RefreshTokenRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.refreshTokenAsync(request)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun changePassword(changePasswordRequest: ChangePasswordRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.changePasswordAsync(changePasswordRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun leaveKinship(reason: String): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.leaveKinshipAsync(reason)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun deleteAccount(reason: String): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.deleteKinshipAsync(reason)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun logout(logoutRequest: LogOutRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.logoutAsync(logoutRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun conceiveRequest(conceiveRequest: ConceiveRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.conceiveQuestionFlowAsync(conceiveRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun pregnantRequest(conceiveRequest: PregnantRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.pregnantQuestionFlowAsync(conceiveRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }
    override fun babyRequest(babyRequest: BabyRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.babyQuestionFlowAsync(babyRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun adoptedRequest(adoptedRequest: AdoptedRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.adoptedQuestionFlowAsync(adoptedRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun userProfileRequest(userProfileRequest: UserProfileRequest): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.userProfileAsync(userProfileRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getHobbiesList(): Flow<NetworkResult<ApiResponseNew<HobbiesData>>> = flow {
        try {
            val response = apiServices.getHobbiesAsync()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getCreateGroup(): Flow<NetworkResult<ApiResponse<CreateGroup>>> = flow {
        try {
            val response = apiServices.getGroup()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun createWithImageEvent(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<CreateEventResponse>>> = flow {
        try {
            val response = apiServices.createEventWithImageAsync(req, profileImage)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun createEvent(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<CreateEventResponse>>> =
        flow {
            try {
                val response = apiServices.createEventAsync(req)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getMyEvents(): Flow<NetworkResult<ApiResponseNew<MyEventsData>>> = flow {
        try {
            val response = apiServices.getMyEvents()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getUpcomingEvents(): Flow<NetworkResult<ApiResponseNew<MyEventsData>>> = flow {
        try {
            val response = apiServices.getUpcomingEvents()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getEvents(): Flow<NetworkResult<ApiResponseNew<MyEventsData>>> = flow {
        try {
            val response = apiServices.getEvents()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun registerForPush(request: RegisterForPushRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.registerForPush(request)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun changeEventStatus(request: EventStatusRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.changeEventStatus(request)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getNotificationList(): Flow<PagingData<NotificationListResponse>> = Pager(
    config = PagingConfig(pageSize = 40, enablePlaceholders = false),
    pagingSourceFactory = { NotificationPagingSource(apiService = apiServices) }
    ).flow

    override fun notificationSetting(request: HashMap<String, String>): Flow<NetworkResult<ApiResponse<NotificationSettingResponse>>> =
        flow {
            try {
                val response = apiServices.notificationSettings(request)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getKinshipGroupChatList(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String,
        page: Int,
    ): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>> = flow {
        try {
            val response = apiServices.getChatListForKinshipGroup(
                id = id,
                type = type,
                imageLinkType = imageLinkType,
                search = search,
                page = page,
                perPage = 40
            )
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }

    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getKinshipGroupChatListWithPaging(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String,
        apiCallback: ApiCallback?
    ): Flow<PagingData<KinshipGroupChatListData>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            KinshipGroupChatPagingSource(
                apiService = apiServices,
                id = id,
                type = type,
                imageLinkType = imageLinkType,
                search = search,
                apiCallback = apiCallback,
            )
        }
    ).flow


    override fun sendMessage(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<SendMessage>>> = flow {
        try {
            val response = apiServices.sendMessage(req, profileImage)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun isLikeDislike(isLikeDislike: IsLikeDislikeRequest): Flow<NetworkResult<ApiResponse<IsLikeDislikeResponse>>> =
        flow {
            try {
                val response = apiServices.isLikeDislike(isLikeDislike)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }

        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getKinshipDataChatList(
        id: String,
        type: Int,
        imageLinkType: Int,
        search: String
    ): Flow<PagingData<KinshipGroupChatListData>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            GetKinshipDataPagingSource(
                apiService = apiServices,
                id = id,
                type = type,
                imageLinkType = imageLinkType,
                search = search
            )
        }
    ).flow

    override fun userFlag(): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response = apiServices.userFlag()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun userGroupList(page: Int): Flow<NetworkResult<ApiResponseNew<MessageTabResponse>>> =
        flow {
            try {
                val response = apiServices.getUserGroupList(page = page, perPage = 40)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }

        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }


    override fun addGroupMemberRequest(addGroupMemberRequest: AddGroupMemberRequest): Flow<NetworkResult<ApiResponse<AddGroupMember>>> =
        flow {
            try {
                val response = apiServices.addGroupMember(addGroupMemberRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun searchChat(
        id: String,
        groupId: String
    ): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>> = flow {
        try {
            val response = apiServices.getSearchChat(id, groupId)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun groupMemberList(groupId: String): Flow<NetworkResult<ApiResponseNew<UserGroupMember>>> =
        flow {
            try {
                val response = apiServices.userGroupMember(groupId)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }

    override fun kinshipNameEdit(kinshipNameEditRequest: KinshipNameEditRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.kinshipNameEdit(kinshipNameEditRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getEventName(
        eventId: String,
        type: Int
    ): Flow<NetworkResult<ApiResponseNew<EventNameData>>> = flow {
        try {
            val response = apiServices.eventName(eventId, type)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun eventDelete(eventId: String): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.eventDelete(eventId)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun editEvent(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<CreateEventResponse>>> =
        flow {
            try {
                val response = apiServices.editEventAsync(req)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun editEventWithImage(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<CreateEventResponse>>> = flow {
        try {
            val response = apiServices.editEventWithImage(req, profileImage)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun kinshipVersion(kinshipVersionRequest: KinshipVersionRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.kinshipVersion(kinshipVersionRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }

    override fun contactSupport(contactSupportRequest: ContactSupportRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.contactSupport(contactSupportRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }

        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun userForgetPassword(userForgetPasswordRequest: UserForgetPasswordRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.userForgetPassword(userForgetPasswordRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun newPassword(newPasswordRequest: NewPasswordRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.newPassword(newPasswordRequest)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }

        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun otpNewPasswordVerification(newPasswordOtpVerificationReq: NewPasswordOtpVerificationReq): Flow<NetworkResult<ApiResponse<UserAuthResponseData>>> =
        flow {
            try {
                val response = apiServices.newPasswordOtpVerification(newPasswordOtpVerificationReq)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }


    override fun editProfile(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<ProfileData>>> =
        flow {
            try {
                val response = apiServices.editProfileAsync(req)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun editProfileWithImage(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<ProfileData>>> = flow {
        try {
            val response = apiServices.editProfileWithImageAsync(req, profileImage)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun myCommunities(search: String?): Flow<NetworkResult<ApiResponseNew<MyCommunitiesResponse>>> =
        flow {
            try {
                val response = apiServices.getCommunitiesList(search)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }


    override fun submitNewSuggestion(newSuggestionReq: NewSuggestionReq): Flow<NetworkResult<ApiResponse<Any>>> =
        flow {
            try {
                val response = apiServices.submitNewSuggestion(newSuggestionReq)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun exploreCommunities(
        type: Int?,
        search: String?
    ): Flow<NetworkResult<ApiResponseNew<MyCommunitiesResponse>>> = flow {
        try {
            val response = apiServices.getExploreCommunitiesList(type = type, search = search)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun communityPost(
        communityId: String,
        page: Int,
    ): Flow<NetworkResult<ApiResponseNew<CommunityPostResponse>>> = flow {
        try {
            val response =
                apiServices.getCommunityPost(communityId = communityId, page = page, perPage = 40)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addNewPostCommunity(
        req: Map<String, RequestBody>,
        communityPost: MultipartBody.Part
    ): Flow<NetworkResult<ApiResponse<Any>>> = flow {
        try {
            val response = apiServices.addNewPostCommunity(req = req, communityPost = communityPost)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addNewPostCommunityWithOutImage(
        req: Map<String, RequestBody>,
    ): Flow<NetworkResult<ApiResponse<Any>>> = flow {
        try {
            val response = apiServices.addNewPostCommunityWithOutImage(req = req)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun joinCommunity(communityId: String?): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.joinCommunity(communityId = communityId)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun leaveCommunity(communityId: String?): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.leaveCommunity(communityId = communityId)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun postComment(
        postId: String?,
        page: Int
    ): Flow<NetworkResult<ApiResponseNew<CommunityPostResponse>>> = flow {
        try {
            val response = apiServices.postComment(postId = postId, page = page, perPage = 100)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addComment(addCommentRequest: AddCommentRequest): Flow<NetworkResult<ApiResponse<CommunityPostResponse>>> = flow {
        try {
            val response = apiServices.addComment(addCommentRequest = addCommentRequest)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun communityPostLikeDislike(
        postId: String?,
        like: Boolean?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response = apiServices.communityPostLikeDislike(postId = postId, like = like)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun commentPostLikeDislike(
        commentId: String?,
        like: Boolean?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response = apiServices.commentPostLikeDislike(commentId = commentId, like = like)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun termAndCondition(): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow{
        try {
            val response = apiServices.termAndCondition()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun userNotificationRedirect(
        mainId: String,
        subId: String,
        type: Int
    ): Flow<NetworkResult<ApiResponseNew<CommunityPostResponse>>> = flow {
        try {
            val response = apiServices.userNotificationRedirect(mainId = mainId, subId = subId, type = type)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun userCommentRedirect(
        mainId: String,
        subId: String,
        type: Int
    ): Flow<NetworkResult<ApiResponse<CommentRedirectResponse>>> = flow {
        try {
            val response = apiServices.userCommentRedirect(mainId = mainId, subId = subId, type = type)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun userKinshipGroupNotificationRedirect(
        mainId: String,
        subId: String,
        type: Int
    ): Flow<NetworkResult<ApiResponseNew<KinshipGroupChatListData>>> = flow {
        try {
            val response = apiServices.userKinshipGroupNotificationRedirect(mainId = mainId, subId = subId, type = type)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(Constants.NetworkError.NETWORK_ERROR))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

}


