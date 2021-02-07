package ventures.g45.kebunsehati.surveyor.modal;

public class modal_horeka {
    String id_horeka, nama, koordinat;

    public modal_horeka(){}

    public modal_horeka(String id_horeka, String nama, String koordinat) {
        this.id_horeka = id_horeka;
        this.nama = nama;
        this.koordinat = koordinat;
    }

    public String getId_horeka() {
        return id_horeka;
    }

    public void setId_horeka(String id_horeka) {
        this.id_horeka = id_horeka;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }
}
