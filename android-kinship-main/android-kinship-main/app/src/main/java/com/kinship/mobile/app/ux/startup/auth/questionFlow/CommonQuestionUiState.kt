package com.kinship.mobile.app.ux.startup.auth.questionFlow

data class CommonQuestionUiState(
    var onConceive1Click: () -> Unit = {},
    var onPregnantClick: () -> Unit = {},
    var onBaby1Click: () -> Unit = {},
    var onAdoptedClick: () -> Unit = {},
    var conceiveValueSave: () -> Unit = {},
    var pregnantValueSave: () -> Unit = {},
    var babyValueSave: () -> Unit = {},
    var adoptedValueSave: () -> Unit = {},
)
