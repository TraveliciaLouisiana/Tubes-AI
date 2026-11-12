// Menyimpan hasil dari satu kali menjalankan Genetic Algorithm (GA).
// Hasil ini dikirim dari AdapterGA ke Runner.

// class berisi informasi hasil eksperimen (cost, solusi terbaik, jumlah evaluasi, dan status eksperimen)
// Data akan digunakan oleh Runner untuk diperiksa dan disimpan ke dalam file CSV.

public class HasilRun {
    public double cost;            //rata-rata jarak rumah ke FS terdekat dari solusi terbaik
    public long evaluasiTerpakai;  //berapa kali fitness dihitung (harus <= budget)
    public int generasi;           //jumlah generasi yang dijalankan
    public String alasanBerhenti;  //BUDGET_HABIS, STAGNAN, atau WAKTU_HABIS
    public String posisiFS;        //koordinat FS terbaik, format "x y;x y;..." dengan (1,1) = kiri atas
}
