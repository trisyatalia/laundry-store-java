CREATE DATABASE laundry
USE laundry

minta request laundry, liat STATUS transaksi, 


CREATE TABLE rs_request_transaction (
	id_request VARCHAR(10) NOT NULL PRIMARY KEY,
	id_rumah_sakit VARCHAR(10) NOT NULL,
	id_pegawai VARCHAR(10) NOT NULL,
	tanggal_masuk VARCHAR(20) NOT NULL,
	status_request VARCHAR(10),
	FOREIGN KEY (id_rumah_sakit) REFERENCES t_rumah_sakit(id_rumah_sakit),
	FOREIGN KEY (id_pegawai) REFERENCES t_pegawai(id_pegawai)
	
);

DROP TABLE rs_request_transaction

CREATE TABLE t_pegawai (
	ID_PEGAWAI VARCHAR(10) NOT NULL PRIMARY  KEY,
	NAMA_PEGAWAI VARCHAR(50) NOT NULL,
	JENIS_KELAMIN VARCHAR (20),
	JABATAN VARCHAR (20),
	ALAMAT TEXT,
	KONTAK VARCHAR(20),
	passwd VARCHAR(12) NOT NULL
);

CREATE TABLE t_rumah_sakit (
	id_rumah_sakit VARCHAR(10) NOT NULL PRIMARY KEY,
	nama_rumah_sakit VARCHAR(25) NOT NULL,
	alamat_rs TEXT,
	kontak VARCHAR(20),
	passwd VARCHAR(12) NOT NULL

);

--new transaksi table
CREATE TABLE transaksi (
	id_transaksi VARCHAR(10) NOT NULL  PRIMARY KEY,
	id_pegawai VARCHAR(10) NOT NULL,
	id_rumah_sakit VARCHAR(10) NOT NULL,
	tanggal_masuk VARCHAR(20) NOT NULL,	
	bayar_akhir INT,
	dibayar INT,
	kembalian INT,
	tanggal_keluar VARCHAR(20) NULL,	
	statuscheckout VARCHAR(20),
	FOREIGN KEY (id_pegawai) REFERENCES t_pegawai(id_pegawai),
	FOREIGN KEY (id_rumah_sakit) REFERENCES t_rumah_sakit(id_rumah_sakit)
);

CREATE TABLE detail_transaksi (
	id_transaksi VARCHAR(10) NOT NULL,
	id_produk VARCHAR(10) NOT NULL,
	quantity INT NOT NULL,
	harga_total INT,
	FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
	FOREIGN KEY (id_produk) REFERENCES produk(id_produk)
);

--new produk table
CREATE TABLE produk (
	ID_PRODUK VARCHAR(10) NOT NULL PRIMARY KEY,
	nama_produk TEXT,
	harga_produk INT
);

---------------

-- jangan dipake
CREATE TABLE produk (
	ID_PRODUK VARCHAR(10) NOT NULL PRIMARY KEY,
	nama_produk TEXT,
	harga_produk int

);

-- jangan dipake
CREATE TABLE transaksi (
	id_transaksi VARCHAR(10) NOT NULL  PRIMARY KEY,
	id_pegawai VARCHAR(10),
	kontak_pembeli VARCHAR(20) NOT NULL,
	tanggal_masuk VARCHAR(20) NOT NULL,
	tanggal_keluar VARCHAR(20) NOT NULL,	
	bayar_akhir INT,
	FOREIGN KEY (id_pegawai) REFERENCES pegawai(id_pegawai)
);






select right(id_transaksi,2) as no_akhir from transaksi ORDER BY id_transaksi asc

SELECT * FROM vdatastruk ORDER BY id_transaksi asc

USE test
SELECT * FROM pegawai;
SELECT * FROM produk;
SELECT * FROM transaksi;
SELECT * FROM detail_transaksi

INSERT INTO detail_transaksi VALUES ('T0001', 'B0001', 1, 50000)

select CURDATE();

SELECT id_pegawai FROM pegawai

DROP TABLE pegawai;
DROP TABLE produk;

DROP TABLE detail_transaksi;
DROP TABLE transaksi;

DROP VIEW vdatastruk

DROP DATABASE laundry


CREATE VIEW vdatastruk AS
SELECT  t.id_transaksi, t.id_pegawai, t.kontak_pembeli, t.tanggal_masuk, t.tanggal_keluar,
			pr.nama_produk, d.quantity, pr.harga_produk, d.harga_total, t.bayar_akhir
FROM    transaksi t
        INNER JOIN detail_transaksi d
            ON t.id_transaksi = d.id_transaksi
        INNER JOIN produk pr
            ON d.id_produk = pr.ID_PRODUK


SELECT * FROM vdatastruk