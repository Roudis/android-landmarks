from django.core.management.base import BaseCommand
from landmarks.models import Landmark

class Command(BaseCommand):
    help = 'Creates a test landmark'

    def handle(self, *args, **kwargs):
        landmark = Landmark.objects.create(
            title="Test Landmark",
            category="HISTORICAL",
            description="This is a test landmark for development purposes.",
            latitude=37.7749,
            longitude=-122.4194
        )
        self.stdout.write(self.style.SUCCESS(f'Successfully created test landmark "{landmark.title}"')) 