package map; // class ini berada di package map

/**
 * class CostCalculator buat ngitung cost untuk satu penempatan fire station.
 *
 * cost(S) = (1/|H|) * jumlah dari (jarak tiap rumah ke fire station terdekat)
 * dengan dist sudah berisi penalti untuk rumah yang tidak terjangkau.
 */
public class CostCalculator {

    private final int[][] dist; // matriks jarak dari DistanceMatrix
    private final int numHouses; // jumlah rumah

    /**
     * parameter dm untuk matriks jarak yang sudah dihitung
     */
    public CostCalculator(DistanceMatrix dm) {
        this.dist = dm.dist; // simpen referensi matriks jarak
        this.numHouses = dm.dist.length; // jumlah rumah = jumlah baris matriks
    }

    /**
     * mau hitung cost untuk satu set fire station.
     *
     * parameter fsCells itu daftar nomor cell kosong tempat fire station ditaruh (isinya p angka)
     * return nya nanti rata-rata jarak setiap rumah ke fire station terdekatnya
     */
    public double cost(int[] fsCells) {
        if (fsCells.length == 0) { // tidak ada fire station sama sekali
            throw new IllegalArgumentException("Minimal harus ada satu Fire Station");
        }
        double total = 0; // jumlah jarak semua rumah
        for (int h = 0; h < numHouses; h++) { // untuk setiap rumah
            total += nearestDistance(h, fsCells); // tambahkan jarak ke fire station terdekatnya
        }
        return total / numHouses; // rata-rata = jumlah dibagi banyaknya rumah
    }

    /**
     * jarak rumah h ke fire station terdekat.
     *
     * parameter h itu untuk nomor rumah
     * paramater fsCells itu untuk daftar nomor cell fire station
     * return nya jarak minimum (bisa kayak penalti kalau ga terjangkau)
     */
    public int nearestDistance(int h, int[] fsCells) {
        int best = Integer.MAX_VALUE; // mulai dari nilai terbesar biar gampang dibandingin
        for (int f : fsCells) { // check setiap fire station
            if (dist[h][f] < best) { // kalau jarak ke fire station ini lebih kecil
                best = dist[h][f]; // simpen sebagai jarak terbaik
            }
        }
        return best; // return jarak terdekat
    }
}
