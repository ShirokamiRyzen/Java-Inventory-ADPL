# 📦 Aplikasi Manajemen Inventaris Gudang

Aplikasi manajemen inventaris barang gudang desktop berbasis **Java** dengan antarmuka grafis (**GUI**) modern menggunakan **javax.swing** dan **FlatLaf Look & Feel**. Data disimpan dalam database **MariaDB** yang dapat dikonfigurasi secara fleksibel (mendukung koneksi server lokal maupun cloud) melalui berkas konfigurasi `db.properties`.

Aplikasi ini diimplementasikan berdasarkan diagram spesifikasi sistem yang diberikan (ERD, Use Case, Activity Diagram, dan Context Diagram) untuk mendukung 3 peran pengguna: **Admin Sistem**, **Admin Gudang**, dan **Pemilik**.

> [!IMPORTANT]
> * Untuk panduan langkah instalasi lengkap dan kredensial uji coba, silakan buka: **[installation.md](file:///e:/Codes/Github/5--JAVA/Java-Inventory/installation.md)**
> * Untuk panduan lengkap pengoperasian Database CLI Utility (`migrate`, `fresh`, `seed`, `clear`), silakan buka: **[database-cli-utility.md](file:///e:/Codes/Github/5--JAVA/Java-Inventory/database-cli-utility.md)**

---

## 🔑 Akun Uji Coba (Credentials)
Setelah database diinisialisasi otomatis pada peluncuran pertama, Anda dapat masuk menggunakan salah satu akun berikut untuk menguji fitur berdasarkan peran masing-masing:

| Peran (Role) | Username | Password | Deskripsi Otoritas |
| :--- | :--- | :--- | :--- |
| **Admin Sistem** | `admin` | `admin123` | Manajemen penuh barang (CRUD), manajemen akun user, pengajuan pembelian, dashboard & monitoring. |
| **Admin Gudang** | `gudang` | `gudang123` | Pencatatan barang masuk (restock), pencatatan barang keluar (distribusi), monitoring stok, laporan. |
| **Pemilik (Owner)** | `pemilik` | `pemilik123` | Verifikasi (Persetujuan/Penolakan) pengajuan pembelian, penyesuaian harga barang, cetak laporan audit. |

---

## 🛠️ Persyaratan Sistem & Dependensi
Aplikasi ini berjalan mandiri tanpa Maven/Gradle di environment target dengan menyertakan JAR berikut di direktori `lib/` (diunduh otomatis):
- **FlatLaf (v3.5.1)**: Look & feel modern bergaya IntelliJ Dark Mode (Slate/Indigo).
- **MariaDB Java Client (v3.5.1)**: Driver konektor database relasional MariaDB.
- **SLF4J API & Simple (v2.0.12)**: Library API logging standar.

---

## 🚀 Cara Menjalankan & Mengelola Aplikasi

Aplikasi ini dilengkapi dengan skrip automasi PowerShell untuk mempermudah pembangunan dan eksekusi:

### 1. Kompilasi Source Code
Buka terminal PowerShell di folder proyek ini dan jalankan perintah:
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```
Skrip ini akan secara otomatis memindai semua file `.java` di dalam subfolder `src/` dan mengompilasinya ke direktori `bin/` tanpa Byte Order Mark (BOM) issue.

### 2. Jalankan Aplikasi
Jalankan skrip peluncur berikut di PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File run.ps1
```
Skrip akan memeriksa hasil kompilasi dan langsung memicu jalannya aplikasi GUI.

### 3. Reset Database (Fresh Migrate)
Untuk melakukan pengosongan database secara total (menghapus database lama, melakukan kompilasi ulang bersih, dan melakukan *seeding* ulang hanya pada 3 akun uji coba utama), jalankan skrip berikut:
```powershell
powershell -ExecutionPolicy Bypass -File migrate-fresh.ps1
```

### 4. Mengisi Data Uji Coba Sembako (Optional Seeder)
Jika Anda ingin mengisi database dengan banyak data uji coba (berkaitan dengan **Toko Sembako** seperti beras, telur, minyak, terigu, kopi, sabun, transaksi barang masuk/keluar, serta pengajuan pembelian berstatus *Pending* untuk menguji fitur verifikasi Pemilik), jalankan skrip berikut:
```powershell
powershell -ExecutionPolicy Bypass -File seed.ps1
```

---

## 📐 Fitur Utama & Kesesuaian Diagram

### 1. Struktur Database Relasional (Kesesuaian ERD)
Skema MariaDB diimplementasikan di [DatabaseHelper.java](file:///e:/Codes/Github/5--JAVA/Java-Inventory/src/com/inventory/database/DatabaseHelper.java) mencakup tabel-tabel berikut. Secara default, database diisi secara bersih (**hanya akun pengguna**) tanpa barang tiruan agar data tetap segar (fresh):
- **`users`**: Menyimpan kredensial login, nama lengkap, dan peran akses.
- **`barang`**: Data master barang (Kode, Nama, Kategori, Harga, Stok).
- **`barang_masuk`**: Pencatatan unit masuk (Supplier, Tanggal, Jumlah) dengan relasi Foreign Key ke barang.
- **`barang_keluar`**: Pencatatan unit keluar (Penerima, Tanggal, Jumlah) untuk melacak penyerahan barang keluar.
- **`pengajuan_pembelian`**: Pengajuan restock dengan kolom status (`Pending`, `Disetujui`, `Ditolak`).
- **`laporan`**: Audit log untuk pencetakan laporan.

### 2. Alur Pengajuan & Penerimaan (Kesesuaian DFD & Activity Diagram)
- **Cek & Ajukan**: Admin Gudang/Sistem dapat melihat tabel "Peringatan Stok Menipis" di dashboard. Tombol **Buat Pengajuan Baru** di tab Pengajuan memungkinkan pembuatan request restock.
- **Analisis & Pesanan**: Pemilik menganalisis kebutuhan melalui dashboard dan tab Pengajuan, lalu mengeklik tombol **Setujui** atau **Tolak**.
- **Kelola Data Baru oleh Pemilik (Sesuai DFD Level 0 & Diagram Konteks)**: Pemilik dapat mendaftarkan barang baru ("Data barang baru") dan menyunting detail barang. Jika menyunting barang yang sudah ada, Pemilik dibatasi hanya dapat mengubah harga ("Perubahan harga barang") demi integritas persediaan.
- **Penerimaan Barang**: Saat barang dikirim supplier, Admin Gudang menginput data melalui **Barang Masuk**. Jumlah stok di tabel `barang` secara otomatis ditambahkan melalui mekanisme SQL Transaction yang aman di [BarangMasukDAO.java](file:///e:/Codes/Github/5--JAVA/Java-Inventory/src/com/inventory/dao/BarangMasukDAO.java).
- **Penyesuaian Harga**: Setelah barang masuk diterima, Pemilik dapat masuk ke menu **Master Barang** dan mengeklik **Sesuaikan Harga** untuk memperbarui harga jual barang sesuai pasar.

### 3. Tampilan GUI Premium, Keamanan & Fitur Audit (Aesthetics, Security & Use Cases)
- **Skema Warna HSL Sleek**: Menggunakan FlatLaf Dark Mode yang dimodifikasi dengan latar belakang Slate (`#0f172a` / `#1e293b`) dan warna aksen Indigo (`#6366f1`).
- **Visualisasi Dinamis**: Menyertakan komponen grafik [StockChartComponent.java](file:///e:/Codes/Github/5--JAVA/Java-Inventory/src/com/inventory/ui/components/StockChartComponent.java) yang menggambar chart balok (bar chart) tingkat stok barang teratas langsung menggunakan Java2D.
- **Laporan Sesuai Periode & Cetak Fisik (Sesuai Use Case "Lihat Laporan Gudang" & "Cetak Laporan Bulanan")**: Menu Laporan menyertakan filter tanggal "Mulai" dan "Selesai" untuk membatasi rentang data transaksi masuk/keluar yang ditampilkan (Use Case: Tampilkan). Pemilik dan Admin juga dapat mengeklik tombol **Cetak Laporan** untuk mencetak laporan resmi dalam format cetak fisik kertas atau ekspor ke PDF secara langsung dari JTextArea.
- **Vector Icons Nativ (`MenuIcon`)**: Untuk memastikan kompatibilitas tinggi di seluruh platform Windows (tanpa ada kotak kosong akibat keterbatasan font unicode OS), sidebar menggunakan kelas renderer visual khusus `MenuIcon` di [MainFrame.java](file:///e:/Codes/Github/5--JAVA/Java-Inventory/src/com/inventory/ui/MainFrame.java) yang menggambar chart, map folder, profil user, panah, dan checklist secara dinamis.
- **Layout Stabil & Responsif**: Menggunakan `GridLayout` di [PanelLaporan.java](file:///e:/Codes/Github/5--JAVA/Java-Inventory/src/com/inventory/ui/PanelLaporan.java) untuk memastikan pemisah kolom preview audit selalu terbagi seimbang 50%-50% dan tidak pernah ter-reset atau menciut ketika Anda berpindah-pindah tab.
- **Keamanan Stok**: Field `Stok` pada form ubah barang dinonaktifkan secara paksa demi integritas data. Stok hanya dapat bertambah lewat pencatatan barang masuk, dan berkurang lewat barang keluar.
- **Proteksi Hapus Diri Sendiri**: Sistem melarang admin menghapus akun miliknya sendiri yang sedang login untuk mencegah kerusakan hak akses.

---

## 📂 Struktur Direktori Proyek
```text
Java-Inventory/
├── lib/                      # JAR Library dependensi (FlatLaf, SQLite, SLF4J)
├── src/                      # Source code aplikasi Java
│   └── com/inventory/
│       ├── main/             # Entry point (Main.java)
│       ├── database/         # Database helper & Database init
│       ├── model/            # Entitas POJO data model
│       ├── dao/              # Data Access Objects (SQL query CRUD)
│       └── ui/               # JFrame & JPanels antarmuka aplikasi
│           ├── theme/        # Tema warna & font styling FlatLaf
│           └── components/   # Widget custom (Card, Grafik Chart)
├── bin/                      # Hasil kompilasi (.class files)
├── build.ps1                 # Skrip build PowerShell compiler
├── run.ps1                   # Skrip peluncur eksekusi aplikasi
├── db.ps1                    # Skrip pembungkus Database CLI Utility
├── migrate-fresh.ps1         # Skrip reset database (fresh migrate)
├── seed.ps1                  # Skrip mengisi data seeder Toko Sembako
├── installation.md           # Panduan instalasi dan setup langkah demi langkah
├── database-cli-utility.md   # Panduan perintah pengelolaan database CLI
└── README.md                 # Dokumentasi utama panduan aplikasi
```
