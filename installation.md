# Panduan Instalasi Aplikasi Java Inventory

Dokumen ini berisi panduan langkah demi langkah untuk melakukan instalasi, setup dependensi, kompilasi, dan menjalankan aplikasi **Java Inventory (Sistem Manajemen Gudang & Aset)** di sistem operasi Windows menggunakan PowerShell.

---

## 📋 Persyaratan Sistem

Sebelum memulai, pastikan perangkat Anda telah terpasang perangkat lunak berikut:

1. **Java Development Kit (JDK) 21 atau lebih tinggi**
   - Pastikan perintah `java -version` dan `javac -version` dapat diakses dari command line/terminal Anda.
2. **Microsoft PowerShell 5.1** atau **PowerShell Core (7+)**
3. **Koneksi Internet** (hanya diperlukan saat pertama kali menjalankan skrip setup untuk mengunduh pustaka eksternal/dependensi `.jar`).

---

## 🛠️ Langkah-Langkah Instalasi & Setup

### Langkah 1: Unduh Dependensi dan Siapkan Direktori
Buka PowerShell di direktori utama proyek (`Java-Inventory`), kemudian jalankan perintah berikut untuk mengunduh pustaka visual (`FlatLaf`), driver database (`MariaDB Java Connector`), dan logger (`SLF4J`):

```powershell
powershell -ExecutionPolicy Bypass -File setup.ps1
```

*Skrip ini akan membuat folder `lib` dan `bin` secara otomatis, kemudian mengunduh pustaka-pustaka `.jar` yang diperlukan.*

---

### Langkah 2: Konfigurasi & Inisialisasi Database (Migrasi)
1. Buka berkas **[db.properties](file:///e:/Codes/Github/5--JAVA/Java-Inventory/db.properties)** di direktori utama proyek.
2. Sesuaikan konfigurasi host, port, nama database, username, dan password sesuai dengan server MariaDB Anda (baik lokal maupun di server cloud).
3. Jalankan perintah migrasi berikut untuk membuat database (jika belum ada) beserta seluruh tabel yang dibutuhkan:

```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 migrate
```

---

### Langkah 3: Pengisian Data Awal (Seeding - Opsional)
Jika Anda ingin mengisi database dengan contoh data transaksi toko sembako (seperti Beras, Minyak Goreng, Gula Pasir, dll.) untuk keperluan uji coba fitur, jalankan perintah berikut:

```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 seed
```

---

### Langkah 4: Jalankan Aplikasi
Jalankan skrip berikut untuk mengompilasi kode sumber Java dan meluncurkan GUI aplikasi secara langsung:

```powershell
powershell -ExecutionPolicy Bypass -File run.ps1
```

*Skrip ini akan secara otomatis mengompilasi seluruh file `.java` di bawah direktori `src/` ke dalam folder `bin/` sebelum menjalankan class utama `com.inventory.main.Main`.*

---

## 🔑 Akun Pengguna Bawaan (Default Accounts)

Setelah database berhasil diinisialisasi atau di-seed, Anda dapat masuk ke dalam sistem menggunakan salah satu dari akun dengan role berikut:

| No | Nama Akun | Username | Password | Hak Akses (Role) | Fitur Utama |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | **Administrator** | `admin` | `admin123` | **Admin Sistem** | Manajemen Pengguna & Audit |
| 2 | **Staf Gudang** | `gudang` | `gudang123` | **Admin Gudang** | Transaksi Barang Masuk/Keluar, Ajukan Pembelian |
| 3 | **Bapak Owner** | `pemilik` | `pemilik123` | **Pemilik** | Persetujuan Pengajuan & Cetak Laporan |

---

> [!NOTE]
> Seluruh pengaturan dimensi dan ukuran window aplikasi akan disimpan secara otomatis di file `window.properties` saat Anda berpindah halaman atau menutup aplikasi, sehingga ukuran window tetap konsisten saat aplikasi dibuka kembali.
