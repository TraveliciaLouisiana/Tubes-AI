# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

University assignment ("Tubes AI", Tugas 1): a Genetic Algorithm in plain Java (no build tool, no dependencies, no tests) that places `p` fire stations on empty cells of an `m x n` grid map so that the average distance from each house to its nearest fire station is as small as possible. Code comments and error messages are in Indonesian. Keep that style when editing existing files.

Work is split across feature branches (`fitur/bagian-1` … `fitur/bagian-4`) that merge into `main`.

## Build / run

There is no build system. All sources are under `src/` in feature packages. Compile everything from the repo root:

```sh
# Bash
javac -d out $(find src -name '*.java')
# PowerShell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

To run one GA on one map, use the entry point `ga.Main`. Every argument is `key=value`, and only `map` is required:

```sh
java -cp out ga.Main map=peta.txt rep=INDEX_LIST sel=TOURNAMENT cx=ONE_POINT mut=MOVE_ONE \
     pop=100 pc=0.8 pm=0.05 elit=0.2 stag=100 time=60000 k=3 seed=1 out=hasil.txt
```

- `budget` defaults to the Table 1 value for the map size: up to 10x10 cells → 10k, 20x20 → 50k, 40x40 → 200k, larger → 500k.
- stdout receives the required `p+1`-line output. A `seed/evaluasi/waktu` summary line goes to stderr.
- For quick checks, the 5x5 map from Figure 2 with `p=1` should give `2.00000`, and a house walled in by trees should give the penalty `m*n`.

`experiment.AdapterGA` still has no implementation that wraps `ga.GeneticAlgorithmEngine`. Batch experiments need one.

### Package layout (`src/`)

Packages are split by feature and match the team roles (see "Team plan"). Put a new class in the package for its feature, and start every file with `package <pkg>;`.

| Package | Contents | Owner |
|---|---|---|
| `map` | `Grid`, `MapReader`, `DistanceMatrix` (BFS), `CostCalculator`, `OutputWriter` | Orang 1 |
| `chromosome` | the `Chromosome` interface, `BitStringChromosome`, `IndexListChromosome`, `Repair` | Orang 2 |
| `ga` | `GAConfig`, `Individual`, `Fitness`, `GeneticAlgorithmEngine` | Orang 2 |
| `operator.selection` / `.crossover` / `.mutation` | the operator interfaces and their implementations | Orang 3 |
| `experiment` | `AdapterGA`, `Eksperimen`, `JalankanEksperimen`, `PenulisCsv`, `SeedGenerator`, … | Orang 4 |

`chromosome` and `ga` import from each other, because the chromosome classes use the `GAConfig.CrossoverType` and `GAConfig.MutationType` enums. Java allows that.

Things the team still has to settle:
- **Duplicate code.** `Fitness.calculateCost` (in `ga`) and `CostCalculator.cost` (in `map`) compute the same thing, and the team needs to pick one. The operator classes in `operator.*` aren't wired in yet either. `GeneticAlgorithmEngine` has its own tournament and roulette-wheel selection, and crossover and mutation are methods on each `Chromosome` class.
- **Unfinished operators.** The operators have unfinished stubs, and `LinearRankSelection` has logic bugs: integer division in `(index-1)/(populationNum-1)`, an off-by-one in `index-1`, and it sorts the caller's list in place.

`Tubes-AI/` is an untracked nested git clone that holds an older copy of `Grid.java` and `MapReader.java`. It's outside `src/`, so it isn't part of the build.

## Architecture

Data flow: `MapReader.read(path)` → `Grid` → `new DistanceMatrix(grid).dist` → `GeneticAlgorithmEngine(random, grid, dist, GAConfig).run()` → best `Individual` → `OutputWriter.write(...)`.

- **Map file format** (`MapReader`): first line `m n p`, then `m` lines of `n` characters, each `H` (house), `T` (tree) or `.` (empty). Blank lines are skipped.
- **`Grid`** turns the map into flat indices. Houses are numbered `0..numHouses-1` and empty cells `0..numEmpty-1`, in row-major order. `cellId[r][c]` maps a position to its empty-cell index (`-1` if the cell isn't empty). Internally everything is 0-based `[row][col]`. The assignment's coordinates are 1-based `(x=col, y=row)`, so convert them with `idFromXY`. `penalty = m*n`.
- **`distanceMatrix[house][emptyCellIndex]`**: the shortest-path distance, built once by `map.DistanceMatrix` with one BFS per house. Unreachable cells hold `penalty`. The engine checks that the matrix is exactly `numHouses x numEmpty`.
- **`Fitness`**: cost is the mean distance from each house to its nearest selected cell. A house that can't reach any station adds `penalty` instead. Fitness is `1/(cost+1)`.
- **`Chromosome`** interface (`decode`, `isValid`, `copy`, `crossover`, `mutate`). It has two representations, chosen by `GAConfig.RepresentationType`:
  - `IndexListChromosome` stores exactly `p` sorted, distinct empty-cell indices.
  - `BitStringChromosome` stores a bit array of length `numEmpty` with exactly `p` ones.
  A chromosome is valid when `decode()` returns exactly `p` distinct, in-range indices. After crossover or mutation, the engine passes an invalid child to `Repair.repairIndices` / `Repair.repairBits` and rebuilds it with `fromGenes`.
- **`Individual`** computes cost and fitness once, in its constructor. Every `new Individual(...)` counts as one fitness evaluation toward `evaluationBudget`. `copy()` is used for elitism and does not re-evaluate. `compareTo` sorts by fitness in **descending** order, so `population.get(0)` is the best individual.
- **`GeneticAlgorithmEngine.run()`**: each generation copies the top `floor(elitism * popSize)` individuals, then creates children (select → crossover with probability `pc` → mutate with probability `pm` → repair) until the population is full. It stops when the evaluation budget runs out, the time limit is reached, or the best cost hasn't improved for `stagnationLimit` generations. A value of `0` turns off the time or stagnation check.
- **`GAConfig`** holds immutable, validated hyperparameters and the choice of operators. `GAConfig.defaultConfig(rep)` is the baseline for the assignment's first experiment: pop 100, budget 10000, pc 0.8, pm 0.05, elitism 20%, stagnation 100, time 60s, tournament k=3, one-point crossover, MOVE_ONE mutation. The budget is hardcoded to the Tiny value, so pass the right budget for each map size (see the table below).

## Assignment spec (AI 61 – Tugas 1 – Fire Stations, v2, 8 Nov 2026)

**Problem.** Place exactly `p` fire stations (FS) on distinct empty cells of an `m x n` grid. The goal is to minimise `cost(S) = (1/|H|) * Σ_h min_{s∈S} d(h,s)`.
- Movement is 4-directional, one step per move. Paths may only pass through empty cells, and a cell holding an FS still counts as empty. Houses and trees are obstacles.
- The distance is the length of the path from the house to the FS. A house directly next to an FS has distance 1. A house that can't reach any FS gets the penalty `P = m*n`.
- Distances from houses to empty cells don't depend on where the FS are placed. Run BFS once per house at startup and store a `[houses][emptyCells]` matrix.
- Reference test from Figure 1 (10x7 grid, 5 houses, 6 trees, 2 FS): the distances are 8, 3, 5, 3, 2, so the average is 4.2.

**Program output** (to the screen or a file), `p+1` lines:
- Line 1: `p f`, where `f` has exactly 5 decimal places.
- Then `p` lines of `x y`, using 1-based coordinates with (1,1) at the **top-left**. The Figure 3 caption says "bottom-left". That's a known inconsistency in the spec, and the team plans to ask the lecturer about it.

GA hyperparameters come in through program args or a separate config file, never the map file.

**Code requirements.** The code must be modular, because later assignments will reuse it. The lecturer also requires a comment on (almost) every line, so add per-line Indonesian comments to any code you write.

**Experiments.** Map sizes:

| Size | Grid | Eval budget | p |
|---|---|---|---|
| Tiny | 10x10 | 10,000 | 3 |
| Small | 20x20 | 50,000 | 10 |
| Normal | 40x40 | 200,000 | 24 |
| Large | 80x80 | 500,000 | 40 |

Map types are random (acak), cluster, and maze (labirin).
- **Tahap 1:** 2 representations x 2 selection x 2 crossover x 2 mutation = 16 models, each run on tiny/small/normal with 4 maps per size (2 acak, 1 cluster, 1 labirin) and 30 seeds. That's 5760 runs. Fixed settings: pop 100, gen = budget/pop, pc 0.8, pm 0.05, elitism 20%. A run stops when the budget runs out, when progress stagnates, or when the time limit is reached.
- **Tahap 2:** take at least the 3 best models from Tahap 1 (the group chooses the ranking criteria), and use **new** maps with the same mix.
  - (A) Tune one hyperparameter at a time, in the order pop → pc → pm → elitism, trying 5 values each and carrying each winner forward. That's 3 x (5 x 30) x 4 x 3 = 5400 runs.
  - (B) Run the full grid over the 2 best pop values x the 2 best pc values, with the best pm and elitism. That's 4320 runs.
- **Pengujian:** run the final best model on 6 large maps (2 of each type) with at least 10 seeds each, for 60 runs.
- Save every result to CSV, with enough columns to identify exactly which experiment each row belongs to.
- **Seeds:** use `new Random(npm_ketua)`, then `seed[i] = rand.nextInt((1<<16) + 1)` for 30 seeds. Use the **same** 30 seeds for every model. The spec's sample loop has a bug (`for (int j...; i++)`), so use a single loop variable.

**Report (XX.pdf).** No cover page and no table of contents. It's report-style, with 4 parts:
1. The conceptual model behind every aspect of the GA.
2. A short explanation of the code: where each part came from (written by the group, taken from elsewhere, or generated by AI), and what was changed and why.
3. The experiment plan and results, with a chart and short analysis for each experiment, ending with an overall conclusion.
4. A very detailed work split per member in %: which document sections or pages, which experiments, and which code parts.

Format if using Word: body text in Aptos Narrow 11, headings in Aptos Semibold 14 or larger, A4 paper, moderate margins, justified paragraphs.

**Deliverable `XX.zip`:** contains
- all `*.java` files
- instructions for compiling, running and doing the experiments
- all CSVs
- `XX.pdf`
- `AIXX.pdf` (AI-use statement; its format is still pending)
- `PromptXX.pdf` (**every** AI prompt and response, so keep logging them from the start)
- supporting files such as batch scripts

**Grading:** code 15, document 25, experiments 50, extras 10. Extras include more operators, custom maps, and additional experiments. Each member's grade is scaled by their share of the work.

## Team plan (work distribution)

**Shared contract**, which every part must follow:
- Empty cells are numbered `0..E-1`. Every chromosome ultimately decodes to "a list of `p` empty-cell numbers that hold an FS".
- The distance matrix is `int[][] dist[numHouses][E]`. Unreachable cells hold penalty `P`. `map.DistanceMatrix` does this. `ga.Fitness` also accepts negative values as "unreachable" and then adds `grid.penalty`, so both conventions give the same cost.
- Cost is computed as `double cost(int[] fsCells)`.
- `Random` is always passed in from the caller and never created inside a class, so runs can be reproduced from a seed.
- Agree on the CSV columns up front, for example: `tahap, model, ukuran, tipe_peta, nama_peta, pop, pc, pm, elitism, seed, cost, waktu`.

**Roles.** Branch `fitur/bagian-N` belongs to Orang N. They were merged and moved into `src/` packages on `fitur/restruktur-package`.
1. **Orang 1 (Grid, BFS, cost):**
   - Code: `Grid`, `MapReader`, `DistanceMatrix` (BFS), `CostCalculator`, and `OutputWriter` (p+1 lines, `%.5f`, 1-based top-left `x y`).
   - Required tests: a small map that can be checked by hand, the Figure 1 example (4.2), a house surrounded by trees to check the penalty, and a house adjacent to an FS (distance 1).
   - Document: the problem concept, grid and BFS, and the small-map tests.
2. **Orang 2 (chromosome, fitness, GA engine):**
   - Code: `Chromosome` plus `BitStringChromosome` and `IndexListChromosome`, `Individual`, `Fitness` (roulette needs positive fitness), `Repair`, `GAConfig`, and the GA loop (elitism 20%, evaluation counting, stagnation and time stopping).
   - Document: GA concept, the choice of representations, fitness design, and how invalid chromosomes are handled.
3. **Orang 3 (genetic operators):**
   - Code: the `SelectionOperator`, `CrossoverOperator` and `MutationOperator` interfaces, plus 2 implementations of each:
     - selection: roulette/rank and tournament
     - crossover: one-point and uniform, both with repair so the child keeps exactly `p` FS
     - mutation: move one FS to another empty cell, and swap/shift
   - Still open: per-representation operators vs. generic operators through `decode()`.
   - Document: the reasoning and analysis for each operator.
4. **Orang 4 (test maps and experiments):**
   - Code: `MapGenerator` (acak, cluster, labirin, written in the input file format), `SeedGenerator`, `ModelFactory` (the 16 combinations), `ExperimentRunner`, `CsvWriter`, and batch scripts. Runs should be able to resume after an interruption.
   - Maps: 30 in total. 12 for Tahap 1, 12 new ones for Tahap 2, and 6 large ones for Pengujian.
   - Document: test maps, methodology, seeds.
5. **Orang 5 (results and final assembly):**
   - Code: a CSV summary script (mean and std of cost per model, size and map type; picks the top 3 models; draws charts), the README with compile/run/experiment instructions, and the `XX.zip` package.
   - Keeps the shared log of AI prompts.
   - Document: introduction, results, analysis, conclusion, and merging everything into the final PDF.

**Dependency order.**
1. Orang 1 and Orang 4 can start right away.
2. Orang 2 and Orang 3 start once the interfaces are agreed.
3. Orang 4 integrates everything through `ModelFactory` and `ExperimentRunner`, and the whole team tests on small maps until all 16 models run.
4. Then Tahap 1 → Tahap 2 (A, then B) → Pengujian. Start the long runs early.
5. Orang 5 processes the CSVs and the team writes the report.

Only work on the extra-credit items once the core work is safe.
