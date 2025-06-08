import os
import requests
from django.core.files import File
from django.core.files.temp import NamedTemporaryFile
from django.core.management.base import BaseCommand
from landmarks.models import Landmark
import time

class Command(BaseCommand):
    help = "Populates the database with real landmarks"

    def download_image(self, url, retries=3):
        for attempt in range(retries):
            try:
                response = requests.get(url, timeout=10)
                if response.status_code == 200:
                    img_temp = NamedTemporaryFile(delete=True)
                    img_temp.write(response.content)
                    img_temp.flush()
                    return img_temp
                else:
                    self.stdout.write(self.style.WARNING(f'Failed to download image (status {response.status_code}): {url}'))
            except Exception as e:
                self.stdout.write(self.style.WARNING(f'Attempt {attempt + 1}/{retries} failed for {url}: {str(e)}'))
                if attempt < retries - 1:
                    time.sleep(1)  # Wait before retrying
        return None

    def handle(self, *args, **kwargs):
        # Clear existing landmarks
        Landmark.objects.all().delete()
        
        landmarks = [
            {
                "title": "Eiffel Tower",
                "category": "HISTORICAL",
                "description": "The Eiffel Tower is a wrought-iron lattice tower located on the Champ de Mars in Paris. Built in 1889, it has become both a global cultural icon of France and one of the most recognizable structures in the world.",
                "latitude": 48.8584,
                "longitude": 2.2945,
                "country": "France",
                "image_url": "https://images.pexels.com/photos/2082103/pexels-photo-2082103.jpeg"
            },
            {
                "title": "Taj Mahal",
                "category": "HISTORICAL",
                "description": "The Taj Mahal is an ivory-white marble mausoleum on the right bank of the river Yamuna in Agra, India. It was commissioned in 1632 by the Mughal emperor Shah Jahan to house the tomb of his favorite wife, Mumtaz Mahal.",
                "latitude": 27.1751,
                "longitude": 78.0421,
                "country": "India",
                "image_url": "https://images.pexels.com/photos/1603650/pexels-photo-1603650.jpeg"
            },
            {
                "title": "Great Wall of China",
                "category": "HISTORICAL",
                "description": "The Great Wall of China is a series of fortifications that were built across the historical northern borders of ancient Chinese states and Imperial China as protection against nomadic incursions.",
                "latitude": 40.4319,
                "longitude": 116.5704,
                "country": "China",
                "image_url": "https://images.pexels.com/photos/2412603/pexels-photo-2412603.jpeg"
            },
            {
                "title": "Machu Picchu",
                "category": "HISTORICAL",
                "description": "Machu Picchu is an Incan citadel set high in the Andes Mountains in Peru. Built in the 15th century and later abandoned, it is renowned for its sophisticated dry-stone walls that fuse huge blocks without the use of mortar.",
                "latitude": -13.1631,
                "longitude": -72.5450,
                "country": "Peru",
                "image_url": "https://images.pexels.com/photos/2356045/pexels-photo-2356045.jpeg"
            },
            {
                "title": "Grand Canyon",
                "category": "NATURAL",
                "description": "The Grand Canyon is a steep-sided canyon carved by the Colorado River in Arizona. The canyon is 277 miles long, up to 18 miles wide and attains a depth of over a mile.",
                "latitude": 36.0544,
                "longitude": -112.1401,
                "country": "United States",
                "image_url": "https://images.pexels.com/photos/33041/antelope-canyon-lower-canyon-arizona.jpg"
            },
            {
                "title": "Petra",
                "category": "HISTORICAL",
                "description": "Petra is a famous archaeological site in Jordans southwestern desert. Dating to around 300 B.C., it was the capital of the Nabataean Kingdom. It is accessed via a narrow canyon called Al Siq.",
                "latitude": 30.3285,
                "longitude": 35.4444,
                "country": "Jordan",
                "image_url": "https://images.pexels.com/photos/1631665/pexels-photo-1631665.jpeg"
            },
            {
                "title": "Christ the Redeemer",
                "category": "RELIGIOUS",
                "description": "Christ the Redeemer is an Art Deco statue of Jesus Christ in Rio de Janeiro, Brazil. Created by French sculptor Paul Landowski, it is 98 feet tall, not including its 26-foot pedestal, and its arms stretch 92 feet wide.",
                "latitude": -22.9519,
                "longitude": -43.2105,
                "country": "Brazil",
                "image_url": "https://images.pexels.com/photos/2868242/pexels-photo-2868242.jpeg"
            },
            {
                "title": "Colosseum",
                "category": "HISTORICAL",
                "description": "The Colosseum is an oval amphitheatre in the centre of Rome, Italy. Built of travertine limestone, tuff, and brick-faced concrete, it is the largest amphitheatre ever built and was used for gladiatorial contests and public spectacles.",
                "latitude": 41.8902,
                "longitude": 12.4922,
                "country": "Italy",
                "image_url": "https://images.pexels.com/photos/1797161/pexels-photo-1797161.jpeg"
            },
            {
                "title": "Northern Lights",
                "category": "NATURAL",
                "description": "The Aurora Borealis (Northern Lights) is a natural light display in the Earths sky, predominantly seen in high-latitude regions. Tromso, Norway is one of the best places to view this phenomenon.",
                "latitude": 69.6492,
                "longitude": 18.9553,
                "country": "Norway",
                "image_url": "https://images.pexels.com/photos/1933239/pexels-photo-1933239.jpeg"
            },
            {
                "title": "Angkor Wat",
                "category": "RELIGIOUS",
                "description": "Angkor Wat is a temple complex in Cambodia and is the largest religious monument in the world. Originally constructed as a Hindu temple dedicated to the god Vishnu for the Khmer Empire, it was gradually transformed into a Buddhist temple.",
                "latitude": 13.4125,
                "longitude": 103.8670,
                "country": "Cambodia",
                "image_url": "https://images.pexels.com/photos/3290071/pexels-photo-3290071.jpeg"
            }
        ]

        for landmark_data in landmarks:
            # Extract the image URL and remove it from the data dict
            image_url = landmark_data.pop('image_url')
            
            # Create the landmark without the image first
            landmark = Landmark.objects.create(**landmark_data)
            
            # Download and attach the image
            if image_url:
                img_temp = self.download_image(image_url)
                if img_temp:
                    file_name = f"{landmark.title.lower().replace(' ', '_')}.jpg"
                    landmark.cover_image.save(file_name, File(img_temp), save=True)
                    img_temp.close()
                    self.stdout.write(self.style.SUCCESS(f'Successfully created landmark "{landmark.title}" with image'))
                else:
                    self.stdout.write(self.style.WARNING(f'Created landmark "{landmark.title}" but failed to download image'))
            else:
                self.stdout.write(self.style.SUCCESS(f'Successfully created landmark "{landmark.title}" without image')) 