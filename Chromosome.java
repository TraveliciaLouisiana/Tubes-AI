import java.util.Random;

/**
 * Class Chromosome: untuk merepresentasikan semua kromosom pada Genetic Algorithm.
 * Setiap kromosom harus dapat diubah menjadi indeks sel kosong, divalidasi,
 * disalin, di-crossover, dan dimutasi.
 */
public interface Chromosome {
    // menghasilkan indeks sel kosong yang dipilih sebagai lokasi Fire Station
    int[] decode();

    // memeriksa apakah kromosom memiliki tepat p indeks yang valid dan berbeda
    boolean isValid();

    // membuat salinan kromosom agar parent tidak ikut berubah saat reproduksi 
    Chromosome copy();

    // menghasilkan anak dari dua parent menggunakan operator crossover terpilih
    Chromosome crossover(Chromosome other, GAConfig.CrossoverType type, Random random);

    // mengubah kromosom menggunakan operator mutasi terpilih
    void mutate(GAConfig.MutationType type, Random random);
}
