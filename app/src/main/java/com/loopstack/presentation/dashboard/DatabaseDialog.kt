package com.loopstack.presentation.dashboard

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.data.local.dao.SessionLogDao
import com.loopstack.data.local.database.AppDatabase
import com.loopstack.data.local.dao.FileRegistryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val sessionLogDao: SessionLogDao,
    private val fileRegistryDao: FileRegistryDao,
    private val appDatabase: AppDatabase
) : ViewModel() {

    var logCount by mutableStateOf(0)
        private set

    fun loadStats() {
        viewModelScope.launch {
            logCount = sessionLogDao.getLogCount()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            sessionLogDao.clearAllLogs()
            fileRegistryDao.clearAllFiles()
            appDatabase.openHelper.writableDatabase.execSQL("VACUUM")
            loadStats()
        }
    }
}

@Composable
fun DatabaseDialog(
    onDismiss: () -> Unit,
    viewModel: DatabaseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var dbSizeMB by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        viewModel.loadStats()
        val dbFile = context.getDatabasePath("loopstack_db")
        if (dbFile.exists()) {
            dbSizeMB = dbFile.length() / (1024.0 * 1024.0)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Local Database", color = MaterialTheme.colorScheme.primary) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding()) {
                Text("Total Session Logs: ${viewModel.logCount}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(String.format("Database Size: %.2f MB", dbSizeMB), style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.clearCache() }) {
                Text("Clear Cache / Vacuum")
            }
        }
    )
}
