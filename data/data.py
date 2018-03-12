# (C) 2018 Nathan Corbyn
# Transforms existing data into constrained subset (with modifications)

kanji_file = open("kanji", "r")
kanji = kanji_file.readlines()
for x in range(0, len(kanji)):
    kanji[x] = kanji[x].split("\n")[0] 

decomposition = []
loaded = []

def pseudo_load(character):
    pseudo_character = ""
    decomposition_file = open("decomp", "r")
    for point in decomposition_file:
        if point.startswith(character):
            components = (point.split(":")[1])[:-2].split("(")[1].split(",")
            for component in components:
                if len(component) == 1:
                    if component not in kanji:
                        kanji.append(component)
                    pseudo_character = pseudo_character + component + ","
                else:
                    pseudo_character = pseudo_character + pseudo_load(component)
            break
    return pseudo_character


def load(character):
    decomposition_file = open("decomp", "r")
    for point in decomposition_file:
        if point.startswith(character):
            print(character)
            character = character + ":"
            components = (point.split(":")[1])[:-2].split("(")[1].split(",")
            for component in components:
                if len(component) == 1:
                    if component not in kanji:
                        kanji.append(component)
                    character = character + component + ","
                else:
                    character = character + pseudo_load(component)
            character = character[:-1] + "\n"
            decomposition.append(character)
            print(character)
            break

for character in kanji:
    load(character)

for index, character in enumerate(decomposition):
    split = character.split(":")
    if split[0] in (split[1][:-1]).split(","):
        decomposition[index] = split[0] 

output_file = open("jouyou_decomposition", "w")
for point in decomposition:
    output_file.write(point)

