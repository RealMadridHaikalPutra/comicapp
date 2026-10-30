# 📚 Aplikasi Baca Komik Digital

Aplikasi desktop untuk membaca dan mengelola konten komik digital, dibangun dengan **Java Swing** sebagai antarmuka grafis dan **Spring Boot** sebagai backend, dengan penyimpanan data menggunakan **MySQL**.

---

## 🛠️ Teknologi yang Digunakan

| Komponen | Teknologi |
| --- | --- |
| Bahasa Pemrograman | Java 17 |
| Framework Backend | Spring Boot 3.3.4 |
| ORM / Database Access | Spring Data JPA (Hibernate) |
| Database | MySQL |
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
│       │   │   ├── User.java                  # Entity pengguna
│       │   │   ├── Comic.java                 # Entity komik
│       │   │   ├── Chapter.java               # Entity chapter
│       │   │   ├── Favorite.java              # Entity favorit
│       │   │   └── ReadingHistory.java        # Entity riwayat baca
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── ComicRepository.java
│       │   │   ├── ChapterRepository.java
│       │   │   ├── FavoriteRepository.java
│       │   │   └── ReadingHistoryRepository.java
│       │   ├── service/
│       │   │   ├── UserService.java
│       │   │   ├── ComicService.java
│       │   │   ├── ChapterService.java
│       │   │   ├── FavoriteService.java
│       │   │   └── ReadingHistoryService.java
│       │   ├── web/
│       │   │   ├── auth/
│       │   │   │   ├── LoginForm.java         # Form login (JFrame)
│       │   │   │   └── RegisterForm.java      # Form registrasi (JFrame)
│       │   │   ├── admin/
│       │   │   │   └── AdminDashboardFrame.java  # Dashboard admin (JFrame)
│       │   │   └── user/
│       │   │       └── MainDashboardFrame.java   # Dashboard user (JFrame)
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
- **MySQL Server 8.0+**
- **IDE** yang direkomendasikan: IntelliJ IDEA / NetBeans / Eclipse

---

## 🚀 Cara Menjalankan Aplikasi

### 1. Clone / Download Proyek

```bash
git clone https://github.com/username/comicapp.git
cd comicapp
```

### 2. Buat Database MySQL

Buka MySQL client (MySQL Workbench, HeidiSQL, atau terminal) dan jalankan:

```sql
CREATE DATABASE db_comicapp;
```

> Tabel akan dibuat otomatis oleh Hibernate saat aplikasi pertama kali dijalankan (`ddl-auto=update`).

### 3. Konfigurasi Koneksi Database

Edit file `src/main/resources/application.properties` sesuaikan dengan konfigurasi MySQL kamu:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_comicapp?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=PASSWORD_KAMU
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
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

### 🔑 ADMIN

- Menambah, mengubah, dan menghapus data komik
- Mengelola chapter (tambah / hapus) untuk setiap komik
- Melihat seluruh daftar komik

### 📖 USER

- Melihat dan mencari katalog komik berdasarkan judul atau genre
- Menambah dan menghapus komik dari daftar favorit
- Membaca chapter dan riwayat membaca tercatat otomatis

---

## 🖥️ Alur Penggunaan Aplikasi

```
[Aplikasi Berjalan]
       ↓
  [Form Login]
  ├── Belum punya akun? → [Form Registrasi] → kembali ke Login
  └── Login berhasil
       ├── Role ADMIN → [Dashboard Admin]
       │     ├── Tambah / Ubah / Hapus Komik
       │     └── Kelola Chapter per Komik
       └── Role USER → [Dashboard User]
             ├── Tab Katalog  : Cari & filter komik, tambah favorit, lihat detail & chapter
             ├── Tab Favorit  : Lihat & hapus komik favorit
             └── Tab Riwayat : Lihat riwayat chapter yang pernah dibaca
```

---

## 🗄️ Skema Database

Hibernate akan otomatis membuat tabel berikut:

| Tabel | Deskripsi |
| --- | --- |
| `users` | Data akun pengguna (username, password, role) |
| `comics` | Data komik (judul, genre, penulis, sinopsis, cover URL) |
| `chapters` | Data chapter yang berelasi ke komik |
| `favorites` | Relasi many-to-many antara user dan komik favorit |
| `reading_histories` | Riwayat membaca: user, komik, chapter terakhir, waktu |

---

## 📌 Catatan Pengembangan

- Password pengguna saat ini disimpan dalam bentuk **plain text**. Untuk lingkungan produksi, gunakan enkripsi seperti **BCrypt**.
- `SessionManager` menggunakan variabel statis — cocok untuk aplikasi desktop single-user.
- Semua komponen GUI (`JFrame`) didaftarkan sebagai Spring `@Component` dengan `@Lazy` agar tidak diinisialisasi sebelum waktunya.

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan tugas akademik di **Universitas Gunadarma**.
