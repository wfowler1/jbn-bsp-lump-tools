# Introduction #

As the decompiler is now in nearly perfect shape, it's time to make a compatibility list. Below is a list of maps and their optimal decompiler options.

One thing to keep in mind, although 87 or 72 bad solids may sound like a lot, compared to the several thousand solids in the entire level this is a relatively small number.

Note: There might be a coefficient that works better than the one listed here. Also, when using a coefficient of 1, round-off errors may cause solids to not line up properly. Use a larger coefficient if necessary (such as 10 or 100).

# Details #

## Official Gearbox Maps ##

### Mission maps ###

| **Map** | **Skip Vertex Check** | **Best Known Coefficient** | **Status** | **Notes** |
|:--------|:----------------------|:---------------------------|:-----------|:----------|
| m1\_austria01 | no                    | 25                         | 10 invalid |           |
| m1\_austria02 | no                    | 10                         | 14 invalid |           |
| m1\_austria03 | no                    | 10                         | 4 invalid  |           |
| m1\_austria04 | no                    | 10                         | 1 invalid  |           |
| m2\_airfield01 | no                    | 100                        | 87 invalid | Most of the bad solids are terrain around the map |
| m3\_japan01 | no                    | 100                        | 2 invalid  |           |
| m3\_japan02 | no                    | 50                         | 1 invalid  |           |
| m3\_japan03 | no                    | 50                         | 12 invalid |           |
| m3\_japan04 | no                    | 50                         | 6 invalid  |           |
| m4\_infiltrate01 | no                    | 1                          | Perfect    |           |
| m4\_infiltrate02 | no                    | 5                          | Perfect    |           |
| m4\_infiltrate03 | no                    | 100                        | 1 invalid  | It's a playerclip brush around the sculpture in the middle |
| m4\_infiltrate04 | no                    | 10                         | 6 invalid  |           |
| m4\_infiltrate05 | no                    | 1                          | Perfect    |           |
| m4\_infiltrate06 | no                    | 1                          | Perfect    |           |
| m4\_infiltrate07 | no                    | 20                         | Perfect    | Penthouse ceiling glass seems to disappear, but there's no bad solids |
| m5\_power01 | no                    | 10                         | 72 invalid |           |
| m5\_power02 | no                    | 1                          | Perfect    |           |
| m6\_escape01 | no                    | 5                          | 1 invalid  |           |
| m6\_escape03 | no                    | 25                         | 1 invalid  |           |
| m6\_escape04 | no                    | 10                         | 8 invalid  |           |
| m6\_escape05 | no                    | 10                         | 2 invalid  |           |
| m6\_escape06 | no                    | 5                          | 1 invalid  |           |
| m6\_escape07 | no                    | 5                          | Perfect    |           |
| m7\_island01 | no                    | 1                          | 3 invalid  |           |
| m7\_island02 | no                    | 5                          | 1 invalid  |           |
| m7\_island03 | no                    | 1                          | 5 invalid  | The biggest map in the game! |
| m7\_island04 | no                    | 1                          | Perfect    |           |
| m7\_island05 | no                    | 1                          | 27 invalid |           |
| m7\_island06 | no                    | 1                          | 14 invalid |           |
| m8\_missile01 | no                    | 10                         | 2 invalid  |           |
| m8\_missile02 | no                    | 5                          | 5 invalid  |           |
| m8\_missile03 | no                    | 15                         | 6 invalid  |           |
| m8\_missile04 | no                    | 1                          | 8 invalid  |           |
| m9\_space01 | no                    | 5                          | 11 invalid |           |

### Deathmatch maps ###

| **Map** | **Skip Vertex Check** | **Best Known Coefficient** | **Status** | **Notes** |
|:--------|:----------------------|:---------------------------|:-----------|:----------|
| dm\_austria | no                    | 10                         | 10 invalid |           |
| dm\_casino | no                    | 1                          | Perfect    |           |
| dm\_caviar | no                    | 1                          | Perfect    |           |
| dm\_island | no                    | 1                          | 1 invalid  |           |
| dm\_japan | no                    | 25                         | 3 invalid  |           |
| dm\_jungle | no                    | 1                          | 4 invalid  |           |
| dm\_knox | no                    | 1                          | Perfect    |           |
| dm\_maint | no                    | 1                          | Perfect    |           |
| dm\_office | no                    | 5                          | Perfect    |           |
| dm\_power | no                    | 1                          | 37 invalid |           |

### Capture the Flag maps ###

| **Map** | **Skip Vertex Check** | **Best Known Coefficient** | **Status** | **Notes** |
|:--------|:----------------------|:---------------------------|:-----------|:----------|
| ctf\_austria | no                    | 25                         | 15 invalid |           |
| ctf\_island | no                    | 1                          | Perfect    |           |
| ctf\_japan | no                    | 25                         | 10 invalid |           |
| ctf\_jungle | no                    | 1                          | 38 invalid |           |
| ctf\_knox | no                    | 1                          | Perfect    |           |
| ctf\_office | no                    | 25                         | 3 invalid  |           |
| ctf\_romania | no                    | 25                         | 1 invalid  |           |
| ctf\_tower | no                    | 1                          | Perfect    |           |

## Identifying Bad Solids ##
Does the map you're decompiling have bad solids? Wondering where or what they are? Follow these steps:
  1. Open the map in GearCraft
  1. In one of the 2D views, zoom out all the way
  1. Press Ctrl+A to select the entire map
  1. Go into vertex manipulation mode (this forces the editor to recalculate all vertices and save them)
  1. Go back into selection mode
  1. Go into Map -> Check for problems
  1. Select one of the "Invalid solid structure" entries
  1. Find the solid in the 2D and 3D views