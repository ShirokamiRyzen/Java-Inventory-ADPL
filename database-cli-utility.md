# Dokumentasi Database CLI Utility

Aplikasi Java Inventory dilengkapi dengan utilitas antarmuka baris perintah (CLI) database yang terintegrasi. Utilitas ini terinspirasi oleh mekanisme migrasi pada framework modern (seperti `php artisan` pada Laravel) untuk mempermudah pengelolaan siklus hidup database SQLite (`inventory.db`).

---

## 🚀 Cara Penggunaan

Utilitas ini dapat dipanggil langsung menggunakan skrip pembungkus PowerShell `db.ps1`:

```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 [perintah]
```

### 📋 Daftar Perintah

#### 1. `migrate`
Membuat tabel-tabel relasional yang dibutuhkan aplikasi di dalam database `inventory.db`. Jika tabel sudah ada, proses inisialisasi akan dilewati.
```powershell
powershell -ExecutionPolicy Bypass -File db.ps1 migrate
```

#### 2. `fresh`
Menghapus file database `inventory.db` saat ini secara total dan membangun ulang struktur tabel dalam keadaan kosong (hanya berisi 3 akun pengguna bawaan: admin, gudang, pemilik).
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

Database menggunakan mesin relasional **SQLite 3** dengan skema sebagai berikut:

```mermaid
erDiagram
    users {
        INTEGER id_user PK
        TEXT username UNIQUE
        TEXT password
        TEXT role
        TEXT nama_user
    }
    barang {
        TEXT id_barang PK
        TEXT nama_barang
        REAL harga
        TEXT kategori
        INTEGER stok
    }
    barang_masuk {
        TEXT id_barang_masuk PK
        TEXT tanggal_masuk
        TEXT id_barang FK
        INTEGER jumlah_masuk
        TEXT supplier
    }
    barang_keluar {
        TEXT id_barang_keluar PK
        TEXT tanggal_keluar
        TEXT id_barang FK
        INTEGER jumlah_keluar
        TEXT penerima
    }
    pengajuan_pembelian {
        TEXT id_pengajuan PK
        TEXT id_barang FK
        TEXT tanggal_pengajuan
        INTEGER jumlah_pengajuan
        TEXT status_pengajuan
    }
    laporan {
        TEXT id_laporan PK
        TEXT tanggal_laporan
        TEXT jenis_laporan
        TEXT keterangan
    }

    barang ||--o{ barang_masuk : "distribusi masuk"
    barang ||--o{ barang_keluar : "distribusi keluar"
    barang ||--o{ pengajuan_pembelian : "permintaan restock"
```

---

## ⚙️ Logika Transaksi & Trigger

1. **Foreign Key Integrity**: Driver SQLite dipaksa mengaktifkan validasi foreign key menggunakan perintah `PRAGMA foreign_keys = ON;` pada setiap koneksi agar relasi data tetap terjaga konsistensinya.
2. **On Delete Cascade**: Jika data barang pada tabel `barang` dihapus, seluruh log barang masuk, barang keluar, dan pengajuan pembelian yang berelasi dengan barang tersebut akan terhapus otomatis secara berantai.
3. **Pemberlakuan Check Constraints**:
   - `role` pengguna dibatasi hanya bernilai: `'Admin Sistem'`, `'Admin Gudang'`, atau `'Pemilik'`.
   - `harga` barang harus `>= 0`.
   - `stok` barang tidak boleh kurang dari `0` (`stok >= 0`).
   - `jumlah_masuk`, `jumlah_keluar`, dan `jumlah_pengajuan` harus bernilai positif (`> 0`).
