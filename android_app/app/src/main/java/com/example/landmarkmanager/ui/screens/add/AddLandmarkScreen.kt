package com.example.landmarkmanager.ui.screens.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.landmarkmanager.data.model.LandmarkCategory
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddLandmarkScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddLandmarkViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LandmarkCategory.OTHER) }
    var latitude by remember { mutableStateOf("0.0") }
    var longitude by remember { mutableStateOf("0.0") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempCameraUri
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir("Pictures")
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is AddLandmarkState.Success -> {
                Toast.makeText(context, "Landmark saved successfully!", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            }
            is AddLandmarkState.Error -> {
                Toast.makeText(context, (state as AddLandmarkState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Landmark") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Title Field (Required)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (Required)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Category Dropdown (Optional)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = { },
                    label = { Text("Category (Optional)") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { isDropdownExpanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, "Show categories")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    LandmarkCategory.values().forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCategory = category
                                isDropdownExpanded = false
                            }
                        ) {
                            Text(category.displayName)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Description Field (Optional)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Coordinates Fields (Optional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { 
                        // Allow only numeric input
                        if (it.isEmpty() || it.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            latitude = it
                        }
                    },
                    label = { Text("Latitude") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { 
                        // Allow only numeric input
                        if (it.isEmpty() || it.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            longitude = it
                        }
                    },
                    label = { Text("Longitude") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Image Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Pick from Gallery")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        try {
                            val photoFile = createImageFile()
                            tempCameraUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                photoFile
                            )
                            cameraLauncher.launch(tempCameraUri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error creating camera file", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Take Photo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }
            }

            // Selected Image Preview
            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.addLandmark(
                        title = title,
                        category = selectedCategory.apiValue,
                        description = description,
                        latitude = latitude.toDoubleOrNull() ?: 0.0,
                        longitude = longitude.toDoubleOrNull() ?: 0.0,
                        imageUri = selectedImageUri,
                        context = context
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Landmark")
            }
        }
    }
}