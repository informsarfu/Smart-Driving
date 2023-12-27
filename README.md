# **Collision Detection Algorithm:**
Challenge Description: 
The issue at hand involves utilizing a webcam to measure real-world distance by detecting and recognizing cars. The objective is to create a prototype that aids the collision detection algorithm to support individuals in contacting emergency services effortlessly.

Utilized Algorithm: YOLOv4 (You Only Look Once)
Dataset Employed: COCO (Common Objects in Context)

Flow of the Code
1. Import the OpenCV module to establish a function for car detection.
2. Develop two additional functions for distance calculation and focal length.
3. Instantiate a DetectionModel and provide reference images for object detection.
4. Utilize cv2 to capture real-time video through the webcam and measure distances in inches.

Screenshots:
![Screenshot (43)](https://github.com/Rama4/CSE-535-Project5/assets/130247471/e30e4770-86a0-4995-abeb-03d1ae8759d4)

Steps: 
1. pip install -r requirements.txt
2. python main.py

**Location-aware music recommendation algorithm:**

Collaborative filters operate with an interaction matrix, also known as a rating matrix. The objective of this algorithm is to acquire a function that predicts whether a user will derive value from an item—indicating the likelihood that the user will purchase, listen to, or watch the item.

Within collaborative-based systems, there are two types: user-item filtering and item-item filtering.

We will walk through the process of creating a music recommender system, employing a matrix factorization approach this time.

Utilized algorithm: Implicit Deep Learning - AlternatingLeastSquares
DataSets : LastFM dataset to train the model according to User Preferences and Spotify Million Song DataSet to generate recommendations.

Flow of the code:
1. Install the required modules
2. Host the code in a Django Server
3. Do an HTTP GET request to `http://127.0.0.1:8000/myapp/` with parameters of city and country `http://localhost:8000/myapp?q=new%20york&fl=United%20States`
4. Parse the string of artists seperated by the delimiter `###`

Sample Output:
http://localhost:8000/myapp?q=new%20york&fl=United%20States yeilded the following Artists with Similarity Score: 
```
Heart: 1.056212067604065
Jae Millz: 1.0267150402069092
50 Cent: 0.991989254951477
Simon & Garfunkel: 0.9826768636703491
T.I.: 0.981890857219696
Gym Class Heroes: 0.9342354536056519
Neon Indian: 0.8805388808250427
```

Personalization of sound:
A moving average calculation class with a configurable window size (testing with 5 currently) is implemented to tune the sound level of music which the user prefers to listen the music in different scenarios of Slow traffic, Normal Traffic and Traffic Jam.
It's adaptive to the changes made recently to predict an optimum level of sound.
It works as per the user's past history of changing the volume in Jam, Slow Traffic and Normal Traffic cases.


Django Framework :

Django server and Web API endpoints setup
    pip install django
    django-admin startproject DarkWebScraperServer
    cd DarkWebScraperServer
    python manage.py startapp myapp
Add endpoint for each of the functionalities.

Run the Server: 
cd CODE/DarkWebScraperServer
    python manage.py runserver
