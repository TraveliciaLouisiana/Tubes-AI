
package experiment; // class ini berada di package experiment

import java.io.*; // untuk menulis file peta
import java.util.*; // untuk List, Random, ArrayDeque, dan lain-lain
import map.Grid; // untuk menghitung statistik peta seperti jumlah rumah dan sel kosong

// class untuk membuat peta uji dengan jenis ACAK, CLUSTER, dan LABIRIN (peta dibuat dalam beberapa ukuran kemudian disimpan sebagai file)
public class MapGenerator {

    //menyimpan daftar ukuran peta yang dapat digunakan
    public static final String[] UKURAN = {"tiny", "small", "normal", "large"};

    //menyimpan nama jenis peta agar penulisannya konsisten
    public static final String ACAK = "ACAK";
    public static final String CLUSTER = "CLUSTER";
    public static final String LABIRIN = "LABIRIN";

    //menentukan panjang sisi peta berdasarkan ukuran yang dipilih
    //peta berbentuk persegi sehingga jumlah baris dan kolom sama
    public static int sisi(String ukuran) {

        //memberi label panjang sisi berdasarkan nama ukuran peta
        switch (ukuran) {
            case "tiny": return 10; // ukuran 10 x 10
            case "small": return 20; // ukuran 20 x 20
            case "normal": return 40; // ukuran 40 x 40
            case "large": return 80; // ukuran 80 x 80

            // menghentikan program jika ukuran tidak dikenal
            default: throw new IllegalArgumentException("Ukuran tidak dikenal: " + ukuran);
        }
    }

    //menentukan jumlah fire station yang dibutuhkan berdasarkan ukuran peta
    public static int jumlahFS(String ukuran) {

        //memilih jumlah fire station sesuai ukuran peta
        switch (ukuran) {
            case "tiny": return 3;
            case "small": return 10;
            case "normal": return 24;
            case "large": return 40;

            // menghentikan program jika ukuran tidak dikenal
            default: throw new IllegalArgumentException("Ukuran tidak dikenal: " + ukuran);
        }
    }

    // menentukan batas jumlah evaluasi fitness berdasarkan ukuran peta
    public static int budget(String ukuran) {

        // memilih budget sesuai ukuran peta
        switch (ukuran) {
            case "tiny": return 10000;
            case "small": return 50000;
            case "normal": return 200000;
            case "large": return 500000;

            // menghentikan program jika ukuran tidak dikenal
            default: throw new IllegalArgumentException("Ukuran tidak dikenal: " + ukuran);
        }
    }

    // membuat satu peta, menyimpannya ke file, lalu mengembalikan informasinya
    // tahap digunakan untuk menentukan folder penyimpanan peta
    // nomor digunakan untuk membedakan nama file dan seedPeta menentukan isi peta
    public static InfoPeta buat(String tahap, String ukuran, String tipe, int nomor, long seedPeta, String folder) throws IOException {

        // mengambil panjang sisi peta berdasarkan ukuran yang dipilih
        int s = sisi(ukuran);

        // mengambil jumlah fire station yang dibutuhkan
        int p = jumlahFS(ukuran);

        // menyiapkan variabel untuk menyimpan isi peta dalam bentuk baris dan kolom
        // nilainya null karena peta belum dibuat
        char[][] peta = null;

        // menyimpan seed yang digunakan untuk membuat peta
        // seed dapat berubah jika peta yang dihasilkan belum memenuhi syarat
        long seedDipakai = seedPeta;

        // mengulangi pembuatan peta sampai mendapatkan peta yang memenuhi syarat
        // batas 1000 percobaan digunakan agar program tidak terus mengulang tanpa batas
        for (int coba = 0; coba < 1000; coba++) {

            // membuat objek Random berdasarkan seed agar peta dapat dibuat ulang
            Random rnd = new Random(seedDipakai);

            // memeriksa jenis peta yang akan dibuat
            if (tipe.equals(ACAK)) {

                // membuat peta dengan penempatan rumah, pohon, dan sel kosong secara acak
                peta = petaAcak(s, rnd);

            } else if (tipe.equals(CLUSTER)) {

                // membuat peta dengan rumah yang berkumpul di beberapa pusat
                peta = petaCluster(s, rnd);

            } else if (tipe.equals(LABIRIN)) {

                // membuat peta berbentuk labirin dengan lorong dan rumah
                peta = petaLabirin(s, rnd);

            } else {

                // menghentikan program jika jenis peta tidak dikenal
                throw new IllegalArgumentException("Tipe peta tidak dikenal: " + tipe);
            }

            // membuat objek Grid untuk menghitung jumlah rumah dan sel kosong
            // menggunakan class Grid yang dibuat oleh anggota kelompok lain
            Grid g = new Grid(s, s, p, peta);

            // memeriksa apakah peta memenuhi syarat untuk digunakan
            // jumlah rumah minimal 1 dan sel kosong minimal dua kali jumlah fire station
            if (g.numHouses >= 1 && g.numEmpty >= 2 * p) {

                // menghentikan perulangan karena peta sudah memenuhi syarat
                break;
            }

            // mengganti seed agar percobaan berikutnya menghasilkan peta berbeda
            seedDipakai++;

            // mengosongkan variabel peta karena peta sebelumnya tidak memenuhi syarat
            peta = null;
        }

        // memeriksa apakah pembuatan peta gagal setelah 1000 percobaan
        if (peta == null) {

            // menghentikan proses dan menampilkan pesan kesalahan
            throw new IllegalStateException("Gagal membuat peta " + ukuran + " " + tipe);
        }

        // membuat nama file berdasarkan ukuran, jenis, dan nomor peta
        // contoh: tiny_acak_1.txt
        String nama = ukuran + "_" + tipe.toLowerCase() + "_" + nomor + ".txt";

        // menentukan lokasi penyimpanan file berdasarkan folder dan tahap percobaan
        File f = new File(folder, tahap.toLowerCase() + File.separator + nama);

        // membuat folder penyimpanan jika belum tersedia
        f.getParentFile().mkdirs();

        // membuka file untuk menuliskan isi peta
        // try digunakan agar file otomatis ditutup setelah selesai ditulis
        try (PrintWriter out = new PrintWriter(new FileWriter(f))) {

            // menuliskan informasi ukuran peta dan jumlah fire station pada baris pertama
            // format: jumlah baris, jumlah kolom, jumlah fire station
            out.println(s + " " + s + " " + p);

            // melakukan perulangan untuk menuliskan setiap baris peta
            for (int r = 0; r < s; r++) {

                // mengubah isi baris dari array karakter menjadi String lalu menuliskannya ke file
                out.println(new String(peta[r]));
            }
        }

        // membuat kembali objek Grid untuk menghitung statistik dari peta yang sudah jadi
        Grid g = new Grid(s, s, p, peta);

        // membuat objek InfoPeta untuk menyimpan informasi peta ke dalam katalog
        InfoPeta info = new InfoPeta();

        // mengisi informasi peta berdasarkan data yang sudah dibuat
        info.tahap = tahap; // tahap pembuatan peta

        // menyimpan lokasi file dengan tanda / agar format path konsisten
        info.file = f.getPath().replace('\\', '/');

        info.ukuran = ukuran; // ukuran peta
        info.tipe = tipe; // jenis peta
        info.baris = s; // jumlah baris peta
        info.kolom = s; // jumlah kolom peta
        info.p = p; // jumlah fire station
        info.rumah = g.numHouses; // jumlah rumah pada peta

        // menghitung jumlah pohon dari total sel dikurangi rumah dan sel kosong
        info.pohon = s * s - g.numHouses - g.numEmpty;

        info.kosong = g.numEmpty; // jumlah sel kosong

        // menghitung jumlah rumah yang tidak memiliki tetangga berupa sel kosong
        info.rumahTerisolasi = hitungRumahTerisolasi(g);

        // menghitung jumlah kelompok sel kosong yang terpisah satu sama lain
        info.komponenKosong = hitungKomponenKosong(g);

        // menyimpan seed yang benar-benar digunakan untuk membuat peta
        info.seedPeta = seedDipakai;

        // mengembalikan informasi peta agar dapat dimasukkan ke katalog
        return info;
    }

    // membuat seluruh 30 peta untuk kebutuhan eksperimen
    // 12 peta untuk TAHAP1, 12 peta untuk TAHAP2, dan 6 peta large untuk pengujian
    // seluruh informasi peta kemudian disimpan ke dalam katalog CSV
    public static List<InfoPeta> buatSemua(String folder, long seedDasar) throws IOException {

        // List untuk menampung informasi dari seluruh peta yang berhasil dibuat
        List<InfoPeta> semua = new ArrayList<>();

        // menentukan komposisi jenis peta untuk setiap ukuran
        // terdapat 2 peta acak, 1 cluster, dan 1 labirin
        String[] tipe4 = {ACAK, ACAK, CLUSTER, LABIRIN};

        // menentukan ukuran peta yang digunakan pada tahap 1 dan tahap 2
        String[] ukuran3 = {"tiny", "small", "normal"};

        // penghitung untuk menghasilkan seed yang berbeda pada setiap peta
        long hitung = 0;

        // membuat peta untuk dua tahap percobaan dengan komposisi yang sama
        for (String tahap : new String[]{"TAHAP1", "TAHAP2"}) {

            // mengulangi proses untuk setiap ukuran peta
            for (String u : ukuran3) {

                // membuat array penghitung nomor untuk setiap jenis peta
                // indeks 0 untuk acak, 1 untuk cluster, dan 2 untuk labirin
                int[] nomorPerTipe = new int[3];

                // mengulangi proses untuk setiap jenis peta
                for (String t : tipe4) {

                    // menentukan indeks jenis peta agar nomor masing-masing jenis dapat dihitung
                    int idx = t.equals(ACAK) ? 0 : (t.equals(CLUSTER) ? 1 : 2);

                    // menambah nomor peta berdasarkan jenisnya
                    // khusus peta acak akan memiliki nomor 1 dan 2
                    nomorPerTipe[idx]++;

                    // menambah penghitung agar setiap peta memiliki seed berbeda
                    hitung++;

                    // membuat peta menggunakan tahap, ukuran, jenis, nomor, dan seed yang ditentukan
                    // informasi peta yang berhasil dibuat langsung dimasukkan ke List semua
                    semua.add(buat(tahap, u, t, nomorPerTipe[idx], seedDasar * 1000 + hitung, folder));
                }
            }
        }

        // menentukan komposisi enam peta large untuk tahap pengujian
        // terdiri dari 2 acak, 2 cluster, dan 2 labirin
        String[] tipe6 = {ACAK, ACAK, CLUSTER, CLUSTER, LABIRIN, LABIRIN};

        // penghitung nomor untuk setiap jenis peta large
        int[] nomor6 = new int[3];

        // membuat seluruh peta large untuk tahap pengujian
        for (String t : tipe6) {

            // menentukan indeks jenis peta
            int idx = t.equals(ACAK) ? 0 : (t.equals(CLUSTER) ? 1 : 2);

            // menambah nomor peta sesuai jenisnya
            nomor6[idx]++;

            // menggunakan seed berikutnya agar berbeda dari peta sebelumnya
            hitung++;

            // membuat peta large dan menyimpan informasinya ke dalam List semua
            semua.add(buat("PENGUJIAN", "large", t, nomor6[idx], seedDasar * 1000 + hitung, folder));
        }

        // menulis seluruh informasi peta ke dalam file katalog CSV
        InfoPeta.tulisKatalog(folder + "/katalog.csv", semua);

        // mengembalikan daftar seluruh informasi peta yang sudah dibuat
        return semua;
    }

    // ---------- tiga jenis peta ----------

    // membuat peta acak dengan peluang rumah 15%, pohon 20%, dan kosong 65%
    // setiap sel ditentukan secara terpisah sehingga posisi isinya tidak saling bergantung
    private static char[][] petaAcak(int s, Random rnd) {

        // membuat array dua dimensi untuk menyimpan isi peta
        // ukuran baris dan kolom mengikuti nilai s
        char[][] peta = new char[s][s];

        // melakukan perulangan untuk setiap baris peta
        for (int r = 0; r < s; r++) {

            // melakukan perulangan untuk setiap kolom peta
            for (int c = 0; c < s; c++) {

                // menghasilkan angka acak antara 0 sampai kurang dari 1
                // angka ini digunakan untuk menentukan isi setiap sel
                double x = rnd.nextDouble();

                // jika angka kurang dari 0.15, sel menjadi rumah
                if (x < 0.15) {
                    peta[r][c] = Grid.HOUSE;

                // jika angka minimal 0.15 dan kurang dari 0.35, sel menjadi pohon
                } else if (x < 0.35) {
                    peta[r][c] = Grid.TREE;

                // jika tidak memenuhi dua kondisi sebelumnya, sel menjadi kosong
                } else {
                    peta[r][c] = Grid.EMPTY;
                }
            }
        }

        // mengembalikan peta acak yang sudah selesai dibuat
        return peta;
    }

    // membuat peta cluster dengan rumah yang berkumpul di sekitar beberapa pusat
    // semakin jauh dari pusat cluster, peluang rumah akan semakin kecil
    private static char[][] petaCluster(int s, Random rnd) {

        // menentukan jumlah pusat cluster berdasarkan ukuran peta
        // Math.max digunakan agar jumlah pusat minimal 2
        int k = Math.max(2, s / 10);

        // menyimpan posisi baris dari setiap pusat cluster
        int[] pr = new int[k];

        // menyimpan posisi kolom dari setiap pusat cluster
        int[] pc = new int[k];

        // menentukan posisi setiap pusat cluster secara acak
        for (int i = 0; i < k; i++) {

            // menentukan posisi baris pusat cluster
            pr[i] = rnd.nextInt(s);

            // menentukan posisi kolom pusat cluster
            pc[i] = rnd.nextInt(s);
        }

        // menentukan lebar penyebaran rumah di sekitar pusat cluster
        // nilai ini digunakan untuk menghitung peluang munculnya rumah
        double sigma = s / 8.0;

        // membuat array dua dimensi untuk menyimpan isi peta cluster
        char[][] peta = new char[s][s];

        // memeriksa setiap baris peta
        for (int r = 0; r < s; r++) {

            // memeriksa setiap kolom peta
            for (int c = 0; c < s; c++) {

                // menyimpan jarak kuadrat terkecil dari sel ke pusat cluster
                // nilai awal dibuat sangat besar agar dapat diganti oleh jarak sebenarnya
                double dMin = Double.MAX_VALUE;

                // mencari pusat cluster yang paling dekat dengan sel saat ini
                for (int i = 0; i < k; i++) {

                    // menghitung selisih posisi baris antara sel dan pusat cluster
                    double dr = r - pr[i];

                    // menghitung selisih posisi kolom antara sel dan pusat cluster
                    double dc = c - pc[i];

                    // menghitung jarak kuadrat dan menyimpan nilai terkecil
                    // jarak kuadrat digunakan agar tidak perlu menghitung akar
                    dMin = Math.min(dMin, dr * dr + dc * dc);
                }

                // menghitung peluang munculnya rumah berdasarkan jarak ke pusat terdekat
                // peluang lebih besar di dekat pusat dan semakin kecil jika semakin jauh
                double peluangRumah = 0.60 * Math.exp(-dMin / (2 * sigma * sigma));

                // menentukan apakah sel menjadi rumah berdasarkan peluang yang dihitung
                if (rnd.nextDouble() < peluangRumah) {
                    peta[r][c] = Grid.HOUSE;

                // jika bukan rumah, ada peluang 12% untuk menjadi pohon
                } else if (rnd.nextDouble() < 0.12) {
                    peta[r][c] = Grid.TREE;

                // jika tidak menjadi rumah atau pohon, sel menjadi kosong
                } else {
                    peta[r][c] = Grid.EMPTY;
                }
            }
        }

        // mengembalikan peta cluster yang sudah selesai dibuat
        return peta;
    }

    // membuat peta labirin dengan pohon sebagai dinding dan sel kosong sebagai lorong
    // lorong dibuat menggunakan metode recursive backtracker
    // sebagian dinding yang menempel pada lorong kemudian diubah menjadi rumah
    private static char[][] petaLabirin(int s, Random rnd) {

        // membuat array dua dimensi untuk menyimpan isi peta labirin
        char[][] peta = new char[s][s];

        // mengisi seluruh sel peta dengan pohon sebagai dinding awal
        for (char[] baris : peta) {
            Arrays.fill(baris, Grid.TREE);
        }

        // menyimpan tanda apakah suatu sel lorong sudah pernah digali
        // true berarti sudah dikunjungi dan false berarti belum
        boolean[][] dikunjungi = new boolean[s][s];

        // membuat tumpukan untuk menyimpan posisi sel yang sedang ditelusuri
        // tumpukan digunakan untuk melanjutkan atau mundur ketika menemukan jalan buntu
        ArrayDeque<int[]> tumpukan = new ArrayDeque<>();

        // menentukan titik awal pembuatan lorong pada posisi baris 1 kolom 1
        dikunjungi[1][1] = true;

        // mengubah titik awal menjadi sel kosong agar menjadi bagian dari lorong
        peta[1][1] = Grid.EMPTY;

        // memasukkan posisi awal ke dalam tumpukan
        tumpukan.push(new int[]{1, 1});

        // menyimpan perubahan posisi baris untuk empat arah gerakan
        // nilai -2 dan 2 berarti bergerak dua sel ke atas atau bawah
        int[] dr = {-2, 2, 0, 0};

        // menyimpan perubahan posisi kolom untuk empat arah gerakan
        // nilai -2 dan 2 berarti bergerak dua sel ke kiri atau kanan
        int[] dc = {0, 0, -2, 2};

        // mengulangi proses selama masih ada posisi yang tersimpan di tumpukan
        while (!tumpukan.isEmpty()) {

            // mengambil posisi paling atas dari tumpukan tanpa menghapusnya
            // posisi ini menjadi titik awal untuk mencari lorong berikutnya
            int[] cur = tumpukan.peek();

            // menampung arah yang masih bisa digunakan untuk menggali lorong
            List<Integer> arah = new ArrayList<>();

            // memeriksa empat arah gerakan yaitu atas, bawah, kiri, dan kanan
            for (int a = 0; a < 4; a++) {

                // menghitung posisi baris tujuan berdasarkan arah yang diperiksa
                int nr = cur[0] + dr[a];

                // menghitung posisi kolom tujuan berdasarkan arah yang diperiksa
                int nc = cur[1] + dc[a];

                // memeriksa apakah posisi tujuan masih berada di dalam batas peta
                // sekaligus memastikan sel tujuan belum pernah digali
                if (nr >= 1 && nr <= s - 2 && nc >= 1 && nc <= s - 2 && !dikunjungi[nr][nc]) {

                    // menambahkan arah yang memenuhi syarat ke dalam daftar arah
                    arah.add(a);
                }
            }

            // memeriksa apakah tidak ada lagi arah yang bisa digunakan
            if (arah.isEmpty()) {

                // menghapus posisi terakhir dari tumpukan untuk mundur ke posisi sebelumnya
                tumpukan.pop();

            } else {

                // memilih salah satu arah yang tersedia secara acak
                int a = arah.get(rnd.nextInt(arah.size()));

                // menghitung posisi baris tujuan berdasarkan arah yang dipilih
                int nr = cur[0] + dr[a];

                // menghitung posisi kolom tujuan berdasarkan arah yang dipilih
                int nc = cur[1] + dc[a];

                // mengubah dinding di antara posisi sekarang dan tujuan menjadi lorong
                // pembagian dua digunakan karena jarak gerakan adalah dua sel
                peta[cur[0] + dr[a] / 2][cur[1] + dc[a] / 2] = Grid.EMPTY;

                // mengubah sel tujuan menjadi lorong
                peta[nr][nc] = Grid.EMPTY;

                // menandai bahwa sel tujuan sudah pernah digali
                dikunjungi[nr][nc] = true;

                // memasukkan posisi tujuan ke tumpukan untuk melanjutkan pembuatan lorong
                tumpukan.push(new int[]{nr, nc});
            }
        }

        // setelah lorong selesai dibuat, sebagian dinding yang menempel pada lorong
        // akan dipilih untuk diubah menjadi rumah

        // menyimpan perubahan posisi baris untuk memeriksa empat tetangga
        int[] ar = {-1, 1, 0, 0};

        // menyimpan perubahan posisi kolom untuk memeriksa empat tetangga
        int[] ac = {0, 0, -1, 1};

        // menampung posisi dinding yang berpotensi diubah menjadi rumah
        List<int[]> calon = new ArrayList<>();

        // memeriksa setiap baris peta
        for (int r = 0; r < s; r++) {

            // memeriksa setiap kolom peta
            for (int c = 0; c < s; c++) {

                // hanya sel pohon yang dapat dipilih menjadi calon rumah
                // jika bukan pohon, lanjut memeriksa sel berikutnya
                if (peta[r][c] != Grid.TREE) {
                    continue;
                }

                // memeriksa empat tetangga dari sel pohon tersebut
                for (int a = 0; a < 4; a++) {

                    // menghitung posisi baris tetangga
                    int nr = r + ar[a];

                    // menghitung posisi kolom tetangga
                    int nc = c + ac[a];

                    // memastikan posisi tetangga berada di dalam peta
                    // dan merupakan sel kosong atau bagian dari lorong
                    if (nr >= 0 && nr < s && nc >= 0 && nc < s && peta[nr][nc] == Grid.EMPTY) {

                        // memasukkan posisi pohon ke daftar calon rumah
                        calon.add(new int[]{r, c});

                        // menghentikan pemeriksaan karena sudah ditemukan satu lorong di sekitarnya
                        break;
                    }
                }
            }
        }

        // memeriksa setiap dinding yang sudah masuk ke daftar calon rumah
        for (int[] x : calon) {

            // memberikan peluang 25% agar dinding tersebut berubah menjadi rumah
            if (rnd.nextDouble() < 0.25) {

                // mengubah isi sel yang terpilih menjadi rumah
                peta[x[0]][x[1]] = Grid.HOUSE;
            }
        }

        // mengembalikan peta labirin yang sudah selesai dibuat
        return peta;
    }

    // menghitung jumlah rumah yang tidak memiliki tetangga berupa sel kosong
    // rumah seperti ini dianggap terisolasi karena tidak dapat dijangkau dari empat arah
    private static int hitungRumahTerisolasi(Grid g) {

        // menyimpan perubahan posisi baris untuk memeriksa empat tetangga
        int[] ar = {-1, 1, 0, 0};

        // menyimpan perubahan posisi kolom untuk memeriksa empat tetangga
        int[] ac = {0, 0, -1, 1};

        // penghitung jumlah rumah yang tidak memiliki tetangga kosong
        int hasil = 0;

        // memeriksa setiap rumah yang terdapat pada peta
        for (int h = 0; h < g.numHouses; h++) {

            // menandai apakah rumah memiliki setidaknya satu tetangga kosong
            // awalnya false karena belum ada tetangga yang diperiksa
            boolean punyaTetanggaKosong = false;

            // memeriksa empat arah di sekitar rumah
            for (int a = 0; a < 4; a++) {

                // mengambil posisi baris rumah kemudian menambahkan arah pemeriksaan
                int nr = g.houseRow[h] + ar[a];

                // mengambil posisi kolom rumah kemudian menambahkan arah pemeriksaan
                int nc = g.houseCol[h] + ac[a];

                // memastikan posisi tetangga masih berada di dalam peta
                // sekaligus memeriksa apakah sel tersebut kosong
                if (nr >= 0 && nr < g.m && nc >= 0 && nc < g.n && g.isEmpty(nr, nc)) {
                    punyaTetanggaKosong = true; //rumah memiliki tetangga kosong
                }
            }

            // jika setelah memeriksa empat arah tidak ditemukan sel kosong
            if (!punyaTetanggaKosong) {

                // menambah jumlah rumah yang terisolasi
                hasil++;
            }
        }

        // mengembalikan jumlah rumah yang terisolasi
        return hasil;
    }

    // menghitung jumlah kelompok sel kosong yang saling terhubung
    // pemeriksaan dilakukan ke empat arah menggunakan metode flood fill
    private static int hitungKomponenKosong(Grid g) {

        // menyimpan perubahan posisi baris untuk memeriksa empat tetangga
        int[] ar = {-1, 1, 0, 0};

        // menyimpan perubahan posisi kolom untuk memeriksa empat tetangga
        int[] ac = {0, 0, -1, 1};

        // menandai sel yang sudah diperiksa agar tidak dihitung berulang kali
        boolean[][] sudah = new boolean[g.m][g.n];

        // menghitung jumlah kelompok sel kosong yang ditemukan
        int komponen = 0;

        // memeriksa seluruh baris peta
        for (int r = 0; r < g.m; r++) {

            // memeriksa seluruh kolom peta
            for (int c = 0; c < g.n; c++) {

                // melewati sel jika bukan sel kosong atau sudah pernah diperiksa
                if (!g.isEmpty(r, c) || sudah[r][c]) {
                    continue;
                }

                // menambah jumlah kelompok karena ditemukan sel kosong baru
                // sel ini menjadi awal kelompok yang akan ditelusuri
                komponen++;

                // membuat antrian untuk menampung sel yang akan diperiksa
                // antrian digunakan untuk menelusuri seluruh sel kosong yang saling terhubung
                ArrayDeque<int[]> antrian = new ArrayDeque<>();

                // memasukkan posisi sel kosong pertama ke dalam antrian
                antrian.add(new int[]{r, c});

                // menandai sel awal agar tidak diperiksa atau dimasukkan kembali
                sudah[r][c] = true;

                // menelusuri seluruh sel kosong yang terhubung dengan kelompok ini
                while (!antrian.isEmpty()) {

                    // mengambil dan menghapus sel paling depan dari antrian
                    int[] cur = antrian.poll();

                    // memeriksa empat tetangga dari sel yang sedang diproses
                    for (int a = 0; a < 4; a++) {

                        // menghitung posisi baris tetangga
                        int nr = cur[0] + ar[a];

                        // menghitung posisi kolom tetangga
                        int nc = cur[1] + ac[a];

                        // memeriksa apakah tetangga berada di dalam batas peta
                        // sel juga harus kosong dan belum pernah diperiksa
                        if (nr >= 0 && nr < g.m && nc >= 0 && nc < g.n && g.isEmpty(nr, nc) && !sudah[nr][nc]) {

                            // menandai sel sebagai sudah diperiksa
                            sudah[nr][nc] = true;

                            // memasukkan sel kosong tersebut ke antrian untuk diperiksa tetangganya
                            antrian.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }

        // mengembalikan jumlah kelompok sel kosong yang ditemukan
        return komponen;
    }
}
