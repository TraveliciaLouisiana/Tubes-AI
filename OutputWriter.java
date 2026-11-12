import java.io.*;
import java.util.*;

/**
 * class OutputWriter itu buat menyusun return sesuai soal.
 * baris pertama: p dan f (rata-rata jarak), f dengan 5 angka di belakang koma.
 * p baris berikut: koordinat (x,y) setiap fire station, (1,1) = kiri atas.
 */
public class OutputWriter {
    /**
     * nyusun output
     *
     * parameter g itu untuk peta (dipake buat ubah nomor cell jadi koordinat)
     * parameter fsCells itu untuk daftar nomor cell tempat fire station ditaro
     * parameter cost itu untuk nilai rata-rata jarak f
     * return nya nanti teks output, yang udah ada isi baris baris dengan newline
     */
    public static String format(Grid g, int[] fsCells, double cost) {
        StringBuilder sb = new StringBuilder(); // tempat nyuusn teks nya
        // Locale.US dipake supaya titik (.) yang misahin desimal, bukan koma
        sb.append(String.format(Locale.US, "%d %.5f%n", fsCells.length, cost)); // baris pertama: p dan f
        for (int f : fsCells) { // setiap fire station
            int x = g.emptyCol[f] + 1; // kolom 0-based jadi x 1-based
            int y = g.emptyRow[f] + 1; // baris 0-based jadi y 1-based
            sb.append(x).append(' ').append(y).append('\n'); // tulis "x y"
        }
        return sb.toString(); // return semua teks
    }

    /**
     * nulis output ke file.
     *
     * parameter path itu untuk lokasi file tujuannya
     * parameter g itu untuk peta
     * parameter fsCells itu untuk daftar nomor cell fire station
     * parameter cost itu untuk nilai rata rata jarak
     * throws IOException itu kalau file ga bisa ditulis
     */
    public static void write(String path, Grid g, int[] fsCells, double cost) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) { // buka file buat ditulis
            pw.print(format(g, fsCells, cost)); // tulis isi output
        }
    }
}