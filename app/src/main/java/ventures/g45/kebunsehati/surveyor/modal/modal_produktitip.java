package ventures.g45.kebunsehati.surveyor.modal;

public class modal_produktitip {
    String id_produk, nama_produk, jumlah_titip, harga, waktu_titip;

    public modal_produktitip(){}

    public modal_produktitip(String id_produk, String nama_produk, String jumlah_titip, String harga, String waktu_titip) {
        this.id_produk = id_produk;
        this.nama_produk = nama_produk;
        this.jumlah_titip = jumlah_titip;
        this.harga = harga;
        this.waktu_titip = waktu_titip;
    }

    public String getId_produk() {
        return id_produk;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getJumlah_titip() {
        return jumlah_titip;
    }

    public void setJumlah_titip(String jumlah_titip) {
        this.jumlah_titip = jumlah_titip;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getWaktu_titip() {
        return waktu_titip;
    }

    public void setWaktu_titip(String waktu_titip) {
        this.waktu_titip = waktu_titip;
    }
}
