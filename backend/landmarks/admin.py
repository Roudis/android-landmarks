from django.contrib import admin
from .models import Landmark

@admin.register(Landmark)
class LandmarkAdmin(admin.ModelAdmin):
    list_display = ('title', 'category', 'country', 'created_at', 'updated_at')
    list_filter = ('category', 'country')
    search_fields = ('title', 'description', 'country')
    readonly_fields = ('created_at', 'updated_at')
    fieldsets = (
        (None, {
            'fields': ('title', 'category', 'description', 'country')
        }),
        ('Location', {
            'fields': ('latitude', 'longitude')
        }),
        ('Media', {
            'fields': ('cover_image',)
        }),
        ('Timestamps', {
            'fields': ('created_at', 'updated_at'),
            'classes': ('collapse',)
        }),
    )
