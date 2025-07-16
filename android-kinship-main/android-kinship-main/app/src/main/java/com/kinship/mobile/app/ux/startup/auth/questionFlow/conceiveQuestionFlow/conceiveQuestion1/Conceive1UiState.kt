package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1

data class Conceive1UiState(
    val onConceive2Click:() ->Unit = {},
    val onFirstValueSave:() ->Unit = {},
    val onSecondValueValueSave:() ->Unit = {},
    val onThirdValueValueSave:() ->Unit = {},
    val onBackClick: () -> Unit = {},

)
