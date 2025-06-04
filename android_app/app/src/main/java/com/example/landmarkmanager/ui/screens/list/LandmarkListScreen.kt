package com.example.landmarkmanager.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun LandmarkListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Landmarks") },
                actions = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            viewModel.loadLandmarks(title = it.takeIf { it.isNotBlank() })
                        },
                        placeholder = { Text("Search landmarks") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
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
                                .padding(8.dp)
                                .clickable { onNavigateToDetail(landmark.id) },
                            elevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = landmark.title,
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        text = landmark.category,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                                landmark.imageUrl?.let { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = landmark.title,
                                        modifier = Modifier.size(64.dp)
                                    )
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
                    Text((state as LandmarkListState.Error).message)
                }
            }
        }
    }
} 