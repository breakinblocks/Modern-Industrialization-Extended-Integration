# MIEI

**Modern Industrial Extended Integration**

MIEI lets [Modern Industrialization](https://modrinth.com/mod/modern-industrialization) multiblocks
draw on resources from other mods as part of their recipes:

- **[Replication](https://modrinth.com/mod/replication) matter**, consumed or produced by a recipe.
- **[Create](https://modrinth.com/mod/create) stress units**, loaded onto a rotational network while
  a recipe runs.
- **[PneumaticCraft: Repressurized](https://modrinth.com/mod/pneumaticcraft-repressurized) compressed
  air**, drawn from a pressurised air network.
- **PneumaticCraft heat**, as a temperature the recipe requires and optionally draws down.
- **[Mekanism](https://modrinth.com/mod/mekanism) chemicals**, consumed or produced by a recipe.
- **[Tempad](https://modrinth.com/mod/tempad) chronons**, pulled from a nearby Metronome into a
  hatch buffer and consumed by a recipe.
- **[Ars Nouveau](https://modrinth.com/mod/ars-nouveau) source**, fed in by relays or an AE2 export
  bus and consumed by a recipe.
- **Nautec lasers**, as a beam strength and purity the recipe needs for its whole duration.

They all work the same way: a hatch in the multiblock shape carries the resource, and a recipe
condition declares what the recipe needs from it. Every integration is optional and independent, so
install whichever ones you want. Modern Industrialization is the only hard requirement.

There is also a `miei:no_energy` condition that needs no other mod at all. It lets a recipe run
with no EU or steam, so a pack can power a multiblock entirely through the resources above, and a
`miei:stage` condition that locks a recipe behind a player stage.

## Matter

Requires Replication. Without it, none of this registers and `miei:matter` recipes never run.

### Matter hatches

Six new hatches that fit any Modern Industrialization multiblock shape which accepts a hatch in that
position. They come in three tiers, matching MI's casing tiers:

| Hatch | Item ID |
|---|---|
| EV Matter Input Hatch | `modern_industrialization:ev_matter_input_hatch` |
| EV Matter Output Hatch | `modern_industrialization:ev_matter_output_hatch` |
| IV Matter Input Hatch | `modern_industrialization:iv_matter_input_hatch` |
| IV Matter Output Hatch | `modern_industrialization:iv_matter_output_hatch` |
| SV Matter Input Hatch | `modern_industrialization:superconductor_matter_input_hatch` |
| SV Matter Output Hatch | `modern_industrialization:superconductor_matter_output_hatch` |

Each hatch holds up to 8000 of every matter type at once, so one hatch can feed a recipe that needs
several types whether it is filled from a network, by an AE2 storage bus or by anything else that
inserts matter.

Input hatches accept matter from a Replication network and hand it to the multiblock. Output hatches
collect matter produced by a recipe and let a Replication network pull it out. The direction is
enforced: a network cannot drain an input hatch, and it cannot push matter into an output hatch.

Connect them to Replication with the same matter pipes and network parts you already use. Hatches
expose Replication's matter handler capability on every side.

A hatch touching a matter pipe, tank or other Replication network block also reaches that network's
tanks. An input condition takes matter from the multiblock's input hatches first and then from the
tanks on any network those hatches touch, and an output condition fills the output hatches first and
then those networks' tanks. One input hatch on a network therefore serves every matter type the
network holds, whatever the hatch itself stores.

### The matter recipe condition

Recipes can declare a `miei:matter` process condition. A multiblock will not start a craft unless
every condition is met, and the matter is taken or produced as the craft runs, alongside the normal
item and fluid handling.

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:matter", "matter": "precious", "amount": 64.0 },
    { "type": "miei:matter", "matter": "quantum", "amount": 8.0, "output": true }
  ]
}
```

| Field | Meaning |
|---|---|
| `matter` | Matter type name. Case-insensitive. |
| `amount` | How much to consume or produce. Decimals allowed. |
| `output` | `false` (default) consumes from input hatches, `true` produces into output hatches. |

Valid matter types are the ones Replication defines: `earth`, `nether`, `organic`, `ender`,
`metallic`, `precious`, `living` and `quantum`.

Conditions show up in the machine's recipe tooltip, so players can see what a recipe needs before
they build for it.

MIEI ships no recipes of its own. It is a platform for packs and addons to build on, so the matter
economy is entirely yours to design.

### How a matter craft behaves

- The multiblock must be formed. An unformed shape has no hatches, so no matter recipe can run.
- A craft starts only when every input condition has enough matter available across the input
  hatches, and every output condition has enough room across the output hatches.
- Matter is drawn from, and pushed into, all matching hatches in the structure. Several hatches of
  the same type stack their capacity for one recipe.
- If matter runs short or an output hatch backs up, the craft stops rather than losing the matter.

## Stress units

Requires Create. Without it, none of this registers and `miei:stress` recipes simply never run.

### Stress Input Hatch and Kinetic Coupler

Create's rotational network only admits Create's own kinetic blocks, so the connection is two
blocks: a hatch that sits in the multiblock shape, and a coupler that carries the shaft.

| Block | Item ID |
|---|---|
| EV Stress Input Hatch | `modern_industrialization:ev_stress_input_hatch` |
| IV Stress Input Hatch | `modern_industrialization:iv_stress_input_hatch` |
| SV Stress Input Hatch | `modern_industrialization:superconductor_stress_input_hatch` |
| Kinetic Coupler | `miei:kinetic_coupler` |

Put a Stress Input Hatch in the multiblock, then place a Kinetic Coupler against its exposed face.
The coupler orients itself toward the hatch on placement, and takes a shaft on its opposite face.
Drive that shaft from any Create source.

```
[ multiblock ] - [ Stress Input Hatch ] - [ Kinetic Coupler ] === shaft === Create network
```

The coupler is a real member of the Create network, so the load is real. While the multiblock is
running a recipe that asks for SU, the coupler applies exactly that many stress units, and the
network can be overstressed by it like any other machine. When the multiblock is idle it applies
nothing.

### The stress recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:stress", "su": 512.0, "rpm": 64 }
  ]
}
```

| Field | Meaning |
|---|---|
| `su` | Stress units the recipe draws while it runs. Decimals allowed. |
| `rpm` | Optional minimum rotation speed. Defaults to any speed above zero. |

The cost is flat: 512 SU is 512 SU whether the shaft turns at 16 RPM or 256 RPM. Speed only matters
if the recipe sets an `rpm` minimum.

### How a stress craft behaves

- The multiblock must be formed, the hatch must have a coupler attached, and the shaft must turn.
- A craft starts only when the network has enough spare capacity for the whole cost.
- The condition is re-checked every tick, so losing speed or capacity mid-craft stops the machine.
  The load drops with it, which lets the network recover.
- An overstressed network reports zero speed, which also stops the machine.
- With several stress hatches in one multiblock, the cost is split evenly between them.

## Compressed air

Requires PneumaticCraft: Repressurized. Without it, none of this registers and `miei:pressure`
recipes never run.

### Air hatches

| Hatch | Item ID | Pressure tier |
|---|---|---|
| EV Air Input Hatch | `modern_industrialization:ev_air_input_hatch` | Tier 1 (5 bar danger) |
| IV Air Input Hatch | `modern_industrialization:iv_air_input_hatch` | Tier 1.5 (10 bar danger) |
| SV Air Input Hatch | `modern_industrialization:superconductor_air_input_hatch` | Tier 2 (20 bar danger) |

Each holds 10000 mL of air. The MI casing tier picks the PneumaticCraft pressure tier, so a
higher-tier hatch tolerates more pressure before it starts venting.

The hatch is a normal PneumaticCraft air handler on all six faces. Run a pressure tube to it from a
compressor and it fills like any other machine and shows on a pressure gauge. No extra block is
needed.

Each air hatch has a single slot that accepts a PneumaticCraft **Security Upgrade**, and what
happens when the hatch goes over its danger pressure depends on whether one is fitted:

- **With a Security Upgrade**, the hatch vents air from its top face, exactly like a protected
  PneumaticCraft machine. It never breaks.
- **Without one**, the hatch ruptures. That produces a bang and explosion particles but harms
  nothing: no blocks are broken, no entities are hurt, and no fire is started. The hatch itself pops
  off into item form so you can put it straight back.

The rupture is deliberately deterministic. It fires as soon as the danger pressure is passed, rather
than rolling PneumaticCraft's random chance somewhere between danger and critical pressure, because
MIEI has to act first to keep PneumaticCraft's own block-destroying explosion from going off.

### The pressure recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:pressure", "air": 5000, "pressure": 4.5 }
  ]
}
```

| Field | Meaning |
|---|---|
| `air` | Air in mL taken once per craft. |
| `pressure` | Optional minimum pressure in bar. Defaults to 0, so any hatch with air qualifies. |

Air is consumed once when the craft starts, the same way matter is, rather than drained per tick.

### How an air craft behaves

- The multiblock must be formed and hold at least one air hatch.
- Only hatches at or above the required pressure count toward the recipe, both for the check and for
  the withdrawal. A hatch sitting below the threshold is skipped entirely.
- Air is pooled across every qualifying hatch in the structure.
- If the air is not there, the craft does not start and nothing is taken.

## Heat

Requires PneumaticCraft: Repressurized. Without it, none of this registers and `miei:heat` recipes
never run.

### Heat hatches

| Hatch | Item ID | Thermal capacity |
|---|---|---|
| EV Heat Hatch | `modern_industrialization:ev_heat_hatch` | 10 |
| IV Heat Hatch | `modern_industrialization:iv_heat_hatch` | 25 |
| SV Heat Hatch | `modern_industrialization:superconductor_heat_hatch` | 50 |

The hatch is a normal PneumaticCraft heat exchanger connected on all six faces. Heat pipes, heat
sinks, vortex tubes, a lava block underneath, anything that moves heat in PneumaticCraft will move
heat into or out of it. A higher thermal capacity means the hatch holds more heat, so a recipe that
draws heat cools a higher-tier hatch less.

### The heat recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:heat", "min": 473, "max": 673, "heat": 2000.0 }
  ]
}
```

| Field | Meaning |
|---|---|
| `min` | Minimum temperature in Kelvin. Optional, defaults to 0. |
| `max` | Maximum temperature in Kelvin. Optional, defaults to unbounded. |
| `heat` | Heat energy drawn once per craft, cooling the hatch. Optional, defaults to 0. |

Ambient temperature is around 300 K, so a recipe wanting a hot machine should set `min` above that
and a recipe wanting a cold one should set `max` below it. Leaving `heat` out makes the temperature
a pure requirement that costs nothing to hold.

### How a heat craft behaves

- At least one heat hatch in the structure must sit inside the temperature range.
- Only in-range hatches are used, and the heat draw is split evenly between them.
- Drawing heat cools the hatch, so an under-supplied machine falls out of range and stops until its
  heat source catches up.

## Chemicals

Requires Mekanism. Without it, none of this registers and `miei:chemical` recipes never run.

Mekanism 10.7 merged gases, infusions, pigments and slurries into a single chemical type, so one
pair of hatches covers all four. Anything a Mekanism pressurised tube can carry works here.

### Chemical hatches

| Hatch | Item ID | Capacity |
|---|---|---|
| EV Chemical Input Hatch | `modern_industrialization:ev_chemical_input_hatch` | 64000 mB |
| EV Chemical Output Hatch | `modern_industrialization:ev_chemical_output_hatch` | 64000 mB |
| IV Chemical Input Hatch | `modern_industrialization:iv_chemical_input_hatch` | 256000 mB |
| IV Chemical Output Hatch | `modern_industrialization:iv_chemical_output_hatch` | 256000 mB |
| SV Chemical Input Hatch | `modern_industrialization:superconductor_chemical_input_hatch` | 1024000 mB |
| SV Chemical Output Hatch | `modern_industrialization:superconductor_chemical_output_hatch` | 1024000 mB |

Each hatch holds one chemical at a time. Input hatches accept chemicals from a Mekanism network and
refuse extraction; output hatches do the opposite, so a tube can never drain a machine's input
buffer. Connect them with pressurised tubes as usual.

### The chemical recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:chemical", "chemical": "mekanism:hydrogen", "amount": 500 },
    { "type": "miei:chemical", "chemical": "mekanism:oxygen", "amount": 250, "output": true }
  ]
}
```

| Field | Meaning |
|---|---|
| `chemical` | Registry id of the chemical, for example `mekanism:hydrogen`. |
| `amount` | Amount in mB. |
| `output` | `false` (default) consumes from input hatches, `true` produces into output hatches. |

A chemical id that is not registered gives a condition that can never be satisfied, rather than a
crash, so check the id first if a machine refuses to run.

### How a chemical craft behaves

- Amounts are pooled across every matching hatch in the structure.
- A craft starts only when the full input amount is present and there is room for the full output.
- Nothing is taken if the craft cannot start.

## Chronons

Requires Tempad. Without it, none of this registers and `miei:chronon` recipes never run.

### Chronon Input Hatch

| Hatch | Item ID | Buffer |
|---|---|---|
| Chronon Input Hatch | `modern_industrialization:chronon_input_hatch` | 10000 chronons |

The hatch has no cable or pipe. Place a Tempad Metronome anywhere within 8 blocks of it and the
hatch draws from that Metronome at up to 500 chronons per tick until its buffer is full. The
Metronome must have finished booting and must have an owner, since the chronons come from the
owner's Metronome pool. If several Metronomes are in range the nearest one is used.

The hatch remembers which Metronome it found and keeps using it. If that Metronome is broken or
moved, the hatch notices on its next tick and searches again. When there is no Metronome in range
the search backs off: it retries after 1 second, then 2, 4 and 8, and from then on every 10
seconds. Placing a Metronome next to a hatch that has been waiting a while can therefore take up to
10 seconds to register. Jade shows the countdown to the next search.

The hatch also exposes Tempad's chronon capability on every side, accepting chronons and refusing
extraction, so anything that can push chronons into a block can fill it directly.

### Charging by hand

Sneak and right-click the hatch with any chronon battery to empty it into the buffer on the spot.
This works with anything Tempad lets you draw chronons out of, including the Chronon Cell, a
Tempad and a Chronometer, so a hatch can be topped up without a Metronome nearby at all. The
transfer stops when the buffer is full, and the amount moved is shown above your hotbar.

### The chronon recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:chronon", "amount": 2000 }
  ]
}
```

| Field | Meaning |
|---|---|
| `amount` | Chronons consumed when the craft starts. |

### How a chronon craft behaves

- Amounts are pooled across every Chronon Input Hatch in the structure.
- A craft starts only when the buffers together hold the full amount, and the chronons are taken
  from them at that moment. Nothing is drawn from the Metronome as part of the craft itself; the
  hatch refills on its own afterwards.
- Nothing is taken if the craft cannot start.

## Source

Requires Ars Nouveau. Without it, none of this registers and `miei:source` recipes never run.

### Source Input Hatches

| Hatch | Item ID | Capacity |
|---|---|---|
| LV Source Input Hatch | `modern_industrialization:lv_source_input_hatch` | 10000 |
| MV Source Input Hatch | `modern_industrialization:mv_source_input_hatch` | 25000 |
| HV Source Input Hatch | `modern_industrialization:hv_source_input_hatch` | 50000 |
| EV Source Input Hatch | `modern_industrialization:ev_source_input_hatch` | 100000 |
| IV Source Input Hatch | `modern_industrialization:iv_source_input_hatch` | 250000 |
| SV Source Input Hatch | `modern_industrialization:superconductor_source_input_hatch` | 1000000 |

The IV hatch only exists in packs that add an IV casing tier.

The hatch exposes Ars Nouveau's source capability on every side, accepting source and refusing
extraction. Anything that pushes source into a block through that capability can fill it:

- **Relays.** Link a relay to the hatch with a Dominion Wand, the same way you would link one to a
  Source Jar.
- **AE2, with [Ars Énergistique](https://modrinth.com/mod/ars-energistique).** Point an export bus
  at the hatch with source in its filter, and source stored in the ME network is pushed straight in.

### The source recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "ars_nouveau:source_gem", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:source", "amount": 1000 }
  ]
}
```

| Field | Meaning |
|---|---|
| `amount` | Source consumed when the craft starts. |

### How a source craft behaves

- Amounts are pooled across every Source Input Hatch in the structure.
- A craft starts only when the hatches together hold the full amount, and the source is taken at that
  moment.
- Nothing is taken if the craft cannot start.

## Lasers

Requires Nautec. Without it, none of this registers and `miei:laser` recipes never run.

### Nautechnical Hatch

| Hatch | Item ID |
|---|---|
| Nautechnical Hatch | `modern_industrialization:nautechnical_hatch` |

Aim a Nautec laser at any face of the hatch, the same way you would aim one at a Nautec machine. The
hatch accepts beams on every side and stores nothing: it only reports what is reaching it right now.
Several beams into one hatch add their AP together, and their purity is merged the way Nautec merges
beams that meet. If a beam stops, the hatch reads zero within two ticks.

### The laser recipe condition

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:prismarine_crystals", "amount": 4 }],
  "process_conditions": [
    { "type": "miei:laser", "power": 500, "purity": 0.75 }
  ]
}
```

| Field | Meaning |
|---|---|
| `power` | Laser strength in AP the multiblock must be receiving. At least 1. |
| `purity` | Optional minimum beam purity, from 0 to 1. Defaults to 0. |

### How a laser craft behaves

- Power is added up across every Nautechnical Hatch in the structure, and purity is merged across
  them the same way.
- A craft starts only while the beams meet both requirements.
- The requirement is checked every tick for the whole duration. Whenever the beams fall short, the
  craft pauses and keeps its progress, then carries on once they recover.
- Nothing is consumed, and a stronger beam does not make the craft faster.
- In a parallel crafter such as the Multi Processing Array, each copy needs its own `power`. A
  machine receiving 1,500 AP runs at most three copies of a 500 AP recipe at once, and while it
  runs, the beams must keep supplying `power` times the number of copies. Purity does not scale.

## Recipes without energy

Every Modern Industrialization recipe has to declare `eu` and `duration`, and a machine will not
even look for a recipe until it can draw 1 EU. That makes it hard to build a multiblock that is
driven purely by matter, stress, air, heat, chemicals, chronons, source or lasers. The `miei:no_energy` condition
removes the energy requirement from a single recipe.

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:no_energy" },
    { "type": "miei:stress", "su": 512.0, "rpm": 64 }
  ]
}
```

The condition has no fields. It works in any MI crafting machine, electric or steam, single block or
multiblock, and does not need a hatch of any kind.

The same idea with chronons as the only power source. This assembler needs a Chronon Input Hatch
in its shape and no energy hatch at all; the 2000 chronons are taken from the hatch buffers when
the craft starts, and the machine then works through the 32 EU/t for 200 ticks without drawing
anything:

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_inputs": [{ "item": "minecraft:iron_ingot", "amount": 1 }],
  "item_outputs": [{ "item": "tempad:time_steel", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:no_energy" },
    { "type": "miei:chronon", "amount": 2000 }
  ]
}
```

### How an energy-free craft behaves

- The recipe draws no EU and no steam at any point. A structure with no energy hatch, or a machine
  whose buffer is empty, runs it at full speed.
- `eu` and `duration` are still required by Modern Industrialization, and `eu` still sets the
  crafting rate: the machine works through `eu` per tick for `duration` ticks, clamped to its
  tier's base and maximum. That range is 8 to 128 for electric multiblocks, 8 to 32 for LV
  machines, a fixed 2 or 4 for bronze and steel steam machines, and higher with overclock upgrades.
  Inside the range the recipe takes exactly `duration` ticks; below it the machine runs faster,
  above it slower, exactly as a powered machine would.
- Lubricant overclocking and overdrive apply as normal.
- A machine with no energy only considers recipes carrying `miei:no_energy`. It will not lock its
  inputs into a normal recipe it could never finish.
- A machine that does have energy behaves as before for every other recipe.
- Machines with no energy only start searching for recipes once a `miei:no_energy` recipe exists for
  their machine type. Packs that ship none pay nothing.

## Recipes gated by stage

The `miei:stage` condition locks a recipe behind a player stage. The machine runs the recipe only
while the player who placed it has that stage. Like `miei:no_energy` it needs no hatch and no other
mod.

```json
{
  "type": "modern_industrialization:assembler",
  "eu": 32,
  "duration": 200,
  "item_inputs": [{ "item": "minecraft:iron_ingot", "amount": 1 }],
  "item_outputs": [{ "item": "minecraft:diamond", "amount": 1 }],
  "process_conditions": [
    { "type": "miei:stage", "stage": "ftb_era_modern" }
  ]
}
```

Stages are read from the player's entity tags. That is where KubeJS stores its stages unless Game
Stages is installed, so stages granted by KubeJS scripts, FTB Quests stage rewards and `/tag`
all count.

### Naming a stage

Stage ids are usually internal names that players should not have to read. In JEI, REI or EMI a
stage-gated recipe shows a knowledge book icon along its top edge. Hovering it shows the condition
as "Requires stage" followed by the translation of the lang key `miei.stage.<stage>`, where
`<stage>` is the id exactly as written in the recipe. When that key has no translation, the raw
stage id is shown instead. The Jade line described below uses the same name.

MIEI ships no stage names, since stages belong to the pack. Add one entry per stage to
`kubejs/assets/miei/lang/en_us.json`, or to the same path in any resource pack:

```json
{
  "miei.stage.ftb_era_modern": "Modern Era"
}
```

With that entry the recipe reads "Requires stage Modern Era". Lang files from every namespace are
merged, so the entries can also go in a lang file the pack already has. Other languages work the
same way, with a file per locale such as `de_de.json`. Names are client side: press F3 + T to
reload them without restarting.

### How a stage-gated craft behaves

- The owner is the player who placed the machine, or the controller of a multiblock. Machines placed
  by anything other than a player have no owner and never run stage-gated recipes.
- The stage is checked every tick while the recipe runs, so removing it stops the machine.
- When the owner is offline the machine uses the stages they had when they logged out. After a
  server restart, an offline owner's stages are read from their saved player data.
- Stack several `miei:stage` conditions to require more than one stage.
- Modern Industrialization's machine screen never says why a recipe is not running. With Jade
  installed, looking at the machine shows "Waiting for stage" in red for each stage it is missing,
  but only when that stage is the thing holding it back: the inputs are present and there is room
  for the outputs. A machine with no owner also says so. The line clears once the stage is granted
  and the recipe starts, or about 6 seconds after the recipe stops being a candidate.

## JEI and Jade

Both are optional and need no configuration.

**JEI.** Recipe conditions already show in Modern Industrialization's own recipe view, because MI
renders every process condition it is given, MIEI's included. On top of that, each hatch and the
Kinetic Coupler carry an information page: look one up in JEI and press the usage key to read what
it does and how to connect it.

**Jade.** Looking at a hatch shows its live contents in the tooltip:

| Hatch | Shown |
|---|---|
| Matter | Matter type, stored amount and capacity |
| Stress | Shaft speed in RPM and the stress units currently applied, or a note if no coupler is attached |
| Air | Air in mL, current and danger pressure, and whether a Security Upgrade is fitted |
| Heat | Temperature in Kelvin |
| Chemical | Chemical name, stored amount and capacity |
| Chronon | Stored chronons and capacity, the linked Metronome's position, or a note that none is in range and when the next search happens |
| Source | Stored source and capacity |
| Nautechnical | AP and purity of the beams reaching it, or a note that no beam is |

Looking at a machine whose stage-gated recipe is blocked shows which stages it is waiting for. See
[Recipes gated by stage](#recipes-gated-by-stage).

Hatch contents are server-side only, so Jade requests them from the server as you look at the block.
Each hatch family is a separate Jade provider, so they can be toggled individually in Jade's config.
## A note on block IDs and mining

The hatches are registered through Modern Industrialization's own machine helper, so they carry the
`modern_industrialization` namespace rather than `miei`. The Kinetic Coupler is registered by MIEI
directly and is `miei:kinetic_coupler`.

Every block MIEI adds is pickaxe-mineable at stone tier and drops itself, matching Modern
Industrialization's own machines.

## Requirements

| | | |
|---|---|---|
| Minecraft | 1.21.1 | |
| Loader | NeoForge 21.1.219 or newer | |
| Modern Industrialization | 2.5.6 or newer | required |
| Replication | 1.21-1.2.7 | optional, needed for matter |
| Create | 6.0 or newer | optional, needed for stress units |
| PneumaticCraft: Repressurized | 8.2.23 or newer | optional, needed for compressed air and heat |
| Mekanism | 10.7.19 or newer | optional, needed for chemicals |
| Tempad | 3.0.4 or newer | optional, needed for chronons |
| Ars Nouveau | 5.13 or newer | optional, needed for source |
| Nautec | 2.0.19 or newer | optional, needed for lasers |
| Ars Énergistique | 2.1 or newer | optional, lets an AE2 export bus fill source hatches |
| JEI | 19.36 or newer | optional, adds hatch information pages |
| Jade | 15.10 or newer | optional, adds hatch contents to the tooltip |

Modern Industrialization is required on both the client and the server. The others are each optional
and independent: whichever one is missing, its hatches are not registered and its recipe conditions
can never be satisfied, while the rest keep working. With none of them installed MIEI adds nothing
at all.

## License

MIT. See [LICENSE.md](LICENSE.md).
