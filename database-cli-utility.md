# Dokumentasi Database CLI Utility

Aplikasi Java Inventory dilengkapi dengan utilitas antarmuka baris perintah (CLI) database yang terintegrasi. Utilitas ini terinspirasi oleh mekanisme migrasi pada framework modern (seperti `php artisan` pada Laravel) untuk mempermudah pengelolaan siklus hidup database MariaDB melalui berkas konfigurasi `db.properties`.

---

## 🚀 Cara Penggunaan

Utilitas ini dapat dipanggil langsung menggunakan skrip pembungkus PowerShell `db.ps1`:

```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 [perintah]
```

### 📋 Daftar Perintah

#### 1. `migrate`
Membuat tabel-tabel relasional yang dibutuhkan aplikasi di dalam database MariaDB. Jika tabel sudah ada, proses inisialisasi akan dilewati. Database akan dibuat secara otomatis jika belum ada di server.
```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 migrate
```

#### 2. `fresh`
Menghapus skema database MariaDB saat ini secara total dan membangun ulang struktur tabel dalam keadaan kosong (hanya berisi 3 akun pengguna bawaan: admin, gudang, pemilik).
```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 fresh
```
*Atau gunakan skrip pintasan:*
```powershell
powershell -ExecutionPolicy Bypass -File migrate-fresh.ps1
```

#### 3. `seed`
Mengisi database dengan dataset master barang toko sembako serta beberapa log transaksi (barang masuk, barang keluar, pengajuan pembelian) untuk keperluan testing fungsionalitas aplikasi.
```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 seed
```
*Atau gunakan skrip pintasan:*
```powershell
powershell -ExecutionPolicy Bypass -File seed.ps1
```

#### 4. `clear`
Mengosongkan semua transaksi dan data master barang dari database (tabel `barang`, `barang_masuk`, `barang_keluar`, `pengajuan_pembelian`, `laporan`), namun **tetap mempertahankan** data akun pengguna pada tabel `users`.
```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 clear
```

---

## 🗄️ Struktur Skema & Relasi Database

Database menggunakan mesin relasional **MariaDB** dengan skema sebagai berikut:

```mermaid
erDiagram
    users {
        INT id_user PK
        VARCHAR username UNIQUE
        VARCHAR password
        VARCHAR role
        VARCHAR nama_user
    }
    barang {
        VARCHAR id_barang PK
        VARCHAR nama_barang
        DOUBLE harga
        VARCHAR kategori
        INT stok
    }
    barang_masuk {
        VARCHAR id_barang_masuk PK
        VARCHAR tanggal_masuk
        VARCHAR id_barang FK
        INT jumlah_masuk
        VARCHAR supplier
    }
    barang_keluar {
        VARCHAR id_barang_keluar PK
        VARCHAR tanggal_keluar
        VARCHAR id_barang FK
        INT jumlah_keluar
        VARCHAR penerima
    }
    pengajuan_pembelian {
        VARCHAR id_pengajuan PK
        VARCHAR id_barang FK
        VARCHAR tanggal_pengajuan
        INT jumlah_pengajuan
        VARCHAR status_pengajuan
    }
    laporan {
        VARCHAR id_laporan PK
        VARCHAR tanggal_laporan
        VARCHAR jenis_laporan
        TEXT keterangan
    }

    barang ||--o{ barang_masuk : "distribusi masuk"
    barang ||--o{ barang_keluar : "distribusi keluar"
    barang ||--o{ pengajuan_pembelian : "permintaan restock"
```

---

## ⚙️ Logika Transaksi & Trigger

1. **Foreign Key Integrity**: Validasi foreign key diaktifkan dan dikelola oleh database MariaDB secara native agar relasi data tetap terjaga konsistensinya.
2. **On Delete Cascade**: Jika data barang pada tabel `barang` dihapus, seluruh log barang masuk, barang keluar, dan pengajuan pembelian yang berelasi dengan barang tersebut akan terhapus otomatis secara berantai.
3. **Pemberlakuan Check Constraints**:
   - `role` pengguna dibatasi hanya bernilai: `'Admin Sistem'`, `'Admin Gudang'`, atau `'Pemilik'`.
   - `harga` barang harus `>= 0`.
   - `stok` barang tidak boleh kurang dari `0` (`stok >= 0`).
   - `jumlah_masuk`, `jumlah_keluar`, dan `jumlah_pengajuan` harus bernilai positif (`> 0`).
