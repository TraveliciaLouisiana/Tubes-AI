package experiment; // class ini berada di package experiment

import java.io.*;
import java.util.*;

// Membuat 30 seed dari NPM Ketua (6182401012)
// Hasil akan disimpan di seeds.txt

public class SeedGenerator{

    //Method 1: Membuat sejumlah seed berdasarkan NPM Ketua
    public static int[] buat(long npmKetua, int jumlah){
        Random random = new Random(npmKetua);   //buat objek random
        int batas = 65536;                      //batas angka seed 2^16   
        int[] seed = new int[jumlah];           //array untuk menyimpan hasil seed    
        
        //loop untuk menghasilkan seed sebanyak "jumlah"
        for(int i = 0; i < jumlah; i++){        
            seed[i] = random.nextInt(batas+1);
        }

        return seed;                            //kembalikan array yang berisi seluruh seed
    }

    //Method 2: Menyimpan array seed ke file
    public static void simpan(int[] seed, String path) throws IOException{
        //Membuka file untuk ditulis, jika file belum ada maka akan dibuat, jika sudah ada isinya ditimpa
        try(PrintWriter out = new PrintWriter(new FileWriter(path))){
            
            //Looping untuk ambil setiap nilai seed dari array
            for(int s : seed){

                //menuliskan satu seed ke dalam file
                out.println(s); //satu seed per satu baris
            }
        }
    }

    //Method 3: Membaca seed dari file dan mengembalikannya dalam bentuk array int
    public static int[] readSeeds(String path) throws IOException{
        
        //membuat arraylist nilai untuk menampung seed yang dibaca dari file
        List<Integer> nilai = new ArrayList<>();    //pakai arraylist karena jumlah data belum diketahui
        
        //membuka file untuk dibaca
        try(Scanner sc = new Scanner(new File(path))){

            //akan dibaca selama masih ada data bertipe int
            while (sc.hasNextInt()) {
                nilai.add(sc.nextInt()); //memasukan data ke arraylist
            }
        }

        //Memastikan jumlah seed yang dibaca tepat 30
        if(nilai.size() != 30){
            throw new IllegalArgumentException(path + " harus berisi tepat 30 seed");
        }

        //Membuat array int dengan ukuran menyesuaikan jumlah data
        int[] seed = new int[nilai.size()];
        
        //Looping untuk memidnahkan data dari arraylist ke array
        for (int i = 0; i < seed.length; i++) {
            seed[i] = nilai.get(i);
        }

        //mengembalikan array yang sudah berisi 30 seed
        return seed;
    }
}
