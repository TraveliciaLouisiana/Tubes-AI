package experiment; // class ini berada di package experiment

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Class untuk menulis hasil eksperimen ke CSV.
// 1 baris = hasil dari 1 run eksperimen.

public class PenulisCsv {

    //Daftar nama kolom CSV.
    public static final String[] KOLOM = {
        "run_id", "tahap", "file_peta", "ukuran_peta", "tipe_peta", "seed",
        "model", "representasi", "seleksi", "crossover", "mutasi",
        "populasi", "pc", "pm", "elitisme", "budget", "cost",
        "evaluasi_terpakai", "generasi", "alasan_berhenti", "waktu_ms",
        "posisi_fs", "status", "error"
    };

    //Menggabungkan semua nama kolom menjadi satu baris header CSV.
    private static final String HEADER = String.join(",", KOLOM);

    //Menyimpan posisi kolom status berdasarkan nama kolom.
    private static final int INDEX_STATUS = index("status");

    //Path lokasi file CSV.
    private final String path;

    //Menyimpan ID run yang sudah berhasil.
    private final Set<String> selesai = new HashSet<>(); //menggunakan hashSet untuk mencegah ID yang sama disimpan berulang

    //Mencari posisi kolom berdasarkan nama.
    public static int index(String namaKolom) {

        for (int i = 0; i < KOLOM.length; i++) {
            if (KOLOM[i].equals(namaKolom)) {
                return i;
            }
        }

        //eror jika nama kolom tidak ditemukan
        throw new IllegalArgumentException("Kolom tidak ada: " + namaKolom);
    }

    //Constructor
    public PenulisCsv(String path) throws IOException {
        this.path = path;
        File f = new File(path);

        //membuat folder utama jika belum ada.
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }

        //jika file sudah ada dan tidak kosong,
        if (f.exists() && f.length() > 0) {
            bersihkanDanMuat(); //membaca data sebelumnya agar eksperimen bisa dilanjutkan

        } else {
            //jika file masih kosong,
            try (PrintWriter out = new PrintWriter(new FileWriter(f))) {
                out.println(HEADER); //membuat file baru dan menulskan header
            }
        }
    }

    // class untuk membersihkan dan membaca hasil eksperimen sebelumnya.
    private void bersihkanDanMuat() throws IOException {

        //baca seluruh baris dari file CSV.
        List<String> baris = Files.readAllLines(Paths.get(path));

        //memastikan header sesuai dengan struktur program.
        if (baris.isEmpty() || !baris.get(0).equals(HEADER)) {
            throw new IllegalStateException("Header " + path + " berbeda dengan versi program ini");
        }

        //menyimpan baris yang valid.
        List<String> simpan = new ArrayList<>();    //menyimpan baris yang valid
        simpan.add(HEADER);                         //header tetap dipertahankan

        int dibuang = 0;

        //membaca data mulai baris kedua.
        for (int i = 1; i < baris.size(); i++) {

            if (baris.get(i).isEmpty()) {
                continue;
            }

            //memisahkan kolom berdasarkan koma.
            String[] c = baris.get(i).split(",", KOLOM.length);

            //mempertahankan baris dengan jumlah kolom benar,
            if (c.length == KOLOM.length && c[INDEX_STATUS].equals("OK") && selesai.add(c[0])) {
                simpan.add(baris.get(i));

            } else {
                dibuang++;
            }
        }

        //menulis ulang CSV dengan data yang sudah dibersihkan.
        Files.write(Paths.get(path), simpan);

        if (dibuang > 0) {
            System.out.println("Resume: " + dibuang + " baris ERROR/rusak dibuang, akan dicoba ulang.");
        }

        System.out.println("Resume: " + selesai.size() + " run sudah selesai di " + path);
    }

    //memeriksa apakah suatu run sudah pernah berhasil.
    public boolean sudahSelesai(String runId) {
        return selesai.contains(runId);
    }

    //menulis satu hasil eksperimen ke CSV.
    //menggunakan synchronized untuk mencegah dua thread menulis bersamaan.
    public synchronized void tulis(String[] nilai) throws IOException {

        //memastikan jumlah nilai sama dengan jumlah kolom.
        if (nilai.length != KOLOM.length) {
            throw new IllegalArgumentException("Jumlah kolom salah");
        }

        // StringBuilder digunakan untuk menyusun satu baris CSV.
        String baris = "";

        for (int i = 0; i < nilai.length; i++) {

            // Memberikan koma sebagai pemisah antar kolom.
            if (i > 0) {
                baris += ",";
            }

            // Menambahkan nilai yang sudah diamankan.
            baris += escape(nilai[i]);
        }

        // Membuka file dalam mode append (menambahkan data).
        try (PrintWriter out = new PrintWriter(new FileWriter(path, true))) {
            // Menulis satu baris hasil eksperimen.
            out.println(baris);
        }

        // Jika status berhasil, masukkan run_id ke Set.
        if (nilai[INDEX_STATUS].equals("OK")) {
            selesai.add(nilai[0]);
        }
    }

    //memastikan teks tidak merusak format CSV.
    private static String escape(String v) {

        //ubah menjadi string kosong jika nilai null
        String s;
        if (v == null) {
            s = "";
        } else {
            s = v;
        }

        //jika ada koma/kutip/ baris baru, teks dibungkus dengan tanda kutip.
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }

        return s;
    }
}
