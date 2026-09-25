package com.loopstack.ui.agent.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentPreviewSheet(
    previewFile: File?,
    onDismiss: () -> Unit
) {
    if (previewFile == null) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val extension = previewFile.extension.lowercase()
                    val icon = when (extension) {
                        "html", "htm" -> Icons.Default.Web
                        "png", "jpg", "jpeg", "svg" -> Icons.Default.Image
                        "txt", "md", "json", "py", "js", "kt" -> Icons.Default.Code
                        else -> Icons.AutoMirrored.Filled.InsertDriveFile
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "File Type Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = previewFile.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Preview"
                    )
                }
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (previewFile.extension.lowercase()) {
                    "html", "htm" -> {
                        val parentFile = previewFile.parentFile
                        if (parentFile != null) {
                            LocalHtmlPreview(
                                workspaceRoot = parentFile,
                                startUrlPath = "/${previewFile.name}",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Invalid file path for HTML preview.")
                        }
                    }
                    "png", "jpg", "jpeg", "svg" -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(previewFile)
                                .memoryCachePolicy(CachePolicy.DISABLED)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = "Image Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    "txt", "md", "json", "py", "js", "kt" -> {
                        var fileContent by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(previewFile) {
                            withContext(Dispatchers.IO) {
                                try {
                                    fileContent = previewFile.readText()
                                } catch (e: Exception) {
                                    fileContent = "Error reading file: ${e.message}"
                                }
                            }
                        }

                        if (fileContent != null) {
                            Text(
                                text = fileContent ?: "",
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "Preview not supported for .${previewFile.extension} files.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
