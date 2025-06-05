package com.example.landmarkmanager.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun LandmarkListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.loadLandmarks(query = searchQuery.takeIf { it.isNotBlank() })
    }

    Scaffold(
        topBar = {
            if (isSearchVisible) {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.primary,
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { newQuery ->
                                searchQuery = newQuery
                            },
                            placeholder = { 
                                Text(
                                    "Search landmarks...",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                cursorColor = Color.White,
                                textColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                isSearchVisible = false
                                searchQuery = ""
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close Search",
                                tint = Color.White
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.primary,
                    title = { Text("Landmarks", color = Color.White) },
                    actions = {
                        IconButton(onClick = { isSearchVisible = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Landmark")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is LandmarkListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LandmarkListState.Success -> {
                    val landmarks = (state as LandmarkListState.Success).landmarks
                    if (landmarks.isEmpty()) {
                        if (searchQuery.isNotBlank()) {
                            Text(
                                text = "No landmarks found",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            Text(
                                text = "No landmarks added yet",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        LazyColumn {
                            items(landmarks) { landmark ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .clickable { onNavigateToDetail(landmark.id) },
                                    elevation = 4.dp
                                ) {
                                    Column {
                                        landmark.imageUrl?.let { url ->
                                            AsyncImage(
                                                model = url,
                                                contentDescription = landmark.title,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp),
                                                contentScale = ContentScale.Crop
                                            )
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
                }
                is LandmarkListState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (state as LandmarkListState.Error).message,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.body1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadLandmarks() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
} 