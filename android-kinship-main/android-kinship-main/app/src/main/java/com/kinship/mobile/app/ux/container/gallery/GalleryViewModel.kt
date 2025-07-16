package com.kinship.mobile.app.ux.container.gallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getGalleryUiStateUseCase: GetGalleryUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: GalleryUiState = getGalleryUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}