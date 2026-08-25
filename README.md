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

They all work the same way: a hatch in the multiblock shape carries the resource, and a recipe
condition declares what the recipe needs from it. Every integration is optional and independent, so
install whichever ones you want. Modern Industrialization is the only hard requirement.

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

Each hatch holds up to 8000 of a single matter type.

Input hatches accept matter from a Replication network and hand it to the multiblock. Output hatches
collect matter produced by a recipe and let a Replication network pull it out. The direction is
enforced: a network cannot drain an input hatch, and it cannot push matter into an output hatch.

Connect them to Replication with the same matter pipes and network parts you already use. Hatches
expose Replication's matter handler capability on every side.

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
| JEI | 19.36 or newer | optional, adds hatch information pages |
| Jade | 15.10 or newer | optional, adds hatch contents to the tooltip |

Modern Industrialization is required on both the client and the server. The others are each optional
and independent: whichever one is missing, its hatches are not registered and its recipe conditions
can never be satisfied, while the rest keep working. With none of them installed MIEI adds nothing
at all.

## License

MIT. See [LICENSE.md](LICENSE.md).
