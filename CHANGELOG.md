# Changelog

## 1.21.1-1.0.9

- Every MIEI recipe condition now shows its hatch as an icon in JEI, the way Modern Industrialization's own conditions do, and hovering it lists what the recipe needs. A recipe whose only costs were matter, chemicals, chronon, heat, air, source, laser power or stress used to show nothing, because the conditions had no icon and the viewer skipped them

## 1.21.1-1.0.8

- `miei:matter` conditions accept matter types added by other mods, by name or full id (for example `continuum2:paradox`). They used to fall back to Empty for anything outside Replication's built-in types
- A matter hatch holds up to 8000 of every matter type at once instead of 8000 of one type, so a single hatch can feed a recipe that needs several types even when something inserts matter into it directly. Jade lists every type it holds

## 1.21.1-1.0.7

- Added Nautechnical Hatch and `miei:laser` recipe condition for Nautec lasers
- Fixed Matter Input and Output Hatches never receiving matter from a Replication network. A hatch now reaches the tanks of any network it touches, so one input hatch serves every matter type on the network
- Fixed `miei:no_energy` recipes on a multiblock with no energy hatch making no progress and restarting every tick, which consumed their matter and other resources again each time

## 1.21.1-1.0.6

- Added `miei:stage` recipe condition
- Added Jade line showing which stage a machine is waiting for

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
