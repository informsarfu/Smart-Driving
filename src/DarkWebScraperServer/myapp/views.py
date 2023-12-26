from django.shortcuts import HttpResponse

# Create your views here.
from django.http import JsonResponse
from django.http import StreamingHttpResponse
import os
import base64

import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl


import implicit
import scipy
import pandas as pd
from pathlib import Path
from typing import Tuple, List

from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render



# BASE_DIR = os.path.dir
MEDIA_ROOT = '/c/users/rama4/Music'

# Load environment variables from .env file

def landing(request):
    response = HttpResponse("hi da")
    response['Access-Control-Allow-Origin'] = 'null'
    return response


def artists_loader(artists_loc: Path) -> scipy.sparse.csr_matrix:
    artist_preferences = pd.read_csv(artists_loc, sep="\t")
    artist_preferences.set_index(["userID", "artistID"], inplace=True)
    coo = scipy.sparse.coo_matrix(
        (
            artist_preferences.weight.astype(float),
            (
                artist_preferences.index.get_level_values(0),
                artist_preferences.index.get_level_values(1),
            ),
        )
    )
    return coo.tocsr()


class ArtistDesc:

    def __init__(self):
        self._artists_df = None

    def artist_id_2_data(self, artist_id: int):
        parsed = self._artists_df.loc[self._artists_df['id'] == artist_id, 'artist_lastfm'].tolist()
        return parsed[0]

    def artist_location_filter(self, location: str, country: str):
        self._artists_df = self._artists_df[self._artists_df['country_mb'].str.contains(country)]
        temp = self._artists_df[self._artists_df['tags_lastfm'].str.contains(location,na=False)]
        return temp

    def get_artists(self, artists_file: Path) -> None:
        artists_csv = pd.read_csv('../artists.csv')
        artists_csv_filtered = artists_csv.dropna()
        art_filtered_sorted = artists_csv_filtered.sort_values(by='listeners_lastfm', ascending=False)
        art_filtered_sorted.insert(0, 'id', range(1, 1 + len(art_filtered_sorted)))
        artists_df = art_filtered_sorted
        print(art_filtered_sorted.shape)
        self._artists_df = art_filtered_sorted

class ImplicitRecommender:

    def __init__(
        self,
        artist_desc: ArtistDesc,
        implicit_model: implicit.recommender_base.RecommenderBase,
    ):
        self.artist_desc = artist_desc
        self.implicit_model = implicit_model

    def fit(self, user_artists_matrix: scipy.sparse.csr_matrix) -> None:
        self.implicit_model.fit(user_artists_matrix)

    def recommend(
        self,
        user_id: int,
        user_artists_matrix: scipy.sparse.csr_matrix,
        n: int = 10,
    ) -> Tuple[List[str], List[float]]:
        artist_ids, scores = self.implicit_model.recommend(
            user_id, user_artists_matrix[n], N=n
        )
        print(artist_ids)
        artists = [
            self.artist_desc.artist_id_2_data(artist_id)
            for artist_id in artist_ids
        ]
        return artists, scores

# @csrf_exempt
# def upload_mp3(request):
#     if request.method == 'POST':
#         form = UploadFileForm(request.POST, request.FILES)
#         if form.is_valid():
#             uploaded_file = form.cleaned_data['file']
#             print(uploaded_file)
#             # with open(os.path.join(MEDIA_ROOT, filename), 'wb') as f:
#             #     for chunk in uploaded_file.chunks():
#             #         f.write(chunk)
#         else:
#             print('form invalid')
#         response = HttpResponse('MP3 file uploaded successfully')
#         response['Access-Control-Allow-Origin'] = 'null'
#         return response
#     else:
#         form = UploadFileForm()
#     return render(request, 'upload.html', {'form': form})
def getFilePath(folderName, fileName):
    return os.path.join(folderName,fileName);


@csrf_exempt
def upload(request):
        
    print('upload:')
    if request.method == 'POST':
        
        filename = request.POST['fileName']
        chunk_data = request.POST['chunkData']
        chunk_index = int(request.POST['chunkIndex'])
        total_chunks = int(request.POST['totalChunks'])
        print("filename=",filename)
        print("chunk_index=",chunk_index)        
        print("total_chunks=",total_chunks)        

        fileChunkName = getFilePath('caller' , f'{filename}_{chunk_index}')
        fileFinalName = getFilePath('caller',filename)

        # Store the chunk data in a temporary location
        with open(fileChunkName, 'wb') as chunk_file:
            chunk_file.write(base64.b64decode(chunk_data))
        
        # Check if all chunks have been received
        response = HttpResponse('MP3 file uploaded successfully')
        
        if chunk_index == total_chunks - 1:
            # Reassemble the chunks into the original file
            with open(fileFinalName, 'wb') as mp3_file:
                for i in range(total_chunks):
                    with open(getFilePath('caller' , f'{filename}_{i}'), 'rb') as chunk_file:
                        mp3_file.write(chunk_file.read())
            
            # Remove temporary chunk files
            for i in range(total_chunks):
                os.remove(getFilePath('caller' , f'{filename}_{i}'))

        else:
            response = HttpResponse('MP3 file upload fail!')
        

        response['Access-Control-Allow-Origin'] = 'null'
        return response
    else:
        print("Error while uploading file")
        return HttpResponse('Invalid request method')



@csrf_exempt
def _upload_mp3(request):
        
    print('upload:')
    if request.method == 'POST':
            if request.FILES:
                print("files present in request")
                for file_key in request.FILES:
                    uploaded_file = request.FILES[file_key]
                    print(uploaded_file)
            else:
                print("files NOT present in request")
        # filename = request.POST['fileName']
        # chunk_data = request.POST['chunkData']
        # chunk_index = int(request.POST['chunkIndex'])
        # total_chunks = int(request.POST['totalChunks'])
        # print("filename=",filename)
        # print("chunk_index=",chunk_index)        
        # print("total_chunks=",total_chunks)        

        # fileChunkName = getFilePath('caller' , f'{filename}_{chunk_index}')
        # fileFinalName = getFilePath('caller',filename)

        # # Store the chunk data in a temporary location
        # with open(fileChunkName, 'wb') as chunk_file:
        #     chunk_file.write(base64.b64decode(chunk_data))
        
        # # Check if all chunks have been received
        # response = HttpResponse('MP3 file uploaded successfully')
        
        # if chunk_index == total_chunks - 1:
        #     # Reassemble the chunks into the original file
        #     with open(fileFinalName, 'wb') as mp3_file:
        #         for i in range(total_chunks):
        #             with open(getFilePath('caller' , f'{filename}_{i}'), 'rb') as chunk_file:
        #                 mp3_file.write(chunk_file.read())
            
        #     # Remove temporary chunk files
        #     for i in range(total_chunks):
        #         os.remove(getFilePath('caller' , f'{filename}_{i}'))

        # else:
        #     response = HttpResponse('MP3 file upload fail!')
        

        # response['Access-Control-Allow-Origin'] = 'null'
        # return response
    else:
        print("Error while uploading file")
        return HttpResponse('Invalid request method')


@csrf_exempt
def upload_mp3(request):
        
    print('upload:')
    if request.method == 'POST':
        
        filename = request.POST['fileName']
        chunk_data = request.POST['chunkData']
        chunk_index = int(request.POST['chunkIndex'])
        total_chunks = int(request.POST['totalChunks'])
        print("filename=",filename)
        print("chunk_index=",chunk_index)        
        print("total_chunks=",total_chunks)        

        fileChunkName = getFilePath('caller' , f'{filename}_{chunk_index}')
        fileFinalName = getFilePath('caller',filename)

        # Store the chunk data in a temporary location
        with open(fileChunkName, 'wb') as chunk_file:
            chunk_file.write(base64.b64decode(chunk_data))
        
        # Check if all chunks have been received
        response = HttpResponse('MP3 file uploaded successfully')
        
        if chunk_index == total_chunks - 1:
            # Reassemble the chunks into the original file
            with open(fileFinalName, 'wb') as mp3_file:
                for i in range(total_chunks):
                    with open(getFilePath('caller' , f'{filename}_{i}'), 'rb') as chunk_file:
                        mp3_file.write(chunk_file.read())
            
            # Remove temporary chunk files
            for i in range(total_chunks):
                os.remove(getFilePath('caller' , f'{filename}_{i}'))

        else:
            response = HttpResponse('MP3 file upload fail!')
        

        response['Access-Control-Allow-Origin'] = 'null'
        return response
    else:
        print("Error while uploading file")
        return HttpResponse('Invalid request method')


def download_file(request):
    print('download_file() nana')
    print(request.GET)
    filename = request.GET['fileName']
    file_path = getFilePath('caller',filename)
    if os.path.exists(file_path):
        print('file exists on server')
        with open(file_path, 'rb') as file:
            response = HttpResponse(file.read(), content_type='application/force-download')
            response['Content-Disposition'] = f'attachment; filename="{filename}"'
            return response
    else:
        return HttpResponse('File not found', status=404)


def list_pending_files(request):
    print('list_pending_files()')
    print(request.GET)
    folderpath = 'caller'
    if os.path.exists(folderpath):
        file_names = []
        for file in os.listdir(folderpath):
            if os.path.isfile(os.path.join(folderpath, file)):
                file_names.append(file)
        return JsonResponse({'file_names': file_names})
    else:
        return HttpResponse('Folder not found in server.', status=500)


def driving_score(carSpeed, carPeaks, carRoadCondition):
    peak = ctrl.Antecedent(np.arange(0, 11, 1), 'peak')
    road_condition = ctrl.Antecedent(np.arange(0, 2, 1), 'road_condition')
    speed = ctrl.Antecedent(np.arange(10, 81, 1), 'speed')  
    driver_score = ctrl.Consequent(np.arange(0, 101, 1), 'driver_score')
    peak['low'] = fuzz.trimf(peak.universe, [0, 0, 5])
    peak['medium'] = fuzz.trimf(peak.universe, [0, 5, 10])
    peak['high'] = fuzz.trimf(peak.universe, [5, 10, 10])

    road_condition['low'] = fuzz.trapmf(road_condition.universe, [0, 0, 0.5, 1])
    road_condition['high'] = fuzz.trapmf(road_condition.universe, [0.5, 1, 1, 1])

    speed['low'] = fuzz.trimf(speed.universe, [10, 10, 30])
    speed['medium'] = fuzz.trimf(speed.universe, [20, 45, 70])  
    speed['high'] = fuzz.trimf(speed.universe, [60, 80, 80])

    driver_score['very_low'] = fuzz.trimf(driver_score.universe, [0, 0, 20])
    driver_score['low'] = fuzz.trimf(driver_score.universe, [10, 30, 50])
    driver_score['medium'] = fuzz.trimf(driver_score.universe, [40, 60, 80])
    driver_score['high'] = fuzz.trimf(driver_score.universe, [70, 90, 100])

    rule1 = ctrl.Rule(peak['low'] & road_condition['high'] & speed['low'], driver_score['high'])
    rule2 = ctrl.Rule(peak['medium'] & road_condition['low'] & speed['medium'], driver_score['medium'])
    rule3 = ctrl.Rule(peak['high'] & road_condition['high'] & speed['low'], driver_score['high'])
    rule4 = ctrl.Rule(peak['low'] & road_condition['high'] & speed['high'], driver_score['medium'])
    rule5 = ctrl.Rule(peak['medium'] & road_condition['low'] & speed['medium'], driver_score['low'])
    rule6 = ctrl.Rule(peak['high'] & road_condition['low'] & speed['medium'], driver_score['very_low'])
    rule7 = ctrl.Rule(peak['low'] & road_condition['high'] & speed['low'], driver_score['high'])

    rule8 = ctrl.Rule(peak['medium'] & road_condition['high'] & speed['medium'], driver_score['high'])
    rule9 = ctrl.Rule(peak['high'] & road_condition['low'] & speed['low'], driver_score['low'])
    rule10 = ctrl.Rule(peak['low'] & road_condition['low'] & speed['high'], driver_score['very_low'])
    rule11 = ctrl.Rule(peak['high'] & road_condition['high'] & speed['high'], driver_score['very_low'])
    rule12 = ctrl.Rule(peak['medium'] & road_condition['high'] & speed['high'], driver_score['medium'])
    rule13 = ctrl.Rule(peak['low'] & road_condition['low'] & speed['low'], driver_score['low'])
    rule14 = ctrl.Rule(peak['high'] & road_condition['low'] & speed['low'], driver_score['low'])

    driver_score_ctrl = ctrl.ControlSystem(
        [rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8, rule9, rule10, rule11, rule12, rule13, rule14]
    )
    driver_score_simulation = ctrl.ControlSystemSimulation(driver_score_ctrl)
    driver_score_simulation.input['peak'] =  int(carPeaks)
    driver_score_simulation.input['road_condition'] = int(carRoadCondition)
    # print("driver_score_simulation.input=",driver_score_simulation.input)
    # print("driver_score_simulation.input type=",type(driver_score_simulation.input))
    driver_score_simulation.input['speed'] = int(carSpeed)

    driver_score_simulation.compute()
    drvingScoreVal = driver_score_simulation.output['driver_score']
    return drvingScoreVal

def check_collision(request):
    file_path = '../Collision/myfile.txt'
    accelerometer_collision=  request.GET.get('acc',False)
    peaks=  request.GET.get('peak',5)
    roadCondition=  request.GET.get('roadCondition',0)
    speed_val = 0
    if roadCondition == 1:
        speed_val = 25
    else:
        speed_val = 45
    speed=  request.GET.get('speed',speed_val)
    print("accelerometer_collision=",accelerometer_collision)
    dScore = driving_score(speed, peaks, roadCondition)
    # we need to get the number of peaks previous and current as this will give us the collided state data
    response = JsonResponse({"collision": False, "drivingscore" :dScore})
    if os.path.exists(file_path):
        print('The file exists!')
        if  accelerometer_collision:
            response = JsonResponse({"collision": True, "drivingscore" :dScore})
    else:
        print('The file does not exist.')
    response['Access-Control-Allow-Origin'] = 'null'
    return response


# print("Driver Score:", drivingValue)
def home(request):
    query = request.GET.get('q', '')
    field = request.GET.get('fl', '')
    print("query=", query)
    print("field=", field)

    # load user artists matrix
    user_artists = artists_loader('../lastfmdata/user_artists.dat')

    # instantiate artist retriever
    artist_desc = ArtistDesc()
    artist_desc.get_artists(Path("../artists.csv"))

    # instantiate ALS using implicit
    implict_model = implicit.als.AlternatingLeastSquares(
        factors=50, iterations=10, regularization=0.01
    )

    # instantiate recommender, fit, and recommend
    recommender = ImplicitRecommender(artist_desc, implict_model)
    recommender.fit(user_artists)
    artists, scores = recommender.recommend(2, user_artists, n=100)
    artist_location_filtered = artist_desc.artist_location_filter(str(query),str(field))
    print("Begin----------------------------------------------------------------------------------")
    ar_sr = []
    for artist, score in zip(artists, scores):
        result_match = artist_location_filtered['artist_mb'].str.contains(artist, case=False).any()
        if result_match:		
            print(f"{artist}: {score}")
            ar_sr.append(artist)
			 
    print("Done---------------------------------------------------------------------------------")
    response = JsonResponse({"artists": "###".join(ar_sr)})
    response['Access-Control-Allow-Origin'] = 'null'
    return response
