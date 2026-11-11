import java.util.ArrayList; 
import java.util.List; 
import java.util.Random; 

/* class Repair: kumpulan fungsi untuk memperbaiki kromosom setelah crossover atau mutasi */
public final class Repair { 

    private Repair() { // constructor private agar class utilitas ini tidak bisa dibuat objeknya
        // kelas utilitas tidak perlu dibuat menjadi objek
    } 

    /**
     * memperbaiki daftar indeks agar berisi tepat grid.p indeks sel kosong yang unik
     * nilai di luar rentang dan duplikat dibuang, lalu diganti dengan indeks yang tersedia
     */
    public static int[] repairIndices(int[] genes, Grid grid, Random random) { // method untuk memperbaiki indeks kromosom
        validateGrid(grid); // memanggil method untuk validasi objek grid
        if (random == null) { // cek apakah objek random bernilai null
            throw new IllegalArgumentException("random tidak boleh null"); // lempar error jika random null
        } 

        boolean[] used = new boolean[grid.numEmpty]; // membuat array penanda sel kosong yang sudah dipakai
        List<Integer> repaired = new ArrayList<>(); // membuat list untuk menampung gen yang valid

        // pertahankan gen pertama yang valid dan belum pernah digunakan
        if (genes != null) { // cek apakah array genes tidak null
            for (int gene : genes) { // perulangan mengecek setiap gen
                if (gene >= 0 && gene < grid.numEmpty && !used[gene]
                        && repaired.size() < grid.p) { // cek apakah gen dalam batas, belum dipakai, dan ukurannya belum penuh
                    used[gene] = true; // tandai sel sudah digunakan
                    repaired.add(gene); // masukkan gen ke list repaired
                } 
            } 
        } 

        // kumpulkan semua indeks sel kosong yang belum digunakan
        List<Integer> available = new ArrayList<>(); // buat list untuk sel kosong yang tersedia
        for (int i = 0; i < grid.numEmpty; i++) { // perulangan sebanyak jumlah sel kosong
            if (!used[i]) { // jika sel belum dipakai
                available.add(i); // masukkan ke list available
            } 
        } 

        // acak kandidat pengganti agar repair tidak selalu memilih sel yang sama
        java.util.Collections.shuffle(available, random); // mengacak urutan sel kosong yang tersedia

        // tambahkan kandidat sampai jumlah fire station tepat p
        int next = 0; // inisialisasi indeks penunjuk list available
        while (repaired.size() < grid.p) { // perulangan selama jumlah gen kurang dari p
            repaired.add(available.get(next)); // ambil sel kosong dari list available
            next++; // geser ke indeks berikutnya
        } 

        // urutkan indeks supaya representasi lebih mudah dibaca dan dibandingkan
        java.util.Collections.sort(repaired); // mengurutkan list gen hasil perbaikan

        // ubah list<integer> menjadi array int supaya konsisten
        int[] result = new int[grid.p]; // buat array hasil dengan ukuran p
        for (int i = 0; i < grid.p; i++) { // perulangan memindahkan isi list ke array
            result[i] = repaired.get(i); // masukkan nilai dari list ke array result
        } 
        return result; // kembalikan array hasil perbaikan
    } 

    /**
     * memperbaiki bit string agar panjangnya numEmpty dan jumlah bit 1 tepat p.
     * jika angka 1 terlalu banyak, sebagian diubah menjadi 0; jika kurang, sebagian angka 0 diubah menjadi 1.
     */
    public static int[] repairBits(int[] genes, Grid grid, Random random) { // method untuk memperbaiki kromosom bit string
        validateGrid(grid); // panggil validasi grid
        if (random == null) { // cek apakah random null
            throw new IllegalArgumentException("random tidak boleh null"); // lempar error jika null
        } 

        int[] bits = new int[grid.numEmpty]; // buat array bit sejumlah sel kosong
        List<Integer> ones = new ArrayList<>(); // buat list untuk posisi bit bernilai 1
        List<Integer> zeros = new ArrayList<>(); // buat list untuk posisi bit bernilai 0

        // salin hanya nilai bit 0 atau 1 dari gen yang tersedia
        for (int i = 0; i < bits.length; i++) { // perulangan menyalin nilai bit
            if (genes != null && i < genes.length && genes[i] == 1) { // jika gen bernilai 1
                bits[i] = 1; // set array bit jadi 1
                ones.add(i); // catat posisi index bit 1
            } else { // jika tidak bernilai 1
                bits[i] = 0; // set array bit jadi 0
                zeros.add(i); // catat posisi index bit 0
            } 
        } 

        // jika jumlah fire station berlebih, pilih bit 1 yang akan dimatikan
        java.util.Collections.shuffle(ones, random); // acak posisi bit 1
        while (ones.size() > grid.p) { // selama jumlah bit 1 kelebihan
            int index = ones.remove(ones.size() - 1); // ambil dan hapus posisi bit 1 terakhir
            bits[index] = 0; // ubah nilainya jadi 0
            zeros.add(index); // masukkan posisinya ke list zeros
        } 

        // jika jumlah fire station kurang, pilih bit 0 yang akan diaktifkan
        java.util.Collections.shuffle(zeros, random); // acak posisi bit 0
        while (ones.size() < grid.p) { // selama jumlah bit 1 kekurangan
            int index = zeros.remove(zeros.size() - 1); // ambil dan hapus posisi bit 0 terakhir
            bits[index] = 1; // ubah nilainya jadi 1
            ones.add(index); // masukkan posisinya ke list ones
        } 

        return bits; // kembalikan array bit yang sudah diperbaiki
    } 

    // memastikan parameter grid memungkinkan penempatan tepat p fire station
    private static void validateGrid(Grid grid) { // method privat untuk validasi grid
        if (grid == null) { // cek jika grid bernilai null
            throw new IllegalArgumentException("grid tidak boleh null"); // lempar error jika null
        } 
        if (grid.p < 0 || grid.p > grid.numEmpty) { // cek jika nilai p tidak masuk akal
            throw new IllegalArgumentException(
                    "jumlah fire station harus berada di antara 0 dan jumlah sel kosong"); // lempar error jika salah
        } 
    } 
}