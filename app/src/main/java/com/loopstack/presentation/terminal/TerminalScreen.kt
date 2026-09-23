package com.loopstack.presentation.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import com.loopstack.domain.model.TerminalLine

fun LazyListState.isAtBottom(): Boolean {
    val layoutInfo = this.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    if (layoutInfo.totalItemsCount == 0) return true
    val lastVisibleItem = visibleItemsInfo.lastOrNull() ?: return true
    return lastVisibleItem.index == layoutInfo.totalItemsCount - 1
}

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val lines = viewModel.terminalLines
    TerminalScreenContent(lines = lines)
}

@Composable
fun TerminalScreenContent(lines: List<TerminalLine>) {
    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) {
        if (listState.isAtBottom() && lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            reverseLayout = false
        ) {
            items(
                items = lines,
                key = { it.id }
            ) { line ->
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
