
package experiment; // class ini berada di package experiment

import java.io.*;          
import java.nio.file.*;    
import java.util.*; 

//class untuk membaca hasil percobaan dari CSV dan memilih kandidat terbaik (berdasarkan model atau nilai hyperparameter)
public class AnalisisHasil {

    //menyimpan data dari satu baris hasil percobaan CSV.
    public static class Baris {

        //informasi percobaan yang dibutuhkan untuk analisis.
        public String tahap;       //tahap percobaan, misalnya Tahap 1, 2A, atau 2B.
        public String file;        //nama file peta yang digunakan dalam percobaan.
        public String model;       //ID model atau kombinasi konfigurasi GA.

        //nilai parameter disimpan sebagai String karena berasal dari teks CSV.
        public String populasi;    //jumlah individu dalam populasi.
        public String pc;          //nilai probabilitas crossover.
        public String pm;          //nilai probabilitas mutasi.
        public String elitisme;    //persentase individu terbaik yang dipertahankan.

        public double cost;        //cost solusi terbaik yang dihasilkan oleh GA.
        public double waktuMs;     //waktu yang dibutuhkan untuk menjalankan percobaan (milidetik).
    }

    //menyimpan hasil penilaian atau peringkat dari satu kandidat.
    public static class Skor {
        public String kunci;            //nama kandidat yang sedang dinilai.
        public double peringkatRata;    //rata-rata peringkat dari seluruh peta.
        public double costRata;         //rata-rata cost seluruh percobaan kandidat.
    }

    //membaca file CSV dan mengambil data percobaan yang berhasil (status OK).
    public static List<Baris> baca(String path) throws IOException {

        //list untuk menampung semua data percobaan yang berhasil dibaca.
        List<Baris> hasil = new ArrayList<>();

        //membaca seluruh isi file CSV dan menyimpannya sebagai kumpulan baris teks.
        List<String> semua = Files.readAllLines(Paths.get(path));

        //looping untuk membaca isi CSV, mulai dari indeks 1 (indeks 0 = header)
        for (int i = 1; i < semua.size(); i++) {

            //jika baris kosong, lewati dan lanjut baca baris berikutnya.
            if (semua.get(i).isEmpty()) {
                continue;
            }

            //memisahkan isi baris CSV berdasarkan tanda koma.
            String[] c = semua.get(i).split(",", PenulisCsv.KOLOM.length);

            //memastikan jumlah kolom sesuai dengan format CSV, jika jumlah kolom tidak sesuai = baris tidak lengkap/rusak.
            if (c.length != PenulisCsv.KOLOM.length) {
                continue;
            }

            //hanya ambil percobaan yang memiliki status OK.
            if (!c[PenulisCsv.index("status")].equals("OK")) {
                continue;
            }

            //membuat objek Baris untuk menampung data dari satu baris CSV.
            Baris b = new Baris();

            //Ambil nilai setiap kolom berdasarkan nama kolom pada PenulisCsv.
            b.tahap = c[PenulisCsv.index("tahap")];
            b.file = c[PenulisCsv.index("file_peta")];
            b.model = c[PenulisCsv.index("model")];

            b.populasi = c[PenulisCsv.index("populasi")];
            b.pc = c[PenulisCsv.index("pc")];
            b.pm = c[PenulisCsv.index("pm")];
            b.elitisme = c[PenulisCsv.index("elitisme")];

            //cost dan waktu diubah menjadi double karena akan digunakan dalam perhitungan.
            b.cost = Double.parseDouble(c[PenulisCsv.index("cost")]);
            b.waktuMs = Double.parseDouble(c[PenulisCsv.index("waktu_ms")]);

            //masukan data yang sudah dibaca ke dalam List hasil.
            hasil.add(b);
        }

        //kembalikan seluruh data percobaan yang berhasil dibaca.
        return hasil;
    }

    //memberikan peringkat kepada setiap kandidat berdasarkan jenis yang dipilih (Kandidat yang rata-rata peringkat paling kecil = paling baik)
    public static List<Skor> peringkat(List<Baris> baris, String jenis) {

        //menyimpan cost setiap kandidat berdasarkan nama peta (kandidat mau dibandingkan pada peta yang sama)
        Map<String, Map<String, List<Double>>> dataPeta = new TreeMap<>();

        //menyimpan seluruh cost setiap kandidat dari semua peta (dipakai untuk hitung rata-rata cost keseluruhan kandidat)
        Map<String, List<Double>> dataKandidat = new TreeMap<>();

        //looping setiap data percobaan untuk mengelompokkan kandidat.
        for (Baris b : baris) {

            //inisiasi variabel kunci untuk menentukan kandidat yang sedang dibandingkan.
            String kunci = "";

            //menentukan nilai kandidat berdasarkan jenis analisis yang diminta.
            if (jenis.equals("model")) {
                kunci = b.model;
            } else if (jenis.equals("populasi")) {
                kunci = b.populasi;
            } else if (jenis.equals("pc")) {
                kunci = b.pc;
            } else if (jenis.equals("pm")) {
                kunci = b.pm;
            } else if (jenis.equals("elitisme")) {
                kunci = b.elitisme;
            }

            //memeriksa apakah nama peta sudah memiliki tempat penyimpanan.
            if (!dataPeta.containsKey(b.file)) {
                dataPeta.put(b.file, new TreeMap<String, List<Double>>());
            }

            //ambil tempat penyimpanan kandidat dari peta yang sedang diproses (dibuat agar tidak berulang kali menulis data.get(b.file))
            Map<String, List<Double>> kandidatPeta = dataPeta.get(b.file);

            //memeriksa apakah kandidat sudah memiliki daftar cost pada peta ini.
            if (!kandidatPeta.containsKey(kunci)) {
                kandidatPeta.put(kunci, new ArrayList<Double>()); //buat list baru untuk menampung kost jika belum ada
            }

            //menambah cost percobaan ke daftar cost kandidat pada peta tersebut.
            kandidatPeta.get(kunci).add(b.cost);

            //memeriksa apakah kandidat sudah memiliki daftar cost keseluruhan.
            if (!dataKandidat.containsKey(kunci)) {
                dataKandidat.put(kunci, new ArrayList<Double>()); //buat list baru untuk simpan semua kost kandidat (jika belum ada)
            }

            //menyimpan cost kandidat tanpa membedakan peta.
            dataKandidat.get(kunci).add(b.cost);
        }

        //menyimpan jumlah seluruh peringkat yang diperoleh setiap kandidat.
        Map<String, Double> jumlahPeringkat = new TreeMap<>();

        //menyimpan jumlah peta yang diikuti oleh setiap kandidat (Map untuk menghitung rata-rata peringkat)
        Map<String, Integer> banyakPeta = new TreeMap<>();

        // Looping setiap peta untuk menentukan peringkat kandidat.
        for (String namaPeta : dataPeta.keySet()) {

            //Ambil seluruh kandidat beserta daftar cost dari peta yang sedang diproses.
            Map<String, List<Double>> kandidatPeta = dataPeta.get(namaPeta);

            //buat list untuk menyimpan hasil rata-rata cost setiap kandidat pada peta ini.
            List<Skor> daftarSkor = new ArrayList<>();

            //looping untuk hitung rata-rata cost masing-masing kandidat pada peta ini.
            for (String namaKandidat : kandidatPeta.keySet()) {

                // Membuat objek Skor untuk menyimpan informasi kandidat.
                Skor skor = new Skor();

                // Menyimpan nama kandidat yang sedang dihitung.
                skor.kunci = namaKandidat;

                // Menghitung rata-rata cost kandidat berdasarkan seluruh percobaannya pada peta ini.
                skor.costRata = mean(kandidatPeta.get(namaKandidat));

                // Memasukkan hasil perhitungan ke daftar skor.
                daftarSkor.add(skor);
            }

            //sort kandidat berdasarkan rata-rata cost.
            Collections.sort(daftarSkor, new Comparator<Skor>() {
                public int compare(Skor a, Skor b) {
                    return Double.compare(a.costRata, b.costRata); //kandidat dengan cost terkecil akan berada di urutan pertama 
                }
            });

            //memberikan peringkat kepada setiap kandidat yang sudah diurutkan.
            for (int i = 0; i < daftarSkor.size(); i++) {

                //Ambil nama kandidat pada urutan yang sedang diproses.
                String namaKandidat = daftarSkor.get(i).kunci;

                double peringkat = i + 1; //index bertambah 1 (peringkat bertambah)

                //memeriksa apakah kandidat sudah memiliki jumlah peringkat.
                if (!jumlahPeringkat.containsKey(namaKandidat)) {
                    jumlahPeringkat.put(namaKandidat, 0.0); //nilai awal 0 jika belum ada
                }

                //menambah peringkat yang diperoleh kandidat pada peta ini.
                jumlahPeringkat.put(
                    namaKandidat,
                    jumlahPeringkat.get(namaKandidat) + peringkat
                );

                //memeriksa apakah jumlah peta kandidat sudah tercatat.
                if (!banyakPeta.containsKey(namaKandidat)) {
                    banyakPeta.put(namaKandidat, 0);
                }

                //menambah jumlah peta yang sudah diikuti oleh kandidat.
                banyakPeta.put(
                    namaKandidat,
                    banyakPeta.get(namaKandidat) + 1
                );
            }
        }

        //menampung hasil akhir peringkat seluruh kandidat.
        List<Skor> hasil = new ArrayList<>();

        //looping untuk hitung rata-rata peringkat setiap kandidat.
        for (String namaKandidat : jumlahPeringkat.keySet()) {

            //membuat objek Skor untuk menyimpan hasil akhir kandidat
            Skor skor = new Skor();

            skor.kunci = namaKandidat; //simpan nama kandidat

            //menghitung rata-rata peringkat dengan membagi jumlah peringkat
            skor.peringkatRata = jumlahPeringkat.get(namaKandidat) / banyakPeta.get(namaKandidat);

            //menghitung rata-rata seluruh cost kandidat dari semua peta.
            skor.costRata = mean(dataKandidat.get(namaKandidat));

            //menambah hasil akhir kandidat ke List hasil.
            hasil.add(skor);
        }

        // Mengurutkan hasil akhir berdasarkan: 
        // 1. Rata-rata peringkat paling kecil.
        // 2. Jika seri, rata-rata cost paling kecil.
        // 3. Jika masih seri, nama kandidat berdasarkan alfabet.
        Collections.sort(hasil, new Comparator<Skor>() {
            public int compare(Skor a, Skor b) {

                //membandingkan rata-rata peringkat kedua kandidat.
                int hasilBanding = Double.compare(a.peringkatRata, b.peringkatRata);

                //jika hasilnya tidak sama, hasil perbandingan pertama digunakan
                if (hasilBanding != 0) {
                    return hasilBanding;
                }

                //jika rata-rata peringkat sama, bandingkan rata-rata cost.
                hasilBanding = Double.compare(a.costRata, b.costRata);

                //jika cost berbeda maka kandidat dengan cost lebih kecil didahulukan.
                if (hasilBanding != 0) {
                    return hasilBanding;
                }

                //jika keduanya masih sama akan diurutkan berdasarkan nama kandidat.
                return a.kunci.compareTo(b.kunci);
            }
        });

        //kembalikan hasil peringkat dari kandidat terbaik hingga terburuk.
        return hasil;
    }

    //menyaring data percobaan berdasarkan nama tahap.
    public static List<Baris> saring(List<Baris> baris, String tahap) {

        //list untuk menampung data yang sesuai dengan tahap yang diminta.
        List<Baris> hasil = new ArrayList<>();

        //looping setiap baris untuk memeriksa seluruh data percobaan.
        for (Baris b : baris) {

            //jika tahap pada data sama dengan tahap yang dicari,
            if (b.tahap.equals(tahap)) {
                hasil.add(b); //data dimasukan kedalam hasil
            }
        }

        //kembalikan hanya data yang memiliki tahap sesuai.
        return hasil;
    }

    //menyaring data percobaan berdasarkan ID model (dibuat agar bisa mengambil model tertentu saja)
    public static List<Baris> saringModel(List<Baris> baris, String model) {

        //list untuk menampung data yang memiliki ID model sesuai.
        List<Baris> hasil = new ArrayList<>();

        //looping untuk periksa seluruh data percobaan.
        for (Baris b : baris) {

            // Jika ID model pada data sama dengan model yang dicari,
            if (b.model.equals(model)) {
                hasil.add(b); //data dimasukan kedalam hasil
            }
        }

        //kembalikan seluruh data yang memiliki model sesuai.
        return hasil;
    }

    //menghitung rata-rata dari sekumpulan angka.
    private static double mean(List<Double> angka) {

        //variabel jumlah untuk simpan hasil penjumlahan seluruh angka
        double jumlah = 0;

        //looping untuk menjumlahkan setiap nilai yang ada di dalam List.
        for (double nilai : angka) {
            jumlah = jumlah + nilai;
        }

        //menghitung rata-rata dengan membagi jumlah seluruh angka dengan banyaknya angka yang tersedia.
        return jumlah / angka.size();
    }
}
