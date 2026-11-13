package experiment; // class ini berada di package experiment

//class untuk menyimpan seluruh informasi yang dibutuhkan dalam satu kali percobaan GA.
//setiap objek Eksperimen mewakili satu percobaan dan nantinya dicatat sebagai satu baris di CSV.

//class menyimpan gabungan informasi peta, model GA, parameter, seed dan budget

public class Experiment {
    public String tahap;              // TAHAP1, TAHAP2A_POPULASI, TAHAP2B, PENGUJIAN, dan lainnya
    public InfoPeta peta;             //peta yang dipakai
    public KonfigurasiModel model;    //kombinasi operator
    public ParameterGA param;         //hyperparameter 
    public int seed;                  //seed random percobaan ini
    public int budget;                //evaluation budget


    //membuat id unik untuk membedakan percobaan dan memeriksa apakah percobaan sudah berhasil dijalnkan
    public String id() {
        //informasi digabung jadi 1 string dan dipisahkan dengan |
        return tahap + "|" + peta.file + "|" + model.id() + "|" + param.populasi + "|" + param.pc + "|" + param.pm + "|" + param.elitisme + "|" + seed;
    }
}
