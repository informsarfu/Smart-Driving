import implicit
import scipy
import pandas as pd
from pathlib import Path
from typing import Tuple, List


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
        artists_csv = pd.read_csv('~/Desktop/cse535/mine/artists.csv')
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

if __name__ == "__main__":

    # load user artists matrix
    user_artists = artists_loader(Path("~/Desktop/cse535/mine/musiccollaborativefiltering/lastfmdata/user_artists.dat"))

    # instantiate artist retriever
    artist_desc = ArtistDesc()
    artist_desc.get_artists(Path("~/Desktop/cse535/mine/artists.csv"))

    # instantiate ALS using implicit
    implict_model = implicit.als.AlternatingLeastSquares(
        factors=50, iterations=10, regularization=0.01
    )

    # instantiate recommender, fit, and recommend
    recommender = ImplicitRecommender(artist_desc, implict_model)
    recommender.fit(user_artists)
    artists, scores = recommender.recommend(2, user_artists, n=100)

    # print results
    artist_location_filtered = artist_desc.artist_location_filter("utah","United States")
    print("Begin----------------------------------------------------------------------------------")
    for artist, score in zip(artists, scores):
        result_match = artist_location_filtered['artist_mb'].str.contains(artist, case=False).any()
        if result_match:		
            print(f"{artist}: {score}")
			 
    print("Done---------------------------------------------------------------------------------")