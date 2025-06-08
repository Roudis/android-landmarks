from rest_framework import serializers
from .models import Landmark
import logging

logger = logging.getLogger(__name__)

class NullableDecimalField(serializers.DecimalField):
    def to_internal_value(self, data):
        if data == '' or data is None or data == 'null':
            return None
        try:
            return super().to_internal_value(data)
        except Exception as e:
            logger.error(f"Error converting decimal value: {data}, error: {str(e)}")
            return None

class LandmarkSerializer(serializers.ModelSerializer):
    cover_image = serializers.ImageField(required=False, allow_null=True, allow_empty_file=True)
    latitude = NullableDecimalField(max_digits=9, decimal_places=6, required=False, allow_null=True)
    longitude = NullableDecimalField(max_digits=9, decimal_places=6, required=False, allow_null=True)
    country = serializers.CharField(required=False, allow_null=True, allow_blank=True)

    def get_cover_image_url(self, obj):
        if obj.cover_image:
            request = self.context.get('request')
            if request is not None:
                return request.build_absolute_uri(obj.cover_image.url)
        return None

    def validate(self, data):
        # Log the incoming data for debugging
        logger.info(f"Validating data in serializer: {data}")
        
        # Log image data if present
        if 'cover_image' in data:
            logger.info(f"Cover image in validation: {data['cover_image']}")
            if hasattr(data['cover_image'], 'content_type'):
                logger.info(f"Image content type: {data['cover_image'].content_type}")
        else:
            logger.info("No cover image in data during validation")
            
        # Handle empty or "0.0" coordinate values
        if 'latitude' in data:
            if data['latitude'] in ['', None, '0.0', 0.0]:
                data['latitude'] = None
        if 'longitude' in data:
            if data['longitude'] in ['', None, '0.0', 0.0]:
                data['longitude'] = None
            
        return data

    def create(self, validated_data):
        # Log the validated data before creation
        logger.info(f"Creating landmark with data: {validated_data}")
        if 'cover_image' in validated_data:
            logger.info(f"Image present in validated_data: {validated_data['cover_image']}")
        
        instance = super().create(validated_data)
        
        # Log the created instance
        logger.info(f"Created landmark instance: {instance.id}")
        if instance.cover_image:
            logger.info(f"Image saved at: {instance.cover_image.path}")
        
        return instance

    class Meta:
        model = Landmark
        fields = '__all__'
        extra_kwargs = {
            'title': {'required': False},
            'category': {'required': False},
            'description': {'required': False},
            'cover_image': {
                'required': False,
                'allow_null': True,
                'use_url': True
            },
            'latitude': {'required': False},
            'longitude': {'required': False},
            'country': {'required': False}
        } 