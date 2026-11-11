# Changelog

## 1.21.1-1.0.16

- A recipe that needs no energy (defined by the no energy conditional) shows "No energy" on a multiblock's screen instead of a base and current EU/t
- A machine whose hatches can run both a conditioned recipe and the plain recipe it copies runs the conditioned one. 
- A Chronon Input Hatch no longer draws from a Tempad Metronome that is still booting.
- The Kinetic Coupler's shaft side is now a brass casing face with the shaft end drawn on it. It used Create's shaft-end overlay as the whole face, which is almost all transparent, so that side looked missing
- Jade on a Stress Input Hatch shows how much spare stress the attached network has. When a recipe is waiting for stress the hatch cannot supply, it also shows in red how many SU and how much RPM that recipe needs, so a network that is too slow or capped below the recipe no longer looks the same as a working one

## 1.21.1-1.0.12

- When more than one recipe matches what a machine holds, the one with the most recipe conditions is tried first. Modern Industrialization tried them in no fixed order, so a pack that added a stress or matter version of a stock recipe got whichever one it happened to pick, which could change from one restart to the next. A machine now runs the conditioned version whenever its hatches can supply it and uses the plain recipe otherwise
- Fixed Jade chronon bar on the Chronon Input Hatch (FTBTesting/Testing-Issues#4837)

## 1.21.1-1.0.11

- New common config option `stack_stress_hatches`. When true, the default and the old behaviour, a multiblock adds up the spare stress of every Stress Input Hatch on a separate kinetic network and splits the load between them. When false only the hatch with the most spare stress at the recipe's RPM counts and it carries the whole load, so a machine can never draw more than one network can carry. Packs that cap kinetic networks by tier, as Create Tiers does, can set it to false so extra hatches on separate capped networks cannot add up past that cap

## 1.21.1-1.0.10

- Matter hatches have a side the wrench can turn, so Modern Industrialization's wrench overlay shows on them like it does on item and fluid hatches. A hatch still reads the network through whichever side a pipe touches
- Replication matter pipes draw a connection to matter hatches. They always fed the hatch, but showed no arm joining it, which looked broken
- Recipes with the `miei:no_energy` condition show "No energy" in JEI instead of an EU/t cost, without the energy icon, and their tooltip lists only the recipe's conditions instead of a total EU cost and steam lines that never applied

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
