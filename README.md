# Smart Driving Assistant: Music Recommendation Integration

## Overview

This repository contains the code for the music recommendation integration component in the Smart Driving Assistant project. The Smart Driving Assistant is a comprehensive system designed to enhance the driving experience by acting as a guardian angel, providing safety features and personalized infotainment.

## Music Recommendation Integration

The music recommendation integration focuses on recommending the top 5 songs of the most listened-to artist in a specific location. This is achieved through a machine learning model that predicts popular artists for a given location. The Android application then utilizes the YouTube Music API to fetch and display the top songs of the predicted artist.

## How to Use

### Prerequisites

- Android Studio installed.
- API key for the YouTube Data API. You can obtain one [here](https://developers.google.com/youtube/registering_an_application).

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/smart-driving-assistant-music.git

2. Open the project in Android Studio.
3. Replace the placeholder API key in MainActivity.java with your own YouTube Data API key.
    - private val apiKey = "YOUR_API_KEY"
4. Build and run the application on an Android emulator or device.
5. Enter the artist's name in the provided EditText and click the "Search" button.
6. The top 5 songs of the predicted artist for the location will be displayed.

### Code Structure
- 'MainActivity.java': Main activity handling user interactions, API requests, and audio playback.
- 'FetchTopSongsTask': AsyncTask for fetching top songs using the YouTube Data API.
- 'res/layout/activity_main.xml': XML layout file defining the UI components.

## Tech Stack
- Android (Kotlin)
- YouTube Data API
- OkHttpClient for HTTP requests

## License
This project is licensed under the terms of the [MIT License](LICENSE).

## Acknowledgement
The Smart Driving Assistant team for collaboration and contribution.

