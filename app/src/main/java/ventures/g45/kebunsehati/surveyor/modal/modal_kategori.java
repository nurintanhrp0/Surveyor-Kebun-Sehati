package ventures.g45.kebunsehati.surveyor.modal;

public class modal_kategori {
    String nama, thumbnail;
    Integer id_kategori;

    public modal_kategori(){}

    public modal_kategori(String nama, String thumbnail, Integer id_kategori) {
        this.nama = nama;
        this.thumbnail = thumbnail;
        this.id_kategori = id_kategori;
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

    public Integer getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(Integer id_kategori) {
        this.id_kategori = id_kategori;
    }
}
