# Changelog

## 1.21.1-1.0.5

- Added an item slot to the Chronon Input Hatch that drains a charged Chronon Cell or Battery, so chronon recipes can run before a Metronome exists
- Pipes and hoppers can insert chronon items into the hatch and only extract spent ones
- Added the `chronon_item_slot` common config option (default true) to turn the Chronon Input Hatch item slot off

## 1.21.1-1.0.4

- Fixed Source, chronon, air, matter and chemical recipes losing crafts when the hatch runs low (breakinblocks/Modern-Industrialization-Extended-Integration#3)
- Fixed the overclock resetting with an Overdrive Module when a hatch runs empty (breakinblocks/Modern-Industrialization-Extended-Integration#3)
- Fixed MIEI recipe conditions not working in the Multi Processing Array and Processing Array (breakinblocks/Modern-Industrialization-Extended-Integration#2)
- Fixed loot table errors in the log for hatches that are not registered

## 1.21.1-1.0.3

- Added Ars Nouveau source input hatches and `miei:source` recipe condition
- Fixed LV, MV, HV, bronze and steel hatches dropping nothing when broken

## 1.21.1-1.0.2

- Fixed startup crash without an IV tier
- Added `miei:no_energy` recipe condition
- Added LV, MV and HV air and heat hatches
- Added bronze, steel, LV, MV and HV stress input hatches
- Requires Modern Industrialization 2.5.8 or newer

## 1.21.1-1.0.1

- Fixed block tags

## 1.21.1-1.0.0

Initial Mod release
