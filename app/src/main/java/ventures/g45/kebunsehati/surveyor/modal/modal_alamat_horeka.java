package ventures.g45.kebunsehati.surveyor.modal;

public class modal_alamat_horeka {
    String id, id_horeka, alamat, koordinat, nama;

    public modal_alamat_horeka(){
    }

    public modal_alamat_horeka(String id, String id_horeka, String alamat, String koordinat, String nama) {
        this.id = id;
        this.id_horeka = id_horeka;
        this.alamat = alamat;
        this.koordinat = koordinat;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_horeka() {
        return id_horeka;
    }

    public void setId_horeka(String id_horeka) {
        this.id_horeka = id_horeka;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
