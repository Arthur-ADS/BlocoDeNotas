package com.example.blocodenotas.data

data class NotasUiState(
    val notas : List<Nota> = emptyList(),
    val ordenacao : Ordenacao = Ordenacao.RECENTES,
    val titulo : String = "",
    val conteudo : String = "",
    val idEmEdicao : Int? = null,
    val erro : String? = null
){
    val totalFixadas : Int get() = notas.count { it.fixada }
}
