
//class untuk menyimpan hyperparameter yang digunakan dalam satu eksperimen Genetic Algorithm (GA).
//hyperparameter menentukan pengaturan GA, seperti jumlah populasi, probabilitas crossover, mutasi, dan elitisme.

public class ParameterGA {
    public final int populasi;      //menyimpan jumlah individu dalam setiap generasi
    public final double pc;         //menyimpan probabilitas terjadinya crossover
    public final double pm;         //menyimpan probabilitas terjadinya mutasi
    public final double elitisme;   //menyimpan persentase individu terbaik yang dipertahankan ke generasi berikutnya

    //objek yang menjadi nilai awal untuk eksperimen.
    public static final ParameterGA STANDAR_TAHAP1 = new ParameterGA(100, 0.8, 0.05, 0.20);

    //constructor 
    public ParameterGA(int populasi, double pc, double pm, double elitisme) {
        this.populasi = populasi;
        this.pc = pc;
        this.pm = pm;
        this.elitisme = elitisme;
    }

    //membuat objek ParameterGA baru dengan mengganti satu parameter saja.
    public ParameterGA dengan(String nama, double nilai) {

        //jika parameter yang ingin diubah adalah populasi
        if (nama.equals("populasi")) {
            return new ParameterGA((int) nilai, pc, pm, elitisme); //buat objek baru dengan populasi yang diganti.
        }

        //jika parameter yang ingin diubah adalah probabilitas crossover.
        if (nama.equals("pc")) {
            return new ParameterGA(populasi, nilai, pm, elitisme);
        }

        //jika parameter yang ingin diubah adalah probabilitas mutasi.
        if (nama.equals("pm")) {
            return new ParameterGA(populasi, pc, nilai, elitisme);
        }

        //jika parameter yang ingin diubah adalah persentase elitisme.
        if (nama.equals("elitisme")) {
            return new ParameterGA(populasi, pc, pm, nilai);
        }

        //jika nama parameter tidak sesuai dengan pilihan yang tersedia
        throw new IllegalArgumentException("Nama parameter harus: populasi, pc, pm, atau elitisme"); //menampilkan pesan kesalahan
    }

    //mengubah seluruh informasi parameter menjadi String dan ditampilkan
    @Override
    public String toString() {
        return "populasi=" + populasi + " pc=" + pc + " pm=" + pm + " elitisme=" + elitisme;
    }
}
