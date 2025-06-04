package com.example.landmarkmanager.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.landmarkmanager.data.model.LandmarkCategory

@Composable
fun LandmarkListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<LandmarkCategory?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                Column {
                    LandmarkCategory.values().forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = if (selectedCategory == category) null else category
                                    viewModel.loadLandmarks(
                                        category = selectedCategory?.apiValue,
                                        title = searchQuery.takeIf { it.isNotBlank() }
                                    )
                                    showCategoryDialog = false
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(category.displayName)
                            if (selectedCategory == category) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Selected"
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Landmarks") },
                actions = {
                    IconButton(onClick = { showCategoryDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter by category"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Landmark")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.loadLandmarks(
                        category = selectedCategory?.apiValue,
                        title = it.takeIf { it.isNotBlank() }
                    )
                },
                placeholder = { Text("Search landmarks") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Category Chip (if selected)
            selectedCategory?.let { category ->
                Surface(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { showCategoryDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category: ${category.displayName}",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }

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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items((state as LandmarkListState.Success).landmarks) { landmark ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
} 