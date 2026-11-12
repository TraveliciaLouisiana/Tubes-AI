package experiment; // class ini berada di package experiment

import java.io.*;
import java.util.*;

//class untuk menyimpan informasi setiap peta dan mengelola katalog peta dalam file CSV.
//katalog ini digunakan agar program bisa mengetahui informasi setiap peta tanpa harus membaca dan menghitung ulang semuanya.
//informasi yang disimpan meliputi ukuran peta, jumlah rumah, pohon, sel kosong, dan seed peta.

public class InfoPeta {
    //menyimpan informasi dasar dari sebuah peta.
    public String tahap;                //tahap peta: TAHAP1, TAHAP2, atau PENGUJIAN
    public String file;                 //lokasi file peta
    public String ukuran;               //ukuran peta: tiny, small, normal, atau large
    public String tipe;                 //jenis peta: ACAK, CLUSTER, atau LABIRIN
    public int baris, kolom, p;         //jumlah baris, kolom, dan fire station (FS)
    public int rumah, pohon, kosong;    //jumlah rumah, pohon, dan sel kosong
    public int rumahTerisolasi;         //jumlah rumah yang tidak memiliki tetangga berupa sel kosong
    public int komponenKosong;          //jumlah kelompok sel kosong yang saling terpisah
    public long seedPeta;               //seed yang digunakan untuk membuat peta agar bisa dibuat ulang

    //heade yang berisii nama kolom yang akan digunakan pada file CSV
    private static final String HEADER = "tahap,file,ukuran,tipe,baris,kolom,p,rumah,pohon,kosong,rumah_terisolasi,komponen_kosong,seed_peta";

    //method untuk menulis seluruh daftar peta ke satu file katalog.
    public static void tulisKatalog(String path, List<InfoPeta> daftar) throws IOException {
        
        //membuka/membuat file CSV untuk ditulis
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.println(HEADER); //menuliskan nama-nama kolom sebagai baris pertama csv
            
            //looping untuk mengambil informasi peta dari daftar
            for (InfoPeta i : daftar) {
                //informasi digabungkan dengan koma
                out.println(String.join(",", i.tahap, i.file, i.ukuran, i.tipe, "" + i.baris, "" + i.kolom, "" + i.p, "" + i.rumah, "" + i.pohon, "" + i.kosong, "" + i.rumahTerisolasi, "" + i.komponenKosong, "" + i.seedPeta));
            }
        }
    }

    //method untuk membaca katalog, hanya mengambil peta dari tahap tertentu 
    public static List<InfoPeta> bacaKatalog(String path, String tahap) throws IOException {
        
        //list untuk menyimpan informasi oeta yang ditemukan
        List<InfoPeta> hasil = new ArrayList<>();
        
        //membaca seluruh baris dari file katalog CSV 
        List<String> baris = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(path));
        
        //looping untuk membaca isi CSV dimulai dari baris index 1 (index 0 adalah header)
        for (int i = 1; i < baris.size(); i++) {        
            
            //melewati baris yang isinya kosong
            if (baris.get(i).trim().isEmpty()){
                continue;        
            }

            //memisahkan isi baris CSV jadi beberapa kolom berdasarkan ,
            String[] c = baris.get(i).split(",");
            
            //memastikan tahap peta sesuai dengan tahap yang berbeda
            if (!c[0].equals(tahap)){
                continue; //jika berbeda maka bagian tersebut tidak diproses
            }

            InfoPeta x = new InfoPeta();        //objek untuk menyimpan informasi dari satu barus CSV
            
            //Mengusu informasi peta berdasarkan posisi masing-masing kolom
            x.tahap = c[0]; 
            x.file = c[1]; 
            x.ukuran = c[2]; 
            x.tipe = c[3];

            //mengubah data String dari CSV menjadi tipe sesuai kebutuhan
            x.baris = Integer.parseInt(c[4]); 
            x.kolom = Integer.parseInt(c[5]); 
            x.p = Integer.parseInt(c[6]);
            x.rumah = Integer.parseInt(c[7]); 
            x.pohon = Integer.parseInt(c[8]); 
            x.kosong = Integer.parseInt(c[9]);
            x.rumahTerisolasi = Integer.parseInt(c[10]); 
            x.komponenKosong = Integer.parseInt(c[11]);
            x.seedPeta = Long.parseLong(c[12]);

            //memasukan objek peta yang sudah terisi ke dalam list hasil
            hasil.add(x);
        }
    
        return hasil; //kembalikan daftar peta
    }
}
