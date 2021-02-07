package ventures.g45.kebunsehati.surveyor.modal;

public class modal_penugasan {
    String id, nama, thumbnail;

    public modal_penugasan(){}

    public modal_penugasan(String id, String nama, String thumbnail) {
        this.id = id;
        this.nama = nama;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
