# Driving Score Calculation using Fuzzy inference System

My project deals with finding the driving score of the driver which can be further used to calculate data about the fuel cost efficiency, driver safety and likelihood of crash. I have implemented the fuzzy inference system which calculates the driver score based on three fuzzy variables: Road Condition (Low Cognitive, High Cognitive), Speed of the vehicle and No. of peaks detected ( this is based on the peak detection algorithm implemented as part of the project.)

As part of the peak detection algorithm, It needs to calculate instances such as sudden breaks, lane changes and rash driving as per the driver and this is a continuous process. ( Made use of accelerometer to calculate the data). 

## Installation Guide
Start by Creating virtual environment in the src folder

    -> cd src # entering src
    -> pip install virtualenv # installing virtual env
    -> python -m venv project_env # creating a virtual env

Step to activate the virtual environment (do the following in git bash):
(Same commands can be used if using cmd)

    -> source project_env/Scripts/activate

Continue to install any relevant dependancies from src/requirements.txt

    -> cd src
    -> pip install -r requirements.txt

Run the server locally (in a seperate terminal):

    -> cd src/DarkWebScraperServer
    -> python runserver 0.0.0.0:8000

Now you can interact with the app.

## To test my django URL Endpoint use the following command:
curl "http://0.0.0.0:8000/drivingScore?peak=5&roadCondition=1&speed=45"

The above command will trigger the endpoint corresponding to calculate the driving score making use of the fuzzy logic provided.

Feel free to replace the above provided values with desired values indicating the peaks, roadCondtion and speed of the vehicle.

( Peaks are normalized to be under 10, and road Condition is being segregated to 0 and 1, corresponding to high and low cognitive workload, and the speed is normalized to be between 10 and 100 for removing any sporadic inputs)

# Peak Detection extending the First Project's Accelerometer Data

## prerequisites
Install Android SDK

I have extended the peak detection algorithm implemented during the first project, which can now consider the car spatial data and based on the change of the data points from previous compared to current, It can segregate the values to determine whether it is under the considerable threshold for the data collected. 

To run the project:
 - Export the Code in the app folder, to the android studio
 - Run the application, and click on the Measure Peak Detected Button
 - Try to replicate the car motion, using human actions, giving it jerks(replicating the sudden break scenario), try to move faster from left to right(replicating the lane change scenario) This should give out the number of peaks ( I have logged the value as we have integrated the above java module to react native module for our final application)
 - The data will be sent back to your phone, which is then showed under the button value representing the peaks.

# Calculating the Fuel Cost Efficiency
 To calculate the Fuel Efficiency, Follow the below steps:
Prerequisites:
  Need to have Node.JS Installed
Steps to Follow:
  - cd FuelCostCalculation
  - node fuelCostCalculation.js 
  - ( Need to manually configure the values passed as parameters, as these are being integrated by other teammate)
