from django.db import models

class Landmark(models.Model):
    CATEGORY_CHOICES = [
        ('RELIGIOUS', 'Religious Tourism'),
        ('HISTORICAL', 'Historical'),
        ('NATURAL', 'Natural'),
        ('CULTURAL', 'Cultural'),
        ('OTHER', 'Other')
    ]

    title = models.CharField(max_length=200, null=True, blank=True)
    category = models.CharField(max_length=20, choices=CATEGORY_CHOICES, null=True, blank=True)
    description = models.TextField(null=True, blank=True)
    cover_image = models.ImageField(upload_to='landmarks/', null=True, blank=True)
    latitude = models.DecimalField(max_digits=9, decimal_places=6, null=True, blank=True)
    longitude = models.DecimalField(max_digits=9, decimal_places=6, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.title or 'Untitled Landmark'

    class Meta:
        ordering = ['-created_at']
