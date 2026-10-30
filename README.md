# 🛒 Aplikasi Jual Beli Komik Digital

Aplikasi desktop untuk menjual dan membeli komik digital, dibangun dengan **Java Swing** sebagai antarmuka grafis dan **Spring Boot** sebagai backend, dengan penyimpanan data menggunakan **PostgreSQL**.

---

## 🛠️ Teknologi yang Digunakan

| Komponen | Teknologi |
| --- | --- |
| Bahasa Pemrograman | Java 17 |
| Framework Backend | Spring Boot 3.3.4 |
| ORM / Database Access | Spring Data JPA (Hibernate) |
| Database | PostgreSQL |
| GUI | Java Swing |
| Build Tool | Apache Maven |
| Utilitas | Lombok |

---

## 📁 Struktur Proyek

```
comicapp/
├── src/
│   └── main/
│       ├── java/com/comicapp/comicapp/
│       │   ├── ComicappApplication.java       # Entry point aplikasi
│       │   ├── model/
│       │   │   ├── User.java                  # Entity pengguna (admin/pembeli)
│       │   │   ├── Comic.java                 # Entity komik (produk digital + harga)
│       │   │   ├── Favorite.java              # Entity wishlist pembeli
│       │   │   └── Purchase.java              # Entity transaksi pembelian
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── ComicRepository.java
│       │   │   ├── FavoriteRepository.java
│       │   │   └── PurchaseRepository.java
│       │   ├── service/
│       │   │   ├── UserService.java
│       │   │   ├── ComicService.java
│       │   │   ├── FavoriteService.java
│       │   │   └── PurchaseService.java
│       │   ├── web/
│       │   │   ├── auth/
│       │   │   │   ├── LoginForm.java         # Form login (JFrame)
│       │   │   │   └── RegisterForm.java      # Form registrasi (JFrame)
│       │   │   ├── admin/
│       │   │   │   └── AdminDashboardFrame.java  # Dashboard penjual (JFrame)
│       │   │   └── user/
│       │   │       └── MainDashboardFrame.java   # Dashboard pembeli (JFrame)
│       │   └── util/
│       │       └── SessionManager.java        # Manajemen sesi login
│       └── resources/
│           └── application.properties         # Konfigurasi database
└── pom.xml
```

---

## ⚙️ Prasyarat

Pastikan perangkat kamu sudah terinstal:

- **Java Development Kit (JDK) 17** atau lebih baru
- **Apache Maven 3.6+** (atau gunakan `./mvnw` yang sudah disertakan)
- **PostgreSQL 14+**
- **IDE** yang direkomendasikan: IntelliJ IDEA / NetBeans / Eclipse

---

## 🚀 Cara Menjalankan Aplikasi

### 1. Clone / Download Proyek

```bash
git clone https://github.com/username/comicapp.git
cd comicapp
```

### 2. Buat Database PostgreSQL

Buka PostgreSQL client (pgAdmin atau terminal) dan jalankan:

```sql
CREATE DATABASE db_comicapp;
```

> Tabel akan dibuat otomatis oleh Hibernate saat aplikasi pertama kali dijalankan (`ddl-auto=update`).

### 3. Konfigurasi Koneksi Database

Edit file `src/main/resources/application.properties` sesuaikan dengan konfigurasi PostgreSQL kamu:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_comicapp
spring.datasource.username=postgres
spring.datasource.password=PASSWORD_KAMU
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 4. Build dan Jalankan

**Menggunakan Maven Wrapper (direkomendasikan):**

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

**Atau menggunakan Maven yang sudah terinstal:**

```bash
mvn spring-boot:run
```

**Atau build dulu lalu jalankan JAR-nya:**

```bash
mvn clean package
java -jar target/comicapp-0.0.1-SNAPSHOT.jar
```

> Setelah dijalankan, jendela **Form Login** akan otomatis muncul.

---

## 👤 Role Pengguna

Aplikasi ini memiliki dua role pengguna:

### 🔑 ADMIN (Penjual / Pengelola Toko)

- Menambah, mengubah, dan menghapus produk komik
- Menentukan harga jual setiap komik
- Melihat data penjualan beserta total pendapatan

### 🛍️ USER (Pembeli)

- Melihat dan mencari katalog komik berdasarkan judul atau genre
- Melihat detail komik beserta harga
- Membeli komik dan melihat riwayat pembelian
- Menambah dan menghapus komik dari daftar favorit (wishlist)

---

## 🖥️ Alur Penggunaan Aplikasi

```
[Aplikasi Berjalan]
       ↓
  [Form Login]
  ├── Belum punya akun? → [Form Registrasi] → kembali ke Login
  └── Login berhasil
       ├── Role ADMIN → [Dashboard Admin]
       │     ├── Tambah / Ubah / Hapus Komik (termasuk harga)
       │     └── Data Penjualan (daftar transaksi & total pendapatan)
       └── Role USER → [Dashboard Pembeli]
             ├── Tab Katalog        : Cari & filter komik, lihat detail, tambah favorit, beli komik
             ├── Tab Favorit        : Lihat, beli, & hapus komik dari wishlist
             └── Tab Pembelian      : Lihat riwayat pembelian komik
```

---

## 🗄️ Skema Database

Hibernate akan otomatis membuat tabel berikut:

| Tabel | Deskripsi |
| --- | --- |
| `users` | Data akun pengguna (username, password, role) |
| `comics` | Data komik (judul, genre, penulis, sinopsis, cover URL, harga) |
| `favorites` | Relasi many-to-many antara user dan komik pada wishlist |
| `purchases` | Transaksi pembelian: user (pembeli), komik, harga saat dibeli, waktu pembelian |

---

## 📌 Catatan Pengembangan

- Password pengguna saat ini disimpan dalam bentuk **plain text**. Untuk lingkungan produksi, gunakan enkripsi seperti **BCrypt**.
- Satu komik hanya bisa dibeli satu kali oleh user yang sama (dicek pada `PurchaseService`).
- Harga disimpan pada harga saat transaksi terjadi, sehingga perubahan harga komik tidak mengubah riwayat pembelian.
- `SessionManager` menggunakan variabel statis — cocok untuk aplikasi desktop single-user.
- Semua komponen GUI (`JFrame`) didaftarkan sebagai Spring `@Component` dengan `@Lazy` agar tidak diinisialisasi sebelum waktunya.

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan tugas akademik di **Universitas Gunadarma**.
