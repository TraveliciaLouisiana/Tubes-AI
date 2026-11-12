package map; // class ini berada di package map

import java.io.*;
import java.util.*;

/**
 * class MapReader: buat baca file peta
 * format filenya:
 *   baris 1: m n p (dipisah spasi)
 *   m baris: masing2 n karakter, isinya H, T, atau . (titik)
 */

public class MapReader {
    /**
     * buat baca file peta dari path dan return objek Grid
     * path itu lokasi file peta
     * returnnya Grid yang udah isinya semua informasi peta
     * IOException guna nya kalau file ga bisa dibaca
     */
    public static Grid read(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringTokenizer st = new StringTokenizer(nextLine(br)); // ambil baris pertama dan pisahin per spasinya
            int m = Integer.parseInt(st.nextToken()); // token pertama: jumlah baris
            int n = Integer.parseInt(st.nextToken()); // token kedua: jumlah kolom
            int p = Integer.parseInt(st.nextToken()); // token ketiga: jumlah fire station

            char[][] map = new char[m][n]; // ini variabel buat naro/tempat isi petanya
            for (int r = 0; r < m; r++) {
                String line = nextLine(br); // ambil buat input baris berikutnya
                if (line.length() != n) {
                    throw new IllegalArgumentException("Baris peta ke-" + (r + 1)
                            + " panjangnya " + line.length() + ", seharusnya " + n);
                }
                for (int c = 0; c < n; c++) { // check tiap karakter di baris
                    char ch = line.charAt(c); // ambil dulu satu karakternya
                    if (ch != Grid.HOUSE && ch != Grid.TREE && ch != Grid.EMPTY) { // cuma "H", "T", atau "." yang boleh
                        throw new IllegalArgumentException("Karakter tidak dikenal '" + ch
                                + "' di baris " + (r + 1) + " kolom " + (c + 1));
                    }
                    map[r][c] = ch; // simpen karakternya ke peta
                }
            }
            return new Grid(m, n, p, map); // wrap jadi objek Grid
        }
    }

    // ambil baris berikutnya yang ga kosong, terus buang spasinya di ujung
    private static String nextLine(BufferedReader br) throws IOException {
        String line; // ini variabel buat naro baris yang dibaca nanti
        while ((line = br.readLine()) != null) { // baca dulu sampe akhir file
            line = line.trim();
            if (!line.isEmpty()) {
                return line; // kalau ga kosong, return
            }
        }
        throw new IOException("File peta berakhir lebih cepat dari yang diharapkan");
    }
}
