
package experiment; // class ini berada di package experiment

import java.io.*;
import java.util.*; 

//class untuk menjalankan seluruh percobaan GA dan menyimpan hasilnya ke CSV
public class ExperimentRunner {
    private final AdapterGA ga;     //menyimpan adapter yang digunakan untuk menjalankan algoritma GA
    private final PenulisCsv csv;   //menyimpan objek yang digunakan untuk menulis hasil percobaan ke CSV

    //constructor untuk menerima adapter GA dan penulis CSV
    public ExperimentRunner(AdapterGA ga, PenulisCsv csv) {
        this.ga = ga;
        this.csv = csv;
    }

    //menjalankan seluruh percobaan yang terdapat di dalam daftar
    public void jalankan(List<Experiment> daftar) throws IOException {

        //variabel untuk hitung jumlah seluruh percobaan yang akan dijalankan
        int total = daftar.size();

        //variabel untuk hitung jumlah percobaan yang sudah pernah berhasil dan dilewati
        int dilewati = 0;

        //variabel untuk menghitung jumlah percobaan yang berhasil dijalankan
        int sukses = 0;

        // menghitung jumlah percobaan yang mengalami kesalahan
        int gagal = 0;

        // menghitung jumlah percobaan yang menggunakan evaluasi melebihi budget
        int melebihiBudget = 0;

        // menghitung urutan percobaan yang sedang diproses
        int urut = 0;

        // mencatat waktu ketika seluruh proses percobaan dimulai
        long mulai = System.currentTimeMillis();

        //looping untuk mengambil dan menjalankan setiap percobaan
        for (Experiment e : daftar) {

            // menambah urutan setiap kali satu percobaan mulai diperiksa
            urut++;

            // memeriksa apakah percobaan ini sudah pernah berhasil disimpan ke CSV
            if (csv.sudahSelesai(e.id())) {

                // menambah jumlah percobaan yang dilewati
                dilewati++;

                // melewati percobaan ini dan lanjut ke percobaan berikutnya
                continue;
            }

            // mencatat waktu mulai untuk menghitung durasi satu percobaan
            long waktuMulai = System.currentTimeMillis();

            // menyimpan status percobaan, awalnya dianggap berhasil
            String status = "OK";

            // menyimpan pesan kesalahan jika terjadi masalah
            String error = "";

            // menyiapkan variabel untuk menampung hasil dari algoritma GA
            HasilRun h = null;

            // mencoba menjalankan algoritma GA dan memeriksa hasilnya
            try {

                // menjalankan satu percobaan GA berdasarkan konfigurasi Experiment
                h = ga.jalankanSatu(e);

                // memeriksa apakah hasil dari GA kosong atau null
                if (h == null) {
                    throw new IllegalStateException("GA mengembalikan null");
                }

                // memeriksa apakah nilai cost valid
                // cost tidak boleh NaN, tak hingga, atau bernilai negatif
                if (Double.isNaN(h.cost) || Double.isInfinite(h.cost) || h.cost < 0) {
                    throw new IllegalStateException("Cost tidak valid: " + h.cost);
                }

            } catch (Throwable t) {

                // mengubah status menjadi ERROR jika terjadi kesalahan
                status = "ERROR";

                // mengambil pesan kesalahan dan menghapus baris baru agar CSV tetap rapi
                error = t.toString().replace('\n', ' ').replace('\r', ' ');
            }

            // menghitung lama waktu yang dibutuhkan untuk menjalankan satu percobaan
            long waktu = System.currentTimeMillis() - waktuMulai;

            // memeriksa apakah evaluasi fitness yang digunakan melebihi budget
            // diberikan toleransi tambahan sebesar satu ukuran populasi
            if (status.equals("OK")) {

                if (h.evaluasiTerpakai > e.budget + e.param.populasi) {
                    melebihiBudget++;
                }
            }

            // menghitung jumlah percobaan yang berhasil atau gagal
            if (status.equals("OK")) {
                sukses++;
            } else {
                gagal++;
            }

            // menyiapkan data hasil percobaan untuk disimpan ke CSV
            String[] data = new String[25];

            // menyimpan identitas dan konfigurasi percobaan
            data[0] = e.id();
            data[1] = e.tahap;
            data[2] = e.peta.file;
            data[3] = "" + e.peta.ukuran;
            data[4] = e.peta.tipe;
            data[5] = "" + e.seed;
            data[6] = e.model.id();
            data[7] = e.model.representasi;
            data[8] = e.model.seleksi;
            data[9] = e.model.crossover;
            data[10] = e.model.mutasi;

            // menyimpan nilai hyperparameter dan budget
            data[11] = "" + e.param.populasi;
            data[12] = "" + e.param.pc;
            data[13] = "" + e.param.pm;
            data[14] = "" + e.param.elitisme;
            data[15] = "" + e.budget;

            // menyimpan cost jika percobaan berhasil
            // format cost menggunakan lima angka di belakang koma
            if (status.equals("OK")) {
                data[16] = String.format(Locale.US, "%.5f", h.cost);
            } else {
                data[16] = "";
            }

            // menyimpan jumlah evaluasi fitness jika percobaan berhasil
            if (status.equals("OK")) {
                data[17] = "" + h.evaluasiTerpakai;
            } else {
                data[17] = "";
            }

            // menyimpan jumlah generasi jika percobaan berhasil
            if (status.equals("OK")) {
                data[18] = "" + h.generasi;
            } else {
                data[18] = "";
            }

            // menyimpan alasan algoritma GA berhenti jika percobaan berhasil
            if (status.equals("OK")) {
                data[19] = h.alasanBerhenti;
            } else {
                data[19] = "";
            }

            // menyimpan lama waktu eksekusi dalam milidetik
            data[20] = "" + waktu;

            // menyimpan posisi fire station dari solusi terbaik jika berhasil
            if (status.equals("OK")) {
                data[21] = h.posisiFS;
            } else {
                data[21] = "";
            }

            // menyimpan status akhir percobaan
            data[22] = status;

            // menyimpan pesan kesalahan jika terjadi kegagalan
            data[23] = error;

            // menulis seluruh data percobaan ke file CSV
            csv.tulis(data);

            // menampilkan perkembangan proses setiap 25 percobaan
            // laporan juga ditampilkan ketika seluruh percobaan sudah diperiksa
            if (urut % 25 == 0 || urut == total) {

                // menghitung waktu yang sudah digunakan sejak seluruh proses dimulai
                double detik = (System.currentTimeMillis() - mulai) / 1000.0;

                // menampilkan jumlah percobaan dan hasil sementara
                System.out.printf(
                    Locale.US,
                    "[%d/%d] sukses=%d gagal=%d dilewati=%d (%.0f detik)%n",
                    urut, total, sukses, gagal, dilewati, detik
                );
            }
        }

        // menampilkan ringkasan setelah seluruh percobaan selesai
        System.out.println("SELESAI. sukses=" + sukses + " gagal=" + gagal + " dilewati(resume)=" + dilewati);

        // menampilkan peringatan jika ada percobaan yang melebihi budget
        if (melebihiBudget > 0) {
            System.out.println("PERINGATAN: " + melebihiBudget +"  periksa GA"
            );
        }

        // memberi tahu pengguna jika ada percobaan yang gagal
        if (gagal > 0) {
            System.out.println("Ada run gagal: jalankan perintah yang sama lagi untuk mengulang yang gagal");
        }
    }
}
