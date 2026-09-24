package com.loopstack.presentation.agent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loopstack.core.theme.ElectricCyan
import com.loopstack.core.theme.CyberViolet
import com.loopstack.core.theme.glassmorphic
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

val LocalAgentDrawerController = compositionLocalOf { AgentDrawerState() }

class AgentDrawerState {
    var isOpen by mutableStateOf(false)
    fun toggle() { isOpen = !isOpen }
    fun open() { isOpen = true }
    fun close() { isOpen = false }
}

@Composable
fun AgentDrawerWrapper(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val drawerState = remember { AgentDrawerState() }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val drawerWidth = if (screenWidth > 600.dp) 400.dp else screenWidth * 0.85f

    CompositionLocalProvider(LocalAgentDrawerController provides drawerState) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            if (drawerState.isOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickableNoRipple { drawerState.close() }
                )
            }

            AnimatedVisibility(
                visible = drawerState.isOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                AgentDrawerContent(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(drawerWidth)
                        .background(Color(0xCC1E1E2E))
                        .glassmorphic(),
                    navController = navController,
                    onClose = { drawerState.close() }
                )
            }
        }
    }
}

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    onClose: () -> Unit,
    viewModel: AgentViewModel = hiltViewModel()
) {
    val messages = viewModel.messages
    val inputText by viewModel.inputText.collectAsState()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val contextString = resolveAgentContext(currentRoute = currentRoute, viewModel = viewModel)

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("LoopStack Agent", color = ElectricCyan, style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val quickActions = listOf("System Health", "Summarize Logs", "Explain Screen")
            quickActions.forEach { action ->
                FilterChip(
                    selected = false,
                    onClick = { viewModel.sendQuickAction(action, contextString) },
                    label = { Text(action, color = Color.White) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = Color(0x33FFFFFF))
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
                val bgColor = if (msg.isUser) CyberViolet.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                val textColor = if (msg.isError) MaterialTheme.colorScheme.error else Color.White

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
                    Box(
                        modifier = Modifier
                            .background(bgColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = if (!msg.isUser) FontFamily.Monospace else FontFamily.Default
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = viewModel::updateInputText,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask the agent...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.sendMessage(contextString) },
                    modifier = Modifier.background(ElectricCyan.copy(alpha = 0.2f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ElectricCyan)
                }
            }
        }
    }
}
