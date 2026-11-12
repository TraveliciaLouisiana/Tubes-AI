import java.util.*;

/**
 * kelas ini buat nyimpen jarak terpendek dari setiap rumah ke setiap cell
 * kosong
 * dihitung sekali di awal sama bfs. hasilnya nanti dipake berulang kali sama
 * GA.
 * 
 * dist[h][e] = jarak dari rumah ke-h ke cell kosong nomor e
 * kalau ga bisa kecapai, nilainya penalti P = m x n
 */

public class DistanceMatrix {

    // arah gerak: atas, bawah, kiri, kanan
    private static final int[] DR = { -1, 1, 0, 0 }; // ini buat perubahan baris
    private static final int[] DC = { 0, 0, -1, 1 }; // ini buat perubahan kolom

    public final int[][] dist; // matriks jarak, ukuran [jumlah rumah][jumlah sel kosong]

    /**
     * ini buat matriks jarak dari sebuah grid.
     * parameter g itu peta yang sudah dibaca
     */
    public DistanceMatrix(Grid g) {
        dist = new int[g.numHouses][g.numEmpty]; // siapin dulu matriks sesuai ukuran
        for (int h = 0; h < g.numHouses; h++) { // jalanin BFS buat tiap rumah
            Arrays.fill(dist[h], g.penalty); // awalnya semua cell = penalti (belum terjangkau)
            bfsFromHouse(g, h); // isi jarak yang bener untuk rumah h
        }
    }

    /**
     * BFS dari satu rumah.
     * aturannya: cuma boleh lewat cell kosong. rumah dan pohon adalah penghalang.
     * rumah yang bersebelahan sama cell kosong dapet jarak 1.
     *
     * parameter g itu peta
     * parameter h itu nomor rumah yang dijadikan titik awal
     */
    private void bfsFromHouse(Grid g, int h) {
        int[][] d = new int[g.m][g.n]; // d[r][c] = jarak BFS dari rumah ke cell (r,c)
        for (int[] row : d)
            Arrays.fill(row, -1); // -1 artinya belum dikunjungi

        ArrayDeque<int[]> queue = new ArrayDeque<>(); // antrian BFS, isinya pasangan {baris, kolom}
        int sr = g.houseRow[h]; // baris rumah awal
        int sc = g.houseCol[h]; // kolom rumah awal
        d[sr][sc] = 0; // jarak rumah ke dirinya sendiri = 0
        queue.add(new int[] { sr, sc }); // masukkan rumah ke antrian

        while (!queue.isEmpty()) { // selama masih ada cell yang harus diproses
            int[] cur = queue.poll(); // ambil cell paling depan antrian
            int r = cur[0]; // baris cell sekarang
            int c = cur[1]; // kolom cell sekarang

            if (g.isEmpty(r, c)) { // kalau cell ini kosong, catet jaraknya
                dist[h][g.cellId[r][c]] = d[r][c]; // simpen jarak ke matriks
            }

            for (int k = 0; k < 4; k++) { // coba keempat tetangga
                int nr = r + DR[k]; // baris tetangga
                int nc = c + DC[k]; // kolom tetangga
                if (nr < 0 || nr >= g.m || nc < 0 || nc >= g.n)
                    continue; // di luar peta, lewati
                if (d[nr][nc] != -1)
                    continue; // sudah pernah dikunjungi, lewati
                if (g.map[nr][nc] == Grid.TREE || g.map[nr][nc] == Grid.HOUSE)
                    continue; // tembok, lewati
                d[nr][nc] = d[r][c] + 1; // jarak tetangga = jarak sekarang + 1 langkah
                queue.add(new int[] { nr, nc }); // masukkan tetangga ke antrean
            }
        }
    }
}