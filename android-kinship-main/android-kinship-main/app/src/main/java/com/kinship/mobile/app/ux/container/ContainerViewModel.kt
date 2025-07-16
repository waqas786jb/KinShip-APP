package com.kinship.mobile.app.ux.container

import androidx.lifecycle.ViewModel
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel
@Inject constructor(): ViewModel(), ViewModelNav by ViewModelNavImpl() {

}