package com.example.blocodenotas.data

import kotlinx.coroutines.flow.Flow

class NotasRepository (private val dao : NotaDao) {

    fun observarNotas(ordenacao : Ordenacao) : Flow<List<Nota>> = when (ordenacao) {
        Ordenacao.TITULO -> dao.observarPorTitulo()
        Ordenacao.RECENTES -> dao.observarPorRecentes()
    }

    suspend fun adicionar(titulo : String, conteudo: String){
        val agora = System.currentTimeMillis()

        dao.inserir(
            Nota(titulo = titulo, conteudo = conteudo, criadaEm = agora, atualizadaEm = agora)
        )
    }

    suspend fun atualizar(nota : Nota, titulo : String, conteudo : String){
        dao.atualizar(
            nota.copy(
                titulo = titulo,
                conteudo = conteudo,
                atualizadaEm = System.currentTimeMillis()
            )
        )
    }

    suspend fun alternarFixada(nota : Nota){
        dao.atualizar(
            nota.copy(fixada = !nota.fixada)
        )
    }

    suspend fun excluir(nota : Nota){
        dao.excluir(nota)
    }
}