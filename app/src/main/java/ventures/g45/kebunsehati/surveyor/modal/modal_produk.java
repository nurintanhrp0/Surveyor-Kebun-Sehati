package ventures.g45.kebunsehati.surveyor.modal;

public class modal_produk {
    String nama, thumbnail, stock, satuan, ukuran, catatan_temp, kelipatan_order, minimal_order,keterangan_stock, stock_habis, idtitipjual, harga_temp;
    Integer harga, id_produk, id_kategori, qty_temp, diskon;

    public modal_produk(){}

    public modal_produk(String nama, String thumbnail, String stock, String satuan, String ukuran, String catatan_temp, String kelipatan_order, String minimal_order, String keterangan_stock, String stock_habis, String idtitipjual, String harga_temp, Integer harga, Integer id_produk, Integer id_kategori, Integer qty_temp, Integer diskon) {
        this.nama = nama;
        this.thumbnail = thumbnail;
        this.stock = stock;
        this.satuan = satuan;
        this.ukuran = ukuran;
        this.catatan_temp = catatan_temp;
        this.kelipatan_order = kelipatan_order;
        this.minimal_order = minimal_order;
        this.keterangan_stock = keterangan_stock;
        this.stock_habis = stock_habis;
        this.idtitipjual = idtitipjual;
        this.harga_temp = harga_temp;
        this.harga = harga;
        this.id_produk = id_produk;
        this.id_kategori = id_kategori;
        this.qty_temp = qty_temp;
        this.diskon = diskon;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    public Integer getId_produk() {
        return id_produk;
    }

    public void setId_produk(Integer id_produk) {
        this.id_produk = id_produk;
    }

    public Integer getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(Integer id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public String getCatatan_temp() {
        return catatan_temp;
    }

    public void setCatatan_temp(String catatan_temp) {
        this.catatan_temp = catatan_temp;
    }

    public Integer getQty_temp() {
        return qty_temp;
    }

    public void setQty_temp(Integer qty_temp) {
        this.qty_temp = qty_temp;
    }

    public String getKelipatan_order() {
        return kelipatan_order;
    }

    public void setKelipatan_order(String kelipatan_order) {
        this.kelipatan_order = kelipatan_order;
    }

    public String getMinimal_order() {
        return minimal_order;
    }

    public void setMinimal_order(String minimal_order) {
        this.minimal_order = minimal_order;
    }

    public String getKeterangan_stock() {
        return keterangan_stock;
    }

    public void setKeterangan_stock(String keterangan_stock) {
        this.keterangan_stock = keterangan_stock;
    }

    public String getStock_habis() {
        return stock_habis;
    }

    public void setStock_habis(String stock_habis) {
        this.stock_habis = stock_habis;
    }

    public String getIdtitipjual() {
        return idtitipjual;
    }

    public void setIdtitipjual(String idtitipjual) {
        this.idtitipjual = idtitipjual;
    }

    public Integer getDiskon() {
        return diskon;
    }

    public void setDiskon(Integer diskon) {
        this.diskon = diskon;
    }

    public String getHarga_temp() {
        return harga_temp;
    }

    public void setHarga_temp(String harga_temp) {
        this.harga_temp = harga_temp;
    }
}
