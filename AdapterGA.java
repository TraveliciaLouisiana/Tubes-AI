
public interface AdapterGA {
    // Menjalankan satu eksperimen GA berdasarkan konfigurasi yang diterima melalui objek Eksperimen.

    // Mengembalikan hasil eksperimen dalam bentuk HasilRun.
    public HasilRun jalankanSatu(Eksperimen e) throws Exception;
}