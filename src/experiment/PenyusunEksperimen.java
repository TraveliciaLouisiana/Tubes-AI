
package experiment; // class ini berada di package experiment

import java.util.*; // untuk menggunakan List, ArrayList, dan Arrays

// class untuk menyusun daftar Experiment
// satu objek Experiment mewakili satu kali percobaan atau satu run
// setiap run nantinya dapat dicatat sebagai satu baris di CSV
public class PenyusunEksperimen {

    // membuat daftar run untuk satu model berdasarkan kumpulan peta, parameter, dan seed
    // digunakan oleh Tahap 1, Tahap 2A, dan Tahap 2B
    public static List<Experiment> susun(String tahap, List<InfoPeta> peta, KonfigurasiModel model, List<ParameterGA> daftarParam, int[] seed) {

        // membuat daftar kosong untuk menampung seluruh run yang akan disusun
        List<Experiment> hasil = new ArrayList<>();

        //looping untuk ambil setiap peta yang akan digunakan dalam eksperimen
        for (InfoPeta pt : peta) {

            //ambil setiap kombinasi parameter GA yang akan diuji pada peta ini
            for (ParameterGA prm : daftarParam) {

                //looping untuk ambil setiap seed untuk mengulang percobaan dengan nilai seed berbeda
                for (int s : seed) {
                    Experiment e = new Experiment(); //buat satu objek Experiment untuk mewakili satu kali percobaan
                    e.tahap = tahap; // simpan nama tahap eksperimen, misalnya TAHAP1 atau TAHAP2A
                    e.peta = pt; //simpan informasi peta yang digunakan dalam percobaan ini
                    e.model = model; //simpan kombinasi model GA yang digunakan
                    e.param = prm; //simpan nilai hyperparameter yang digunakan
                    e.seed = s; //simpan seed yang digunakan pada percobaan ini
                    e.budget = MapGenerator.budget(pt.ukuran); //menentukan batas evaluasi berdasarkan ukuran peta

                    //masukan satu run yang sudah lengkap ke dalam daftar hasil
                    hasil.add(e);
                }
            }
        }

        //kembalikan seluruh run yang sudah disusun
        return hasil;
    }

    // menyusun seluruh run untuk Tahap 1
    public static List<Experiment> tahap1(List<InfoPeta> peta, List<KonfigurasiModel> model, int[] seed) {

        // membuat daftar kosong untuk menampung seluruh run Tahap 1
        List<Experiment> hasil = new ArrayList<>();

        // mengambil parameter standar Tahap 1
        // Arrays.asList mengubah satu objek parameter menjadi List agar bisa dikirim ke method susun()
        List<ParameterGA> standar = Arrays.asList(ParameterGA.STANDAR_TAHAP1);

        // mengambil setiap model GA yang akan diuji
        for (KonfigurasiModel m : model) {

            // menyusun run untuk satu model menggunakan seluruh peta, parameter standar, dan seed
            hasil.addAll(susun("TAHAP1", peta, m, standar, seed));
        }

        //kembalikan seluruh run Tahap 1
        return hasil;
    }
}
