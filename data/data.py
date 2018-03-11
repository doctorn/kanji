# (C) 2018 Nathan Corbyn
# Transforms existing data into constrained subset (with modifications)

kanji_file = open("kanji", "r")
kanji = kanji_file.readlines()
for x in range(0, len(kanji)):
    kanji[x] = kanji[x].split("\n")[0] 

decomposition_file = open("decomp", "r")
decomposition = []

for point in decomposition_file:
    point = point.split(":")
    # Filter out all characters apart from the desired Kanji
    if point[0] in kanji:
        character = point[0] + ":"
        # This step filters out underdefined and noisy decompositions
        components = (point[1])[:-2].split("(")[1].split(",")
        for component in components:
            if len(component) == 1:
                character = character + component + ","
        character = character[:-1] + "\n"
        decomposition.append(character)

output_file = open("jouyou_decomposition", "w")
for point in decomposition:
    output_file.write(point)

