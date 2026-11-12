package ga; // class ini berada di package ga

/* class GAConfig untuk menyimpan semua hyperparameter dan pilihan operator algoritma genetik */
public class GAConfig {

    // pilihan jenis representasi kromosom
    public enum RepresentationType { INDEX_LIST, BIT_STRING }
    // pilihan jenis seleksi orang tua
    public enum SelectionType { TOURNAMENT, ROULETTE_WHEEL }
    // pilihan jenis crossover atau pindah silang
    public enum CrossoverType { ONE_POINT, UNIFORM }
    // pilihan jenis mutasi
    public enum MutationType { MOVE_ONE, MOVE_TWO }

    // variabel untuk menyimpan ukuran populasi
    private final int populationSize;
    // variabel untuk menyimpan batas maksimal evaluasi fitness
    private final int evaluationBudget;
    // variabel untuk menyimpan probabilitas crossover
    private final double crossoverRate;
    // variabel untuk menyimpan probabilitas mutasi
    private final double mutationRate;
    // variabel untuk menyimpan persentase elitisme
    private final double elitismPercentage;
    // variabel untuk menyimpan batas generasi stagnan
    private final int stagnationLimit;
    // variabel untuk menyimpan batas waktu eksekusi dalam milidetik
    private final long timeLimitMillis;
    // variabel untuk menyimpan ukuran peserta turnamen seleksi
    private final int tournamentSize;
    // menyimpan jenis representasi yang dipilih
    private final RepresentationType representationType;
    // menyimpan jenis seleksi yang dipilih
    private final SelectionType selectionType;
    // menyimpan jenis crossover yang dipilih
    private final CrossoverType crossoverType;
    // menyimpan jenis mutasi yang dipilih
    private final MutationType mutationType;

    // constructor untuk membuat objek gaconfig sekaligus memvalidasi semua parameter masukannya
    public GAConfig(int populationSize, int evaluationBudget,
                    double crossoverRate, double mutationRate, double elitismPercentage,
                    int stagnationLimit, long timeLimitMillis, int tournamentSize,
                    RepresentationType representationType, SelectionType selectionType,
                    CrossoverType crossoverType, MutationType mutationType) {
        // cek apakah ukuran populasi atau evaluation budget kurang dari satu
        if (populationSize < 1 || evaluationBudget < 1) {
            throw new IllegalArgumentException("population size dan evaluation budget minimal 1");
        }
        // cek apakah nilai rate atau persentase elitisme berada di luar rentang yang diizinkan
        if (crossoverRate < 0.0 || crossoverRate > 1.0
                || mutationRate < 0.0 || mutationRate > 1.0
                || elitismPercentage < 0.0 || elitismPercentage >= 1.0) {
            throw new IllegalArgumentException("pc dan pm harus 0 sampai 1, elitism harus 0 sampai kurang dari 1");
        }
        // cek apakah batas stagnasi, waktu, atau ukuran turnamen tidak valid
        if (stagnationLimit < 0 || timeLimitMillis < 0 || tournamentSize < 1) {
            throw new IllegalArgumentException("batas stagnasi atau waktu dan tournament size tidak valid");
        }
        // pastikan semua pilihan enum tidak bernilai null
        if (representationType == null || selectionType == null || crossoverType == null || mutationType == null) {
            throw new IllegalArgumentException("pilihan representasi dan operator tidak boleh null");
        }

        // masukkan nilai parameter ke variabel global class
        this.populationSize = populationSize;
        this.evaluationBudget = evaluationBudget;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismPercentage = elitismPercentage;
        this.stagnationLimit = stagnationLimit;
        this.timeLimitMillis = timeLimitMillis;
        this.tournamentSize = tournamentSize;
        this.representationType = representationType;
        this.selectionType = selectionType;
        this.crossoverType = crossoverType;
        this.mutationType = mutationType;
    }

    // method untuk membuat konfigurasi standar sesuai aturan eksperimen tahap pertama di soal tugas
    public static GAConfig defaultConfig(RepresentationType representation) {
        return new GAConfig(100, 10000, 0.8, 0.05, 0.20,
                100, 60000L, 3, representation, SelectionType.TOURNAMENT,
                CrossoverType.ONE_POINT, MutationType.MOVE_ONE);
    }

    // getter untuk mengambil nilai ukuran populasi
    public int getPopulationSize() { return populationSize; }
    // getter untuk mengambil nilai evaluation budget
    public int getEvaluationBudget() { return evaluationBudget; }
    // getter untuk mengambil nilai probabilitas crossover
    public double getCrossoverRate() { return crossoverRate; }
    // getter untuk mengambil nilai probabilitas mutasi
    public double getMutationRate() { return mutationRate; }
    // getter untuk mengambil persentase elitisme
    public double getElitismPercentage() { return elitismPercentage; }
    // getter untuk mengambil batas stagnasi
    public int getStagnationLimit() { return stagnationLimit; }
    // getter untuk mengambil batas waktu milidetik
    public long getTimeLimitMillis() { return timeLimitMillis; }
    // getter untuk mengambil ukuran turnamen
    public int getTournamentSize() { return tournamentSize; }
    // getter untuk mengambil jenis representasi
    public RepresentationType getRepresentationType() { return representationType; }
    // getter untuk mengambil jenis seleksi
    public SelectionType getSelectionType() { return selectionType; }
    // getter untuk mengambil jenis crossover
    public CrossoverType getCrossoverType() { return crossoverType; }
    // getter untuk mengambil jenis mutasi
    public MutationType getMutationType() { return mutationType; }
}
