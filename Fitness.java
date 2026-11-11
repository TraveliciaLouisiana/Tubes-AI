/* class fitness untuk menghitung cost dan mengubahnya jadi fitness */
public final class Fitness {

    private Fitness() {
        // kelas ini hanya menyediakan method perhitungan statis saja
    }

    // method untuk menghitung rata-rata jarak rumah ke fire station terdekat
    public static double calculateCost(Chromosome chromosome, int[][] distanceMatrix, Grid grid) {
        // cek apakah ada parameter yang bernilai null untuk menghindari error
        if (chromosome == null || grid == null || distanceMatrix == null) {
            throw new IllegalArgumentException("chromosome, distancematrix, dan grid tidak boleh null");
        }
        // pastikan kromosom dalam kondisi valid sebelum dihitung cost-nya
        if (!chromosome.isValid()) {
            throw new IllegalArgumentException("kromosom harus valid sebelum cost dihitung");
        }
        // pastikan jumlah baris matriks jarak sesuai dengan total rumah di peta
        if (distanceMatrix.length != grid.numHouses) {
            throw new IllegalArgumentException("jumlah baris distancematrix harus sama dengan jumlah rumah");
        }

        // jika di peta tidak ada rumah sama sekali, cost langsung 0
        if (grid.numHouses == 0) {
            return 0.0;
        }

        // ambil daftar sel index fire station dari kromosom
        int[] selectedCells = chromosome.decode();
        // variabel untuk menampung total jarak keseluruhan rumah
        double totalDistance = 0.0;

        // lakukan perulangan untuk mengecek setiap rumah satu per satu
        for (int house = 0; house < grid.numHouses; house++) {
            // pastikan baris matriks jarak rumah tidak null dan ukurannya pas dengan sel kosong
            if (distanceMatrix[house] == null || distanceMatrix[house].length != grid.numEmpty) {
                throw new IllegalArgumentException("ukuran baris distancematrix tidak sesuai dengan jumlah sel kosong");
            }

            // inisialisasi jarak terdekat dengan nilai paling maksimal terlebih dahulu
            int nearestDistance = Integer.MAX_VALUE;

            // cari jarak terpendek dari rumah ini ke semua fire station yang dipilih
            for (int cellIndex : selectedCells) {
                int distance = distanceMatrix[house][cellIndex];
                // jika jarak valid dan lebih kecil dari jarak terdekat sebelumnya, perbarui nilainya
                if (distance >= 0 && distance < nearestDistance) {
                    nearestDistance = distance;
                }
            }

            // jika rumah tidak bisa mencapai fire station mana pun, tambahkan nilai penalti p = m x n
            if (nearestDistance == Integer.MAX_VALUE) {
                totalDistance += grid.penalty;
            } else {
                // jika terjangkau, tambahkan jarak terdekatnya ke total jarak
                totalDistance += nearestDistance;
            }
        }

        // hitung cost akhir dengan cara membagi total jarak dengan jumlah rumah
        return totalDistance / grid.numHouses;
    }

    // method untuk mengubah nilai cost menjadi fitness agar bernilai positif dan sesuai aturan ga
    public static double calculateFitness(double cost) {
        // validasi nilai cost agar berupa angka yang benar dan tidak negatif
        if (cost < 0.0 || Double.isNaN(cost) || Double.isInfinite(cost)) {
            throw new IllegalArgumentException("cost harus berupa angka valid dan tidak negatif");
        }
        // rumus fitness invers supaya cost yang kecil menghasilkan nilai fitness yang besar
        return 1.0 / (cost + 1.0);
    }
}