package com.example.landmarkmanager.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun LandmarkListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue()) }
    
    Scaffold(
        topBar = {
            if (isSearchVisible) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it
                                viewModel.loadLandmarks(title = it.text)
                            },
                            placeholder = { Text("Search landmarks...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                cursorColor = Color.White,
                                textColor = Color.White,
                                placeholderColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSearchVisible = false
                            searchQuery = TextFieldValue("")
                            viewModel.loadLandmarks()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close Search")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary
                )
            } else {
                TopAppBar(
                    title = { Text("Landmarks") },
                    actions = {
                        IconButton(onClick = { isSearchVisible = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
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
                                    }
                                }
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = landmark.title,
                                        style = MaterialTheme.typography.h6
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = landmark.category,
                                        style = MaterialTheme.typography.body2
                                    )
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