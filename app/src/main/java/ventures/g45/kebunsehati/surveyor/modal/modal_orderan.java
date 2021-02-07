package ventures.g45.kebunsehati.surveyor.modal;

public class modal_orderan {
    String id_order, nama, alama, jumlahitem, status;

    public modal_orderan(){}

    public modal_orderan(String id_order, String nama, String alama, String jumlahitem, String status) {
        this.id_order = id_order;
        this.nama = nama;
        this.alama = alama;
        this.jumlahitem = jumlahitem;
        this.status = status;
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlama() {
        return alama;
    }

    public void setAlama(String alama) {
        this.alama = alama;
    }

    public String getJumlahitem() {
        return jumlahitem;
    }

    public void setJumlahitem(String jumlahitem) {
        this.jumlahitem = jumlahitem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
