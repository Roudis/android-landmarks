package com.example.landmarkmanager.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.landmarkmanager.data.model.Landmark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle

enum class SortOption(val displayName: String) {
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)"),
    COUNTRY_ASC("Country (A-Z)"),
    COUNTRY_DESC("Country (Z-A)"),
    CATEGORY_ASC("Category (A-Z)"),
    CATEGORY_DESC("Category (Z-A)")
}

@OptIn(FlowPreview::class)
@Composable
fun LandmarkListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.STARTED)
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var currentSort by remember { mutableStateOf<SortOption?>(null) }

    LaunchedEffect(searchQuery) {
        viewModel.loadLandmarks(search = searchQuery.takeIf { it.isNotBlank() })
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
                        IconButton(onClick = { isSearchVisible = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close search",
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
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Sort",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(onClick = {
                                    currentSort = option
                                    showSortMenu = false
                                    // Apply sorting here
                                    when (state) {
                                        is LandmarkListState.Success -> {
                                            val landmarks = (state as LandmarkListState.Success).landmarks
                                            val sortedLandmarks = when (option) {
                                                SortOption.TITLE_ASC -> landmarks.sortedBy { it.title }
                                                SortOption.TITLE_DESC -> landmarks.sortedByDescending { it.title }
                                                SortOption.COUNTRY_ASC -> landmarks.sortedBy { it.country ?: "" }
                                                SortOption.COUNTRY_DESC -> landmarks.sortedByDescending { it.country ?: "" }
                                                SortOption.CATEGORY_ASC -> landmarks.sortedBy { it.category }
                                                SortOption.CATEGORY_DESC -> landmarks.sortedByDescending { it.category }
                                            }
                                            viewModel.updateSortedLandmarks(sortedLandmarks)
                                        }
                                        else -> {}
                                    }
                                }) {
                                    Text(option.displayName)
                                }
                            }
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add landmark",
                    tint = Color.White
                )
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
                                        .padding(8.dp)
                                        .clickable { onNavigateToDetail(landmark.id) },
                                    elevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        landmark.imageUrl?.let { url ->
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Landmark image",
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .padding(end = 8.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = landmark.title,
                                                style = MaterialTheme.typography.h6,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = landmark.category,
                                                style = MaterialTheme.typography.body2,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                            )
                                            landmark.country?.let { country ->
                                                Text(
                                                    text = country,
                                                    style = MaterialTheme.typography.body2,
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
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