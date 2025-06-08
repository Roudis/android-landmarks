from django.shortcuts import render
from rest_framework import viewsets, status, parsers, permissions
from rest_framework.filters import SearchFilter
from rest_framework.response import Response
from django_filters import rest_framework as filters
from django.views.generic import ListView
from .models import Landmark, User
from .serializers import LandmarkSerializer, UserSerializer
import logging
from django.db.models import Q
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated, AllowAny
from django.shortcuts import get_object_or_404

logger = logging.getLogger(__name__)

# Create your views here.

class LandmarkFilter(filters.FilterSet):
    class Meta:
        model = Landmark
        fields = {
            'category': ['exact', 'icontains'],
            'title': ['exact', 'icontains'],
            'description': ['icontains'],
        }

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    
    def get_permissions(self):
        if self.action == 'create':
            return [AllowAny()]
        return [IsAuthenticated()]

class LandmarkViewSet(viewsets.ModelViewSet):
    queryset = Landmark.objects.all()
    serializer_class = LandmarkSerializer
    filter_backends = [filters.DjangoFilterBackend, SearchFilter]
    filterset_class = LandmarkFilter
    search_fields = ['title', 'description', 'category']
    pagination_class = None  # Disable pagination for this endpoint
    parser_classes = (parsers.MultiPartParser, parsers.FormParser)
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return Landmark.objects.filter(user=self.request.user)

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['request'] = self.request
        return context

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    def create(self, request, *args, **kwargs):
        try:
            # Detailed request logging
            logger.info("=== Request Debug Information ===")
            logger.info(f"Request Method: {request.method}")
            logger.info(f"Request Content-Type: {request.content_type}")
            logger.info(f"Request Headers: {request.headers}")
            logger.info(f"Request POST data: {request.POST}")
            logger.info(f"Request data: {request.data}")
            logger.info(f"Request FILES: {request.FILES}")
            logger.info(f"Request FILES keys: {list(request.FILES.keys())}")
            
            if request.FILES:
                for file_key in request.FILES:
                    file = request.FILES[file_key]
                    logger.info(f"File '{file_key}' details:")
                    logger.info(f"  - Name: {file.name}")
                    logger.info(f"  - Size: {file.size}")
                    logger.info(f"  - Content Type: {file.content_type}")
            else:
                logger.warning("No files found in request.FILES")
                if 'cover_image' in request.data:
                    logger.info("Cover image found in request.data")
                    logger.info(f"Cover image type: {type(request.data['cover_image'])}")
                    logger.info(f"Cover image value: {request.data['cover_image']}")

            # Create a mutable copy of the data
            data = request.data.copy()

            # Handle image file
            if 'cover_image' in request.FILES:
                logger.info("Cover image found in request.FILES")
                data['cover_image'] = request.FILES['cover_image']
                logger.info(f"Image file name: {request.FILES['cover_image'].name}")
            elif 'cover_image' in request.data:
                logger.info("Cover image found in request.data")
                if hasattr(request.data['cover_image'], 'name'):
                    data['cover_image'] = request.data['cover_image']
                    logger.info(f"Image file name from data: {request.data['cover_image'].name}")
            else:
                logger.warning("No cover_image found in request")

            # Explicitly set empty coordinates to None
            if 'latitude' not in data or data['latitude'] in ['', None, '0.0']:
                data['latitude'] = None
            if 'longitude' not in data or data['longitude'] in ['', None, '0.0']:
                data['longitude'] = None

            # Create the serializer with the data
            serializer = self.get_serializer(data=data)
            
            # Log any validation errors
            if not serializer.is_valid():
                logger.error(f"Validation errors: {serializer.errors}")
                return Response(
                    {'error': serializer.errors},
                    status=status.HTTP_400_BAD_REQUEST
                )

            # Save the landmark
            landmark = self.perform_create(serializer)
            logger.info(f"Landmark created successfully with id: {serializer.instance.id}")
            
            # Verify image after save
            if serializer.instance.cover_image:
                logger.info(f"Image saved successfully at: {serializer.instance.cover_image.path}")
            else:
                logger.warning("No image was saved with the landmark")

            return Response(
                serializer.data,
                status=status.HTTP_201_CREATED
            )
        except Exception as e:
            logger.exception("Error creating landmark")
            return Response(
                {'error': str(e)},
                status=status.HTTP_400_BAD_REQUEST
            )

    @action(detail=True, methods=['post'])
    def upload_image(self, request, pk=None):
        landmark = self.get_object()
        if 'image' not in request.FILES:
            return Response({'error': 'No image provided'}, status=status.HTTP_400_BAD_REQUEST)
        
        landmark.image = request.FILES['image']
        landmark.save()
        return Response({'message': 'Image uploaded successfully'}, status=status.HTTP_200_OK)

    def list(self, request, *args, **kwargs):
        logger.info(f"Query params: {request.query_params}")
        return super().list(request, *args, **kwargs)

class LandmarkListView(ListView):
    model = Landmark
    template_name = 'landmarks/landmark_list.html'
    context_object_name = 'landmarks'

    def get_queryset(self):
        queryset = Landmark.objects.all()
        search = self.request.GET.get('search')
        category = self.request.GET.get('category')

        if search:
            queryset = queryset.filter(title__icontains=search)
        if category:
            queryset = queryset.filter(category=category)

        return queryset.order_by('-created_at')

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['categories'] = Landmark.CATEGORY_CHOICES
        return context
