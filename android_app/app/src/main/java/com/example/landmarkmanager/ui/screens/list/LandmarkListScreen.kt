package com.example.landmarkmanager.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import android.widget.Toast

@Composable
fun LandmarkListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    
    val state by viewModel.state.collectAsState()

    // Delete confirmation dialog
    showDeleteDialog?.let { landmarkId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Landmark") },
            text = { Text("Are you sure you want to delete this landmark?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLandmark(landmarkId)
                        Toast.makeText(context, "Landmark deleted successfully", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Landmarks") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Landmark")
            }
        }
    ) { padding ->
        when (state) {
            is LandmarkListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LandmarkListState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items((state as LandmarkListState.Success).landmarks) { landmark ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onNavigateToDetail(landmark.id) },
                            elevation = 4.dp
                        ) {
                            Column {
                                if (landmark.imageUrl != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    ) {
                                        AsyncImage(
                                            model = landmark.imageUrl,
                                            contentDescription = landmark.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        // Delete button overlay
                                        IconButton(
                                            onClick = { showDeleteDialog = landmark.id },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .background(
                                                    color = MaterialTheme.colors.surface.copy(alpha = 0.7f),
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete landmark",
                                                tint = MaterialTheme.colors.error
                                            )
                                        }
                                    }
                                }
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = landmark.title,
                                                style = MaterialTheme.typography.h6
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = landmark.category,
                                                style = MaterialTheme.typography.body2
                                            )
                                        }
                                        if (landmark.imageUrl == null) {
                                            // Show delete button here if there's no image
                                            IconButton(
                                                onClick = { showDeleteDialog = landmark.id }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete landmark",
                                                    tint = MaterialTheme.colors.error
                                                )
                                            }
                                        }
                                    }
                                    if (landmark.description.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = landmark.description,
                                            style = MaterialTheme.typography.body2,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is LandmarkListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (state as LandmarkListState.Error).message,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
} 