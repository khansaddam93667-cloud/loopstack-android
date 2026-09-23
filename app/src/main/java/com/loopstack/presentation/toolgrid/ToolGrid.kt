package com.loopstack.presentation.toolgrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.loopstack.presentation.dashboard.ToolCardModel

@Composable
fun ToolGrid(tools: List<ToolCardModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(8.dp)
            .heightIn(max = 1000.dp), // Provide a maximum height since it will be in a LazyColumn
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tools,
            key = { it.id }
        ) { tool ->
            ToolCard(tool = tool)
        }
    }
}
