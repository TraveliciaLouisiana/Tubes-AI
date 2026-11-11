/* class individual: menyimpan satu solusi ga, yaitu kromosom beserta cost dan fitness-nya */
public class Individual implements Comparable<Individual> { // mendeklarasikan class individual yang mengimplement comparable

    private final Chromosome chromosome; // menyimpan objek kromosom individu
    private final double cost; // menyimpan nilai cost dari individu
    private final double fitness; // menyimpan nilai fitness dari individu

    // membuat individu dan langsung menghitung cost serta fitness-nya
    public Individual(Chromosome chromosome, int[][] distanceMatrix, Grid grid) { // constructor utama
        if (chromosome == null || grid == null) { // validasi apakah kromosom atau grid bernilai null
            throw new IllegalArgumentException("chromosome dan grid tidak boleh null"); // lempar error jika null
        } 
        if (!chromosome.isValid()) { // cek apakah kromosom tidak valid
            throw new IllegalArgumentException("kromosom tidak valid dan tidak dapat dievaluasi"); // lempar error jika tidak valid
        } 
        this.chromosome = chromosome; // mengisi variabel kromosom
        this.cost = Fitness.calculateCost(chromosome, distanceMatrix, grid); // hitung nilai cost menggunakan class fitness
        this.fitness = Fitness.calculateFitness(cost); // ubah nilai cost menjadi fitness
    } 

    // constructor salinan untuk elitisme tanpa menghitung fitness ulang
    private Individual(Chromosome chromosome, double cost, double fitness) { // constructor khusus copy
        this.chromosome = chromosome; // mengisi variabel kromosom
        this.cost = cost; // mengisi nilai cost yang sudah ada
        this.fitness = fitness; // mengisi nilai fitness yang sudah ada
    } 

    // menyalin individu beserta nilai evaluasinya tanpa menambah evaluation count
    public Individual copy() { // method untuk menyalin individu
        return new Individual(chromosome.copy(), cost, fitness); // kembalikan objek individual baru yang merupakan salinan
    } 

    public Chromosome getChromosome() { // getter untuk mengambil kromosom
        return chromosome; 
    } 

    public double getCost() { // getter untuk mengambil nilai cost
        return cost; 
    } 

    public double getFitness() { // getter untuk mengambil nilai fitness
        return fitness; 
    } 

    // mengurutkan individu dari fitness terbesar ke terkecil
    @Override
    public int compareTo(Individual other) { // method perbandingan untuk sorting
        return Double.compare(other.fitness, this.fitness); // bandingkan fitness agar yang besar ada di depan
    } 

    @Override
    public String toString() { // method mengubah objek ke bentuk string
        return "cost=" + cost + ", fitness=" + fitness
                + ", fs=" + java.util.Arrays.toString(chromosome.decode()); // kembalikan format string informasi individu
    } 
} 