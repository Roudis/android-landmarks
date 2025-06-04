from rest_framework import serializers
from .models import Landmark

class LandmarkSerializer(serializers.ModelSerializer):
    cover_image = serializers.ImageField(required=False)
    latitude = serializers.DecimalField(max_digits=9, decimal_places=6, required=False)
    longitude = serializers.DecimalField(max_digits=9, decimal_places=6, required=False)

    def get_cover_image_url(self, obj):
        if obj.cover_image:
            request = self.context.get('request')
            if request is not None:
                return request.build_absolute_uri(obj.cover_image.url)
        return None

    class Meta:
        model = Landmark
        fields = '__all__'
        extra_kwargs = {
            'title': {'required': False},
            'category': {'required': False},
            'description': {'required': False},
            'cover_image': {'required': False},
            'latitude': {'required': False},
            'longitude': {'required': False}
        } 