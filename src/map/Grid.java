package map; // class ini berada di package map

/**
 * Class Grid: nyimpen semua informasi peta yang dipakai bagian lain program.
 * koordinat: 0-based
 * koordinat dari soal: 1-based
 */
public class Grid {
    public static final char HOUSE = 'H'; // rumah
    public static final char TREE = 'T'; // pohon
    public static final char EMPTY = '.'; // cell kosong

    public final int m; // jumlah baris peta
    public final int n; // jumlah kolom peta
    public final int p; // jumlah fire station yang harus ditempatkan
    public final char[][] map; // isi peta: map[baris][kolom]
    public final int penalty; // penalti P = m x n, untuk rumah yang tidak bisa mencapai fire station

    public final int numHouses; // jumlah rumah (H)
    public final int[] houseRow; // houseRow[h] = baris rumah ke-h
    public final int[] houseCol; // houseCol[h] = kolom rumah ke-h

    public final int numEmpty; // e = jumlah cell kosong (T sama H ga dihitung)
    public final int[] emptyRow; // emptyRow[e] = baris cell kosong nomor e
    public final int[] emptyCol; // emptyCol[e] = kolom cell kosong nomor e
    public final int[][] cellId; // cellId[r][c] = nomor sel kosong, atau -1 kalau bukan cell kosong

    /**
     * buat grid dari ukuran, jumlah fire station, dan isi peta
     * m jumlah baris
     * n jumlah kolom
     * p jumlah fire station
     * map isi peta, ukuran m x n
     */
    public Grid(int m, int n, int p, char[][] map) {
        this.m = m; // simpen jumlah baris
        this.n = n; // simpen jumlah kolom
        this.p = p; // simpen jumlah fire station (fs)
        this.map = map; // simpen isi peta
        this.penalty = m * n; // penalti sesuai soal: P = m x n

        // langkah 1: hitung dulu berapa rumah dan berapa cell kosong
        int h = 0; // variabel buat hitung rumah
        int e = 0; // variabel buat hitung cell kosong
        for (int r = 0; r < m; r++) { // untuk setiap baris
            for (int c = 0; c < n; c++) { // untuk setiap kolom
                if (map[r][c] == HOUSE) {
                    h++; // kalau rumah, tambah ke variabel rumah
                } else if (map[r][c] == EMPTY) {
                    e++; // kalau kosong, tambah ke variabel kosong
                }
            }
        }
        this.numHouses = h; // simpen jumlah rumah
        this.numEmpty = e; // simpen jumlah cell kosong

        // langkah 2: buat array sesuai ukuran yang sudah dihitung
        this.houseRow = new int[h]; // baris setiap rumah
        this.houseCol = new int[h]; // kolom setiap rumah
        this.emptyRow = new int[e]; // baris setiap cell kosong
        this.emptyCol = new int[e]; // kolom setiap cell kosong
        this.cellId = new int[m][n]; // nomor cell kosong per posisi nya

        // langkah 3: isi semua array. nomor cell kosong dikasih urutan dari kiri atas ke kanan bawah
        int hi = 0; // indeks rumah yang sedang diisi
        int ei = 0; // nomor cell kosong yang sedang diisi
        for (int r = 0; r < m; r++) { // untuk setiap baris
            for (int c = 0; c < n; c++) { // untuk setiap kolom
                cellId[r][c] = -1; // awalnya anggep bukan cell kosong
                if (map[r][c] == HOUSE) { // kalau cell itu rumah
                    houseRow[hi] = r; // simpen barisnya
                    houseCol[hi] = c; // simpen kolomnya
                    hi++; // lanjut ke rumah berikutnya
                } else if (map[r][c] == EMPTY) { // kalau cell itu kosong
                    cellId[r][c] = ei; // kasih nomor buat cell ini
                    emptyRow[ei] = r; // simpen barisnya
                    emptyCol[ei] = c; // simpen kolomnya
                    ei++; // lanjut ke nomor berikutnya
                }
            }
        }
    }

    // return true kalau sel (r,c) adalah cell kosong -> 0-based
    public boolean isEmpty(int r, int c) {
        return map[r][c] == EMPTY; // check isi cell
    }

    /**
     * ngubah koordinat (x,y) yang dari soal jadi nomor cell kosong
     * x = kolom, y = baris, dua-duanya 1-based, (1,1) = kiri atas
     * @throws IllegalArgumentException kalau koordinat di luar peta atau bukan cell kosong
     */

    public int idFromXY(int x, int y) {
        int r = y - 1; // ubah y (1-based) jadi baris (0-based)
        int c = x - 1; // ubah x (1-based) jadi kolom (0-based)

        if (r < 0 || r >= m || c < 0 || c >= n) { // check dulu apakah koordinat itu ada di dalem peta
            throw new IllegalArgumentException("Koordinat (" + x + "," + y + ") di luar peta");
        }

        if (!isEmpty(r, c)) { // check dulu apakah cell itu kosong
            throw new IllegalArgumentException("Koordinat (" + x + "," + y + ") bukan cell kosong");
        }

        return cellId[r][c]; // return nomor cellnya
    }
}
