import java.util.Arrays;
import java.util.Random;

/**
 * Classe utilitaire pour valider rapidement les trois modes de bit packing
 * sur plusieurs scenarios, y compris un tableau de 1 000 000 d'entiers.
 *
 * NB : cette classe n'est pas integree au flux principal. Elle permet
 * simplement de rejouer des verifications ponctuelles depuis la ligne
 * de commande : {@code java -cp out ValidationScenarios}.
 */
public final class ValidationScenarios {

    private static final Random RNG = new Random(123456789L);
    private static final int BENCHMARK_REPETITIONS = 3;

    public static void main(String[] args) {
        runScenario("deterministic_small", new int[]{0, 1, 3, 7, 15, 31, 63, 127});
        runScenario("ramp_200", buildRamp(200, 0, 20_000));
        runScenario("mixed_overflow", buildMixedWithPeaks());
        runScenario("random_100k_dense", buildRandomArray(100_000, (1 << 22) - 1));
        runScenario("random_1m_light", buildRandomArray(1_000_000, (1 << 16) - 1));
    }

    private static void runScenario(String label, int[] data) {
        int k = Math.max(1, BitUtils.getK(data));
        System.out.println("\n================Scenario " + label + " =======================\n\n");
        System.out.println("Taille: " + data.length + " | k_max détecté: " + k);

        for (String mode : new String[]{"nooverlap", "overlap", "overflow"}) {
            BitPacking compressor = BitPackingFactory.create(mode, k);
            int[] compressed = compressor.compress(Arrays.copyOf(data, data.length));
            int[] restored = compressor.decompress(new int[data.length]);

            if (!Arrays.equals(restored, data)) {
                throw new AssertionError(label + " - " + mode + " : décompression incorrecte");
            }

            checkRandomAccess(label, mode, compressor, data);
            System.out.printf("////%-10s OK | mots compressés: %d%n", mode, compressed.length);
            reportRentability(label, mode, data, k);
        }
        System.out.println();
    }

    private static void checkRandomAccess(String label, String mode, BitPacking compressor, int[] data) {
        int length = data.length;
        int samples = Math.min(length, 1000);
        for (int i = 0; i < samples; i++) {
            int index = (length <= 1000) ? i : RNG.nextInt(length);
            int expected = data[index];
            int actual = compressor.get(index);
            if (expected != actual) {
                throw new AssertionError(
                    String.format("%s - %s : get(%d) = %d, attendu %d", label, mode, index, actual, expected)
                );
            }
        }
    }

    private static void reportRentability(String label, String mode, int[] data, int k) {
        BitPacking compressBenchmark = BitPackingFactory.create(mode, k);
        BitPacking decompressBenchmark = BitPackingFactory.create(mode, k);

        long tCompress = BenchmarkRunner.runBenchmark(
            compressBenchmark,
            Arrays.copyOf(data, data.length),
            "compress",
            BENCHMARK_REPETITIONS
        );

        long tDecompress = BenchmarkRunner.runBenchmark(
            decompressBenchmark,
            Arrays.copyOf(data, data.length),
            "decompress",
            BENCHMARK_REPETITIONS
        );

        long delta = tCompress + tDecompress;
        System.out.println("TEMPS MOYENS (ns)");
        System.out.printf("T_C : Temps pour la Compression : %d ns\n", tCompress);
        System.out.printf("T_D : Temps pour la Décompression : %d ns\n", tDecompress);

        System.out.printf("Coût de Calcul (T_C + T_D) : %d ns\n", delta);
        System.out.println("C'est le gain de temps de transmission minimum nécessaire pour que la compression soit rentable.\n");

    }

    private static int[] buildRamp(int length, int minValue, int maxValue) {
        int[] array = new int[length];
        double step = (double) (maxValue - minValue) / Math.max(1, length - 1);
        for (int i = 0; i < length; i++) {
            array[i] = minValue + (int) Math.round(step * i);
        }
        return array;
    }

    private static int[] buildMixedWithPeaks() {
        int length = 5000;
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = (i % 5 == 0) ? RNG.nextInt(1 << 6) : RNG.nextInt(1 << 12);
        }
        int[] peaks = {1 << 16, 1 << 18, 1 << 20, (1 << 21) - 1};
        for (int i = 0; i < peaks.length; i++) {
            array[i * 700 + 123] = peaks[i];
        }
        return array;
    }

    private static int[] buildRandomArray(int length, int maxValueInclusive) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = RNG.nextInt(maxValueInclusive + 1);
        }
        return array;
    }
}
