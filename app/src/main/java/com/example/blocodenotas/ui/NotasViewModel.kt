package com.example.blocodenotas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.blocodenotas.data.NotasRepository
import com.example.blocodenotas.data.NotasUiState
import com.example.blocodenotas.data.PreferenciasNotasRepository
import com.example.blocodenotas.data.Nota
import com.example.blocodenotas.data.Ordenacao
import com.example.blocodenotas.NotasApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotasViewModel(
    private val notasRepository: NotasRepository,
    private val preferenciasRepository: PreferenciasNotasRepository
) : ViewModel() {

    private data class Formulario(
        val titulo: String = "",
        val conteudo: String = "",
        val idEmEdicao: Int? = null,
        val erro: String? = null
    )

    private val formulario = MutableStateFlow(Formulario())

    private val ordenacaoAtual = preferenciasRepository.ordenacao

    private val notas = ordenacaoAtual.flatMapLatest { ordenacao ->
        notasRepository.observarNotas(ordenacao)
    }

    val uiState: StateFlow<NotasUiState> = combine(ordenacaoAtual, notas, formulario) { ordenacao, lista, form ->
        NotasUiState(
            notas = lista,
            ordenacao = ordenacao,
            titulo = form.titulo,
            conteudo = form.conteudo,
            idEmEdicao = form.idEmEdicao,
            erro = form.erro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotasUiState()
    )

    fun alterarTitulo(valor: String) {
        formulario.update { it.copy(titulo = valor, erro = null) }
    }

    fun alterarConteudo(valor: String) {
        formulario.update { it.copy(conteudo = valor, erro = null) }
    }

    fun salvar() {
        val form = formulario.value
        val titulo = form.titulo.trim()
        val conteudo = form.conteudo.trim()

        val erro = when {
            titulo.isEmpty() -> "O título é obrigatório."
            titulo.length > 60 -> "O título deve ter no máximo 60 caracteres."
            conteudo.isEmpty() -> "O conteúdo é obrigatório."
            else -> null
        }
        if (erro != null) {
            formulario.update { it.copy(erro = erro) }
            return
        }

        viewModelScope.launch {
            val id = form.idEmEdicao
            if (id == null) {
                notasRepository.adicionar(titulo, conteudo)
            } else {
                uiState.value.notas.firstOrNull { it.id == id }?.let { nota ->
                    notasRepository.atualizar(nota, titulo, conteudo)
                }
            }
            formulario.value = Formulario()
        }
    }

    fun editar(nota: Nota) {
        formulario.value = Formulario(
            titulo = nota.titulo,
            conteudo = nota.conteudo,
            idEmEdicao = nota.id
        )
    }

    fun cancelarEdicao() {
        formulario.value = Formulario()
    }

    fun alternarFixada(nota: Nota) {
        viewModelScope.launch { notasRepository.alternarFixada(nota) }
    }

    fun excluir(nota: Nota) {
        viewModelScope.launch { notasRepository.excluir(nota) }
        if (formulario.value.idEmEdicao == nota.id) {
            formulario.value = Formulario()
        }
    }

    fun definirOrdenacao(ordenacao: Ordenacao) {
        viewModelScope.launch { preferenciasRepository.definirOrdenacao(ordenacao) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as NotasApp
                NotasViewModel(
                    app.container.notasRepository,
                    app.container.preferenciasRepository
                )
            }
        }
    }
}