
package experiment; // class ini berada di package experiment

import ga.GAConfig; // menggunakan enum pilihan operator dari class GAConfig milik Orang 2
import java.util.*; // untuk menggunakan List dan ArrayList

// class untuk menyusun 16 model dari kombinasi representasi, seleksi, crossover, dan mutasi
public class ModelFactory {

    //membuat daftar seluruh 16 kombinasi model dengan urutan yang tetap
    public static List<KonfigurasiModel> buatSemua() {

        // ArrayList untuk menampung seluruh konfigurasi model
        List<KonfigurasiModel> daftar = new ArrayList<>();

        //looping untuk ambil setiap pilihan representasi yang tersedia di GAConfig
        // setiap nilai representasi akan dipasangkan dengan semua pilihan lainnya
        for (GAConfig.RepresentationType rep : GAConfig.RepresentationType.values()) {

            // mengambil setiap pilihan metode seleksi
            // setiap representasi akan dicoba menggunakan semua metode seleksi
            for (GAConfig.SelectionType sel : GAConfig.SelectionType.values()) {

                // mengambil setiap pilihan metode crossover
                // setiap kombinasi representasi dan seleksi akan dicoba dengan semua crossover
                for (GAConfig.CrossoverType cx : GAConfig.CrossoverType.values()) {

                    // mengambil setiap pilihan metode mutasi
                    // setiap kombinasi sebelumnya akan dicoba dengan semua metode mutasi
                    for (GAConfig.MutationType mut : GAConfig.MutationType.values()) {

                        // membuat satu konfigurasi model berdasarkan kombinasi empat operator
                        // name() mengubah nilai enum menjadi String, misalnya ONE_POINT atau SWAP
                        // konfigurasi yang sudah dibuat langsung dimasukkan ke dalam daftar
                        daftar.add(new KonfigurasiModel(rep.name(), sel.name(), cx.name(), mut.name()));
                    }
                }
            }
        }

        //kembalikan daftar seluruh konfigurasi model yang sudah dibuat
        return daftar;
    }
}
