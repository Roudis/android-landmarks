package com.example.landmarkmanager.ui.screens.detail

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun LandmarkDetailScreen(
    landmarkId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToMap: (Double, Double, String) -> Unit,
    viewModel: LandmarkDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(landmarkId) {
        viewModel.loadLandmark(landmarkId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Landmark Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (state) {
            is LandmarkDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LandmarkDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text((state as LandmarkDetailState.Error).message)
                }
            }
            is LandmarkDetailState.Success -> {
                val landmark = (state as LandmarkDetailState.Success).landmark
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    landmark.imageUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = landmark.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = landmark.title,
                        style = MaterialTheme.typography.h5
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = landmark.category,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = landmark.description,
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Location: ${String.format("%.6f", landmark.latitude)}, ${String.format("%.6f", landmark.longitude)}",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onNavigateToMap(landmark.latitude, landmark.longitude, landmark.title)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "View on Map"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View on Map")
                    }
                }
            }
        }
    }
} 