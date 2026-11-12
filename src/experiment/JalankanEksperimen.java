package experiment; // class ini berada di package experiment

import java.io.*;
import java.util.*;

//class untuk menjalankan seluruh daftar percobaan GA satu per satu dan menyimpan hasilnya ke CSV

public class JalankanEksperimen {
    private final AdapterGA ga;     //menyimpan adapter yang digunakan untuk menjalankan algoritma GA
    private final PenulisCsv csv;   //menyimpan objek yang bertugas mencatat hasil eksperimen ke file CSV


    //constructor 
    public JalankanEksperimen(AdapterGA ga, PenulisCsv csv) {
        this.ga = ga;
        this.csv = csv;
    }

    //method untuk menjalankan seluruh percobaan yang terdapat dalam daftar.
    public void jalankan(List<Eksperimen> daftar) throws IOException {

        //menyiapkan penghitung jumlah percobaan, hasil sukses, gagal, dilewati, dan yang melebihi budget
        int total = daftar.size();
        int dilewati = 0;
        int sukses = 0;
        int gagal = 0;
        int melebihiBudget = 0;
        int urut = 0;

        //variabel untuk mencatat waktu ketika seluruh proses eksperimen mulai dijalankan
        long mulai = System.currentTimeMillis();

        //looping untuk ambil dan menjalankan setiap objek Eksperimen dari daftar
        for (Eksperimen e : daftar) {

            urut++; //hitung urutan percobaan yang sedang diproses

            //memeriksa apakah percobaan ini sudah pernah berhasil dijalankan.
            if (csv.sudahSelesai(e.id())) {
                dilewati++;
                continue; //jika sudah, lewati percobaan agar tidak terjadi pengulangan
            }

            //mencatat waktu mulai untuk mengukur durasi satu percobaan.
            long t0 = System.currentTimeMillis();

            //variabel status 
            String status = "OK";   //status awal OK
            String error = "";      //error jika terjadi kegagalan

            HasilRun h = null;      //variabel untuk menampung hasil dari GA

            //coba menjalankan GA dan memeriksa apakah hasilnya valid
            try {
                //menjalankan satu percobaan GA berdasarkan konfigurasi pada objek Eksperimen
                h = ga.jalankanSatu(e);

                //jika yang dikembalikan adalah null bukan hasil
                if (h == null) {
                    throw new IllegalStateException("GA mengembalikan null");
                }

                //memastikan nilai cost tidak NaN/tidak tak hingga/tidak negatif.
                if (Double.isNaN(h.cost) || Double.isInfinite(h.cost) || h.cost < 0) {
                    throw new IllegalStateException("Cost tidak valid: " + h.cost);
                }

            } catch (Throwable t) {

                //jika terjadi kesalahan status jadi error
                status = "ERROR";

                //menyimpan pesan kesalahan dalam satu baris agar format CSV tidak rusak
                error = t.toString().replace('\n', ' ').replace('\r', ' ');
            }

            //menghitung lama waktu yang dibutuhkan untuk menjalankan satu percobaan dalam milidetik.
            long waktu = System.currentTimeMillis() - t0;

            //memeriksa apakah jumlah evaluasi fitness melebihi budget yang ditentukan, ada toleransi tambahan sebesar satu ukuran populasi.
            if (status.equals("OK") && h.evaluasiTerpakai > e.budget + e.param.populasi) {
                melebihiBudget++;
            }

            //hitung jumlah percobaan yang berhasil atau gagal.
            if (status.equals("OK")) {
                sukses++;
            } else {
                gagal++;
            }

            //menyimpan informasi percobaan dan hasil GA ke dalam CSV.
            csv.tulis(new String[]{
                //informasi identitas dan konfigurasi percobaan.
                e.id(), e.tahap, e.peta.file, e.peta.ukuran, e.peta.tipe, "" + e.seed, e.model.id(), e.model.representasi, e.model.seleksi, e.model.crossover, e.model.mutasi,

                //nilai hyperparameter dan budget yang digunakan.
                "" + e.param.populasi, "" + e.param.pc, "" + e.param.pm, "" + e.param.elitisme, "" + e.budget,

                //menyimpan cost jika percobaan berhasil, dengan 5 angka di belakang koma.
                status.equals("OK") ? String.format(Locale.US, "%.5f", h.cost) : "",

                //menyimpan jumlah evaluasi fitness yang sudah digunakan.
                status.equals("OK") ? "" + h.evaluasiTerpakai : "",

                //menyimpan jumlah generasi yang berhasil dijalankan.
                status.equals("OK") ? "" + h.generasi : "",

                //menyimpan alasan GA berhenti.
                status.equals("OK") ? h.alasanBerhenti : "",

                //menyimpan waktu eksekusi percobaan.
                "" + waktu,

                //menyimpan posisi fire station dari solusi terbaik.
                status.equals("OK") ? h.posisiFS : "",

                //menyimpan status akhir percobaan dan pesan error jika ada.
                status, error
            });

            //menampilkan laporan kemajuan setiap 25 percobaan atau ketika semua percobaan selesai.
            if (urut % 25 == 0 || urut == total) {

                //menghitung total waktu yang sudah digunakan sejak eksperimen dimulai.
                double detik = (System.currentTimeMillis() - mulai) / 1000.0;

                //menampilkan jumlah percobaan, sukses, gagal, dilewati, dan waktu yang sudah berjalan.
                System.out.printf(Locale.US, "[%d/%d] sukses=%d gagal=%d dilewati=%d (%.0f detik)%n", urut, total, sukses, gagal, dilewati, detik);
            }
        }

        //menampilkan ringkasan hasil setelah seluruh daftar percobaan selesai diproses.
        System.out.println("SELESAI. sukses=" + sukses + " gagal=" + gagal + " dilewati(resume)=" + dilewati );

        //memberikan peringatan jika ada percobaan yang menggunakan evaluasi melebihi budget.
        if (melebihiBudget > 0) {
            System.out.println("PERINGATAN: " + melebihiBudget + " run memakai evaluasi melebihi budget. GA harus diperiksa");
        }

        //memberikan informasi jika ada percobaan yang gagal dan perlu diulang.
        if (gagal > 0) {
            System.out.println("Ada run gagal: jalankan perintah yang sama lagi untuk mengulang yang gagal.");
        }
    }
}
