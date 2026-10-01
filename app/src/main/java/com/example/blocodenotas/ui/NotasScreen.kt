package com.example.blocodenotas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blocodenotas.data.Nota
import com.example.blocodenotas.data.NotasUiState
import com.example.blocodenotas.data.Ordenacao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotasScreen(
    viewModel: NotasViewModel = viewModel(factory = NotasViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotasContent(
        uiState = uiState,
        onTituloChange = viewModel::alterarTitulo,
        onConteudoChange = viewModel::alterarConteudo,
        onSalvar = viewModel::salvar,
        onCancelar = viewModel::cancelarEdicao,
        onEditar = viewModel::editar,
        onAlternarFixada = viewModel::alternarFixada,
        onExcluir = viewModel::excluir,
        onOrdenacaoChange = viewModel::definirOrdenacao
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasContent(
    uiState: NotasUiState,
    onTituloChange: (String) -> Unit,
    onConteudoChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
    onEditar: (Nota) -> Unit,
    onAlternarFixada: (Nota) -> Unit,
    onExcluir: (Nota) -> Unit,
    onOrdenacaoChange: (Ordenacao) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Bloco de Notas") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1) Formulário
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.conteudo,
                onValueChange = onConteudoChange,
                label = { Text("Conteúdo") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.erro != null) {
                Text(
                    text = uiState.erro,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSalvar) {
                    Text(if (uiState.idEmEdicao == null) "Salvar" else "Atualizar")
                }
                if (uiState.idEmEdicao != null) {
                    OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
                }
            }

            // 2) Barra de controle
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.ordenacao == Ordenacao.RECENTES,
                    onClick = { onOrdenacaoChange(Ordenacao.RECENTES) },
                    label = { Text("Recentes") }
                )
                FilterChip(
                    selected = uiState.ordenacao == Ordenacao.TITULO,
                    onClick = { onOrdenacaoChange(Ordenacao.TITULO) },
                    label = { Text("Título") }
                )
            }
            Text(
                text = "${uiState.notas.size} nota(s) · ${uiState.totalFixadas} fixada(s)",
                style = MaterialTheme.typography.bodyMedium
            )

            // 3) Lista ou estado vazio
            if (uiState.notas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma nota cadastrada.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notas, key = { it.id }) { nota ->
                        NotaItem(
                            nota = nota,
                            onEditar = onEditar,
                            onAlternarFixada = onAlternarFixada,
                            onExcluir = onExcluir
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotaItem(
    nota: Nota,
    onEditar: (Nota) -> Unit,
    onAlternarFixada: (Nota) -> Unit,
    onExcluir: (Nota) -> Unit
) {
    Card(
        onClick = { onEditar(nota) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = nota.fixada,
                onCheckedChange = { onAlternarFixada(nota) }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nota.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = nota.conteudo,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatarData(nota.atualizadaEm),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = { onExcluir(nota) }) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir nota")
            }
        }
    }
}

private fun formatarData(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date(millis))