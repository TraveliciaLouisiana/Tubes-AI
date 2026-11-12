package experiment; // class ini berada di package experiment

//class untuk menyimpan kombinasi operator Genetic Algorithm (GA) dalam satu model eksperimen.
//kombinasi menentukan representasi, seleksi, crossover, dan mutasi yang akan digunakan.

public class KonfigurasiModel {

    //menyimpan jenis representasi kromosom yang digunakan.
    public final String representasi;  // BIT_STRING / INDEX_LIST

    //menyimpan metode seleksi parent yang digunakan.
    public final String seleksi;       // ROULETTE / TOURNAMENT

    //menyimpan metode crossover untuk menghasilkan keturunan.
    public final String crossover;     // ONE_POINT / UNIFORM

    //menyimpan metode mutasi untuk mengubah kromosom.
    public final String mutasi;        // REPLACE / SWAP

    //constructor untuk membuat objek model dengan kombinasi operator yang dipilih.
    public KonfigurasiModel(String representasi, String seleksi, String crossover, String mutasi) {
        this.representasi = representasi;
        this.seleksi = seleksi;
        this.crossover = crossover;
        this.mutasi = mutasi;
    }

    //membuat ID unik berdasarkan kombinasi keempat operator (id digunakan untuk membedakan model dan menyimpan identitasnya di CSV atau file parameter)
    public String id() {
        return representasi + "__" + seleksi + "__" + crossover + "__" + mutasi;
    }

    //membuat objek KonfigurasiModel dari ID yang sudah digabung menjadi satu String.
    //ID dipisahkan berdasarkan "__" untuk mendapatkan masing-masing jenis operator.
    public static KonfigurasiModel dariId(String id) {
        //memisahkan ID menjadi empat bagian berdasarkan tanda "__".
        String[] b = id.split("__");

        //memastikan ID memiliki tepat empat bagian sesuai jumlah operator.
        if (b.length != 4) {
            throw new IllegalArgumentException("ID model tidak valid: " + id);
        }

        //membuat objek baru menggunakan empat bagian hasil pemisahan ID.
        return new KonfigurasiModel(b[0], b[1], b[2], b[3]);
    }

    //mengembalikan ID model ketika objek diubah menjadi String.
    //berguna saat objek ditampilkan menggunakan System.out.println().
    @Override
    public String toString() {
        return id();
    }
}
