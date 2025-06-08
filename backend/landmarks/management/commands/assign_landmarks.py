from django.core.management.base import BaseCommand
from landmarks.models import Landmark, User

class Command(BaseCommand):
    help = 'Assigns all unassigned landmarks to a specified user email'

    def add_arguments(self, parser):
        parser.add_argument('email', type=str, help='The email of the user to assign landmarks to')

    def handle(self, *args, **kwargs):
        email = kwargs['email']
        try:
            user = User.objects.get(email=email)
            unassigned_landmarks = Landmark.objects.filter(user__isnull=True)
            count = unassigned_landmarks.count()
            
            if count == 0:
                self.stdout.write(self.style.WARNING('No unassigned landmarks found'))
                return
                
            unassigned_landmarks.update(user=user)
            self.stdout.write(
                self.style.SUCCESS(f'Successfully assigned {count} landmarks to user {email}')
            )
        except User.DoesNotExist:
            self.stdout.write(
                self.style.ERROR(f'User with email {email} does not exist')
            ) 