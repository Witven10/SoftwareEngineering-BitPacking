# SoftwareEngineering-BitPacking

Java implementation of bit packing strategies to compress integer arrays while preserving random access to every element. The repository includes three compression modes, a factory for streamlined creation, correctness checks, and a repeatable benchmarking harness as required for the Software Engineering Project 2025.

## Project Goals
- Reduce transmission size of integer arrays by encoding each element on the minimum required number of bits instead of full 32-bit integers.
- Preserve direct `O(1)` access to the i-th element even after compression.
- Compare multiple packing strategies, including a mode with overflow handling for outliers.
- Provide reliable timing measurements to decide when compression compensates transmission latency.

## Features
- `BitPackingNoOverlap`: packs values into 32-bit words without crossing integer boundaries; ideal when `k` divides 32.
- `BitPackingOverlap`: allows bit fields to span consecutive integers; guarantees compactness even when `k` does not divide 32.
- `BitPackingOverflow`: dynamically selects a smaller width `k'` and stores outliers in an overflow area flagged by a control bit.
- `BitPackingFactory`: centralised creation of the desired strategy from a single string parameter (`"nooverlap"`, `"overlap"`, `"overflow"`).
- `Test` harness: validates `compress`, `decompress`, and `get` for every implementation.
- `BenchmarkRunner`: warm-up aware timing harness returning average nanosecond costs for each primitive operation.
- `ValidationScenarios`: standalone driver that replays representative datasets, validates the three modes, and reports compression profitability per scenario.
- `Main`: demonstration runner that executes validation, benchmarks, and reports compression ratios.

## Repository Layout
- `src/BitPacking.java`: common interface.
- `src/BitPackingNoOverlap.java`, `src/BitPackingOverlap.java`, `src/BitPackingOverflow.java`: strategy implementations.
- `src/BitPackingFactory.java`: factory method pattern.
- `src/BitUtils.java`: reusable bit manipulation helpers (`maskK`, `getBits`, `setBits`, `getK`).
- `src/Test.java`: correctness checks and console reporting.
- `src/BenchmarkRunner.java`: timing protocol with JVM warm-up and averaging.
- `src/Main.java`: entry point combining validation and benchmarking.
- `Stevenson_Jules_report.pdf`: final project report covering requirements, design decisions, benchmarks, and profitability analysis.


## Prerequisites
- JDK 17 or newer (any standard JVM distribution).  
- No external libraries required.

## Build & Run
From the repository root:

```bash
# Compile all sources (outputs class files into out/)
javac -d out src/*.java

# Run validation + benchmark scenario
java -cp out Main
# Run targeted scenarios with per-mode profitability (optional)
java -cp out ValidationScenarios

# The final report is shipped as a PDF: report/Stevenson_Jules_report.pdf
```

The default `Main` workflow:
- derives `k_max` from the sample array via `BitUtils.getK`,
- instantiates each compressor (`nooverlap`, `overlap`, `overflow`) through the factory,
- runs `Test.testCompressionMethod` to confirm round-trip integrity and `get(i)` correctness,
- benchmarks compression, decompression, and random access times, plus calculates compression ratio and space savings,
- prints latency payback information (`T_C + T_D`) to estimate when compression amortises transmission delays,
- and delegates to `ValidationScenarios` for deeper comparisons that reuse `BenchmarkRunner` to display average compression/decompression costs and the minimum transmission speedup required for each dataset and packing mode.

### Customising Experiments
- Edit `src/Main.java` to change the sample array (`input_bench`) or the number of benchmark repetitions (`REPETITIONS`).
- To time a single method in isolation, call `BenchmarkRunner.runBenchmark` with the desired compressor, data set, and method name (`"compress"`, `"decompress"`, `"get"`).
- For targeted validation, create a compressor via the factory and invoke `Test.testCompressionMethod`.

## Benchmark Protocol
- Each benchmark includes 10 warm-up iterations per method to trigger JIT compilation before timing.
- Timing is repeated `REPETITIONS` times and averaged to reduce noise.
- `get` timing is reported per element (total time divided by array length).
- Compression ratios compare the original 32-bit-per-element footprint to the packed representation (`tabCompress.length * 32` bits).

## Handling Overflows
- `BitPackingOverflow` stores metadata (original length and chosen `k'`) in the first 32 bits of the compressed stream.
- Values exceeding `k'` bits are marked with a control bit and the index of their real value in the overflow area.
- Overflow values are appended after the main packed stream; `get(i)` resolves them transparently.
- This strategy mirrors the specification example (e.g., encoding `[1,2,3,1024,4,5,2048]` with 3-bit primaries and 11-bit overflow values).

## License
Academic project for Software Engineering Project 2025. Add a formal license file before distributing beyond the course requirements.

## Author
- Stevenson Jules
