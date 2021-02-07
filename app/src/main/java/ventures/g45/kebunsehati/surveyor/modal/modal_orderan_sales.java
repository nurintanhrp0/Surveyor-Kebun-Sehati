package ventures.g45.kebunsehati.surveyor.modal;

public class modal_orderan_sales {
    String nama, qty, harga, total, tanggal, status_order, satuan, id, tglOrder, thumbnail, catatan, kelipatan_order, minimal_order, stock, harga_normal, harga_temp, id_horeka;
    Integer dari, id_temp;

    public modal_orderan_sales(){}

    public modal_orderan_sales(String nama, String qty, String harga, String total, String tanggal, String status_order, String satuan, String id, String tglOrder, String thumbnail, String catatan, String kelipatan_order, String minimal_order, String stock, String harga_normal, String harga_temp, String id_horeka, Integer dari, Integer id_temp) {
        this.nama = nama;
        this.qty = qty;
        this.harga = harga;
        this.total = total;
        this.tanggal = tanggal;
        this.status_order = status_order;
        this.satuan = satuan;
        this.id = id;
        this.tglOrder = tglOrder;
        this.thumbnail = thumbnail;
        this.catatan = catatan;
        this.kelipatan_order = kelipatan_order;
        this.minimal_order = minimal_order;
        this.stock = stock;
        this.harga_normal = harga_normal;
        this.harga_temp = harga_temp;
        this.id_horeka = id_horeka;
        this.dari = dari;
        this.id_temp = id_temp;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
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

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus_order() {
        return status_order;
    }

    public void setStatus_order(String status_order) {
        this.status_order = status_order;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTglOrder() {
        return tglOrder;
    }

    public void setTglOrder(String tglOrder) {
        this.tglOrder = tglOrder;
    }

    public Integer getDari() {
        return dari;
    }

    public void setDari(Integer dari) {
        this.dari = dari;
    }

    public String getHarga_normal() {
        return harga_normal;
    }

    public void setHarga_normal(String harga_normal) {
        this.harga_normal = harga_normal;
    }

    public Integer getId_temp() {
        return id_temp;
    }

    public void setId_temp(Integer id_temp) {
        this.id_temp = id_temp;
    }

    public String getHarga_temp() {
        return harga_temp;
    }

    public void setHarga_temp(String harga_temp) {
        this.harga_temp = harga_temp;
    }

    public String getId_horeka() {
        return id_horeka;
    }

    public void setId_horeka(String id_horeka) {
        this.id_horeka = id_horeka;
    }
}
