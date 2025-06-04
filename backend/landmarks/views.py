from django.shortcuts import render
from rest_framework import viewsets, status
from rest_framework.filters import SearchFilter
from rest_framework.response import Response
from django_filters import rest_framework as filters
from django.views.generic import ListView
from .models import Landmark
from .serializers import LandmarkSerializer
import logging

logger = logging.getLogger(__name__)

# Create your views here.

class LandmarkFilter(filters.FilterSet):
    category = filters.CharFilter(lookup_expr='iexact')
    title = filters.CharFilter(lookup_expr='icontains')

    class Meta:
        model = Landmark
        fields = ['category', 'title']

class LandmarkViewSet(viewsets.ModelViewSet):
    queryset = Landmark.objects.all()
    serializer_class = LandmarkSerializer
    filter_backends = [filters.DjangoFilterBackend, SearchFilter]
    filterset_class = LandmarkFilter
    search_fields = ['title', 'description', 'category']
    pagination_class = None  # Disable pagination for this endpoint

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['request'] = self.request
        return context

    def create(self, request, *args, **kwargs):
        try:
            # Log the incoming data for debugging
            logger.info(f"Received data: {request.data}")
            logger.info(f"Content type: {request.content_type}")

            # Create a mutable copy of the data
            data = request.data.copy()

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
            self.perform_create(serializer)
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
