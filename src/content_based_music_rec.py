import numpy as n
import pandas as p
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from typing import List, Dict

tracks = p.read_csv('~\Desktop\cse535\music_recommender\music_recommender\content_based_recommender\songdata.csv')

# print(tracks)

tracks = tracks.sample(n=5000).drop('link', axis=1).reset_index(drop=True)

tracks['text'] = tracks['text'].str.replace(r'\n', '')

termFreqIDF = TfidfVectorizer(analyzer='word', stop_words='english')

songLyricDataMtrx = termFreqIDF.fit_transform(tracks['text'])

cos_similarity_arr = cosine_similarity(songLyricDataMtrx) 

similarity_arr = {}

for i in range(len(cos_similarity_arr)): 
    similarityIndexes = cos_similarity_arr[i].argsort()[:-50:-1] 
    similarity_arr[tracks['song'].iloc[i]] = [(cos_similarity_arr[i][x], tracks['song'][x], tracks['artist'][x]) for x in similarityIndexes][1:]

class ContentBasedRecommendationEngine:
    def __init__(self, mtrx):
        self.mtrx_similar = mtrx

    def _print_message(self, track, curr_recommendation):
        recommendedItems = len(curr_recommendation)
        
        print(f'The {recommendedItems} recommended tracks for {track} are:')
        for i in range(recommendedItems):
            print(f"Track {i+1}:")
            print(f"{curr_recommendation[i][1]} by {curr_recommendation[i][2]} with {round(curr_recommendation[i][0], 3)} matching score") 
        
    def recommend(self, recommendation):
        track = recommendation['song']
        number_tracks = recommendation['number_tracks']
        curr_recommendation = self.mtrx_similar[track][:number_tracks]
        self._print_message(track=track, curr_recommendation=curr_recommendation)

recommedations = ContentBasedRecommendationEngine(similarity_arr)

recommendation = {
    "song": tracks['song'].iloc[10],
    "number_tracks": 4 
}

recommedations.recommend(recommendation)