import numpy as np
import random

peak_values = [0, 5, 10]
road_condition_values = [0, 1]
speed_values = [10, 45, 80]

transition_matrix = np.array([
    [[0.1, 0.7, 0.2], [0.3, 0.6, 0.1]],
    [[0.2, 0.5, 0.3], [0.4, 0.5, 0.1]],
    [[0.3, 0.4, 0.3], [0.2, 0.6, 0.2]]
])

current_peak = np.random.choice(peak_values)
current_road_condition = np.random.choice(road_condition_values)
current_speed = np.random.choice(speed_values)

num_iterations = 10

for scenario in range(1, 4):
    print(f"\nScenario {scenario}:")

    for _ in range(num_iterations):
        peak_probabilities = transition_matrix[current_peak % len(peak_values), current_road_condition, :]
        normalized_peak_probabilities = peak_probabilities / np.sum(peak_probabilities)

        road_condition_probabilities = transition_matrix[:, current_road_condition, current_speed % len(speed_values)]
        normalized_road_condition_probabilities = road_condition_probabilities / np.sum(road_condition_probabilities)

        speed_probabilities = transition_matrix[current_peak % len(peak_values), :, current_speed % len(speed_values)]
        normalized_speed_probabilities = speed_probabilities / np.sum(speed_probabilities)

        if len(normalized_road_condition_probabilities) < len(normalized_peak_probabilities):
            normalized_road_condition_probabilities = np.pad(normalized_road_condition_probabilities, (0, len(normalized_peak_probabilities) - len(normalized_road_condition_probabilities)), 'constant')
        elif len(normalized_road_condition_probabilities) > len(normalized_peak_probabilities):
            normalized_peak_probabilities = np.pad(normalized_peak_probabilities, (0, len(normalized_road_condition_probabilities) - len(normalized_peak_probabilities)), 'constant')

        if len(normalized_speed_probabilities) < len(normalized_peak_probabilities):
            normalized_speed_probabilities = np.pad(normalized_speed_probabilities, (0, len(normalized_peak_probabilities) - len(normalized_speed_probabilities)), 'constant')
        elif len(normalized_speed_probabilities) > len(normalized_peak_probabilities):
            normalized_peak_probabilities = np.pad(normalized_peak_probabilities, (0, len(normalized_speed_probabilities) - len(normalized_peak_probabilities)), 'constant')

        current_peak = np.random.choice(peak_values, p=normalized_peak_probabilities)

        if scenario == 1:
            current_road_condition = 0
            current_speed = np.random.choice(speed_values, p=normalized_speed_probabilities)
        elif scenario == 2:
            current_road_condition = 1
            current_speed = np.random.choice(speed_values, p=normalized_speed_probabilities)
        elif scenario == 3:
            current_road_condition = random.choice([0, 1])
            current_speed = np.random.choice(speed_values, p=normalized_speed_probabilities)

        print(f"Iteration: {_ + 1}, Peak: {current_peak}, Road Condition: {current_road_condition}, Speed: {current_speed}")
