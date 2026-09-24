package com.loopstack.presentation.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.model.TerminalLine
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import com.loopstack.presentation.agent.LocalAgentDrawerController
import androidx.compose.material.icons.filled.Face
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import com.loopstack.core.theme.ElectricCyan
import com.loopstack.core.theme.neonGlow
import com.loopstack.core.theme.CyberViolet

fun LazyListState.isAtBottom(): Boolean {
    val layoutInfo = this.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    if (layoutInfo.totalItemsCount == 0) return true
    val lastVisibleItem = visibleItemsInfo.lastOrNull() ?: return true
    return lastVisibleItem.index == layoutInfo.totalItemsCount - 1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    navController: NavController,
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val lines = viewModel.terminalLines
    val inputText by viewModel.inputText.collectAsState()
    val status by viewModel.loopbackStatus.collectAsState()

    val agentDrawerController = LocalAgentDrawerController.current

    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) {
        if (listState.isAtBottom() && lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("Terminal") },
                actions = {
                    IconButton(onClick = { agentDrawerController.toggle() }) {
                        Icon(Icons.Default.Face, contentDescription = "Open Agent")
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(8.dp)
                            .background(
                                color = if (status is LoopbackStatus.Active) Color(0xFF00FF66) else Color(0xFFFFB300),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .neonGlow(if (status is LoopbackStatus.Active) Color(0xFF00FF66) else Color(0xFFFFB300), 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("root@localhost:~# ", color = Color(0xFF00FF66), fontFamily = FontFamily.Monospace)
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = viewModel::updateInputText,
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface),
                        placeholder = { Text("Enter prompt...") }
                    )
                    IconButton(onClick = viewModel::sendMessage) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0E14))
                    .weight(1f),
                reverseLayout = false
            ) {
                items(
                    items = lines,
                    key = { it.id }
                ) { line ->
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        if (line.model != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Model", tint = CyberViolet)
                                Text(
                                    text = "[MODEL: ${line.model}]",
                                    color = CyberViolet,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                            }
                        }

                        if (line.text.contains("```")) {
                            val parts = line.text.split("```")
                            parts.forEachIndexed { index, part ->
                                if (index % 2 == 1) {
                                    val codeLines = part.split("\n")
                                    val language = codeLines.firstOrNull()?.trim() ?: "code"
                                    val codeContent = if (codeLines.size > 1) codeLines.drop(1).joinToString("\n") else ""

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .neonGlow(ElectricCyan, 8.dp)
                                            .background(Color(0xFF0D1117), RoundedCornerShape(8.dp))
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(language, color = ElectricCyan, style = MaterialTheme.typography.labelSmall)
                                                TextButton(
                                                    onClick = { viewModel.exportCode(codeContent) },
                                                    colors = ButtonDefaults.textButtonColors(contentColor = ElectricCyan)
                                                ) {
                                                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Save Code", modifier = Modifier.padding(end = 4.dp))
                                                    Text("Save Code")
                                                }
                                            }
                                            Text(
                                                text = codeContent,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                } else if (part.isNotBlank()) {
                                    Text(
                                        text = part,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (line.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = line.text,
                                fontFamily = FontFamily.Monospace,
                                color = if (line.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
