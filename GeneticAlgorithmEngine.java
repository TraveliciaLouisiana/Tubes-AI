import java.util.ArrayList; 
import java.util.Collections; 
import java.util.List; 
import java.util.Random; 

/* class GeneticAlgorithmEngine: menjalankan siklus genetic algorithm untuk masalah penempatan fire station */
public class GeneticAlgorithmEngine { 

    private final Random random; // menyimpan objek random generator
    private final Grid grid; // menyimpan referensi objek peta grid
    private final int[][] distanceMatrix; // menyimpan matriks jarak dari bfs
    private final GAConfig config; // menyimpan konfigurasi hyperparameter
    private final List<Individual> population; // menyimpan daftar individu dalam populasi
    private int evaluationCount; // menghitung jumlah evaluasi fitness yang sudah dilakukan
    private long startTimeMillis; // mencatat waktu mulai eksekusi dalam milidetik

    // membuat mesin ga dengan grid, matriks jarak hasil bfs, random seed, dan konfigurasi
    public GeneticAlgorithmEngine(Random random, Grid grid, int[][] distanceMatrix, GAConfig config) { // constructor utama
        if (random == null || grid == null || distanceMatrix == null || config == null) { // validasi parameter null
            throw new IllegalArgumentException("random, grid, distancematrix, dan config tidak boleh null"); // lempar error jika ada yang null
        } 
        if (grid.p < 0 || grid.p > grid.numEmpty) { // validasi jumlah fire station
            throw new IllegalArgumentException("jumlah fire station tidak sesuai jumlah sel kosong"); // lempar error jika tidak valid
        } 
        this.random = random; // mengisi variabel random
        this.grid = grid; // mengisi variabel grid
        this.distanceMatrix = distanceMatrix; // mengisi variabel distanceMatrix
        this.config = config; // mengisi variabel config
        this.population = new ArrayList<>(); // menginisialisasi list populasi kosong
        this.evaluationCount = 0; // set hitungan evaluasi awal ke nol
        validateDistanceMatrix(); // memanggil method untuk validasi ukuran matriks jarak
    } 

    // memastikan matriks bfs memiliki satu baris per rumah dan satu kolom per sel kosong
    private void validateDistanceMatrix() { // method validasi matriks
        if (distanceMatrix.length != grid.numHouses) { // cek apakah jumlah baris sesuai jumlah rumah
            throw new IllegalArgumentException("jumlah baris distancematrix tidak sesuai jumlah rumah"); // error jika baris salah
        } 
        for (int[] row : distanceMatrix) { // perulangan mengecek tiap baris matriks
            if (row == null || row.length != grid.numEmpty) { // cek apakah baris null atau kolom tidak sesuai sel kosong
                throw new IllegalArgumentException("jumlah kolom distancematrix tidak sesuai jumlah sel kosong"); // error jika kolom salah
            } 
        } 
    } 

    // membuat satu kromosom acak sesuai jenis representasi yang dipilih di gaconfig 
    private Chromosome createRandomChromosome() { // method membuat kromosom acak
        if (config.getRepresentationType() == GAConfig.RepresentationType.BIT_STRING) { // cek apakah pakai bit string
            return new BitStringChromosome(grid, random); // kembalikan kromosom bit string
        } 
        return new IndexListChromosome(grid, random); // jika bukan, kembalikan index list chromosome
    } 

    // membuat populasi awal, setiap individu yang dibuat dihitung sebagai satu evaluasi
    public void initializePopulation() { // method inisialisasi populasi
        population.clear(); // bersihkan populasi lama
        evaluationCount = 0; // reset hitungan evaluasi
        int initialSize = Math.min(config.getPopulationSize(), config.getEvaluationBudget()); // tentukan ukuran awal populasi

        for (int i = 0; i < initialSize; i++) { // perulangan sebanyak ukuran populasi awal
            Chromosome chromosome = createRandomChromosome(); // buat kromosom acak baru
            population.add(new Individual(chromosome, distanceMatrix, grid)); // buat individu dan masukkan ke populasi
            evaluationCount++; // tambah hitungan evaluasi fitness
        } 
        Collections.sort(population); // urutkan populasi berdasarkan fitness terbaik
    } 

    // menjalankan ga hingga budget, stagnasi, atau batas waktu tercapai
    public Individual run() { // method utama menjalankan GA
        startTimeMillis = System.currentTimeMillis(); // catat waktu mulai program
        initializePopulation(); // panggil inisialisasi populasi awal

        if (population.isEmpty()) { // cek apakah populasi kosong
            throw new IllegalStateException("populasi kosong, tidak ada individu yang dapat dievaluasi"); // lempar error jika kosong
        } 

        int stagnantGenerations = 0; // inisialisasi penghitung generasi stagnan
        double bestCost = population.get(0).getCost(); // ambil cost terbaik dari individu pertama

        // setiap putaran menghasilkan generasi baru selama masih ada budget dan waktu
        while (evaluationCount < config.getEvaluationBudget() && !isTimeLimitReached()) { // perulangan utama ga
            List<Individual> newPopulation = new ArrayList<>(); // buat penampung populasi baru

            // elitisme menyalin individu terbaik tanpa menghitung fitness ulang
            int eliteCount = (int) Math.floor(
                    config.getElitismPercentage() * config.getPopulationSize()); // hitung jumlah individu elit
            eliteCount = Math.min(eliteCount, population.size()); // pastikan tidak melebihi ukuran populasi
            for (int i = 0; i < eliteCount; i++) { // perulangan memasukkan individu elit
                newPopulation.add(population.get(i).copy()); // salin individu elit ke populasi baru
            } 

            // buat anak sampai populasi penuh atau salah satu batas berhenti tercapai
            while (newPopulation.size() < config.getPopulationSize()
                    && evaluationCount < config.getEvaluationBudget()
                    && !isTimeLimitReached()) { // perulangan pengisian anak
                Individual parent1 = selectParent(); // pilih parent pertama
                Individual parent2 = selectParent(); // pilih parent kedua
                Chromosome child; // siapkan variabel untuk anak

                // crossover dilakukan berdasarkan probabilitas pc
                if (random.nextDouble() < config.getCrossoverRate()) { // cek apakah terjadi crossover
                    child = parent1.getChromosome().crossover(
                            parent2.getChromosome(), config.getCrossoverType(), random); // lakukan crossover
                } else { 
                    child = parent1.getChromosome().copy(); // salin saja dari parent pertama
                } 

                // mutasi dilakukan berdasarkan probabilitas pm
                if (random.nextDouble() < config.getMutationRate()) { // cek apakah terjadi mutasi
                    child.mutate(config.getMutationType(), random); // lakukan mutasi pada anak
                } 

                // validasi akhir mencegah kromosom invalid masuk ke populasi
                if (!child.isValid()) { // cek apakah anak tidak valid
                    if (child instanceof BitStringChromosome) { // jika tipe bit string
                        int[] repairedBits = Repair.repairBits(((BitStringChromosome) child).getGenes(), grid, random); // perbaiki bit
                        child = BitStringChromosome.fromGenes(grid, repairedBits); // buat kromosom bit baru dari hasil perbaikan
                    } else { 
                        int[] repairedIndices = Repair.repairIndices(child.decode(), grid, random); // perbaiki index
                        child = IndexListChromosome.fromGenes(grid, repairedIndices); // buat kromosom index baru dari hasil perbaikan
                    } 
                } 

                // pembuatan individual menghitung cost dan fitness tepat satu kali
                newPopulation.add(new Individual(child, distanceMatrix, grid)); // buat individu baru dan masukkan ke populasi baru
                evaluationCount++; // tambah hitungan evaluasi
            } 

            // tidak ada evaluasi baru berarti budget atau waktu sudah habis
            if (newPopulation.isEmpty()) { // cek jika populasi baru kosong
                break; // keluar dari perulangan utama
            } 

            population.clear(); // kosongkan populasi lama
            population.addAll(newPopulation); // masukkan populasi baru
            Collections.sort(population); // urutkan populasi berdasarkan fitness

            // stagnasi dihitung dari perubahan cost terbaik antargenerasi
            double currentBestCost = population.get(0).getCost(); // ambil cost terbaik saat ini
            if (currentBestCost < bestCost - 1.0e-12) { // cek apakah ada perbaikan cost
                bestCost = currentBestCost; // update cost terbaik
                stagnantGenerations = 0; // reset hitungan stagnasi
            } else { // jika tidak ada perbaikan
                stagnantGenerations++; // tambah hitungan stagnasi
            } 

            // nilai 0 pada stagnationlimit berarti pemeriksaan stagnasi dinonaktifkan
            if (config.getStagnationLimit() > 0
                    && stagnantGenerations >= config.getStagnationLimit()) { // cek apakah batas stagnasi tercapai
                break; // keluar dari perulangan utama
            } 
        } 

        return population.get(0); // kembalikan individu terbaik dari hasil akhir
    } 

    // memilih parent dengan metode yang ditentukan di konfigurasi
    private Individual selectParent() { // method pemilihan parent
        if (config.getSelectionType() == GAConfig.SelectionType.ROULETTE_WHEEL) { // cek apakah pakai roulette wheel
            return selectParentRouletteWheel(); // panggil seleksi roulette wheel
        } 
        return selectParentTournament(); // jika tidak, panggil tournament selection
    } 

    // tournament selection: ambil k kandidat acak dan pilih fitness tertinggi
    private Individual selectParentTournament() { // method tournament selection
        Individual best = null; // inisialisasi individu terbaik sebagai null
        for (int i = 0; i < config.getTournamentSize(); i++) { // perulangan sebanyak ukuran turnamen
            Individual candidate = population.get(random.nextInt(population.size())); // ambil kandidat acak dari populasi
            if (best == null || candidate.getFitness() > best.getFitness()) { // bandingkan fitness kandidat dengan yang terbaik
                best = candidate; // update kandidat terbaik jika lebih tinggi
            } 
        } 
        return best; // kembalikan individu terbaik hasil turnamen
    } 

    // roulette wheel: peluang terpilih sebanding dengan fitness positif setiap individu
    private Individual selectParentRouletteWheel() { // method roulette wheel selection
        double totalFitness = 0.0; // siapkan variabel total fitness
        for (Individual individual : population) { // perulangan untuk menjumlahkan seluruh fitness
            totalFitness += individual.getFitness(); // tambahkan fitness individu ke total
        } 

        // fallback defensif jika total fitness tidak positif atau tidak valid
        if (totalFitness <= 0.0 || Double.isInfinite(totalFitness) || Double.isNaN(totalFitness)) { // cek validitas total fitness
            return population.get(random.nextInt(population.size())); // ambil individu acak sebagai cadangan
        } 

        double target = random.nextDouble() * totalFitness; // tentukan target acak dalam rentang total fitness
        double cumulativeFitness = 0.0; // siapkan variabel fitness kumulatif
        for (Individual individual : population) { // perulangan mengecek tiap individu
            cumulativeFitness += individual.getFitness(); // tambahkan fitness ke kumulatif
            if (cumulativeFitness >= target) { // jika kumulatif sudah melewati atau sama dengan target
                return individual; // kembalikan individu tersebut
            } 
        } 
        return population.get(population.size() - 1); // kembalikan individu terakhir sebagai pengaman
    } 

    // mengembalikan true jika batas waktu aktif dan sudah terlampaui 
    private boolean isTimeLimitReached() { // method pengecekan batas waktu
        long limit = config.getTimeLimitMillis(); // ambil batasan waktu dari config
        return limit > 0 && System.currentTimeMillis() - startTimeMillis >= limit; // cek apakah waktu sekarang melebihi limit
    } 

    public int getEvaluationCount() { // getter untuk jumlah evaluasi
        return evaluationCount; // kembalikan nilai evaluation count
    } 

    public List<Individual> getPopulation() { // getter untuk list populasi
        return Collections.unmodifiableList(population); // kembalikan list populasi yang tidak bisa diubah langsung
    } 

    // mengembalikan individu terbaik yang ada saat ini, atau null jika belum ada populasi 
    public Individual getBestIndividual() { // getter individu terbaik
        return population.isEmpty() ? null : population.get(0); // kembalikan individu pertama atau null jika kosong
    } 
} 