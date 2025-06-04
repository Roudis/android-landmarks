# Landmarks Manager

A full-stack application for managing and exploring landmarks around the world. The project consists of an Android app built with Jetpack Compose and a Django backend API.

## Features

- Browse landmarks with images and details
- View landmarks on Google Maps
- Add new landmarks with photos
- Search and filter landmarks
- Categorize landmarks
- Material 3 design

## Tech Stack

### Android App
- Kotlin
- Jetpack Compose
- Material 3
- Google Maps SDK
- Retrofit
- Hilt for dependency injection
- Coil for image loading

### Backend
- Django
- Django REST Framework
- SQLite database
- Media file handling

## Setup

### Android App
1. Clone the repository
2. Open the `android_app` directory in Android Studio
3. Add your Google Maps API key in `local.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
4. Build and run the app

### Backend
1. Create a virtual environment:
   ```bash
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```
2. Install dependencies:
   ```bash
   cd backend
   pip install -r requirements.txt
   ```
3. Run migrations:
   ```bash
   python manage.py migrate
   ```
4. Start the development server:
   ```bash
   python manage.py runserver 0.0.0.0:8000
   ```

## API Endpoints

- `GET /api/landmarks/` - List all landmarks
- `POST /api/landmarks/` - Create a new landmark
- `GET /api/landmarks/{id}/` - Get landmark details
- `PUT /api/landmarks/{id}/` - Update a landmark
- `DELETE /api/landmarks/{id}/` - Delete a landmark

## License

MIT License 