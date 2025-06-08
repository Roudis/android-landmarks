from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import LandmarkViewSet, UserViewSet, RegisterUserView

router = DefaultRouter()
router.register(r'landmarks', LandmarkViewSet)
router.register(r'users', UserViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('users/', RegisterUserView.as_view(), name='register-user'),
] 